package com.bungholes.rfid.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.bungholes.rfid.messaging.TagPosition;
import com.bungholes.rfid.messaging.TagReading;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.math.DoubleMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.bungholes.rfid.messaging.TagReading.dateOrdering;
import static com.google.common.collect.Lists.newArrayList;

public class TagReadingActor extends UntypedActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagReadingActor.class);

    private static final int MAX_BUFFER_SIZE = 30;

    private String tagId;
    private String tid;
    private String tagPositionActorName;

    private List<TagReading> antenna1 = newArrayList();
    private List<TagReading> antenna2 = newArrayList();

    private List<TagReading> antenna1Archive;
    private List<TagReading> antenna2Archive;
    private ActorRef actor;

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof TagReading) {
            TagReading tagReading = (TagReading) message;
            //LOGGER.debug("Received message: {}", message);

            saveTidAndTagId(tagReading);

            recordReading(tagReading);

            if (buffersFull()) {
                archiveBuffers();

                TagPosition tagPosition = calculateTagPosition();

                tagPositionActor().tell(tagPosition, getSelf());
            } else {
                getSender().tell(message, getSelf());
            }
        } else {
            unhandled(message);
        }
    }

    private TagPosition calculateTagPosition() {
        double position = calculatePosition(antenna1Archive, antenna2Archive);

        return new TagPosition(tagId, tid, position);
    }

    private ActorRef tagPositionActor() {
        if (actor == null) {
            actor = context().system().actorOf(Props.create(TagPositionActor.class), tagPositionActorName);
        }

        return actor;
    }

    private boolean buffersFull() {
        boolean buffersFull = ((antenna1.size() > MAX_BUFFER_SIZE) && (antenna2.size() > MAX_BUFFER_SIZE));

        if (buffersFull) {
            //LOGGER.debug("buffersFull {} {}", antenna1.size(), antenna2.size());
        }

        return buffersFull;
    }

    private void archiveBuffers() {
        antenna1Archive = antenna1;
        antenna2Archive = antenna2;

        antenna1 = newArrayList();
        antenna2 = newArrayList();
    }

    private void saveTidAndTagId(TagReading tagReading) {
        if (tid == null) {
            tid = tagReading.getTid();
        }

        if (tagId == null) {
            tagId = tagReading.getTagId();
        }

        if (tagPositionActorName == null) {
            tagPositionActorName = "tagPosition-" + tid;
        }
    }

    private void recordReading(TagReading tagReading) {
        if (tagReading.getAntenna().equals("1")) {
            antenna1.add(tagReading);
        } else if (tagReading.getAntenna().equals("2")) {
            antenna2.add(tagReading);
        }
    }

    private double calculatePosition(List<TagReading> antenna1, List<TagReading> antenna2) {
        List<Float> phaseDifferences = calculatePhaseDifferencesBetweenAntennas(antenna1, antenna2);
        double phaseDifferenceMean = DoubleMath.mean(phaseDifferences);

        return relativePositionFromPhaseDifferenceMean(phaseDifferenceMean);
    }

    private double relativePositionFromPhaseDifferenceMean(double phaseDifferenceMean) {
        ///return Math.floor((phaseDifferenceMean + 90) * 7.6);
        return phaseDifferenceMean + 90;
    }

    private List<Float> calculatePhaseDifferencesBetweenAntennas(
            List<TagReading> antenna1,
            List<TagReading> antenna2) {

        ImmutableList<TagReading> sortedAntenna1 = ImmutableSortedSet.orderedBy(dateOrdering())
                .addAll(antenna1)
                .build()
                .asList();

        ImmutableList<TagReading> sortedAntenna2 = ImmutableSortedSet.orderedBy(dateOrdering())
                .addAll(antenna2)
                .build()
                .asList();

        List<Float> phaseDifferences = calculatePhaseDifferencesBetweenSortedLists(sortedAntenna1, sortedAntenna2);

        return phaseDifferences;
    }

    private List<Float> calculatePhaseDifferencesBetweenSortedLists(
            List<TagReading> antenna1Readings,
            List<TagReading> antenna2Readings) {

        List<Float> phaseDifferenceResults = newArrayList();

        for (int i = 0; i < antenna1Readings.size(); i++) {
            if (i < antenna2Readings.size() && i < antenna2Readings.size()) {
                TagReading tagReading1 = antenna1Readings.get(i);
                TagReading tagReading2 = antenna2Readings.get(i);

                float phaseDifference = calculatePhaseDifferenceBetweenTwoReadings(tagReading1, tagReading2);

                phaseDifferenceResults.add(phaseDifference);
            }
        }

        return phaseDifferenceResults;
    }

    private float calculatePhaseDifferenceBetweenTwoReadings(TagReading tagReading1, TagReading tagReading2) {
        float phaseDifference = tagReading2.getPhaseAngle() - tagReading1.getPhaseAngle();

        if (Math.abs(phaseDifference) > 90) {
            phaseDifference = phaseDifference - ( 180 * Math.signum(phaseDifference));
        }

        return phaseDifference;
    }
}

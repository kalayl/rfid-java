package com.bungholes.rfid.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.bungholes.rfid.messaging.TagPosition;
import com.bungholes.rfid.messaging.TagReading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class TagReadingActor extends UntypedActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagReadingActor.class);
    private static final int MAX_BUFFER_SIZE = 1000;

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

            // if there are enough readings,
            // move the lists aside, assign new lists,
            // spawn the position calculator and send the results on
            if (buffersFull()) {
                TagPosition tagPosition = calculateTagPosition();

                // send the position on
                tagPositionActor().tell(tagPosition, getSelf());
            } else {
                getSender().tell(message, getSelf());
            }
        } else {
            unhandled(message);
        }
    }

    private TagPosition calculateTagPosition() {
        archiveBuffers();
        double position = calculatePosition(antenna1Archive, antenna2Archive);
        TagPosition tagPosition = new TagPosition(tagId, tid, position);

        return tagPosition;
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
            tagPositionActorName = "tagPosition" + tid;
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

        return calculateRelativePositionBasedOnPhaseDifferences(phaseDifferences);
    }

    private double calculateRelativePositionBasedOnPhaseDifferences(List<Float> phaseDifferences) {

        // average phase difference
        float averagePhaseDifference = sum(phaseDifferences) / phaseDifferences.size();

        double relativePosition = averagePhaseDifference; //Math.floor((averagePhaseDifference + 90) * 7.6);

        return relativePosition;
    }

    private float sum(List<Float> phaseDifferences) {
        float sum = 0f;
        for (Float diff : phaseDifferences) {
            sum += diff;
        }

        return sum;
    }

    private List<Float> calculatePhaseDifferencesBetweenAntennas(List<TagReading> antenna1, List<TagReading> antenna2) {
        List<Float> phaseDifferences = newArrayList();

        // calculate the phase difference
        for (int i = 0; i < antenna1.size(); i++)
        {
            if (i < antenna2.size() && i < antenna2.size())
            {
                float phaseDifference = antenna2.get(i).getPhaseAngle() - antenna1.get(i).getPhaseAngle();
                if (Math.abs(phaseDifference) > 90)
                {
                    phaseDifference = phaseDifference - ( 180 * Math.signum(phaseDifference));
                }


                phaseDifferences.add(phaseDifference);
            }
        }


        return phaseDifferences;
    }
}

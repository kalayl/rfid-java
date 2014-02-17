package com.bungholes.rfid.messaging.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.bungholes.rfid.messages.TagPosition;
import com.bungholes.rfid.messages.TagReading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
        double position = PhaseCalculations.calculatePosition(antenna1Archive, antenna2Archive);

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
            tagId = tagReading.getTagId();
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

}

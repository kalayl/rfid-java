package com.bungholes.rfid.akka;

import akka.actor.UntypedActor;
import com.bungholes.rfid.messaging.TagReading;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TagReadingActor extends UntypedActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagReadingActor.class);

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof TagReading) {
            LOGGER.debug("Received message: {}", message);
            getSender().tell(message, getSelf());
        } else {
            unhandled(message);
        }
    }
}

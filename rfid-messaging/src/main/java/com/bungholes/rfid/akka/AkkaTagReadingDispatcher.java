package com.bungholes.rfid.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.bungholes.rfid.messaging.TagReading;
import com.bungholes.rfid.messaging.TagReadingDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class AkkaTagReadingDispatcher implements TagReadingDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagReadingActor.class);

    private ActorSystem actorSystem;

    private Map<String, ActorRef> actors = newHashMap();

    public AkkaTagReadingDispatcher(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }

    public void dispatch(TagReading tagReading) {
        String tid = tagReading.getTid();

        if (tid == null) {
            //LOGGER.warn("Discarding reading with null tid {}", tagReading);
        } else {
            ActorRef actor = getActor("tagReading" + tid);
            actor.tell(tagReading, ActorRef.noSender());
        }
    }

    private ActorRef getActor(String name) {
        ActorRef actor = actors.get(name);

        if (actor == null) {
            LOGGER.debug("Creating new Actor for name {}", name);

            actor = actorSystem.actorOf(Props.create(TagReadingActor.class), name);
            actors.put(name, actor);
        }

        return actor;
    }
}

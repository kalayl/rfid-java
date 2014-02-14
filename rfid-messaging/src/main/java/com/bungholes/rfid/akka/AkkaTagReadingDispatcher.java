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
        ActorRef actor = getActor(tagReading.getTagid());
        actor.tell(tagReading, ActorRef.noSender());
    }

    private ActorRef getActor(String tagId) {
        ActorRef actor = actors.get(tagId);

        if (actor == null) {
            LOGGER.debug("Creating new Actor for tagId {}", tagId);

            actor = actorSystem.actorOf(Props.create(TagReadingActor.class), tagId);
            actors.put(tagId, actor);
        }

        return actor;
    }
}

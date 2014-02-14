package com.bungholes.rfid.server;

import akka.actor.ActorSystem;
import com.bungholes.rfid.akka.AkkaTagReadingDispatcher;
import com.bungholes.rfid.messaging.TagReadingDispatcher;
import com.bungholes.rfid.reader.ConnectionDetails;
import com.bungholes.rfid.reader.sirit.SiritConnection;
import com.bungholes.rfid.reader.sirit.SiritEventManager;
import com.bungholes.rfid.reader.sirit.SiritTagReader;
import com.bungholes.rfid.reader.util.PhaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wiring {
    private static final Logger LOGGER = LoggerFactory.getLogger(Wiring.class);

    // TODO ASAP
    public static final String IP_ADDRESS = "192.168.0.109";
    public static String LOGIN = "admin";
    public static String PASSWORD = "readeradmin";
    public static final ConnectionDetails configuration = new ConnectionDetails(IP_ADDRESS, LOGIN, PASSWORD);
    private String actorSystemName = "rfid";

    private SiritConnection connection;
    private SiritEventManager eventManager;
    private SiritTagReader tagReader;

    public Wiring() {
        wire();
    }

    private void wire() {
        connection = new SiritConnection(configuration);
        eventManager = new SiritEventManager(connection);

        ActorSystem system = ActorSystem.create(actorSystemName);
        TagReadingDispatcher dispatcher = new AkkaTagReadingDispatcher(system);

        tagReader = new SiritTagReader(dispatcher, new PhaseUtils());

        LOGGER.debug("wiring complete...");
    }

    public SiritEventManager getEventManager() {
        return eventManager;
    }

    public SiritConnection getConnection() {
        return connection;
    }

    public SiritTagReader getTagReader() {
        return tagReader;
    }
}

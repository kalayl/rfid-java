package com.bungholes.rfid.server;

import akka.actor.ActorSystem;
import com.bungholes.rfid.akka.AkkaTagReadingDispatcher;
import com.bungholes.rfid.messaging.TagReadingDispatcher;
import com.bungholes.rfid.reader.RfidReaderConnection;
import com.bungholes.rfid.reader.sirit.SiritConnectionDetails;
import com.bungholes.rfid.reader.sirit.SiritConnection;
import com.bungholes.rfid.reader.sirit.SiritEventSubscriptionManager;
import com.bungholes.rfid.reader.sirit.SiritTagReportEventListener;
import com.bungholes.rfid.reader.sirit.PhaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wiring {
    private static final Logger LOGGER = LoggerFactory.getLogger(Wiring.class);

    // TODO ASAP
    public static final String IP_ADDRESS = "192.168.0.109";
    public static String LOGIN = "admin";
    public static String PASSWORD = "readeradmin";

    public static final SiritConnectionDetails configuration = new SiritConnectionDetails(IP_ADDRESS, LOGIN, PASSWORD);

    private String actorSystemName = "rfid";

    private SiritConnection connection;
    private SiritEventSubscriptionManager eventManager;
    private SiritTagReportEventListener tagReader;

    public Wiring() {
        wire();
    }

    private void wire() {
        connection = new SiritConnection(configuration);
        eventManager = new SiritEventSubscriptionManager(connection);

        ActorSystem system = ActorSystem.create(actorSystemName);
        TagReadingDispatcher dispatcher = new AkkaTagReadingDispatcher(system);

        tagReader = new SiritTagReportEventListener(dispatcher, new PhaseUtils());

        LOGGER.debug("wiring complete...");
    }

    public SiritEventSubscriptionManager getEventManager() {
        return eventManager;
    }

    public RfidReaderConnection getConnection() {
        return connection;
    }

    public SiritTagReportEventListener getTagReader() {
        return tagReader;
    }
}

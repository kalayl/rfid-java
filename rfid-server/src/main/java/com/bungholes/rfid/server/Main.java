package com.bungholes.rfid.server;

import com.bungholes.rfid.reader.RfidConnectionException;
import com.bungholes.rfid.reader.RfidReaderConnection;
import com.bungholes.rfid.reader.RfidReaderException;
import com.bungholes.rfid.reader.sirit.SiritEventSubscriptionManager;
import com.bungholes.rfid.reader.sirit.SiritTagReportEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bungholes.rfid.server.Wiring.EVENT_NAME;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private Wiring wiring;

    private final RfidReaderConnection connection;
    private final SiritEventSubscriptionManager eventManager;
    private SiritTagReportEventListener tagReader;

    private String eventId = null;

    public Main() {
        wiring = new Wiring();

        connection = wiring.getConnection();
        eventManager = wiring.getEventManager();
        tagReader = wiring.getSiritTagReportEventListener();
    }

    private void run() throws RfidReaderException, RfidConnectionException, InterruptedException {
        try {
            connection.connect();
            eventId = eventManager.register(EVENT_NAME, tagReader);
            connection.activate();

            while (true) {}

        } catch (RfidReaderException e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    private void stop() throws RfidReaderException, RfidConnectionException {
        eventManager.unregister(eventId, EVENT_NAME);
        connection.standby();
        connection.close();
    }

    public static void main(String[] args) throws Exception {
        final Main main = new Main();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    main.stop();
                } catch (RfidReaderException e) {
                    e.printStackTrace();
                } catch (RfidConnectionException e) {
                    e.printStackTrace();
                }
            }
        });

        main.run();
    }
}

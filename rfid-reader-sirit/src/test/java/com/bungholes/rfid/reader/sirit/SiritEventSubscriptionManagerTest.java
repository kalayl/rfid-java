package com.bungholes.rfid.reader.sirit;

import com.bungholes.rfid.messaging.TagReadingDispatcher;
import com.bungholes.rfid.messages.TagReading;
import com.bungholes.rfid.reader.RfidConnectionException;
import com.bungholes.rfid.reader.RfidReaderException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bungholes.rfid.reader.sirit.BasicIntegrationTest.configuration;
import static org.fest.assertions.Assertions.assertThat;

public class SiritEventSubscriptionManagerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SiritEventSubscriptionManagerTest.class);

    @Test
    public void canRegisterForEvents() throws RfidConnectionException, RfidReaderException, InterruptedException {

        SiritConnection connection = new SiritConnection(configuration);
        SiritEventSubscriptionManager eventManager = new SiritEventSubscriptionManager(connection);

        connection.connect();


        String eventId = eventManager.register("event.tag.report", new SiritTagReportEventListener(new TagReadingDispatcher() {
            @Override
            public void dispatch(TagReading tagReading) {
                // TODO
                assertThat(tagReading).isNotNull();
            }
        }, new PhaseUtils()));

        connection.activate();

        Thread.sleep(500);
        eventManager.unregister(eventId, "event.tag.report");

        connection.standby();
        connection.close();
    }
}

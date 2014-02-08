package com.bungholes.rfid.reader.sirit;

import com.bungholes.rfid.reader.RfidConnectionException;
import com.bungholes.rfid.reader.RfidReaderException;
import com.bungholes.rfid.reader.util.PhaseUtils;
import com.bungholes.rfid.tag.TagReading;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.bungholes.rfid.reader.sirit.BasicIntegrationTest.configuration;
import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.Assertions.assertThat;

public class SiritEventManagerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SiritEventManagerTest.class);

    @Test
    public void canRegisterForEvents() throws RfidConnectionException, RfidReaderException, InterruptedException {

        SiritConnection connection = new SiritConnection(configuration);
        SiritEventManager eventManager = new SiritEventManager(connection);

        connection.connect();

//        String eventId = eventManager.register("event.tag.report", new IEventListener() {
//            @Override
//            public void EventFound(Object o, EventInfo eventInfo) {
//                String tagID = eventInfo.getParameter(EventInfo.EVENT_TAG_ARRIVE_PARAMS.TAG_ID);
//                LOGGER.debug("Tag Id {} ", tagID);
//            }
//        });

        List<TagReading> readings = newArrayList();
        String eventId = eventManager.register("event.tag.report", new SiritTagReader(readings, new PhaseUtils()));

        connection.activate();

        Thread.sleep(500);
        eventManager.unregister(eventId, "event.tag.report");

        connection.standby();
        connection.close();

        assertThat(readings.size()).isGreaterThan(0);
    }
}

package com.bungholes.rfid.reader.sirit;

import com.bungholes.rfid.messaging.TagReadingDispatcher;
import com.bungholes.rfid.messaging.TagReading;
import com.bungholes.rfid.reader.util.PhaseUtils;
import com.sirit.driver.IEventListener;
import com.sirit.mapping.EventInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiritTagReader implements IEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SiritTagReader.class);

    private TagReadingDispatcher tagReadingDispatcher;
    private final PhaseUtils phaseUtils;

    public SiritTagReader(TagReadingDispatcher tagReadingDispatcher, PhaseUtils phaseUtils) {
        this.tagReadingDispatcher = tagReadingDispatcher;
        this.phaseUtils = phaseUtils;
    }

    @Override
    public void EventFound(Object o, EventInfo eventInfo) {
        if (eventInfo.getEventType() == EventInfo.EVENT_TYPES.TAG_REPORT)
        {
            String tagId = eventInfo.getParameter(EventInfo.EVENT_TAG_REPORT_PARAMS.TAG_ID);
            String antenna = eventInfo.getParameter(EventInfo.EVENT_TAG_REPORT_PARAMS.ANTENNA);
            String time = eventInfo.getParameter(EventInfo.EVENT_TAG_REPORT_PARAMS.TIME);
            String phase = eventInfo.getParameter(EventInfo.EVENT_TAG_REPORT_PARAMS.PHASE);
            String frequency = eventInfo.getParameter(EventInfo.EVENT_TAG_REPORT_PARAMS.FREQUENCY);
            String rssi = eventInfo.getParameter(EventInfo.EVENT_TAG_REPORT_PARAMS.RSSI);
            float phaseAngle = phaseUtils.calculatePhaseAngle(phase);

            TagReading reading = new TagReading(tagId, antenna, phaseAngle, frequency, rssi, time);
            //LOGGER.debug("TagReading {} ", reading);
            if (tagId != null)
            {
                tagReadingDispatcher.dispatch(reading);
            }
        }
    }

}

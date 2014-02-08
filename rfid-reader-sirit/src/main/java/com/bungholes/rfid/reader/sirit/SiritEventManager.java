package com.bungholes.rfid.reader.sirit;

import com.bungholes.rfid.reader.RfidReaderException;
import com.sirit.driver.ConnectionException;
import com.sirit.driver.IEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiritEventManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SiritEventManager.class);
    private SiritConnection connection;

    public SiritEventManager(SiritConnection connection) {
        this.connection = connection;
    }

    public String register(String eventName, IEventListener eventHandler) throws RfidReaderException {
        String eventId = null;
        try {
            eventId = connection.getDataManager().getEventChannel(eventHandler);
        } catch (ConnectionException e) {
            throw new RfidReaderException(e);
        }

        LOGGER.debug("DataManager return eventId {}", eventId);

        // Register for event.tag.report
        if(!connection.getReaderManager().eventsRegister(eventId, eventName)) {
            throw new RfidReaderException("Failure to register for event: "
                    + connection.getReaderManager().getLastErrorMessage());
        }

        LOGGER.debug("Registered for {}", eventName);

        return eventId;
    }

    public void unregister(String eventId, String eventName) throws RfidReaderException {
        if(!connection.getReaderManager().eventsUnregister(eventId, eventName)) {
            throw new RfidReaderException("Failure to unregister for event: "
                    + connection.getReaderManager().getLastErrorMessage());
        }

        LOGGER.debug("Unregistered for {} with eventId {}", eventName, eventId);
    }
}

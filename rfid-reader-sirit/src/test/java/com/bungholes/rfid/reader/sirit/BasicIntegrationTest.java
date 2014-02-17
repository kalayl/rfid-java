package com.bungholes.rfid.reader.sirit;

import com.sirit.data.DataManager;
import com.sirit.driver.IEventListener;
import com.sirit.mapping.EventInfo;
import com.sirit.mapping.InfoManager;
import com.sirit.mapping.ReaderManager;
import com.sirit.mapping.SetupManager;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicIntegrationTest implements IEventListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicIntegrationTest.class);

    public static final String IP_ADDRESS = "192.168.0.109";
    public static String LOGIN = "admin";
    public static String PASSWORD = "readeradmin";

    public static final SiritConnectionDetails configuration = new SiritConnectionDetails(IP_ADDRESS, LOGIN, PASSWORD);

    @Test
    public void testBasicConnection()
    {
        try
        {
            long startTime = System.currentTimeMillis();

            // Open a connection to the reader
            DataManager dataManager = new DataManager(DataManager.ConnectionTypes.SOCKET, IP_ADDRESS, 0);
            dataManager.open();
            LOGGER.debug("SiritConnection Opened");

            // Get the reader's name
            InfoManager infoManager = new InfoManager(dataManager);
            String v = infoManager.getName();
            LOGGER.debug("Reader Name {}", v);
            infoManager = null;

            // Login as administrator
            ReaderManager readerManager = new ReaderManager(dataManager);
            if(!readerManager.login(LOGIN, PASSWORD)) {
                throw new Exception("Login attempt failed: " + readerManager.getLastErrorMessage());
            }
            v = readerManager.whoAmI();
            LOGGER.debug("Login {}", v);

            // Open an event channel and get it's ID
            String eventChannelId = dataManager.getEventChannel(this);
            LOGGER.debug("Event Channel ID {}", eventChannelId);

            // Register for event.tag.report
            String eventTagName = "event.tag.report";
            if(!readerManager.eventsRegister(eventChannelId, eventTagName)) {
                throw new Exception("Failure to register for event : " + readerManager.getLastErrorMessage());
            }
            LOGGER.debug("Registered for {}", eventTagName);

            // Set operating mode to active
            SetupManager setupManager = new SetupManager(dataManager);
            setupManager.setOperatingMode(SetupManager.OPERATING_MODE_TYPES.ACTIVE);
            LOGGER.debug("Operating Mode: Active");

            // Sleep while handling tag events
            Thread.sleep(500);

            // Unregister for event.tag.report
            if(!readerManager.eventsUnregister(eventChannelId, eventTagName))
                throw new Exception("Failure to unregister for event: " + readerManager.getLastErrorMessage());
            LOGGER.debug("Unregistered for {}", eventTagName);

            // Set operating mode to standby
            setupManager.setOperatingMode(SetupManager.OPERATING_MODE_TYPES.STANDBY);
            LOGGER.debug("Operating Mode: Standby");

            // Close the connection
            setupManager = null;
            readerManager = null;
            dataManager.close();
            LOGGER.debug("SiritConnection Closed");

            // Output the time to execute application
            long endTime = System.currentTimeMillis();
            long t = endTime - startTime - 500;
            LOGGER.debug("Estimated Time: {} (ms)", t);
        }
        catch(Exception e)
        {
            LOGGER.error("Error: " + e.getMessage());
        }
    }

    /**
     * Event Handler
     */
    public void EventFound(Object sender, EventInfo eventInfo)
    {
        String tagID = eventInfo.getParameter(EventInfo.EVENT_TAG_ARRIVE_PARAMS.TAG_ID);
        LOGGER.debug("Tag ID: " + tagID);
    }
}
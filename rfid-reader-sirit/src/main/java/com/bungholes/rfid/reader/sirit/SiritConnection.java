package com.bungholes.rfid.reader.sirit;

import com.bungholes.rfid.reader.RfidConnectionException;
import com.bungholes.rfid.reader.RfidReaderConnection;
import com.sirit.data.DataManager;
import com.sirit.driver.ConnectionException;
import com.sirit.mapping.InfoManager;
import com.sirit.mapping.ReaderException;
import com.sirit.mapping.ReaderManager;
import com.sirit.mapping.SetupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiritConnection implements RfidReaderConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(SiritEventSubscriptionManager.class);

    private final SiritConnectionDetails siritConnectionDetails;

    private DataManager dataManager;
    private ReaderManager readerManager;
    private SetupManager setupManager;

    public SiritConnection(SiritConnectionDetails siritConnectionDetails) {
        this.siritConnectionDetails = siritConnectionDetails;
    }

    @Override
    public void connect() throws RfidConnectionException {

        // Open a connection to the reader
        try {
            dataManager = new DataManager(DataManager.ConnectionTypes.SOCKET, siritConnectionDetails.getIp(), 0);
            dataManager.open();
        } catch (ConnectionException e) {
            throw new RfidConnectionException(e);
        }

        LOGGER.debug("SiritConnection Opened");

        // Login as administrator
        readerManager = new ReaderManager(dataManager);
        if (!readerManager.login(siritConnectionDetails.getLogin(), siritConnectionDetails.getPassword()))
        {
            throw new RfidConnectionException("Login attempt failed: " + readerManager.getLastErrorMessage());
        }
    }

    @Override
    public void close() throws RfidConnectionException {
        // Close the connection
        setupManager = null;
        readerManager = null;

        try {
            dataManager.close();
        } catch (ConnectionException e) {
            throw new RfidConnectionException(e);
        }

        LOGGER.debug("SiritConnection Closed");
    }

    @Override
    public void activate() throws RfidConnectionException {
        setupManager = new SetupManager(dataManager);
        try {
            setupManager.setOperatingMode(SetupManager.OPERATING_MODE_TYPES.ACTIVE);
        } catch (ReaderException e) {
            throw new RfidConnectionException(e);
        } catch (ConnectionException e) {
            throw new RfidConnectionException(e);
        }

        LOGGER.debug("Operating Mode: Active");
    }

    @Override
    public void standby() throws RfidConnectionException {
        try {
            setupManager.setOperatingMode(SetupManager.OPERATING_MODE_TYPES.STANDBY);
        } catch (ReaderException e) {
            throw new RfidConnectionException(e);
        } catch (ConnectionException e) {
            throw new RfidConnectionException(e);
        }

        LOGGER.debug("Operating Mode: Standby");
    }

    public String getReaderName() {
        InfoManager infoManager = new InfoManager(dataManager);
        String name = infoManager.getName();
        infoManager = null;

        return name;
    }

    public String getLogin() {
        return readerManager.whoAmI();
    }

    public SiritConnectionDetails getSiritConnectionDetails() {
        return siritConnectionDetails;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public ReaderManager getReaderManager() {
        return readerManager;
    }

    public SetupManager getSetupManager() {
        return setupManager;
    }

}

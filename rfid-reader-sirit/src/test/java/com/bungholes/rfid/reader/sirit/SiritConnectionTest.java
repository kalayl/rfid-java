package com.bungholes.rfid.reader.sirit;

import com.bungholes.rfid.reader.RfidConnectionException;
import org.junit.Test;

import static com.bungholes.rfid.reader.sirit.BasicIntegrationTest.configuration;

public class SiritConnectionTest {

    @Test
    public void basicConnectionTestThrowsNoExceptions() throws RfidConnectionException {
        SiritConnection connection = new SiritConnection(configuration);

        connection.connect();
        connection.activate();

        connection.standby();
        connection.close();
    }

}

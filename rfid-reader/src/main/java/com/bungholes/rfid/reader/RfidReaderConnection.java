package com.bungholes.rfid.reader;

public interface RfidReaderConnection {

    void connect() throws RfidConnectionException;

    void close() throws RfidConnectionException;

    void activate() throws RfidConnectionException;

    void standby() throws RfidConnectionException;
}

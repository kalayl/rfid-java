package com.bungholes.rfid.reader;

public class RfidConnectionException extends Exception {

    public RfidConnectionException() {
        super();
    }

    public RfidConnectionException(String s) {
        super(s);
    }

    public RfidConnectionException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public RfidConnectionException(Throwable throwable) {
        super(throwable);
    }
}

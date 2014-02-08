package com.bungholes.rfid.reader;

public class RfidReaderException extends Exception {

    public RfidReaderException() {
        super();
    }

    public RfidReaderException(String s) {
        super(s);
    }

    public RfidReaderException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public RfidReaderException(Throwable throwable) {
        super(throwable);
    }
}

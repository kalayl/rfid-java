package com.bungholes.rfid.messaging;

public interface TagReadingDispatcher {

    void dispatch(TagReading tagReading);

}

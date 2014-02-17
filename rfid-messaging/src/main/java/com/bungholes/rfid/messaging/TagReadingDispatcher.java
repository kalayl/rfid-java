package com.bungholes.rfid.messaging;

import com.bungholes.rfid.messages.TagReading;

public interface TagReadingDispatcher {

    void dispatch(TagReading tagReading);

}

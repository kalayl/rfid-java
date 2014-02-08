package com.bungholes.rfid.reader.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhaseUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhaseUtils.class);

    public float calculatePhaseAngle(String rawPhase)
    {
        int a = Integer.decode(rawPhase);//, 16);
        if (a > 32768)
        {
            a = a - 32768;
        }

        float phasea = (((float)a) / 32768) * 180;

        return phasea;
    }

}

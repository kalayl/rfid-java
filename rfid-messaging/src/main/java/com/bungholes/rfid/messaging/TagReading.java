package com.bungholes.rfid.messaging;

import java.io.Serializable;

public class TagReading implements Serializable {

    private final String tagid;
    private final String antenna;
    private final float phaseAngle;
    private final String frequency;
    private final String rssi;
    private final String time;

    public TagReading(String tagid, String antenna, float phaseAngle, String frequency, String rssi, String time) {
        this.tagid = tagid;
        this.antenna = antenna;
        this.phaseAngle = phaseAngle;
        this.frequency = frequency;
        this.rssi = rssi;
        this.time = time;
    }

    public String getTagid() {
        return tagid;
    }

    public String getAntenna() {
        return antenna;
    }

    public float getPhaseAngle() {
        return phaseAngle;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getRssi() {
        return rssi;
    }

    public String getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TagReading that = (TagReading) o;

        if (Float.compare(that.phaseAngle, phaseAngle) != 0) return false;
        if (antenna != null ? !antenna.equals(that.antenna) : that.antenna != null) return false;
        if (frequency != null ? !frequency.equals(that.frequency) : that.frequency != null) return false;
        if (rssi != null ? !rssi.equals(that.rssi) : that.rssi != null) return false;
        if (tagid != null ? !tagid.equals(that.tagid) : that.tagid != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tagid != null ? tagid.hashCode() : 0;
        result = 31 * result + (antenna != null ? antenna.hashCode() : 0);
        result = 31 * result + (phaseAngle != +0.0f ? Float.floatToIntBits(phaseAngle) : 0);
        result = 31 * result + (frequency != null ? frequency.hashCode() : 0);
        result = 31 * result + (rssi != null ? rssi.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TagReading{" +
                "tagid='" + tagid + '\'' +
                ", antenna='" + antenna + '\'' +
                ", phaseAngle='" + phaseAngle + '\'' +
                ", frequency='" + frequency + '\'' +
                ", rssi='" + rssi + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}

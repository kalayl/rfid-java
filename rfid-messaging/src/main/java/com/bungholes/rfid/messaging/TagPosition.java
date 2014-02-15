package com.bungholes.rfid.messaging;

import java.io.Serializable;

public class TagPosition implements Serializable {

    private final String tagId;
    private final String tid;

    private final double relativePosition;

    public TagPosition(String tagId, String tid, double relativePosition) {
        this.tagId = tagId;
        this.tid = tid;
        this.relativePosition = relativePosition;
    }

    public String getTagId() {
        return tagId;
    }

    public String getTid() {
        return tid;
    }

    public double getRelativePosition() {
        return relativePosition;
    }

    @Override
    public String toString() {
        return "TagPosition{" +
                "tagId='" + tagId + '\'' +
                ", tid='" + tid + '\'' +
                ", relativePosition=" + relativePosition +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TagPosition that = (TagPosition) o;

        if (Double.compare(that.relativePosition, relativePosition) != 0) return false;
        if (tagId != null ? !tagId.equals(that.tagId) : that.tagId != null) return false;
        if (tid != null ? !tid.equals(that.tid) : that.tid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = tagId != null ? tagId.hashCode() : 0;
        result = 31 * result + (tid != null ? tid.hashCode() : 0);
        temp = Double.doubleToLongBits(relativePosition);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}

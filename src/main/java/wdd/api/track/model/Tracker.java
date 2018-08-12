package wdd.api.track.model;

import java.io.Serializable;
import java.math.BigInteger;

public class Tracker implements Serializable {

    private static final long serialVersionUID = 1L;

    private String trackerId;
    private BigInteger userId;

    public String getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(String trackerId) {
        this.trackerId = trackerId;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }
}

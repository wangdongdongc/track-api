package wdd.api.track.model;

import java.io.Serializable;
import java.math.BigInteger;

public class Behavior implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigInteger userId;
    private Long typeId;
    private Long subTypeId;
    private String detail;
    private String url;

    @Override
    public String toString() {
        return "Behavior{" +
                "typeId=" + typeId +
                ", subTypeId=" + subTypeId +
                ", detail='" + detail + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public Behavior(Long typeId, Long subTypeId) {
        this.typeId = typeId;
        this.subTypeId = subTypeId;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public Long getTypeId() {
        return typeId;
    }

    public Long getSubTypeId() {
        return subTypeId;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

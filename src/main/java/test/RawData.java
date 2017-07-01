package test;

import java.io.Serializable;

public class RawData implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String            deviceId;

    private Long              timeKey;

    private double            v;

    private double            p;

    private double            i;

    private double            e;

    public RawData(String deviceId, Long timeKey, double v, double p, double i, double e) {
        this.deviceId = deviceId;
        this.timeKey = timeKey;
        this.v = v;
        this.p = p;
        this.i = i;
        this.e = e;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Long getTimeKey() {
        return timeKey;
    }

    public void setTimeKey(Long timeKey) {
        this.timeKey = timeKey;
    }

    public double getV() {
        return v;
    }

    public void setV(double v) {
        this.v = v;
    }

    public double getP() {
        return p;
    }

    public void setP(double p) {
        this.p = p;
    }

    public double getI() {
        return i;
    }

    public void setI(double i) {
        this.i = i;
    }

    public double getE() {
        return e;
    }

    public void setE(double e) {
        this.e = e;
    }

}

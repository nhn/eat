package com.nhnent.eat.common.Config;

/**
 * Created by NHNEnt on 2017-03-20.
 */
public class QaService {
    private String endPointName;
    private String ipAddress;
    private int port;

    public String getEndPointName() {
        return endPointName;
    }

    public void setEndPointName(String endPointName) {
        this.endPointName = endPointName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

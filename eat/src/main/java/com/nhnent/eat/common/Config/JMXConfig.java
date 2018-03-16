package com.nhnent.eat.common.Config;

public class JMXConfig {

    private String mBeanFilePath;
    private String mBeanName;
    private String endPointName;
    private String ipAddress;
    private int port;

    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }

    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String value) {
        this.ipAddress = value;
    }

    public String getEndPointName() {
        return endPointName;
    }
    public void setEndPointName(String value) {
        this.endPointName = value;
    }

    public String getMBeanName()
    {
        return mBeanName;
    }

    public void setMBeanName(String value) {this.mBeanName = value;}

    public String getmBeanFilePath() {
        return mBeanFilePath;
    }
    public void setmBeanFilePath(String value) {
        this.mBeanFilePath = value;
    }

}

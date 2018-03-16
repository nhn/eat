package com.nhnent.eat.common.Config;

/**
 * Created by NHNEnt on 2016-11-04.
 */
public class Server {
    private String socketType;
    private String ip;
    private int port;
    private int countOfPort;
    private String subUriOfWS;
    private Ssl ssl;

    /**
     * Get target server IP
     *
     * @return target server IP
     */
    public String getIp() {
        return ip;
    }

    /**
     * Set target server IP
     *
     * @param ip target server IP
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Get target server port number.
     *
     * @return target server port number
     */
    public int getPort() {
        return port;
    }

    /**
     * Set target server port number.
     *
     * @param port target server port number
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Get count of port
     *
     * @return count of port
     */
    public int getCountOfPort() {
        return countOfPort;
    }

    /**
     * Set count of port
     *
     * @param countOfPort count of port
     */
    public void setCountOfPort(int countOfPort) {
        this.countOfPort = countOfPort;
    }

    /**
     * Get Socket Type
     * @return String value among(STREAM, WEBSOCKET)
     */
    public String getSocketType() {
        return socketType;
    }

    /**
     * Set Socket Type
     * @param socketType String value among(STREAM, WEBSOCKET)
     */
    public void setSocketType(String socketType) {
        this.socketType = socketType;
    }

    /**
     * Get sub URI of WS
     * @return sub URI of WS
     */
    public String getSubUriOfWS() {
        return subUriOfWS;
    }

    /**
     * Set sub URI of WS
     * @param subUriOfWS sub URI of WS
     */
    public void setSubUriOfWS(String subUriOfWS) {
        this.subUriOfWS = subUriOfWS;
    }

    public Ssl getSsl() {
        return ssl;
    }

    public void setSsl(Ssl ssl) {
        this.ssl = ssl;
    }
}

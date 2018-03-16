package com.nhnent.eat.common.Config;

import java.nio.file.Paths;

/**
 * Created by NHNEnt on 2016-11-04.
 */

public class Common {
    private String rootDirectory;
    private String extraLibraryPath;
    private boolean ignoreUnnecessaryPacket;
    private int countOfRealThread;
    private boolean loggingOnSameFile;
    private int receiveTimeoutSec;


    /**
     * Get root directory of resources.
     * This directory should contains `extraLibrary` and `proto` directories.
     * @return root directory
     */
    public String getRootDirectory() {
        return rootDirectory;
    }

    /**
     * Set root directory of resources.
     * This directory should contains `extraLibrary` and `proto` directories.
     * @return root directory
     */
    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    /**
     * Get Root path of configuration
     * @return Root path of configuration
     */
    public String getExtraLibraryPath() {
        return Paths.get(getRootDirectory(), "extraLibrary").toString();
    }


    /**
     * get config value of 'Ignore unnecessary packet'
     * @return value of 'Ignore unnecessary packet'
     */
    public boolean isIgnoreUnnecessaryPacket() {
        return ignoreUnnecessaryPacket;
    }

    /**
     * set value of 'Ignore unnecessary packet'
     * @param ignoreUnnecessaryPacket value of 'Ignore unnecessary packet'
     */
    public void setIgnoreUnnecessaryPacket(boolean ignoreUnnecessaryPacket) {
        this.ignoreUnnecessaryPacket = ignoreUnnecessaryPacket;
    }

    /**
     * get count of real thread
     * @return count of real thread
     */
    public int getCountOfRealThread() {
        return countOfRealThread;
    }

    /**
     * set count of real thread
     * @param countOfRealThread count of real thread
     */
    public void setCountOfRealThread(int countOfRealThread) {
        this.countOfRealThread = countOfRealThread;
    }

    /**
     * Get loggingOnSameFile
     * @return loggingOnSameFile
     */
    public boolean isLoggingOnSameFile() {
        return loggingOnSameFile;
    }

    /**
     * Set loggingOnSameFile
     * @param loggingOnSameFile loggingOnSameFile
     */
    public void setLoggingOnSameFile(boolean loggingOnSameFile) {
        this.loggingOnSameFile = loggingOnSameFile;
    }

    public int getReceiveTimeoutSec() {
        return receiveTimeoutSec;
    }

    public void setReceiveTimeoutSec(int receiveTimeoutSec) {
        this.receiveTimeoutSec = receiveTimeoutSec;
    }

}

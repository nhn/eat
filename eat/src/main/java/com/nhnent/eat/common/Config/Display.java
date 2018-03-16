package com.nhnent.eat.common.Config;

/**
 * Created by NHNEnt on 2017-01-24.
 */
public class Display {
    private boolean displayTransferredPacket;
    private boolean displayTransferredPacketJson;
    private boolean displayUnitTestResult;
    private boolean displayFinalTestResult;
    private boolean displayStatisticResult;
    private boolean displayFinalStatisticResult;

    /**
     * get Display transferred packet or not
     *
     * @return configured value
     */
    public boolean isDisplayTransferredPacket() {
        return displayTransferredPacket;
    }

    /**
     * set Display transferred packet or not
     *
     * @param displayTransferredPacket configured value
     */
    public void setDisplayTransferredPacket(boolean displayTransferredPacket) {
        this.displayTransferredPacket = displayTransferredPacket;
    }

    /**
     * get Display Uni test result or not
     *
     * @return configured value
     */
    public boolean isDisplayUnitTestResult() {
        return displayUnitTestResult;
    }

    /**
     * set Display Uni test result or not
     *
     * @param displayUnitTestResult configured value
     */
    public void setDisplayUnitTestResult(boolean displayUnitTestResult) {
        this.displayUnitTestResult = displayUnitTestResult;
    }

    /**
     * get Display final test result or not
     *
     * @return configured value
     */
    public boolean isDisplayFinalTestResult() {
        return displayFinalTestResult;
    }

    /**
     * set Display final test result or not
     *
     * @param displayFinalTestResult configured value
     */
    public void setDisplayFinalTestResult(boolean displayFinalTestResult) {
        this.displayFinalTestResult = displayFinalTestResult;
    }

    /**
     * get Display statistics result or not
     *
     * @return configured value
     */
    public boolean isDisplayStatisticResult() {
        return displayStatisticResult;
    }

    /**
     * set Display statistics result or not
     *
     * @param displayStatisticResult configured value
     */
    public void setDisplayStatisticResult(boolean displayStatisticResult) {
        this.displayStatisticResult = displayStatisticResult;
    }

    public boolean isDisplayFinalStatisticResult() {
        return displayFinalStatisticResult;
    }

    public void setDisplayFinalStatisticResult(boolean displayFinalStatisticResult) {
        this.displayFinalStatisticResult = displayFinalStatisticResult;
    }

    public boolean isDisplayTransferredPacketJson() {
        return displayTransferredPacketJson;
    }

    public void setDisplayTransferredPacketJson(boolean displayTransferredPacketJson) {
        this.displayTransferredPacketJson = displayTransferredPacketJson;
    }
}

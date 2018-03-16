package com.nhnent.eat.common.Config;

/**
 * Created by NHNEnt on 2016-11-04.
 */
public class Scenario {
    private String scenarioPath;
    private int playerCount;
    private int testActorStartGap;
    private String[] userId;
    private String[] scenarioFile;

    /**
     * Get scenario files path
     *
     * @return scenario files path
     */
    public String getScenarioPath() {
        return scenarioPath;
    }

    /**
     * Set scenario files path
     *
     * @param scenarioPath scenario files path
     */
    public void setScenarioPath(String scenarioPath) {
        this.scenarioPath = scenarioPath;
    }

    /**
     * Get count of player
     *
     * @return Count of player
     */
    public int getPlayerCount() {
        return playerCount;
    }

    /**
     * Set count of player
     *
     * @param playerCount count of player
     */
    public void setPlayerCount(int playerCount) {
        if(playerCount < 0) {
            playerCount = 0;
        }
        this.playerCount = playerCount;
    }

    /**
     * Get sleep to pause between Tester Actor starting
     * @return sleep time(ms) value
     */
    public int getTestActorStartGap() {
        return testActorStartGap;
    }

    /**
     * Set sleep to pause between Tester Actor starting
     * @param testActorStartGap sleep time(ms) value
     */
    public void setTestActorStartGap(int testActorStartGap) {
        if(testActorStartGap < 0) {
            testActorStartGap = 0;
        }
        this.testActorStartGap = testActorStartGap;
    }

    /**
     * Get Player ID
     *
     * @return Player ID
     */
    public String[] getUserId() {
        return userId;
    }

    /**
     * Set SNO of player
     *
     * @param userId Player ID
     */
    public void setUserId(String[] userId) {
        this.userId = userId;
    }

    /**
     * Get scenario file
     *
     * @return Scenario file
     */
    public String[] getScenarioFile() {
        return scenarioFile;
    }

    /**
     * Set scenario file
     *
     * @param scenarioFile Scenario file
     */
    public void setScenarioFile(String[] scenarioFile) {
        this.scenarioFile = scenarioFile;
    }
}

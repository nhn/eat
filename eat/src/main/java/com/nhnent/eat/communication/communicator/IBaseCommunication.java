package com.nhnent.eat.communication.communicator;

import co.paralleluniverse.fibers.SuspendExecution;
import com.nhnent.eat.entity.ScenarioUnit;
import javafx.util.Pair;

/**
 * Common Interfaces for communications
 */
public interface IBaseCommunication {

    /**
     * Check the given scenario type is need to process or not
     * @param scenarioType Scenario Type
     * @return need to process or not
     */
    Boolean isRegisteredScenarioType(String scenarioType);

    /**
     * Process ScenarioUnit(which contains scenario data)
     * @param scenarioUnit ScenarioUnit(which contains scenario data)
     * @throws SuspendExecution exception handle for Actor(Fiber)
     */
    void execute(ScenarioUnit scenarioUnit) throws SuspendExecution;

    /**
     * Compare given expectation and real received packet(or data)
     * The received packet(or data) will comes from processing of `execute(ScenarioUnit scenarioUnit)`
     * If the executed scenario was receive packet(or data), it will be kept in the implementation class
     * @param scenarioUnit  ScenarioUnit(which contains expected packet or data)
     * @return result of comparison
     * @throws SuspendExecution throw exception for Actor(Fiber)
     * @throws InterruptedException throw exception for Interrupted
     */
    Boolean compareWithRealResponse(ScenarioUnit scenarioUnit) throws SuspendExecution, InterruptedException;

    //--------------------------------------------- for Netty customAPI

    /**
     * Read packet from collection of receiving packet
     * @return Pair collection which contains Packet Name and Packet Byte(s)
     * @throws SuspendExecution throw exception for Actor(Fiber)
     * @throws InterruptedException throw exception for Interrupted
     */
    Pair<String, byte[]> readPacket() throws SuspendExecution, InterruptedException;

    /**
     * Request transfer packet to server
     * @param sendPck requested packet which will transfer to server
     * @throws SuspendExecution throw exception for Actor(Fiber)
     * @throws InterruptedException throw exception for Interrupted
     */
    void transferPacket(final byte[] sendPck) throws SuspendExecution, InterruptedException;
    //---------------------------------------------
}

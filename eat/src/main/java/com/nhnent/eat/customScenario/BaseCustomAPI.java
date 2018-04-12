package com.nhnent.eat.customScenario;

import co.paralleluniverse.fibers.SuspendExecution;
import com.nhnent.eat.TesterActor;
import com.nhnent.eat.common.Config.Config;
import com.nhnent.eat.common.PacketClassPool;
import com.nhnent.eat.communication.communicator.IBaseCommunication;
import com.nhnent.eat.entity.ScenarioExecutionResult;
import com.nhnent.eat.entity.ScenarioUnit;
import com.nhnent.eat.entity.ScenarioUnitType;
import com.nhnent.eat.packets.StreamPacket;
import javafx.util.Pair;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import static com.nhnent.eat.Main.userInfo;

/**
 * Created by NHNEnt on 2017-05-10.
 */
public abstract class BaseCustomAPI {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected PacketClassPool packetClassPool;
    protected HashMap<String, String> runtimeVar;
    protected String userId;
    protected ScenarioExecutionResult scenarioResult;
    protected final HashMap<String, String> globalVariable = new HashMap<>();

    List<IBaseCommunication> listCommunication;

    protected Instant requestTime = Instant.now();
    protected Instant responseTime = Instant.now();

    public void initialize(final PacketClassPool packetClassPool, final List<IBaseCommunication> communicationList, final HashMap<String, String> runtimeVar,
                           final String userId) throws SuspendExecution {

        globalVariable.putAll(TesterActor.globalVariable.get());
        this.packetClassPool = packetClassPool;

        this.listCommunication = communicationList;

        this.runtimeVar = runtimeVar;
        this.userId = userId;
    }

    /**
     * Execute extra function(API)
     *
     * @param scenarioResult Result of scenario execution
     * @param scenario       Scenario Unit to execution
     * @return If need to stop scenario execution return `False`, or continue return `True`
     * @throws SuspendExecution     throw exception for Actor(Fiber)
     * @throws InterruptedException throw exception for Interrupted
     */
    public abstract Boolean executeExtraFunction(ScenarioExecutionResult scenarioResult, ScenarioUnit scenario)
            throws SuspendExecution, InterruptedException;

    public Pair<String, byte[]> recvBodyPacket() throws SuspendExecution, InterruptedException {

        Pair<String, byte[]> recvPacket = null;
        for (IBaseCommunication communication : listCommunication) {
            if (communication.isRegisteredScenarioType(ScenarioUnitType.Response)) {
                recvPacket = communication.readPacket();
                break;
            }
        }

        responseTime = Instant.now();
        scenarioResult.listResponseTime.add(Duration.between(requestTime, responseTime));
        scenarioResult.succeedCount++;
        requestTime = Instant.now();

        if (Config.obj().getDisplay().isDisplayTransferredPacket()) {
            if (Config.obj().getDisplay().isDisplayTransferredPacketJson()) {
                logger.info("[userId:{}][Received packet][{}] \n{}",
                        userInfo.get(),
                        recvPacket.getKey(),
                        decodePacket(recvPacket.getKey(), recvPacket.getValue())
                );
            } else {
                logger.info("[userId:{}][Received packet][{}]",
                        userInfo.get(),
                        recvPacket.getKey()
                );
            }
        }
        return recvPacket;
    }

    public String decodePacket(String packetClass, byte[] packetData) {
        String decodedJson = "";
        Class<?> clsPck;
        try {
            clsPck = packetClassPool.findClassEndWithGivenName(packetClass);

            Object decodedPacket = StreamPacket.obj().decodePacket(userId, clsPck, packetData);

            decodedJson = StreamPacket.obj().packetToJson(userId, decodedPacket);
        } catch (Exception e) {
            logger.error("failed to decode packet[{}]\n{}", packetClass, ExceptionUtils.getStackTrace(e));
        }
        return decodedJson;
    }

    public void sendPacketToServer(ScenarioUnit scenarioUnit) {
        byte[] sendPacket = null;

        try {

            sendPacket = StreamPacket.obj().jsonToPacket(userId, scenarioUnit);

            for (IBaseCommunication communication : listCommunication) {
                if (communication.isRegisteredScenarioType(ScenarioUnitType.Request)) {
                    communication.transferPacket(sendPacket);
                    break;
                }
            }
            
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        if (Config.obj().getDisplay().isDisplayTransferredPacket()) {
            if (Config.obj().getDisplay().isDisplayTransferredPacketJson()) {

                byte[] bodyPacket = StreamPacket.obj().getBodyPacket(userId, sendPacket);

                logger.info("[userId:{}][Transfer Packet][{}]\n{}",
                        userInfo.get(),
                        scenarioUnit.name,
                        decodePacket(scenarioUnit.name, bodyPacket)
                );
            } else {
                logger.info("[userId:{}][Transfer Packet][{}]",
                        userInfo.get(),
                        scenarioUnit.name);
            }
        }
    }
}

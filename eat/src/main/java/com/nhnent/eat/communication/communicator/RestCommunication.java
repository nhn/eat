package com.nhnent.eat.communication.communicator;

import co.paralleluniverse.fibers.SuspendExecution;
import com.nhnent.eat.common.Config.Config;
import com.nhnent.eat.communication.RESTful.RESTClient;
import com.nhnent.eat.entity.ScenarioUnit;
import com.nhnent.eat.entity.ScenarioUnitType;
import com.nhnent.eat.handler.ComparePacket;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Stack;

/**
 * Communication for REST
 */
public class RestCommunication implements IBaseCommunication {

    private Stack<String> restResponseStack = null;
    private RESTClient restClient = null;
    private final Logger logger = LoggerFactory.getLogger(getClass());


    public RestCommunication() {
        restResponseStack = new Stack<>();
        restClient = new RESTClient();
    }

    @Override
    public Boolean isRegisteredScenarioType(String scenarioType) {

        if (scenarioType.equals(ScenarioUnitType.RequestREST)
                || scenarioType.equals(ScenarioUnitType.ResponseREST)

                || scenarioType.equals(ScenarioUnitType.RequestRestCall))   // have to deleted later.. (old version)
            return Boolean.TRUE;

        else return Boolean.FALSE;
    }

    @Override
    public void execute(ScenarioUnit scenarioUnit) throws SuspendExecution {
        restResponseStack.push(restClient.requestRestCall(scenarioUnit));
    }

    @Override
    public Boolean compareWithRealResponse(ScenarioUnit scenarioUnit) throws SuspendExecution, InterruptedException {
        if (restResponseStack.isEmpty()) return Boolean.FALSE;

        String realResultJson = restResponseStack.pop();

        Boolean isSucceed = ComparePacket.ComparePacket(scenarioUnit.json, realResultJson);

        if (Config.obj().getDisplay().isDisplayUnitTestResult()) {
            logger.info("<-------Matching result(" + "RESTful Response" + ")------->");

            if (isSucceed) {
                logger.info("<-------Result : Succeed!");
            } else {
                logger.error("<-------Result : Failure!");
            }
            logger.info("<-------expected:\n" + scenarioUnit.json);
            logger.info("<-------real:\n" + realResultJson);
        }

        return isSucceed;
    }

    @Override
    public Pair<String, byte[]> readPacket() {
        return null;
    }

    @Override
    public void transferPacket(byte[] sendPck) {

    }

    public Boolean isResponseStackEmpty() {
        return restResponseStack.isEmpty();
    }
}

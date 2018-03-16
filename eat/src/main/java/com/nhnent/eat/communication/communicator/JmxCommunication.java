package com.nhnent.eat.communication.communicator;

import co.paralleluniverse.fibers.SuspendExecution;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.nhnent.eat.communication.jmx.CommonJMXClient;
import com.nhnent.eat.entity.ScenarioUnit;
import com.nhnent.eat.entity.ScenarioUnitType;
import com.nhnent.eat.handler.PacketJsonHandler;
import javafx.util.Pair;

import java.util.Stack;

import static com.nhnent.eat.common.CommonDefine.EmptyString;
import static com.nhnent.eat.common.JsonUtil.getValueOfVar;

/**
 * Communication for JMX
 */
public class JmxCommunication implements IBaseCommunication {

    private final Stack<Object> jmxResponseStack;
    private final PacketJsonHandler packetJsonHandler;

    public JmxCommunication() {
        jmxResponseStack = new Stack<>();
        packetJsonHandler = new PacketJsonHandler();
    }

    @Override
    public Boolean isRegisteredScenarioType(String scenarioType) {

        if (scenarioType.equals(ScenarioUnitType.SetJMXQaCommand)
                || scenarioType.equals(ScenarioUnitType.GetJMXQaCommand))
            return Boolean.TRUE;

        else
            return Boolean.FALSE;


    }

    @Override
    public void execute(ScenarioUnit scenarioUnit) throws SuspendExecution {
        Object ret = CommonJMXClient.obj().qaCommand(scenarioUnit.name, scenarioUnit.dest, scenarioUnit.json);

        if (ret != null)
            jmxResponseStack.push(ret);
    }

    @Override
    public Boolean compareWithRealResponse(ScenarioUnit scenarioUnit) throws SuspendExecution, InterruptedException {
        Object realValue = jmxResponseStack.pop();

        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(scenarioUnit.json);

        JsonElement jeExpect = getValueOfVar(je, "Expectation");
        String expect = jeExpect != null ? jeExpect.getAsString() : EmptyString;

        return packetJsonHandler.simpleMatch(expect, realValue);
    }

    @Override
    public Pair<String, byte[]> readPacket() {
        return null;
    }

    @Override
    public void transferPacket(byte[] sendPck) {

    }
}

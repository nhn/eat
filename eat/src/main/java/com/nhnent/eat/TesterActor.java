/*
* Copyright 2016 NHN Entertainment Corp.
*
* NHN Entertainment Corp. licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.nhnent.eat;

import co.paralleluniverse.actors.BasicActor;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;
import com.nhnent.eat.common.Config.Config;
import com.nhnent.eat.communication.communicator.JmxCommunication;
import com.nhnent.eat.communication.communicator.NettyCommunication;
import com.nhnent.eat.communication.communicator.RestCommunication;
import com.nhnent.eat.entity.MessageType;
import com.nhnent.eat.entity.Messages;
import com.nhnent.eat.entity.ScenarioExecutionResult;
import com.nhnent.eat.entity.ScenarioUnit;
import com.nhnent.eat.handler.ScenarioExecutor;
import com.nhnent.eat.handler.ScenarioLoader;
import com.nhnent.eat.packets.StreamPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.nhnent.eat.Main.userInfo;

/**
 * Tester Actor
 * When it receive testing execute, it will load and execute testing scenario.
 */
public final class TesterActor extends BasicActor<Messages, Void> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private int actorIndex = 0;
    private String userId;
    public static final InheritableThreadLocal<HashMap<String, String>> globalVariable = new InheritableThreadLocal<>();

    /**
     * doRun of Actor. It performs requests on Actor Mailbox.
     */
    @Override
    protected Void doRun() throws InterruptedException, SuspendExecution {

        TesterActor.globalVariable.set(new HashMap<>());

        List<ScenarioUnit> listScenarioPacket = new ArrayList<>();

        while(true) {
            Object o = receive();
            logger.debug("Received testing execute (" + this.ref().toString() + ")");
            if (o instanceof Messages) {
                Messages m = (Messages) o;

                //Handle execute of test preparation
                if (m.type == MessageType.PrepareTest) {
                    userInfo.set(m.userId);
                    actorIndex = m.actorIndex;

                    String playerId = String.format("Player_%06d", actorIndex + 1);
                    MDC.put("playerId", playerId);
                    MDC.put("strand", Strand.currentStrand().getName());

                    String logfileName;
                    if(Config.obj().getCommon().isLoggingOnSameFile()) {
                        logfileName = String.format("%s", playerId);
                    } else {
                        Date now = new Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                        String format = formatter.format(now);

                        logfileName = String.format("%s_%s", format, playerId);
                    }

                    MDC.put("logfileName", logfileName);

                    logger.debug("Load Scenario [" + m.scenarioFile + "]");
                    listScenarioPacket.addAll(loadScenario(m.scenarioFile, m.userId));

                    Messages finishMessage = new Messages(this.ref, MessageType.TestPrepared);
                    m.sender.send(finishMessage);
                }

                //Handle execute of test start
                if (m.type == MessageType.TestStart) {
                    ScenarioExecutor scenarioExecutor = new ScenarioExecutor(userId);

                    StreamPacket.obj().initialize(userId);

                    scenarioExecutor.addCommunication(new RestCommunication());
                    scenarioExecutor.addCommunication(new JmxCommunication());
                    scenarioExecutor.addCommunication(new NettyCommunication(userId, actorIndex));

                    ScenarioExecutionResult result = scenarioExecutor.runScenario(listScenarioPacket);

                    listScenarioPacket.clear();

                    Messages finishMessage = new Messages(this.ref, MessageType.TestFinished);
                    finishMessage.userId = userId;
                    finishMessage.scenarioExecutionResult = result;
                    m.sender.send(finishMessage);

                    break;
                }
            }else{
                Strand.sleep(1);
            }
        }
        logger.info("Test is finished");

        MDC.remove("playerId");
        MDC.remove("strand");
        MDC.remove("logfileName");

        return null;
    }

    /**
     * Load Scenario packet from scenario file.
     *
     * @param scenarioFile Scenario file name
     * @param userId    User ID
     * @return List of Scenario Packet
     */
    private List<ScenarioUnit> loadScenario(final String scenarioFile, final String userId) throws SuspendExecution {
        this.userId = userId;
        ScenarioLoader scenarioLoader = new ScenarioLoader();
        String scenarioPath = Config.obj().getScenario().getScenarioPath();
        String fullScenarioFilePath = scenarioPath + "/" + scenarioFile;
        return scenarioLoader.loadScenario(fullScenarioFilePath, userId);
    }
}


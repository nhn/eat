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

package com.nhnent.eat.handler;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;
import com.nhnent.eat.common.Config.Config;
import com.nhnent.eat.common.PacketClassPool;
import com.nhnent.eat.communication.communicator.IBaseCommunication;
import com.nhnent.eat.communication.jmx.JMXClient;
import com.nhnent.eat.customScenario.ApiLoader;
import com.nhnent.eat.entity.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.nhnent.eat.common.CommonDefine.EmptyString;
import static com.nhnent.eat.common.CommonDefine.UsingVariableDelimiter;
import static com.nhnent.eat.common.Util.applyVariable;


/**
 * Scenario executor transfer packets to target server, compare testing result and report it.
 */
public class ScenarioExecutor {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String userId;
    private HashMap<String, String> runtimeVar = new HashMap<>();

    /**
     * Common Interfaces for communication
     */
    List<IBaseCommunication> listCommunication = new LinkedList<>();

    public ScenarioExecutor(String userId) {
        this.userId = userId;
    }

    public void addCommunication(IBaseCommunication communication) {
        listCommunication.add(communication);
    }

    /**
     * This function performs scenario
     *
     * @param listScenarioPacket list of packet scenario
     */
    public final ScenarioExecutionResult runScenario(final List<ScenarioUnit> listScenarioPacket)
            throws SuspendExecution {

        return execute(listScenarioPacket);
    }

    /**
     * Based on scenario list, it will perform transfer packet or receive packet, then it will report the test result.
     *
     * @param listScenario list of packet scenario
     * @return test result(counts of succeed and failure)
     */
    private ScenarioExecutionResult execute(final List<ScenarioUnit> listScenario)
            throws SuspendExecution {

        ReportHandler reportHandler = new ReportHandler();
        int succeedCount = 0;
        int failureCount = 0;
        List<Integer> previousLoopStartIdx = new ArrayList<>();
        List<Integer> loopCountList = new ArrayList<>();
        int loopDepth = -1; // 0 is first depth, so let's set (-1) as a default value.
        ScenarioExecutionResult result = new ScenarioExecutionResult();

        Instant requestTime = Instant.now();
        Instant responseTime;

        String originalJson;

        if (Config.obj().getCustomScenarioAPI().isUse()) {
            ApiLoader.obj().initialize(PacketClassPool.obj(), listCommunication, runtimeVar, userId);
        }
        try {
            for (int scenarioIdx = 0; scenarioIdx < listScenario.size(); scenarioIdx++) {
                ScenarioUnit scenario = listScenario.get(scenarioIdx);

                if (logger.isDebugEnabled()) {
                    logger.debug("Instance making for----, \n"
                            + "pckType:" + scenario.type + ", pckName:" + scenario.name + ", \n"
                            + "dest:" + scenario.dest + ", subId:" + scenario.subId + ", \n"
                            + "pckJson:" + scenario.json);
                }

                // TODO: JMX call Old version.. have to delete
                if (scenario.type.equals(ScenarioUnitType.SetCardDeck)) {
                    JMXClient.obj().setCardDeck(scenario.json);
                    logger.info("-------------------------");
                    logger.info("|      Set Card Deck    |");
                    logger.info("-------------------------");
                    logger.debug("Set Card Deck is transmitted\n.{}", scenario.json);
                    continue;
                }

                // TODO: JMX call Old version.. have to delete
                if (scenario.type.equals(ScenarioUnitType.SetQaCommand)) {
                    JMXClient.obj().setQaCommand(scenario.json);
                    logger.info("-------------------------");
                    logger.info("|      Set QA Command    |");
                    logger.info("-------------------------");
                    logger.debug("Set QA Command is transmitted\n.{}", scenario.json);
                    continue;
                }

                if (scenario.loopType == LoopType.LoopStart) {
                    if (scenario.loopDepth != loopDepth) {
                        loopDepth = scenario.loopDepth;
                        previousLoopStartIdx.add(scenarioIdx);
                        loopCountList.add(scenario.loopCount);

                        logger.debug("Loop Start");
                    }
                    continue;
                }
                if (scenario.loopType == LoopType.LoopEnd) {
                    int currentLoopCount = loopCountList.get(loopDepth) - 1;
                    if (currentLoopCount < 1) {
                        //If loop is finished
                        loopCountList.remove(loopDepth);
                        previousLoopStartIdx.remove(loopDepth);
                        loopDepth--;
                        logger.debug("Loop End");
                    } else {
                        //If need to continue loop
                        loopCountList.set(loopDepth, currentLoopCount);
                        scenarioIdx = previousLoopStartIdx.get(scenario.loopDepth); //To move 1 after from loop start
                        if (logger.isDebugEnabled())
                            logger.debug("LOOP CONTINUE currentLoopCount=> " + currentLoopCount +
                                    " scenarioIdx=> " + scenarioIdx);
                    }
                    continue;
                }
                if (scenario.type.equals(ScenarioUnitType.Sleep)) {
                    if (logger.isDebugEnabled()) logger.debug("Take a Sleep during {} ms", scenario.sleepPeriod);
                    Strand.sleep(scenario.sleepPeriod);
                    continue;
                }
                if (scenario.type.equals(ScenarioUnitType.Print)) {
                    logger.info("[PRINT] {}", scenario.reservedField);
                    continue;
                }
                if (scenario.type.equals(ScenarioUnitType.ExtraFunctionCall)) {
                    if (ApiLoader.obj().executeExtraFunction(result, userId, scenario).equals(Boolean.FALSE)) {
                        //If status is in Loop, exit from current Loop
                        if (loopDepth >= 0) {
                            int currentLoopCount = loopCountList.get(loopDepth) - 1;
                            logger.warn("Stopped by API, currentLoopCount:{}, loopDepth:{}", currentLoopCount, loopDepth);
                            if (currentLoopCount >= 0) {

                                int tmpScenarioIdx = scenarioIdx + 1;

                                while (true) {
                                    ScenarioUnit tmpScenario = listScenario.get(tmpScenarioIdx);
                                    if (tmpScenario.loopType == LoopType.LoopEnd &&
                                            tmpScenario.loopDepth == loopDepth) {
                                        scenarioIdx = tmpScenarioIdx - 1;
                                        break;
                                    }
                                    tmpScenarioIdx++;
                                }
                            }
                            if (loopDepth > 0) {
                                logger.warn("End Loop by API(In-Loop level)");
                            } else {
                                logger.warn("End Loop by API(Top level)");
                            }
                        }
                    }
                    continue;
                }

                if (scenario.json.contains(UsingVariableDelimiter)) {
                    originalJson = scenario.json;
                    scenario.json = applyVariable(runtimeVar, scenario.json);
                } else {
                    originalJson = EmptyString;
                }


                for (IBaseCommunication communication : listCommunication) {
                    if (communication.isRegisteredScenarioType(scenario.type)) {  // Is it REST or Netty or JMX... ?

                        switch (scenario.communicationMethod) {                  // Is it Request or Response ?

                            case CommunicationMethod.Request:
                                requestTime = Instant.now();

                                communication.execute(scenario);
                                break;

                            case CommunicationMethod.Response:

                                Boolean isSucceed = communication.compareWithRealResponse(scenario);

                                if (isSucceed) {
                                    responseTime = Instant.now();
                                    result.listResponseTime.add(Duration.between(requestTime, responseTime));
                                    succeedCount++;
                                } else
                                    failureCount++;
                                break;

                            default:
                                logger.error("Receive invalid packet type : " + scenario.type);
                                break;
                        }
                    }
                }


                // If scenario use run-time variable, the Json will change,
                // So if the original json(with run-time variable) is converted,
                // set it to original JSon string
                if (!originalJson.equals(EmptyString)) {
                    scenario.json = originalJson;
                }
            }
            StatisticsResult statisticsResult = null;

            result.succeedCount += succeedCount;
            result.failureCount += failureCount;

            reportHandler.writeLogForFinalResult(
                    result.succeedCount,
                    result.failureCount);
            statisticsResult = reportHandler.writeLogForStatisticsResult(result.listResponseTime);

            result.statisticsResult = statisticsResult;

        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        return result;
    }

}

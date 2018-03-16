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

import com.nhnent.eat.common.Config.Config;
import com.nhnent.eat.entity.GeneratedPacketJson;
import com.nhnent.eat.entity.ScenarioExecutionResult;
import com.nhnent.eat.entity.ScenarioUnitType;
import com.nhnent.eat.entity.StatisticsResult;
import com.nhnent.eat.packets.StreamPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Duration;
import java.util.List;

import static com.nhnent.eat.Main.userInfo;
import static java.lang.Boolean.TRUE;

/**
 * Report Handler will write log for unit result and final result
 */
public class ReportHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Write log for test result of scenario unit
     *
     * @param pckName       Packet Name
     * @param expect        Expected Packet
     * @param real          Real(Received from server) Packet
     * @param result          Result(Succeed:TRUE or Failure:FALSE)
     */
    public final void writeLogForUnitResult(final String id, final String pckName, final String expect, final Object real,
                                            final Boolean result)
            throws Exception {

        if (Config.obj().getDisplay().isDisplayUnitTestResult()) {

            GeneratedPacketJson packetJson = StreamPacket.obj().packetToJson(id, expect, real);
            String expectJson = packetJson.expectJson;
            String realJson = packetJson.realJson;

            logger.info("<-------Matching result(" + pckName + ", [userId:" + userInfo.get() + "]" + ")------->");
            if (result == TRUE) {
                logger.info("<-------Result : Succeed!");
            } else {
                logger.error("<-------Result : Failure!");
            }
            logger.info("<-------expected:\n" + expectJson);
            logger.info("<-------real:\n" + realJson);

        }
    }

    /**
     * Write log for final test result
     *
     * @param cntOfSucceed Count of Succeed Test Case
     * @param cntOfFailure Count of Failure Test Case
     */
    public final void writeLogForFinalResult(final int cntOfSucceed, final int cntOfFailure) {

        if (!Config.obj().getDisplay().isDisplayFinalTestResult()) {
            return;
        }

        String finalMessage;
        finalMessage = "\n<-------Final matching result" + "[userId:" + userInfo.get() + "]" + "------->";
        finalMessage = finalMessage + "\n - Total test case(s) : " + (cntOfSucceed + cntOfFailure);
        finalMessage = finalMessage + "\n - Succeed : " + cntOfSucceed;
        finalMessage = finalMessage + "\n - Failure : " + cntOfFailure;

        if (cntOfFailure > 0) {
            logger.error(finalMessage);
        } else {
            logger.info(finalMessage);
        }
    }

    public final void writeFinalStatisticsResult(ScenarioExecutionResult[] results) {
        if (!Config.obj().getDisplay().isDisplayFinalStatisticResult()) {
            return;
        }
        NumberFormat formatter = new DecimalFormat("#0.00");
        Double mean, standardDeviation;
        Double median = 0.0, maxValue = 0.0, minValue = 1000000.0;
        Double sumOfMean = 0.0, sumOfStandardOfDeviation = 0.0;
        int countOfResult = 0;
        for (ScenarioExecutionResult result : results) {
            if (result.statisticsResult != null) {
                countOfResult++;
                sumOfMean += result.statisticsResult.mean;
                sumOfStandardOfDeviation += result.statisticsResult.standardDeviation;

                if(minValue > result.statisticsResult.minValue) {
                    minValue = result.statisticsResult.minValue;
                }
                if(maxValue < result.statisticsResult.maxValue) {
                    maxValue = result.statisticsResult.maxValue;
                }
            }
        }
        median = (maxValue + minValue) / 2;
        mean = sumOfMean / countOfResult;
        standardDeviation = sumOfStandardOfDeviation / countOfResult;

        logger.info("\n<======== FINAL STATISTICS RESULT (of response time)" + " result ========>");
        logger.info("(Mean) response time(ms) : " + formatter.format(mean));
        logger.info("(Standard Deviation) response time(ms) : " + formatter.format(standardDeviation));
        logger.info("(Median) response time(ms) : " + formatter.format(median));
        logger.info("(Max) response time(ms) : " + formatter.format(maxValue));
        logger.info("(Min) response time(ms) : " + formatter.format(minValue));
    }
    /**
     * Write log for statistics result of response time
     *
     * @param listResponseTime list of response time
     */
    public final StatisticsResult writeLogForStatisticsResult(List<Duration> listResponseTime) {
        if (!Config.obj().getDisplay().isDisplayStatisticResult()
                && !Config.obj().getDisplay().isDisplayFinalStatisticResult()) {
            return null;
        }

        Double mean;
        Double sum = 0.0;
        Double median;
        Double minValue = Double.MAX_VALUE;
        Double maxValue = Double.MIN_VALUE;
        NumberFormat formatter = new DecimalFormat("#0.00");

        for (Duration duration : listResponseTime) {
            Double durationInMS = duration.getNano() / 1000000.0;

            if (durationInMS > maxValue) maxValue = durationInMS;
            if (durationInMS < minValue) minValue = durationInMS;

            sum += durationInMS;
        }
        mean = sum / listResponseTime.size();
        median = (maxValue + minValue) / 2;

        Double diff;
        Double diffSum = 0.0;
        for (Duration duration : listResponseTime) {
            Double durationInMS = duration.getNano() / 1000000.0;
            diff = durationInMS - mean;
            diffSum += diff * diff;
        }
        Double standardDeviation = Math.sqrt(diffSum / (listResponseTime.size()));

        if(Config.obj().getDisplay().isDisplayStatisticResult()) {
            logger.info("\n<-------Statistics(of response time)" + "[userId:" + userInfo.get() + "]" + " result------->");
            logger.info("(Mean) response time(ms) : " + formatter.format(mean));
            logger.info("(Standard Deviation) response time(ms) : " + formatter.format(standardDeviation));
            logger.info("(Median) response time(ms) : " + formatter.format(median));
            logger.info("(Max) response time(ms) : " + formatter.format(maxValue));
            logger.info("(Min) response time(ms) : " + formatter.format(minValue));
        }

        StatisticsResult result = new StatisticsResult();
        result.mean = mean;
        result.median = median;
        result.standardDeviation = standardDeviation;
        result.minValue = minValue;
        result.maxValue = maxValue;

        return result;
    }

    /**
     * Display transferred packet
     *
     * @param type          Scenario unit type
     * @param name          Packet Name
     * @param json          Json String of Packet
     */
    public final void displayTransferredPacket(final String type,
                                               final String name,
                                               final String json) {
        if (Config.obj().getDisplay().isDisplayTransferredPacket()) {
            StringBuilder sb = new StringBuilder();
            sb.append("[userId:").append(userInfo.get()).append("]");
            switch (type) {
                case ScenarioUnitType.Request:
                    sb.append("[Request Packet]");
                    break;
                case ScenarioUnitType.Send:
                    sb.append("[Send Packet]");
                    break;
                default:
                    logger.error("Packet type is not valid");
                    return;
            }
            sb.append("[").append(name).append("]");
            if (Config.obj().getDisplay().isDisplayTransferredPacketJson()) {
                sb.append("\n").append(json);
            }
            logger.info(sb.toString());
        }
    }

    /**
     * Display transferred packet
     *
     * @param type                  Scenario unit type
     * @param name                  Packet Name
     * @param expectedPacketJson    Expected Packet Json
     * @param packet                Packet object
     */
    public final void displayReceivedPacket(final String type,
                                            final String expectedPacketJson,
                                            final String name,
                                            final Object packet) throws Exception {
        if (Config.obj().getDisplay().isDisplayTransferredPacket()) {
            StringBuilder sb = new StringBuilder();
            sb.append("[userId:").append(userInfo.get()).append("]");
            switch (type) {
                case ScenarioUnitType.Response:
                    sb.append("[Response Packet]");
                    break;
                case ScenarioUnitType.Receive:
                    sb.append("[Receive Packet]");
                    break;
                default:
                    logger.error("Packet type is not valid");
                    return;
            }
            sb.append("[").append(name).append("]");

            if (Config.obj().getDisplay().isDisplayTransferredPacketJson()) {
                String packetJson = StreamPacket.obj()
                        .packetToJson(userInfo.get(), expectedPacketJson, packet).realJson;

                sb.append("\n").append(packetJson);
            }
            logger.info(sb.toString());
        }
    }
}

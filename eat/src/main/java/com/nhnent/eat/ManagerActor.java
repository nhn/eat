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

import co.paralleluniverse.actors.Actor;
import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.BasicActor;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;
import com.nhnent.eat.common.Config.Config;
import com.nhnent.eat.communication.jmx.JMXClient;
import com.nhnent.eat.communication.netty.NettyClient;
import com.nhnent.eat.entity.MessageType;
import com.nhnent.eat.entity.Messages;
import com.nhnent.eat.entity.ScenarioExecutionResult;
import com.nhnent.eat.handler.ReportHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nhnent.eat.Main.transmitDataTotalCount;
import static com.nhnent.eat.common.CommonDefine.EmptyString;


class ActorExecutor implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        TesterActor tester = Actor.newActor(TesterActor.class);
        tester.spawn();
        ManagerActor.actorList.add(tester.ref());
        return 0;
    }
}

/**
 * Manager Actor
 * Based on configured testing count, it requests test to Tester Actor.
 */
public final class ManagerActor extends BasicActor<Void, Void> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final List<ActorRef> actorList = Collections.synchronizedList(new ArrayList<>());

    private String previousSnoString = EmptyString;

    void spawnActor() throws InterruptedException, SuspendExecution {

        int cntOfTest = Config.obj().getScenario().getPlayerCount();
        int realThreadCount = Config.obj().getCommon().getCountOfRealThread();
        ExecutorService executor = Executors.newFixedThreadPool(realThreadCount);

        ActorExecutor actorExecutor = new ActorExecutor();

        List<Future> futures = new ArrayList<>();

        for (int indexOfActor = 0; indexOfActor<cntOfTest;indexOfActor++) {
            futures.add(executor.submit(actorExecutor));
            Strand.sleep(10);
        }

        while(ManagerActor.actorList.size() < cntOfTest ||
                futures.size() < cntOfTest) {
            Strand.sleep(100);
        }
    }

    /**
     * doRun of Actor. It performs requests on Actor Mailbox.
     */
    @Override
    protected Void doRun() throws SuspendExecution {
        try {
            NettyClient.initEventLoopGroup();

            MDC.put("playerId", "Manager");
            MDC.put("strand", Strand.currentStrand().getName());

            //Read config, and Spawn TestActor according to config.
            int cntOfTest = Config.obj().getScenario().getPlayerCount();

            spawnActor();

            //Request test preparation to TestActors
            for (int idx = 0; idx < cntOfTest; idx++) {
                ActorRef tester = actorList.get(idx);
                tester.send(makeRequestTestPreparationMessage(idx));
                logger.debug("Request preparation to tester actor (" + tester.toString() + ")");
            }

            //Wait for preparation from all of TestActors
            int receivedPreparedCnt = 0;
            while(true) {
                Object o = receive();
                if (o instanceof Messages) {
                    Messages m = (Messages) o;
                    if (m.type == MessageType.TestPrepared) {
                        receivedPreparedCnt++;
                        logger.info("Receive message(Test Preparation is Finish), "
                                + receivedPreparedCnt + "/" + cntOfTest);
                        if (receivedPreparedCnt >= cntOfTest) {
                            break;
                        }
                    }
                }
            }

            Date testStartDateTime = new Date();

            //Request test to TestActors
            for (int idx = 0; idx < cntOfTest; idx++) {
                ActorRef tester = actorList.get(idx);

                tester.send(new Messages(this.ref, MessageType.TestStart));
                logger.info("Request preparation to tester actor (" + tester.toString() + ")");
                if (Config.obj().getScenario().getTestActorStartGap() != 0) {
                    Strand.sleep(Config.obj().getScenario().getTestActorStartGap());
                }
            }

            //Wait for test finishing from all of TestActors
            int receivedTestFinishCnt = 0;

            ScenarioExecutionResult[] results = new ScenarioExecutionResult[cntOfTest];
            for (; ; ) {
                Object o = receive();
                if (o instanceof Messages) {
                    Messages m = (Messages) o;
                    if (m.type == MessageType.TestFinished) {
                        results[receivedTestFinishCnt] = m.scenarioExecutionResult;
                        receivedTestFinishCnt++;
                        logger.info("[userId:{}] Receive message(Test is Finished), {}/{}", m.userId, receivedTestFinishCnt, cntOfTest);
                        if (receivedTestFinishCnt >= cntOfTest) {
                            break;
                        }
                    }
                }
            }

            JMXClient.obj().disconnect();

            Date testEndDateTime = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            logger.info(String.format("Testing period : %s ~ %s",
                    dateFormat.format(testStartDateTime),
                    dateFormat.format(testEndDateTime)));

            long diff = testEndDateTime.getTime() - testStartDateTime.getTime();
            logger.info("Elapsed time(sec) : " + (diff / 1000));
            logger.info("Total transferred packet : " + transmitDataTotalCount);
            double avgTransPacketCount = (double)transmitDataTotalCount / (diff / 1000.0);
            logger.info("Average Transferred packet for 1 sec: " +
                    String.format("%.2f", avgTransPacketCount));

            ReportHandler reportHandler = new ReportHandler();
            reportHandler.writeFinalStatisticsResult(results);

            NettyClient.shutdownEventLoopGroup();
        } catch (final Exception e) {
            logger.error("Exception is raised", e);
        }

        MDC.remove("playerId");
        MDC.remove("strand");
        MDC.remove("logfileName");

        return null;
    }

    /**
     * Make execute message of test preparation.
     *
     * @param idx index of test player
     * @return generated message
     */
    private Messages makeRequestTestPreparationMessage(final int idx) {

        String userId = EmptyString;
        int playerCount = Config.obj().getScenario().getUserId().length;

        if(idx < playerCount)
        {
            userId = Config.obj().getScenario().getUserId()[idx];
            previousSnoString = userId;
        }
        else if(idx >= playerCount)
        {
            String tempSno = previousSnoString;
            int endNumber;
            Pattern p = Pattern.compile("[\\D]*[\\d]+$");
            Matcher m = p.matcher(tempSno);

            Boolean isEndByNumber = false;

            while(m.find())
            {
                isEndByNumber = true;
                tempSno = m.group(0);
            }

            if(isEndByNumber)
            {
                String beforeSno = tempSno.replaceAll("[\\D]","");

                endNumber = Integer.parseInt(beforeSno);

                int endNumberLength = Integer.toString(endNumber).length();

                endNumber++;

                userId = previousSnoString.substring(0, previousSnoString.length() - endNumberLength) + Integer.toString(endNumber);

                previousSnoString = userId;
            }
            else
            {
                userId = Integer.toString(idx);
            }

        }


        //TODO : it should changed later
//        try {
//            previousSno = Long.parseLong(userId);
//        } catch (Exception e) {
//            //it means, the userId is not Numeric.
//            previousSno = playerCount;
//        }

        String scenarioFile;
        int scenarioFileCount = Config.obj().getScenario().getScenarioFile().length;

        int recursiveScenarioIdx = idx % scenarioFileCount;

        scenarioFile = Config.obj().getScenario().getScenarioFile()[recursiveScenarioIdx];

        return new Messages(this.ref, MessageType.PrepareTest, userId, scenarioFile, idx);
    }
}

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
import com.nhnent.eat.common.Config.Config;
import com.nhnent.eat.common.Config.PacketPackage;
import com.nhnent.eat.common.PacketClassPool;
import com.nhnent.eat.customScenario.ApiLoader;
import com.nhnent.eat.packets.StreamPacket;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public final class Main {

    public static final ThreadLocal<String> userInfo = new ThreadLocal<>();


    public static long lastTransmitTime = System.currentTimeMillis ();
    public static long transmitDataCountPerPeriod = 0;
    public static long transmitDataTotalCount = 0;

    /**
     * Main will spawn Manager Actor to have a testing
     * @param args main argument(but no need argument for it)
     * @throws ExecutionException ExecutionException for Quasar Actor
     * @throws InterruptedException InterruptedException for Quasar Actor
     */
    public static void main(final String[] args) throws ExecutionException, InterruptedException {

        final Logger logger = LoggerFactory.getLogger("EAT Main");

        //Call to initialize
        if(args.length != 0) {
            Config.setConfigRootPath(args[0]);
        }

        String logfileName;
        if(Config.obj().getCommon().isLoggingOnSameFile()) {
            logfileName = "Initialize";
        } else {
            Date now = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String format = formatter.format(now);

            logfileName = String.format("%s_Initialize", format);
        }

        MDC.put("logfileName", logfileName);

        StreamPacket.obj().loadClass(Config.obj().getPacket().getPluginPackage(),
                Config.obj().getPacket().getPluginClass());
        StreamPacket.obj().initSingletonInstance();

        try {

            for(PacketPackage packetPackage : Config.obj().getPacket().getPacketPackages())
            {
                PacketClassPool.obj().loadClassInfoFromJarFile(packetPackage.getPackageName(),
                        Paths.get(Config.obj().getPacket().getClassPackage()).toString());
            }


            //Load custom API
            if(Config.obj().getCustomScenarioAPI().isUse()) {
                String customApiJarFilePath = Config.obj().getCustomScenarioAPI().getJarFile();
                ApiLoader.obj().loadClass(customApiJarFilePath,
                        Config.obj().getCustomScenarioAPI().getApiClassName());
            }

            ManagerActor manager = Actor.newActor(ManagerActor.class);
            logger.info("Spawn manager actor.");
            manager.spawn();
            manager.join();
        } catch (Exception e) {
            logger.error("Exception is raised", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * private Main
     */
    private Main() {
    }
}

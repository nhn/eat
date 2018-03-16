package com.nhnent.eat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nhnent.eat.common.Config.Config;
import com.nhnent.eat.common.Config.KeyValueObject;
import org.junit.Test;

//import com.nhnent.ngt.common.protocol.Base;

/**
 * Created by NHNEnt on 2016-08-09.
 */
public class PacketLoaderTest {
    @Test
    public void JsonFormatTest() throws Exception {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();


        Config config = new Config();

        config.getDisplay().setDisplayTransferredPacket(false);
        config.getDisplay().setDisplayUnitTestResult(false);
        config.getDisplay().setDisplayFinalTestResult(false);



        String[] protoBufFiles = {"base.desc", "SampleGame.desc"};

        String[] protoPackage = {"com.nhnent.ngt.common.protocol",
                "com.nhnent.sampleGameServer.protocol"};

        KeyValueObject[] packages = new KeyValueObject[2];
        packages[0] = new KeyValueObject("NGT", protoPackage[0]);
        packages[1] = new KeyValueObject("SampleGame", protoPackage[1]);

        String[] protoPackageFile = {
                "C:\\WORK\\02.Base2016\\NGT_SRC\\server\\server\\out\\artifacts\\ngt_common_jar\\ngt-common.jar",
                "C:\\WORK\\02.Base2016\\NGT_SRC\\server\\server\\out\\artifacts\\ngt_sampleGameServer_jar\\ngt-sampleGameServer.jar"};

        KeyValueObject[] packetPackageFile = new KeyValueObject[2];
        packetPackageFile[0] = new KeyValueObject("com.nhnent.ngt.common.protocol", protoPackageFile[0]);
        packetPackageFile[1] = new KeyValueObject("com.nhnent.ngt.common.protocol", protoPackageFile[1]);

        config.getServer().setIp("10.77.95.231");
        config.getServer().setPort(12000);



        config.getScenario().setScenarioPath("C:\\WORK\\02.Base2016\\NGT_SRC\\server\\server\\ngt-eat\\src\\main\\resources");
        config.getScenario().setPlayerCount(1);
        String[] userId = {"100","101"};
        config.getScenario().setUserId(userId);
        String[] scenarioFile = {"game_test.scn"};
        config.getScenario().setScenarioFile(scenarioFile);

        String jsonString = gson.toJson(config);
        System.out.println(jsonString);
    }
}

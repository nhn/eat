package com.nhnent.eat.handler;

import org.junit.Test;

/**
 * Created by NHNEnt on 2016-12-21.
 */
public class ScenarioLoaderTest {
    @Test
    public void convertProtoVarNameToRealVarName() throws Exception {

        String json = "{\"retCode\":\"RET_OK\",\"channelType\":2,\"channelNo\":1,\"channelRoomInfos\":[],\"userInfos\":[{\"userId\":\"1001102\",\"ongameId\":\"1001102\",\"nickname\":\"1001102\",\"gameMoney\":\"1251320000\",\"level\":16.66,\"sex\":0,\"photoPath\":\"https://storage-dev.ongame.vn/content/image/avatar/default-male.png\",\"locationInfo\":{\"channelId\":\"TIENLEN:AMA-1\",\"channelType\":2,\"channelNo\":1,\"state\":2,\"roomNo\":0}},{\"userId\":\"1001120\",\"ongameId\":\"1001120\",\"nickname\":\"1001120\",\"gameMoney\":\"788560000\",\"level\":12.74,\"sex\":0,\"photoPath\":\"https://storage-dev.ongame.vn/content/image/avatar/default-male.png\",\"locationInfo\":{\"channelId\":\"TIENLEN:AMA-1\",\"channelType\":2,\"channelNo\":1,\"state\":2,\"roomNo\":0}}],\"seedMoneyList\":[500000,1000000,5000000],\"roomCreateLimitMoneyList\":[\"5000000\",\"10000000\",\"50000000\"]}";

        ScenarioLoader scenarioLoader = new ScenarioLoader();
        ScenarioUnitParser scenarioUnitParser = new ScenarioUnitParser();
        scenarioUnitParser.convertProtoVarNameToRealVarName(json);
    }

    @Test
    public void extractFunctionElement() throws Exception {
        ScenarioLoader scenarioLoader = new ScenarioLoader();
        ScenarioUnitParser scenarioUnitParser = new ScenarioUnitParser();
        String s = "#FUNCTION @@SeatNo=MSudda.getSeatNo(@@userId, @@subId)";
        scenarioUnitParser.extractExtraFunctionElement(s, null);
    }

}
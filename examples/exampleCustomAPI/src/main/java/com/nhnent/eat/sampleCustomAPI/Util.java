package com.nhnent.eat.sampleCustomAPI;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.nhnent.eat.common.JsonUtil;

public class Util {

    public static String extractUserNickName(String json) {
        /*
           JsonBody Example)

           {
             "resultUserInfo": {
               "userId": "test123",
               "userName": "test123_name"
             },
             "resultType": "SUCCESS"
           }

        */

        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(json);

        JsonElement resultUserInfo = JsonUtil.getValueOfVar(je, "resultUserInfo");
        JsonElement nickname = JsonUtil.getValueOfVar(resultUserInfo, "userName");

        return nickname.getAsString();
    }
}

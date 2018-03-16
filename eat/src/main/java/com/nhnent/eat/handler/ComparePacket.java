package com.nhnent.eat.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by NHNEnt on 2017-03-13.
 */

class JsonValue {
    public String groupId;
    public String variable;
    public String value;

    public boolean equals(JsonValue obj) {
        return this.variable.equals(obj.variable) && this.value.equals(obj.value);
    }
}

public class ComparePacket {

    public static ComparePacket obj = new ComparePacket();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static Boolean ComparePacket(String expectedPacketJson, String realPacketJson) {
        JsonParser jsonParser = new JsonParser();
        JsonElement expectedJe = jsonParser.parse(expectedPacketJson);
        JsonElement realJe = jsonParser.parse(realPacketJson);

        return obj.ComparePacket(expectedJe, realJe);
    }

    private Boolean ComparePacket(JsonElement expectedJe, JsonElement realJe) {

        List<JsonValue> expectedJsonValues = new ArrayList<>();
        getValueFromJE(expectedJsonValues, "", expectedJe, "");

//        Test Code ----------------
        if(logger.isDebugEnabled()) {
            logger.debug("<====begin : ComparePacket====>");
            logger.debug("Expected Value");
            for (JsonValue jsonValue : expectedJsonValues) {
                logger.debug("[" + jsonValue.groupId + "] " + jsonValue.variable + " : " + jsonValue.value);
            }
        }

        List<JsonValue> realJsonValues = new ArrayList<>();
        getValueFromJE(realJsonValues, "", realJe, "1");

//        Test Code ----------------
        if(logger.isDebugEnabled()) {
            logger.debug("Real Value");
            for (JsonValue jsonValue : realJsonValues) {
                logger.debug("[" + jsonValue.groupId + "] " + jsonValue.variable + " : " + jsonValue.value);
            }
            logger.debug("<====end : ComparePacket====>");
        }

        HashMap<String, List<JsonValue>> expectedGroup = jsonValueGrouping(expectedJsonValues);
        HashMap<String, List<JsonValue>> realGroup = jsonValueGrouping(realJsonValues);

        for(String group: expectedGroup.keySet()) {
            if(!findElement(expectedGroup.get(group), realGroup)) {
                return false;
            }
        }
        return true;
    }
    private HashMap<String, List<JsonValue>> jsonValueGrouping(List<JsonValue> jsonValues) {
        HashMap<String, List<JsonValue>> result = new HashMap<>();

        for(JsonValue jsonValue : jsonValues) {
            String group;
            if(jsonValue.groupId.length() != 0) {
                group = jsonValue.groupId.substring(0, jsonValue.groupId.length() - 1);
            } else {
                group = "";
            }
            if(!result.containsKey(group)) {
                List<JsonValue> jsonGroupValues = new ArrayList<>();
                jsonGroupValues.add(jsonValue);
                result.put(group, jsonGroupValues);
            } else {
                result.get(group).add(jsonValue);
            }
        }

        return result;
    }
    private Boolean findElement(List<JsonValue> expectedJsonValues, HashMap<String, List<JsonValue>> realGroup) {
        Boolean result;
        for (String realGroupName : realGroup.keySet()) {
            result = true;
            for (JsonValue expectedJsonValue : expectedJsonValues) {
                Optional<JsonValue> found = realGroup.get(realGroupName).stream()
                        .filter(obj -> obj.equals(expectedJsonValue))
                        .findAny();
                if(!found.isPresent()) {
                    result = false;
                    break;
                }
            }
            if(result) {
                return true;
            }
        }
        return false;
    }
    private void getValueFromJE(List<JsonValue> jsonValues, String path, JsonElement jsonElement, String groupId) {

        if (jsonElement.isJsonObject()) {
            JsonObject jo = jsonElement.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entrySet = jo.entrySet();
            int nextIndex = 1;
            String parentPath = path;
            for (Map.Entry<String, JsonElement> entry : entrySet) {
                if((!(path == null || path.equals("")))&&
                        !parentPath.equals("")) {
                    path = parentPath + "." + entry.getKey();
                } else {
                    path = entry.getKey();
                }
                getValueFromJE(jsonValues, path, entry.getValue(), groupId + nextIndex);
                nextIndex++;
            }
        } else if(jsonElement.isJsonArray()){
            JsonArray ja = jsonElement.getAsJsonArray();
            int nextIndex = 1;
            for(JsonElement je : ja) {
                getValueFromJE(jsonValues, path, je, groupId + (nextIndex));
                nextIndex++;
            }
        } else {
            while(groupId.endsWith("0")) {
                groupId = groupId.substring(0,groupId.length()-1);
            }
            JsonValue jsonValue = new JsonValue();
            jsonValue.groupId = groupId;
            jsonValue.variable = path;
            jsonValue.value = jsonElement.toString();
            jsonValues.add(jsonValue);
        }

    }
}

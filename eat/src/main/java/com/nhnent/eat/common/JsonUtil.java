package com.nhnent.eat.common;

import com.google.gson.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.nhnent.eat.common.CommonDefine.EmptyString;

/**
 * Utility for JSon
 */
public class JsonUtil {

    /**
     * Remove JsonElement which match with given Variable and Value
     * @param element JsonElement
     * @param var Variable
     * @param value Value of given Variable
     * @return Modified(Removed) JSonElement
     */
    public static JsonElement removeJsonElement(JsonElement element, String var, String value)  {
        JsonElement returnValue;
        if (element.isJsonObject()) {
            JsonObject jo = element.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entrySet = jo.entrySet();
            for (Map.Entry<String, JsonElement> entry : entrySet) {
                if(entry.getKey().equals(var) && entry.getValue().toString().equals(value)) {
                    entrySet.remove(entry);
                    return jo;
                }
                returnValue = removeJsonElement(entry.getValue(), var, value);
                if(returnValue != null) {
                    return returnValue;
                }
            }
        } else if(element.isJsonArray()){
            JsonArray ja = element.getAsJsonArray();
            for(JsonElement je : ja) {
                returnValue = removeJsonElement(je, var, value);
                if(returnValue != null) {
                    return returnValue;
                }
            }
        }
        return null;
    }

    /**
     * Change JsonElement value
     * @param element JSonElement
     * @param var Variable
     * @param originalValue Original Value
     * @param newValue New Value
     * @return Modified(updated) JSonElement
     */
    public static  JsonElement changeJsonElement(JsonElement element, String var, String originalValue, JsonElement newValue) {
        JsonElement returnValue;
        if (element.isJsonObject()) {
            JsonObject jo = element.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entrySet = jo.entrySet();
            for (Map.Entry<String, JsonElement> entry : entrySet) {
                if(entry.getKey().equals(var) && entry.getValue().toString().equals(originalValue)) {
                    entry.setValue(newValue);
                    return jo;
                }
                returnValue = changeJsonElement(entry.getValue(), var, originalValue, newValue);
                if(returnValue != null) {
                    return returnValue;
                }
            }
        } else if(element.isJsonArray()){
            JsonArray ja = element.getAsJsonArray();
            for(JsonElement je : ja) {
                returnValue = changeJsonElement(je, var, originalValue, newValue);
                if(returnValue != null) {
                    return returnValue;
                }
            }
        }
        return null;
    }

    /**
     * Get json value of given json variable name from json
     * @param json json string
     * @param var variable name
     * @return json value
     */
    public static String getValueOfVar(String json, String var) {

        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(json);

        JsonElement jeValue = getValueOfVar(je, var);

        if(jeValue != null) {
            return jeValue.toString();
        } else {
            return EmptyString;
        }
    }

    /**
     * Find child JSonElement from given JSonElement with ID(Variable Name)
     * @param element JSonElement
     * @param var Variable Name
     * @return Found child JSonElement
     */
    public static JsonElement getValueOfVar(JsonElement element, String var) {
        JsonElement returnValue;
        if (element.isJsonObject()) {
            JsonObject jo = element.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entrySet = jo.entrySet();
            for (Map.Entry<String, JsonElement> entry : entrySet) {
                if(entry.getKey().equals(var)) {
                    return entry.getValue();
                }
                returnValue = getValueOfVar(entry.getValue(), var);
                if(returnValue != null) {
                    return returnValue;
                }
            }
        } else if(element.isJsonArray()){
            JsonArray ja = element.getAsJsonArray();
            for(JsonElement je : ja) {
                returnValue = getValueOfVar(je, var);
                if(returnValue != null) {
                    return returnValue;
                }
            }
        }

        return null;
    }

    /**
     * Make beauty json for given json
     * @param json json string
     * @return json string with beauty json format
     */
    public static String makeBeautyJson(String json) {
        final Logger logger = LoggerFactory.getLogger("com.nhnent.eat.common.Util");
        Gson beautyGson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = null;
        try {
            je = jp.parse(json);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return beautyGson.toJson(je);
    }

    /**
     * Make simple json(1 line) for given json
     * @param json json string
     * @return json string with simple json format
     */
    public static String makeSimpleJson(String json) {
        final Logger logger = LoggerFactory.getLogger("com.nhnent.eat.common.Util");
        Gson simpleGson = new GsonBuilder().create();
        JsonParser jp = new JsonParser();
        JsonElement je = null;
        try {
            je = jp.parse(json);
        } catch (Exception e) {
            //logger.error("json=>\n{}", json);
            ExceptionUtils.getStackTrace(e);
        }
        return simpleGson.toJson(je);
    }

    /**
     * Check given json is Array type or not
     * @param json JSon String
     * @return True or False
     */
    public static Boolean isJsonArray(String json) {

        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(json);

        if (je.isJsonArray()) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * Generate string(which is json element) list from given Json string
     * @param json Json String
     * @return String list of Json element
     */
    public static List<String> getListFromJsonArray(String json) {
        List<String> returnVal = new LinkedList<>();

        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(json);

        if(je.isJsonArray()) {
            JsonArray ja = je.getAsJsonArray();
            for(JsonElement jsonElement : ja)
                returnVal.add(jsonElement.toString());
        } else {
            returnVal.add(je.toString());
        }
        return returnVal;
    }

}

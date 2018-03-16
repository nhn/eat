package com.nhnent.eat.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by NHNEnt on 2017-03-08.
 */

class SortJsonArray {
    private static final Field elements;
    static {
        try {
            elements = JsonArray.class.getDeclaredField("elements");
            elements.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("JsonArray internal API has changed!");
        }
    }

    public static JsonElement sortJsonElement(JsonElement element) {
        JsonElement returnValue;
        if (element.isJsonObject()) {
            JsonObject jo = element.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entrySet = jo.entrySet();
            for (Map.Entry<String, JsonElement> entry : entrySet) {
                returnValue = sortJsonElement(entry.getValue());
                if(returnValue != null) {
                    return returnValue;
                }
            }
        } else if(element.isJsonArray()){
            JsonArray ja = element.getAsJsonArray();

            sortJsonArray(ja);


            for(JsonElement je : ja) {
                returnValue = sortJsonElement(je);
                if(returnValue != null) {
                    return returnValue;
                }
            }
        }
        return null;
    }

    public static List<JsonElement> getElementList(JsonArray arr) {
        try {
            return (List<JsonElement>) elements.get(arr);
        } catch (IllegalArgumentException e) {
            // this shouldn't happen in practice
        } catch (IllegalAccessException e) {
            // this shouldn't happen in practice
        }
        throw new RuntimeException("JsonArray internal API has changed!");
    }

    public static void sortJsonArray(JsonArray myJsonArray) {
        getElementList(myJsonArray).sort(new
                jsonStringComparator());

        for (JsonElement e: myJsonArray) {
            System.out.println("sortJsonArray=>" + e.toString());
        }
    }
    public static class jsonStringComparator implements
            Comparator<JsonElement> {
        @Override
        public int compare(JsonElement o1, JsonElement o2) {
            if (o1 == null) {
                return o2 == null ? 0 : -1;
            } else if (o2 == null) {
                return 1;
            } else if (o1.isJsonNull()) {
                return o2.isJsonNull() ? 0 : -1;
            } else if (o2.isJsonNull()) {
                return 1;
            } else {
                return o1.toString().compareTo(o2.toString());
            }
        }
    }
}


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

package com.nhnent.eat.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nhnent.eat.TesterActor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nhnent.eat.common.CommonDefine.*;
import static com.nhnent.eat.common.JsonUtil.getValueOfVar;
import static java.lang.System.out;

/**
 * Utility class
 */
public final class Util {

    /**
     * It prints method of class using Java Reflection
     * @param c class which wants print method.
     */
    public static void printMethods(final Class c) {
        out.format("Methods from %s%n", c);
        Method[] meths = c.getDeclaredMethods();
        if (meths.length != 0) {
            for (Method m : meths) {
                out.format("  Method:  %s%n", m.toGenericString());
            }
        } else {
            out.format("  -- no methods --%n");
        }
        out.format("%n");
    }


    /**
     * Extract Json Variable Name from string
     * ex.) from {"var1": 10}, it will extract variable name(var1)
     * @param s Json string
     * @return Extracted variable name
     */
    public static String extractJsonVariableNameFromString(final String s) {
        if (s == null) {
            return EmptyString;
        }

        JsonParser jp = new JsonParser();
        JsonElement je;
        JsonObject jo;
        JsonArray ja;
        try {
            je = jp.parse(s);
            if(je.isJsonArray()) {
                ja = je.getAsJsonArray();
                jo = ja.get(0).getAsJsonObject();
            } else {
                jo = je.getAsJsonObject();
            }
        } catch (Exception e) {
            return "ERROR";
        }
        Set<Map.Entry<String, JsonElement>> entrySet = jo.entrySet();

        for (Map.Entry<String, JsonElement> entry : entrySet) {
            return entry.getKey();
        }

        return je.toString();
    }

    /**
     * Extract Json Variable Value from string
     * ex.) from {"var1": 10}, it will extract variable value(10)
     * @param s Json string
     * @return Extracted variable value
     */
    public static String extractJsonVariableValueFromString(final String s) {
        String variableValue = EmptyString;
        int posStart = s.indexOf(":") + 1;
        int posEnd = s.lastIndexOf("}");
        if (posStart >= 0 && posEnd >= 0) {
            variableValue = s.substring(posStart, posEnd);
        }
        return variableValue;
    }


    /**
     * Extract BytePacket(ex, payload or packetbytes) Value from string
     * ex.)
     *  pckJson:{
     *      "msgType": "EnterLobbyToS",
     *      "payload": {
     *          "[com.nhnent.msg.protocol]EnterLobbyToS": {
     *          }
     *      }
     *  }
     *
     *  From the above Json string example, this function will take the following string
     *  "[com.nhnent.msg.protocol]EnterLobbyToS": { }
     *
     * @param s Json string
     * @return Extracted variable value
     */
    public static String extractBytePacketValueFromString(final String packetName, final String s) {

        if (packetName == null) {
            return EmptyString;
        }

        JsonParser jp = new JsonParser();
        JsonElement je = null;
        try {
            je = jp.parse(s);
        }catch (Exception e) {
            Logger logger = LoggerFactory.getLogger("com.nhnent.eat.common.Util");
            logger.error("packetName:{}\n, json:\n{}\n", packetName, s);
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        JsonElement foundElement= getValueOfVar(je, packetName);
        if(foundElement != null) {
            return foundElement.toString();
        } else {
            return EmptyString;
        }
    }

    /**
     * Extract variable name and value name from line of JSon String
     * @param s line of JSon String
     */
    public static void extractGlobalVariable(final String s) {
        String globalVarString = s.replace(DeclareVariableDelimiter, EmptyString).trim();
        String[] globalVar = globalVarString.split("=");
        String varName = globalVar[0].trim();
        String valName = EmptyString;
        if(globalVar.length == 2) {
            valName = globalVar[1].trim();
        }
        TesterActor.globalVariable.get().put(varName, valName);
    }

    /**
     * Apply variable to JSon string
     * @param s line of JSon string
     * @return applied JSon string
     * @throws UnsupportedEncodingException handle exception
     */
    public static String applyVariable(HashMap<String, String> variableSet, final String s)
            throws UnsupportedEncodingException {
        String target = s + " ";

        int posStart = target.indexOf(UsingVariableDelimiter);

        //Even many variables are exist on 1 line. (ex, @@var1:blabla:@@var2)
        //The following matcher will extract target variable properly.
        String tmpS = target.substring(posStart + 2);
        Matcher matcher = Pattern.compile("[^A-Za-z0-9]").matcher(tmpS);
        int posEnd = matcher.find() ? matcher.start() + posStart + 2 : -1;

        String varString = target.substring(posStart, posEnd).trim();
        varString = varString.replace(",", "");
        varString = varString.replace("\"", "");
        String valString = variableSet.getOrDefault(varString.replace(UsingVariableDelimiter, EmptyString),"N/A");

        String convertedString;
        if(valString.equals("N/A")) {
            //if cannot find proper variable, don't change the target.
            convertedString = target;
        } else {
            convertedString = target.replace(varString, valString).trim();
        }
        return convertedString ;
    }

    /**
     * Load class from JAR file
     * @param pathToJar path of JAR file
     * @param apiClassName class name to load
     */
    public static Class loadClassFromJarFile(final String pathToJar, final String apiClassName) {
        Logger logger = LoggerFactory.getLogger("com.nhnent.eat.common.Util.loadClassFromJarFile");
        JarFile jarFile;
        try {
            jarFile = new JarFile(pathToJar);
            Enumeration<JarEntry> e = jarFile.entries();

            URL[] urls = new URL[] {new URL("jar:file:" + pathToJar + "!/")};
            URLClassLoader cl = URLClassLoader.newInstance(urls);

            while (e.hasMoreElements()) {
                JarEntry je = e.nextElement();
                 if (je.isDirectory() || !je.getName().endsWith(".class")) {
                    continue;
                }
                // -6 because of .class
                final int lengthOfExtension = 6;
                String className = je.getName().substring(0, je.getName().length() - lengthOfExtension);
                className = className.replace('/', '.');
                if (!className.equals(apiClassName)) {
                    continue;
                }

                logger.debug("className is :{}", className);

                jarFile.close();

                return cl.loadClass(className);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    private Util() { }
}

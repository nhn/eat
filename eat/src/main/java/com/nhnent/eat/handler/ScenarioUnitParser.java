package com.nhnent.eat.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nhnent.eat.entity.CommunicationMethod;
import com.nhnent.eat.entity.LoopType;
import com.nhnent.eat.entity.ScenarioUnit;
import com.nhnent.eat.entity.ScenarioUnitType;
import com.nhnent.eat.packets.StreamPacket;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import static com.nhnent.eat.common.CommonDefine.*;

/**
 *  Create a scenarioUnit by parsing packetHeader and json data.
 */
public class ScenarioUnitParser {


    private int loopDepth = 0;

    public ScenarioUnit parse(String packetHeader, String json) {

        ScenarioUnit newScenarioUnit = new ScenarioUnit();

        String pckJson = json;

        if (packetHeader.startsWith(RequestRESTString)) {
            newScenarioUnit.type = ScenarioUnitType.RequestREST;
        } else if (packetHeader.startsWith(ResponseRESTString)) {
            newScenarioUnit.type = ScenarioUnitType.ResponseREST;
        } else if (packetHeader.startsWith(RequestJMXString)) {
            String[] scenarioDefine;

            scenarioDefine = packetHeader.split(PckNameDelimiter);
            String mBeanName = scenarioDefine[1];
            String functionName = scenarioDefine[2];

            newScenarioUnit.name = mBeanName;
            newScenarioUnit.dest = functionName;
            newScenarioUnit.type = ScenarioUnitType.SetJMXQaCommand;

        } else if (packetHeader.startsWith(BeginQaCommandDelimiter)) {
            newScenarioUnit.type = ScenarioUnitType.SetQaCommand;
        } else if (packetHeader.startsWith(ResponseJMXString)) {
            newScenarioUnit.type = ScenarioUnitType.GetJMXQaCommand;
        } else if (packetHeader.startsWith(BeginSetCardDeckDelimiter)) {
            newScenarioUnit.type = ScenarioUnitType.SetCardDeck;
        } else if (packetHeader.startsWith(LoopStartDelimiter)) {
            int loopCount = Integer.parseInt(packetHeader.replace(LoopStartDelimiter, EmptyString).trim());

            newScenarioUnit.loopType = LoopType.LoopStart;
            newScenarioUnit.loopCount = loopCount;
            newScenarioUnit.loopDepth = loopDepth;
            loopDepth++;

        } else if (packetHeader.startsWith(LoopEndDelimiter)) {
            loopDepth--;

            newScenarioUnit.loopType = LoopType.LoopEnd;
            newScenarioUnit.loopDepth = loopDepth;

        } else if (packetHeader.startsWith(SleepDelimiter)) {
            int sleepPeriod = Integer.parseInt(packetHeader.replace(SleepDelimiter, EmptyString).trim());

            newScenarioUnit.type = ScenarioUnitType.Sleep;
            newScenarioUnit.sleepPeriod = sleepPeriod;

        } else if (packetHeader.startsWith(PrintDelimiter)) {
            String printString = packetHeader.replace(PrintDelimiter, EmptyString).trim();

            newScenarioUnit.type = ScenarioUnitType.Print;
            newScenarioUnit.reservedField = printString;

        } else if (packetHeader.startsWith(ExtraFunction)) {
            newScenarioUnit.type = ScenarioUnitType.ExtraFunctionCall;
            extractExtraFunctionElement(packetHeader, newScenarioUnit);

        } else if (packetHeader.startsWith(DisconnectionDelimiter)) {
            newScenarioUnit.type = ScenarioUnitType.Disconnect;

        } else if (packetHeader.startsWith(ConnectionDelimiter)) {
            newScenarioUnit.type = ScenarioUnitType.Connect;

        } else {

            String pckType;

            newScenarioUnit = StreamPacket.obj().decodeScenarioHeader(packetHeader, newScenarioUnit);

            pckType = newScenarioUnit.type;

            if (pckType.equals("END")) {
                return null;
            }

            switch (pckType) {
                case RequestPacketString:
                    newScenarioUnit.type = ScenarioUnitType.Request;
                    break;
                case SendPacketString:
                    newScenarioUnit.type = ScenarioUnitType.Send;
                    break;
                case ResponsePacketString:
                    newScenarioUnit.type = ScenarioUnitType.Response;
                    break;
                case ReceivePacketString:
                    newScenarioUnit.type = ScenarioUnitType.Receive;
                    break;
                case ReceiveUntilPacketString:
                    newScenarioUnit.type = ScenarioUnitType.ReceiveUntil;
                    break;
                default:
                    newScenarioUnit.type = ScenarioUnitType.None;
            }

            // If the current packet is a Protocol Buffer, have to remove "_" for real variable name.
            pckJson = convertProtoVarNameToRealVarName(pckJson);

        }

        // Set communicationMethod
        switch (newScenarioUnit.type) {
            case ScenarioUnitType.Request:
            case ScenarioUnitType.RequestREST:
            case ScenarioUnitType.SetJMXQaCommand:
            case ScenarioUnitType.RequestRestCall:
            case ScenarioUnitType.Send:
                newScenarioUnit.communicationMethod = CommunicationMethod.Request;
                break;

            case ScenarioUnitType.Response:
            case ScenarioUnitType.ResponseREST:
            case ScenarioUnitType.GetJMXQaCommand:
            case ScenarioUnitType.Receive:
                newScenarioUnit.communicationMethod = CommunicationMethod.Response;
                break;

            default:
                newScenarioUnit.communicationMethod = CommunicationMethod.None;
                break;
        }


        newScenarioUnit.json = pckJson;

        return newScenarioUnit;
    }

    /**
     * Remove "_" in Key variables and convert to real variable name
     * when precompile protocol buffers and scenario files.
     * example) "seed_money" to "seedMoney"
     *
     * @param s json string input
     * @return converted json string
     */
    public String convertProtoVarNameToRealVarName(final String s) {

        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(s);

        if (je.isJsonObject()) {
            JsonObject jo = je.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entrySet = jo.entrySet();

            for (Map.Entry<String, JsonElement> entry : entrySet) {
                if (entry.getKey().contains("_")) {
                    String oldVarName = entry.getKey();
                    String changedVarName = entry.getKey();
                    int posConnector = changedVarName.indexOf("_");
                    String asIs = changedVarName.substring(posConnector, posConnector + 2);
                    String toBe = asIs.toUpperCase().replace("_", "");
                    changedVarName = changedVarName.replace(asIs, toBe);

                    String temp = jo.toString();
                    temp = temp.replace(oldVarName, changedVarName);

                    return convertProtoVarNameToRealVarName(temp);
                }

                if (entry.getValue().isJsonObject()) {
                    String changedValueString =
                            convertProtoVarNameToRealVarName(entry.getValue().toString());

                    String temp = changedValueString;
                    temp = temp.replace(entry.getValue().toString(), changedValueString);

                    entry.setValue(jp.parse(temp));
                    convertProtoVarNameToRealVarName(temp);
                }
            }

            return jo.toString();
        } else if (je.isJsonArray()) {
            JsonArray ja = je.getAsJsonArray();

            for (JsonElement jsonElement : ja) {
                String oldJsonElement = jsonElement.toString();
                String changedJsonElement = convertProtoVarNameToRealVarName(jsonElement.toString());

                ja.toString().replace(oldJsonElement, changedJsonElement);
            }

            return ja.toString();
        } else {
            return null;
        }
    }

    /**
     * Extract elements of extra-function from String, and then make Scenario Unit for extra-function call.
     *
     * @param s                 String line of JSon String
     * @param callExtraFunction handle exception.
     */
    public void extractExtraFunctionElement(final String s, ScenarioUnit callExtraFunction) {
        //#FUNCTION @@SeatNo=MSudda.getSeatNo(@@userId, @@subId)
        String returnVariableName = EmptyString;
        String statement = s.replace(ExtraFunction, "");

        //@@SeatNo=MSudda.getSeatNo(@@userId, @@subId)
        if (s.contains("=")) {
            returnVariableName = statement.split("=")[0].replace(UsingVariableDelimiter, EmptyString).trim();
            callExtraFunction.returnVariableName = returnVariableName;
            statement = statement.split("=")[1];
        }
        String functionName = statement.split("\\(")[0].trim();
        String[] parameters = statement.substring(
                statement.indexOf("(") + 1,
                statement.indexOf(")")
        ).split(",");
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = parameters[i].trim();
        }

        callExtraFunction.extraFunctionParameter = new LinkedList(Arrays.asList(parameters));
        callExtraFunction.extraFunctionName = functionName;
    }
}

package com.nhnent.eat.plugin.protobuf;

import com.google.gson.*;
import com.google.protobuf.*;
import com.google.protobuf.util.JsonFormat;
import com.nhnent.eat.common.Config.Config;
import com.nhnent.eat.common.JsonUtil;
import com.nhnent.eat.common.Util;
import com.nhnent.eat.handler.PacketJsonHandler;
import com.nhnent.eat.plugin.protobuf.config.ProtobufConfig;
import com.nhnent.eat.plugin.protobuf.unifiedMessage.OrderedMessage;
import com.nhnent.eat.plugin.protobuf.unifiedMessage.UnifiedMessage;
import com.nhnent.eat.plugin.protobuf.unifiedMessage.UnitMessage;
import com.sun.javafx.binding.StringFormatter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.nhnent.eat.common.JsonUtil.*;
import static com.nhnent.eat.plugin.protobuf.ProtobufUtil.*;

/**
 * This class manage pool of description which comes from Protocol buffer description file
 */
public class ProtobufDescPool {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private JsonFormat.TypeRegistry registry = JsonFormat.TypeRegistry.getEmptyTypeRegistry();
    private List<String> descriptors = new ArrayList<>();
    private static ProtobufDescPool instance = null;

    /**
     * Get singleton instance
     *
     * @return singleton instance
     */
    public static ProtobufDescPool obj() {
        if (instance == null) {
            instance = new ProtobufDescPool();
        }
        return instance;
    }

    /**
     * Get TypeRegistry of Protocol Buffer
     * @return TypeRegistry
     */
    private JsonFormat.TypeRegistry getRegistry() {
        return registry;
    }

    /**
     * Get descriptor with give partial name of descriptor
     * @param partialName partial name of descriptor
     * @return Protocol Buffer Descriptor
     */
    private Descriptors.Descriptor getDescriptor(String partialName) {
        Optional<String> packetFullName = descriptors.stream()
                .filter(s->s.contains(partialName))
                .findAny();
        return packetFullName.map(s -> registry.find(s)).orElse(null);
    }

    /**
     * Get all of descriptors
     * @return list of descriptor
     */
    public List<String> getDescriptors() {
        return descriptors;
    }

    /**
     * load protocol buffer description from description file
     *
     * @param descFiles protocol buffer description file(s)
     * @throws IOException                               Handle description file not found exception
     * @throws Descriptors.DescriptorValidationException Handle exception which comes from Descriptor
     */
    public void loadProtobufDesc(String[] descFiles) throws IOException, Descriptors.DescriptorValidationException {

        HashMap<String, Descriptors.FileDescriptor> fileDescs = new HashMap<>();
        JsonFormat.TypeRegistry.Builder registryBuilder = JsonFormat.TypeRegistry.newBuilder();

        for (String descFile : descFiles) {

            //Dependency is represented in config like the following
            //"MSuddaGameProto.desc-DMSuddaCommonProto.desc"
            //So we can split it with '-D'
            String[] depDescFiles = descFile.split("-D");
            Descriptors.FileDescriptor[] depFileDesc = null;
            if (depDescFiles.length > 1) {
                depFileDesc = new Descriptors.FileDescriptor[depDescFiles.length - 1];

                descFile = depDescFiles[0];

                for (int i = 0; i < depDescFiles.length - 1; i++) {
                    depFileDesc[i] = fileDescs.get(depDescFiles[i + 1]);
                }
            }

            String descFilePath = null;

            String protoFilesDirectory = Paths.get(Config.obj().getCommon().getRootDirectory(), "proto").toString();
            descFilePath = Paths.get(protoFilesDirectory, descFile).toString();

            logger.info("Load desc. file : {}", descFilePath);
            InputStream in = new FileInputStream(descFilePath);
            DescriptorProtos.FileDescriptorSet descriptorSet =
                    DescriptorProtos.FileDescriptorSet.parseFrom(in);

            for (DescriptorProtos.FileDescriptorProto fdp : descriptorSet.getFileList()) {
                if (depFileDesc == null) {
                    depFileDesc = new Descriptors.FileDescriptor[] {};
                }
                Descriptors.FileDescriptor fd = Descriptors.FileDescriptor.buildFrom(fdp, depFileDesc);

                fileDescs.put(descFile, fd);
                for (Descriptors.Descriptor desc : fd.getMessageTypes()) {
                    logger.debug("load from desc.  => {}", desc.getName());
                    descriptors.add(desc.getFullName());
                    registryBuilder.add(desc);
                }
            }
        }
        registry = registryBuilder.build();
    }
    /**
     * Check give json contains BytePacket, and if exist return it
     * It will find BytePacket type from front.
     * ex) In the following case, it will return `BytePacket1`
     * {
     *  "BytePacket1" : 1,
     *  "BytePacket2" : 2
     * }
     * @param json JSon string
     * @return found BytePacket name or null(if not exist)
     */
    private String containsBytePacket(String json) {
        Integer position = Integer.MAX_VALUE;
        String foundBytePacketName = null;
        for (String bytePacketName : Config.obj().getPacket().getBytePacketTypes()) {
            bytePacketName = "\"" + bytePacketName + "\"";
            int foundPosition = json.indexOf(bytePacketName);
            if(foundPosition != -1 && foundPosition < position ) {
                position = foundPosition;
                foundBytePacketName = bytePacketName.replace("\"", "");
            }
        }
        if(foundBytePacketName != null) {
            return foundBytePacketName;
        }
        return null;
    }

    /**
     * Replace BytePacket to Base64
     * @param json JSon String
     * @return Converted Base64
     * @throws InvalidProtocolBufferException handle protocol buffer exception
     */
    private String replaceBytePacketToBase64(String json) throws InvalidProtocolBufferException {
        json = makeSimpleJson(json);
        String markedJson = json; //It will marked which already converted bytePacket.

        while(true) {

            String includedBytePacketName = containsBytePacket(markedJson);

            if (includedBytePacketName != null) {

                String bytePackets = Util.extractBytePacketValueFromString(includedBytePacketName, markedJson);

                Boolean isJsonArray = JsonUtil.isJsonArray(bytePackets);
                List<String> listPacket = JsonUtil.getListFromJsonArray(bytePackets);

                for(String bytePacket : listPacket) {

                    String bytePacketVar = Util.extractJsonVariableNameFromString(bytePacket).trim();

                    //Try to find `[com.nhnent.msg.protocol]LoginResult`
                    if (bytePacketVar.equals("ERROR")) {
                        break;
                    }

                    //Convert from `[com.nhnent.msg.protocol]LoginResult` to `com.nhnent.msg.protocol.LoginResult`
                    bytePacketVar = bytePacketVar.replace("[", "").replace("]", ".");

                    String bytePacketVal = Util.extractJsonVariableValueFromString(bytePacket);

                    if (containsBytePacket(bytePacketVal) != null) {
                        bytePacketVal = replaceBytePacketToBase64(bytePacketVal);
                    }
                    Message msg;
                    try {
                        Descriptors.Descriptor desc = registry.find(bytePacketVar);
                        DynamicMessage.Builder dmBuilder = DynamicMessage.newBuilder(desc);
                        msg = dmBuilder.build();
                    } catch (Exception e) {
                        logger.error("Failed to find [{}]", bytePacketVar);
                        throw e;
                    }

                    Message.Builder builder = msg.toBuilder();
                    JsonFormat.parser().merge(bytePacketVal, builder);

                    String strVal = Base64.encodeBase64String(builder.build().toByteArray());

                    if(isJsonArray) {
                        strVal = "{" + strVal + "}";
                    }

                    int startPos = markedJson.indexOf("\"" + includedBytePacketName + "\"");
                    int endPos = markedJson.indexOf(bytePacketVal, startPos) + bytePacketVal.length();
                    endPos = markedJson.indexOf("}", endPos) + 1;
                    String replaceString = markedJson.substring(startPos, endPos);

                    json = json.replace(bytePacket, "\"" + strVal + "\"");
                    markedJson = markedJson.replace(replaceString, "");
                }
            }
            else {
                break;
            }
        }

        return json;
    }

    /**
     * Convert JSON string to Message(.builder)
     *
     * @param fullMessageName Full message name(such as 'com.nhnent.com.ngt.common.protocol.LoginReq)
     * @param json            JSon string
     * @return Generated Message(.builder)
     * @throws InvalidProtocolBufferException handle protocol buffer exception
     */
    public Message.Builder JsonToMessage(String fullMessageName, String json) throws InvalidProtocolBufferException {

        json = replaceBytePacketToBase64(json);

        logger.debug(StringFormatter.format("(JsonToMessage) msgName:%s , \n%s", fullMessageName, json).get());

        Message msg;
        try {
            Descriptors.Descriptor desc = getRegistry().find(fullMessageName);
            DynamicMessage.Builder dmBuilder = DynamicMessage.newBuilder(desc);
            msg = dmBuilder.build();
        } catch (Exception e) {
            logger.error("Failed to find [{}]", fullMessageName);
            throw e;
        }

        Message.Builder builder = msg.toBuilder();
        try {
            JsonFormat.parser().merge(json, builder);
        } catch (Exception e) {
            logger.error("{}\nDetailed Info. \n fullMessageName==>{}\njson\n{}\n<==", ExceptionUtils.getStackTrace(e), fullMessageName, json);
        }
        return builder;
    }

    /**
     * Convert Expected Json to formal Json
     * From)
         "result_code": 0,
         "payload": {
             "[MSG]JoinRoomResult": {
                "retCode": "RET_OK"
             }
         }
     * To)
         "result_code": 0,
         "payload": {
            "retCode": "RET_OK"
         }
     * @param expectedJson expected Json String
     * @return Formal type Json
     */
    public String convertExpectedJsonToFormalJson(final String expectedJson ) {
        String convertedJson = JsonUtil.makeSimpleJson(expectedJson);
        String includedBytePacketName;
        int foundCount = 0;

        //markedPacketName is exist for replace packet key
        //Ex.) <"payload", "#1#"> <"packetBytes", "#2#">
        //When replace packet value, ("payload" or "packetBytes") will replace to ("#1#" or "#2#")
        //And then, after replace string, it will recover to original bytePacket, such as "payload" or "packetBytes"
        HashMap<String,String> markedPacketName = new HashMap<>();

        int markIndex = 0;
        for (String bytePacketName : Config.obj().getPacket().getBytePacketTypes()) {
            markedPacketName.put(bytePacketName, "#" + markIndex + "#");
            markIndex++;
        }
        while(true) {
            for (String bytePacketName : Config.obj().getPacket().getBytePacketTypes()) {
                if(convertedJson.contains(bytePacketName)) {
                    includedBytePacketName = bytePacketName;
                    String bytePacketValue = Util
                            .extractBytePacketValueFromString(includedBytePacketName, convertedJson);
                    String newPacketValue = Util.extractJsonVariableValueFromString(bytePacketValue);
                    convertedJson = convertedJson
                            .replaceFirst(Pattern.quote(bytePacketValue), newPacketValue);

                    //replace packet value, ("payload" or "packetBytes") will replace to ("#1#" or "#2#")
                    convertedJson = convertedJson
                            .replaceFirst(Pattern.quote(bytePacketName), markedPacketName.get(bytePacketName));

                    foundCount++;
                }
            }
            if(foundCount == 0) {
                break;
            }
            foundCount = 0;
        }

        //recover original bytePacket, such as ("payload" or "packetBytes") from ("#1#" or "#2#")
        for(String packetName : markedPacketName.keySet()) {
            convertedJson = convertedJson.replace(markedPacketName.get(packetName), packetName);
        }

        return convertedJson;
    }

    /**
     * Parse payload which is in packet, and converted JSon
     * @param expectedJson Expected JSon
     * @param realJson Received JSon
     * @return Converted Json
     * @throws Exception Raise Exception regarding parsing error or and so on.
     */
    public String parseReceivedBytePacket(String expectedJson, String realJson) throws Exception {
        String convertedJson = realJson;
        String markedJson = realJson;
        while(true) {
            String includedBytePacketName = null;
            for (String bytePacketName : Config.obj().getPacket().getBytePacketTypes()) {
                if (markedJson.contains(bytePacketName)) {
                    includedBytePacketName = bytePacketName;
                    break;
                }
            }

            if (includedBytePacketName == null) {
                return makeBeautyJson(convertedJson);
            } else {

                String expectedBytePacket = Util.extractBytePacketValueFromString(includedBytePacketName, expectedJson);
                String receivedBytePacket = Util.extractBytePacketValueFromString(includedBytePacketName, markedJson);
                logger.debug("extracted payload:\n{}", expectedBytePacket);

                String expectedBytePacketVar = Util.extractJsonVariableNameFromString(expectedBytePacket).trim();

                //Try to find `[com.nhnent.msg.protocol]LoginResult`
                if (expectedBytePacketVar.equals("ERROR")) {
                    break;
                }

                if(includedBytePacketName.equals("payload")) {
                    //Convert from `[com.nhnent.msg.protocol]LoginResult` to `com.nhnent.msg.protocol.LoginResult`
                    expectedBytePacketVar = expectedBytePacketVar.replace("[", "").replace("]", ".");

                    String decodeVal = decodeBase64ToByteString(expectedBytePacketVar, receivedBytePacket);
                    decodeVal = PacketJsonHandler.removeRedundant(decodeVal);
                    decodeVal = makeBeautyJson(decodeVal.replace("\n", ""));

                    JsonParser decodeJP = new JsonParser();
                    JsonElement decodeJE;
                    decodeJE = decodeJP.parse(decodeVal);

                    JsonParser jp = new JsonParser();
                    JsonElement je;

                    je = jp.parse(markedJson);
                    removeJsonElement(je, includedBytePacketName, receivedBytePacket);
                    markedJson = je.toString();

                    je = jp.parse(convertedJson);
                    changeJsonElement(je, includedBytePacketName, receivedBytePacket, decodeJE);
                    convertedJson = je.toString();
                } else {
                    //In case of `unitMessage`
                    String packetName;
                    String bytePacket;
                    Integer priority;

                    Gson gson = new Gson();
                    UnifiedMessage unifiedMessage = gson.fromJson(realJson, UnifiedMessage.class);

                    JsonParser jp = new JsonParser();

                    JsonArray jaUnifiedMessage = new JsonArray();

                    if(unifiedMessage.getUnitMessage() != null) {
                        JsonArray jaUnitMessage = new JsonArray();
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.add("unitMessage", jaUnitMessage);
                        for (UnitMessage unitMessage : unifiedMessage.getUnitMessage()) {
                            packetName = unitMessage.getPacketName();
                            bytePacket = unitMessage.getPacketBytes();

                            try {
                                String decodeVal = decodeBase64ToByteString(packetName, bytePacket);

                                JsonElement jeUnitMessageContents = jp.parse(decodeVal);

                                JsonObject joUnitMessage = new JsonObject();

                                JsonElement jePacketName = jp.parse(packetName);
                                joUnitMessage.add("packetName", jePacketName);
                                joUnitMessage.add("packetBytes", jeUnitMessageContents);

                                jaUnitMessage.add(joUnitMessage);
                            } catch (Exception e) {
                                logger.error("Failed to decode packet[{}]", packetName);
                                throw e;
                            }
                        }
                        jaUnifiedMessage.add(jsonObject);
                    }
                    if(unifiedMessage.getOrderedMessage() != null) {
                        JsonArray jaOrderedMessage = new JsonArray();
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.add("orderedMessage", jaOrderedMessage);
                        for (OrderedMessage orderedMessage : unifiedMessage.getOrderedMessage()) {
                            packetName = orderedMessage.getPacketName();
                            priority = orderedMessage.getPriority();
                            bytePacket = orderedMessage.getPacketBytes();

                            String decodeVal = decodeBase64ToByteString(packetName, bytePacket);

                            JsonElement jeUnitMessageContents = jp.parse(decodeVal);

                            JsonObject joOrderedMessage = new JsonObject();

                            JsonElement jePacketName = jp.parse(packetName);
                            JsonElement jePriority = jp.parse(priority.toString());

                            joOrderedMessage.add("priority", jePriority);
                            joOrderedMessage.add("packetName", jePacketName);
                            joOrderedMessage.add("packetBytes", jeUnitMessageContents);

                            jaOrderedMessage.add(joOrderedMessage);
                        }
                        jaUnifiedMessage.add(jsonObject);
                    }

                    Gson beautyGson = new GsonBuilder().setPrettyPrinting().create();
                    convertedJson = beautyGson.toJson(jaUnifiedMessage);
                    break;
                }
            }
        }
        convertedJson = makeBeautyJson(convertedJson);
        return convertedJson;
    }

    /**
     * Decode Base64 to ByteString
     * @param packetName Packet name
     * @param base64String Value in Base64
     * @return ByteString
     * @throws InvalidProtocolBufferException handle protocol buffer exception
     */
    private String decodeBase64ToByteString(String packetName, String base64String)
            throws InvalidProtocolBufferException {
        Descriptors.Descriptor desc = getDescriptor(packetName);
        DynamicMessage.Builder dmBuilder = DynamicMessage.newBuilder(desc);
        Message msg = dmBuilder.build();
        Message.Builder builder = msg.toBuilder();

        if(base64String.equals("\"\""))
            base64String = "";

        byte[] tmpByte = Base64.decodeBase64(base64String);
        String decodeVal;
        if (tmpByte == null) {
            decodeVal = "";
        } else {
            ByteString byteString = ByteString.copyFrom(tmpByte);
            try {
                builder.mergeFrom(byteString);
            } catch (Exception e) {
                logger.error("packetName: [{}] Size of byte :[{}]\ncontents:[{}]\n {}",
                        packetName,
                        tmpByte.length,
                        tmpByte,
                        ExceptionUtils.getStackTrace(e));
            }


            decodeVal = JsonFormat.printer().includingDefaultValueFields().print(builder);
        }
        return decodeVal;
    }


}

package com.nhnent.eat.plugin.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;

class ProtobufUtil {

    /**
     * Convert from protocol buffer message to Json
     * @param message protocol buffer message
     * @return Json String
     * @throws InvalidProtocolBufferException handle protocol buffer exception
     */
    public static String messageToSimpleJson(MessageOrBuilder message) throws InvalidProtocolBufferException {
        return JsonFormat.printer().print(message);
    }

    /**
     * Convert message(MessageOrBuilder) to JSon string
     *
     * @param message Message(MessageOrBuilder)
     * @return Generated JSon
     * @throws InvalidProtocolBufferException handle protocol buffer exception
     */
    public static String messageToJson(MessageOrBuilder message) throws InvalidProtocolBufferException {
        return JsonFormat.printer().includingDefaultValueFields().print(message);
    }

    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {

        return OS.contains("win");
    }

    public static boolean isMac() {

        return OS.contains("mac");
    }

    public static boolean isUnix() {

        return OS.contains("nix") || OS.contains("nux") || OS.contains("aix");
    }

    public static boolean isSolaris() {

        return OS.contains("sunos");
    }
}

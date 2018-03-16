package com.nhnent.eat.plugin.protobuf.config;

public class ProtoBufInfo {

    private String key;
    private String originProtoDirPath;
    private String[] protobufFiles;

    public String getKey() {
        return key;
    }

    public String[] getProtoBufFiles() {
        return protobufFiles;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setProtoBufFiles(String[] protoBufFiles) {
        this.protobufFiles = protoBufFiles;
    }

    public String getOriginProtoDirPath() {
        return originProtoDirPath;
    }

    public void setOriginProtoDirPath(String originProtoDirPath) {
        this.originProtoDirPath = originProtoDirPath;
    }
}

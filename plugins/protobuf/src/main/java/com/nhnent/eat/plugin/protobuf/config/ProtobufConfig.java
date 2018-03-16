package com.nhnent.eat.plugin.protobuf.config;

import com.nhnent.eat.common.Config.Config;

public class ProtobufConfig extends Config {

    private static ProtobufConfig instance;

    static {
        instance = Config.obj(ProtobufConfig.class, instance);
    }

    private Protobuf protobuf = new Protobuf();


    public static ProtobufConfig obj() { return instance; }

    /**
     * Get Protocol Buffer group config item
     *
     * @return Protocol Buffer group config item
     */
    public Protobuf getProtobuf() {
        return protobuf;
    }

    /**
     * Set Protocol Buffer group config item
     *
     * @param protobuf Protocol Buffer group config item
     */
    public void setProtobuf(Protobuf protobuf) {
        this.protobuf = protobuf;
    }



}

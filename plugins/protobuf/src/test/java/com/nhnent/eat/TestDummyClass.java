package com.nhnent.eat;

import com.google.protobuf.ByteString;

/**
 * Created by NHNEnt on 2016-08-11.
 */
public class TestDummyClass {

    private ByteString buff;

    public TestDummyClass(){

        byte[] dummy = new byte[3];
        dummy[0]=100;
        dummy[1]=101;
        dummy[2]=102;
        buff = ByteString.copyFrom(dummy);
    }

    public ByteString getBuff() {
        return buff;
    }
    public void setBuff(ByteString val) {
        buff = val;
    }
}


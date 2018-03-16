package com.nhnent.protobufPacket;

import java.io.IOException;

public class Main {

    public static void main(String args[]) throws IOException, InterruptedException, ClassNotFoundException {

        //Run Server with Thread.
        Runnable r = new Server("127.0.0.1", 11200);
        Thread t = new Thread(r);
        t.start();


//        TestClient client = new TestClient();
//        client.startClient();
    }
}

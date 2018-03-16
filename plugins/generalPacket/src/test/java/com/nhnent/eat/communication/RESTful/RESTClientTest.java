package com.nhnent.eat.communication.RESTful;

import org.junit.Test;

public class RESTClientTest {
    @Test
    public void GET() throws Exception {

        RESTClient RESTCommunication = new RESTClient();

        String testResult = RESTCommunication.post("http://httpbin.org/post", "{\"test\" : 4, \"test2\" : \"4\"}");
        System.out.println(testResult);
    }

}
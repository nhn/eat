package com.nhnent.eat.communication;

import com.nhnent.eat.communication.jmx.JMXClient;
import org.junit.Test;

/**
 * Created by NHNEnt on 2017-03-20.
 */
public class JMXClientTest {
    @Test
    public void connect() throws Exception {
        JMXClient jmxClient = new JMXClient();
        jmxClient.disconnect();
    }

}
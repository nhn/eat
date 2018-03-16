package com.nhnent.eat.communication.jmx;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class CommonJMXClientTest {
    @Test
    public void excuteFunction() throws Exception {

        List<String> temp = new LinkedList<>();

        temp.add("ads");
        temp.add("fggss");
        temp.add("azzxc");

        CommonJMXClient.obj().executeFunction("test", "testfunc", temp);

    }

}
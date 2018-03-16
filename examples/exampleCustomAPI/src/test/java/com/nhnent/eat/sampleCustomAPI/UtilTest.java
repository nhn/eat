package com.nhnent.eat.sampleCustomAPI;

import org.junit.Test;

import static org.junit.Assert.*;

public class UtilTest {

    @Test
    public void extractUserNicknameTest()
    {
        String input = "{\n" +
                "             \"resultUserInfo\": {\n" +
                "               \"userId\": \"test123\",\n" +
                "               \"userName\": \"test123_name\"\n" +
                "             },\n" +
                "             \"resultType\": \"SUCCESS\"\n" +
                "           }";



        String userNickname = Util.extractUserNickName(input);

        System.out.print(userNickname);
    }
}
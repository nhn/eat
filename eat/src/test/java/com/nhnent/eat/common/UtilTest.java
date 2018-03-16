package com.nhnent.eat.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nhnent.eat.handler.ComparePacket;
import org.junit.Test;

import java.util.Locale;

import static com.nhnent.eat.common.JsonUtil.makeSimpleJson;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by NHNEnt on 2017-03-05.
 */
public class UtilTest {
    @Test
    public void extractBytePacketValueFromString() throws Exception {

        String s =
                "{\n" +
                        "    \"unitMessage\": [\n" +
                        "        {\n" +
                        "            \"packetBytes\": {\n" +
                        "                    \"[MSG]UserInfoReply\": {\n" +
                        "                    }\n" +
                        "                }\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}";

        s= makeSimpleJson(s);
        String returned = Util.extractBytePacketValueFromString("packetBytes", s);

        System.out.println(returned);
    }

    @Test
    public void test() throws Exception {
        String s =
                "{\n" +
                        "  \"id\": \"\",\n" +
                        "  \"payload\": \"\",\n" +
                        "  \"channelId\": \"\"\n" +
                        "}";

        s= makeSimpleJson(s);
        String returned = Util.extractBytePacketValueFromString("payload",s);

        System.out.println(returned);
    }

    @Test
    public void test2() throws Exception {
        String s =
                "{\n" +
                        "  \"payload\": \"CAI=\"\n" +
                        "}";

        String returned = Util.extractBytePacketValueFromString("payload",s);

        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(s);
        JsonObject jo = je.getAsJsonObject();
        System.out.println(jo.get("payload"));

    }

    @Test
    public void test3() throws Exception {
        String s =
                "{\"[com.nhnent.msg.protocol]LoginResult\":{\"retCode\":\"CREATE\"}}";

        String returned = Util.extractJsonVariableNameFromString(s);
        System.out.println(returned);
    }


    @Test
    public void test4() throws Exception {
        String s =
                "{\n" +
                        "                \"[RPS]RPSGamePayloadForRoomOption\": {\n" +
                        "                    \"option\": \"@@joinRoomOption\"\n" +
                        "                }\n" +
                        "            }";
        String returned = Util.extractBytePacketValueFromString("data", s);
        System.out.println(returned);
    }

    @Test
    public void test6() throws Exception {
        String a =
                "{\n" +
                        "  \"unitMessage\": [\n" +
                        "    {\n" +
                        "      \"arr1\": [\n" +
                        "        {\n" +
                        "          \"ele1\": 1\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"ele2\": 2\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"packetBytes\": 2\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"packetBytes\": {\n" +
                        "        \"EventInfoReply\": \"\"\n" +
                        "      }\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";

        String b =
                "{\n" +
                        "  \"unitMessage\": [\n" +
                        "    {\n" +
                        "      \"packetBytes\": {\n" +
                        "        \"EventInfoReply\": \"\"\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"arr1\": [\n" +
                        "        {\n" +
                        "          \"ele2\": 2\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"ele1\": 1\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"packetBytes\": 2\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";

        ComparePacket comparePacket = new ComparePacket();
        comparePacket.ComparePacket(a, b);
    }

    @Test
    public void testReflectionEquals() {
        //reflectionEquals
        class TestClass {
            private int a;
            private int b;

            public void setValueTypeA() {
                a= 1;
                b= 3;
            }
            public void setValueTypeB() {
                a = 1;
                b = 2;
            }
        }

        TestClass c1 = new TestClass();
        c1.setValueTypeA();
        TestClass c2 = new TestClass();
        c2.setValueTypeB();

        Boolean result = reflectionEquals(c1, c2, false);
        System.out.println(result);

        result = reflectionEquals(c1, c2, true);
        System.out.println(result);

        result = reflectionEquals(c1, c2, "b");
        System.out.println(result);

        result = reflectionEquals(c1, c2, "a");
        System.out.println(result);
    }
    @Test
    public void testJsonElementSort() throws Exception {
        String a =
                "{\n" +
                        "  \"unitMessage\": [\n" +
                        "    {\n" +
                        "      \"arr1\": [\n" +
                        "        {\n" +
                        "          \"ele1\": 1\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"ele2\": 2\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"packetBytes\": 2\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"packetBytes\": {\n" +
                        "        \"EventInfoReply\": \"\"\n" +
                        "      }\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";

        String b =
                "{\n" +
                        "  \"unitMessage\": [\n" +
                        "    {\n" +
                        "      \"packetBytes\": {\n" +
                        "        \"EventInfoReply\": \"\"\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"arr1\": [\n" +
                        "        {\n" +
                        "          \"ele2\": 2\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"ele1\": 1\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"packetBytes\": 2\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";

        JsonParser parser = new JsonParser();
        JsonElement jeA;
        a = makeSimpleJson(a);
        System.out.println(a);
        jeA = parser.parse(a);

        JsonElement jeB;
        b = makeSimpleJson(b);
        System.out.println(b);
        jeB = parser.parse(b);

        SortJsonArray.sortJsonElement(jeA);
        SortJsonArray.sortJsonElement(jeB);

        System.out.println(jeA.equals(jeB));

        JsonElement o1 = parser.parse("{a : {a : 2}, b : 2}");
        JsonElement o2 = parser.parse("{b : 2, a : {a : 2}}");

        System.out.println(o1.equals(o2));
        assertEquals(jeA, jeB);
    }

    @Test
    public void testJsonElementCompare1() throws Exception {
        String a =
                "{\n" +
                        "  \"unitMessage\": [\n" +
                        "    {\n" +
                        "      \"arr1\": [\n" +
                        "        {\n" +
                        "          \"ele\": 1\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"ele\": 5\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"packetBytes\": \"6\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"item\": \"item1\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"item\": \"item1\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"packetBytes\": {\n" +
                        "        \"EventInfoReply\": \"\"\n" +
                        "      }\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";

        String b =
                "{\n" +
                        "  \"unitMessage\": [\n" +
                        "    {\n" +
                        "      \"arr1\": [\n" +
                        "        {\n" +
                        "          \"ele\": 1\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"ele\": 2\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"ele\": 3\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"packetBytes\": \"6\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"arr1\": [\n" +
                        "        {\n" +
                        "          \"ele\": 1\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"ele\": 5\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"packetBytes\": \"6\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"item\": \"item1\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"item\": \"item2\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"packetBytes\": {\n" +
                        "        \"EventInfoReply\": \"\"\n" +
                        "      }\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";

        System.out.println("Final Result=>" + ComparePacket.ComparePacket(a, b));
    }
    @Test
    public void testJsonElementCompare2() throws Exception {
        String a =
                "{\n" +
                        "  \"payload\": {\n" +
                        "    \"retCode\": \"CREATE\"\n" +
                        "  }\n" +
                        "}";

        String b =
                "{\n" +
                        "  \"payload\": {\n" +
                        "    \"retCode\": \"CREATE\"\n" +
                        "  }\n" +
                        "}";

        System.out.println("Final Result=>" + ComparePacket.ComparePacket(a, b));
    }
    @Test
    public void testLocale() throws Exception {
        Locale loc = Locale.getDefault() ;
        System.out.println( "default locale: " + loc.toString() ) ;
    }
    @Test
    public void testJsonElementCompare3() throws Exception {
        String a =
                "{\n" +
                        "  \"unitMessage\": [\n" +
                        "    {\n" +
                        "      \"packetBytes\": {\n" +
                        "        \"buyProductResultInfo\": {\n" +
                        "          \"res\": \"BUY_SUCCESS\"\n" +
                        "        }\n" +
                        "      }\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";

        String b =
                "{\n" +
                        "  \"unitMessage\": [\n" +
                        "    {\n" +
                        "      \"packetName\": \"BuyProductResultInfoReply\",\n" +
                        "      \"packetBytes\": {\n" +
                        "        \"buyProductResultInfo\": {\n" +
                        "          \"res\": \"BUY_SUCCESS\"\n" +
                        "        }\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"packetName\": \"UserInfoReply\",\n" +
                        "      \"packetBytes\": {\n" +
                        "        \"buyProductResultInfo\": {\n" +
                        "          \"res\": \"NONE\"\n" +
                        "        }\n" +
                        "      }\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";

        System.out.println("Final Result=>" + ComparePacket.ComparePacket(a, b));
    }


    @Test
    public void testJsonElementCompare4() throws Exception {
        String a =
                "{\"payload\":{\"retCode\":\"CREATE\",\"reason\":\"\"}}";
        String b =
                "{\"id\":\"\",\"resultCode\":0,\"payload\":{\"retCode\":\"CREATE\",\"reason\":\"\"},\"isRelogined\":false,\"isRoomRejoined\":false,\"rejoinedRoomId\":\"\",\"roomRejoinPayload\":\"\"}";
        System.out.println("Final Result=>" + ComparePacket.ComparePacket(a, b));
    }
}




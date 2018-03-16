/*
* Copyright 2016 NHN Entertainment Corp.
*
* NHN Entertainment Corp. licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.nhnent.eat.handler;

import com.nhnent.eat.common.Config.Config;
import com.nhnent.eat.common.Util;
import com.nhnent.eat.entity.GeneratedPacketJson;
import com.nhnent.eat.packets.StreamPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handle Json of Packet
 */
public class PacketJsonHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public final Boolean simpleMatch(String expect, final Object real) {
        return real.toString().contains(expect);
    }

    /**
     * Compare given packets and return result
     * @param expect Expected Packet(JSon)
     * @param real Real Packet(Message of protobuf)
     * @return Result of comparison
     * @throws Exception Raise Exception
     */
    public final Boolean matchPacket(String id, final String expect, final Object real)
            throws Exception {
        GeneratedPacketJson packetJson = StreamPacket.obj().packetToJson(id, expect, real);
        logger.debug("==> expectJson\n{}", packetJson.expectJson);
        logger.debug("==> realJson\n{}", packetJson.realJson);
        return ComparePacket.ComparePacket(packetJson.expectJson, packetJson.realJson);
    }

    /**
     * Remove redundant string
     *
     * @param s Original string
     * @return String which is removed Redundant char
     */
    public static String removeRedundant(final String s) {
        String rtn = s;

        rtn = rtn.replace("\r", "");
        rtn = rtn.replace("\n", "");
        rtn = rtn.replace("\t", "");
        rtn = rtn.replace(" ", "");
        rtn = rtn.replace(",}", "}");

        String includedBytePacketName = null;
        for(String bytePacketName : Config.obj().getPacket().getBytePacketTypes()) {
            if(s.contains(bytePacketName)) {
                includedBytePacketName = bytePacketName;
                break;
            }
        }

        if (includedBytePacketName != null) {

            String bytePacketValue = Util.extractBytePacketValueFromString(includedBytePacketName, rtn);
            String variableName = Util.extractJsonVariableNameFromString(rtn);

            if (variableName.startsWith("[")) {
                String replacedValue = Util.extractJsonVariableValueFromString(bytePacketValue);
                rtn = rtn.replace(bytePacketValue, replacedValue);
            }
        }

        return rtn;
    }
}


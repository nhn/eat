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

package com.nhnent.eat.entity;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.Strand;
import com.nhnent.eat.common.Config.Config;
import com.nhnent.eat.communication.netty.PacketListener;
import com.nhnent.eat.packets.StreamPacket;
import javafx.util.Pair;
import org.jboss.netty.handler.timeout.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * This class resolve problems which are merged or separated packet.
 */
public class PacketReceiver implements PacketListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BlockingQueue<Pair<String, byte[]>> packetQueue = new LinkedBlockingDeque<>();
    private Date lastPacketReceivedTime = new Date();

    private final Object monitor = new Object();
    private Strand receiveStrand = null;
    private final String userId;

    public PacketReceiver(String userId) {
        this.userId = userId;
        receiveStrand = Strand.currentStrand();
    }

    @Override
    public void receivePacket(byte[] readBuf) {
        try {
            StreamPacket.obj().receivePacket(userId, packetQueue, readBuf);
            logger.debug("size of packetQueue ==>{}", packetQueue.size());
            if (!packetQueue.isEmpty() && receiveStrand != null) {
                //logger.warn("receiveStrand {}", receiveStrand.getName());
                Strand.unpark(receiveStrand, monitor);
            }
        } catch (Exception e) {
            logger.error("Exception is raised", e);
        }
    }

    @Override
    public void exceptionCaught(String exceptionMessage) {
        MDC.put("strand", Strand.currentStrand().getName());
        logger.warn("exceptionCaught[{}] and unpark[{}]", exceptionMessage, receiveStrand);
        Strand.unpark(receiveStrand, monitor);
    }

    public final Pair<String, byte[]> readPacket() throws SuspendExecution, InterruptedException {
        if(packetQueue.isEmpty()) {
            Strand.park(monitor);
        }

        Date now = new Date();
        //When strand is unparked with empty queue, it caused by receiving timeout
        if(packetQueue.isEmpty()) {

            //Sometimes, it happens, even within Timeout period, packetQueue is empty.
            //So, let's check timeout period.
            long elapsedTime = (now.getTime() - lastPacketReceivedTime.getTime()) / 1000 % 60;
            long configTimeout = Config.obj().getCommon().getReceiveTimeoutSec();
            if( elapsedTime >= configTimeout ) {
                throw new TimeoutException();
            } else {
                return readPacket();
            }
        }
        lastPacketReceivedTime = new Date();
        return packetQueue.take();
    }
}

package com.nhnent.eat.communication.jmx;

import co.paralleluniverse.fibers.SuspendExecution;

import java.util.concurrent.ExecutionException;

/**
 * QA Test API
 */
public interface QaTestAPIMBean {
    /**
     * Send QA room command
     *
     * @param command  command
     * @param jsonData data of command(json format)
     *                 Sample of JSon
     *                 {
     *                 "cardsOfPlayer": [{
     *                 "seatNo": 1,
     *                 "cards": [{
     *                 "card": [3, 4]
     *                 }]
     *                 }, {
     *                 "seatNo": 2,
     *                 "cards": [{
     *                 "card": [1, 2]
     *                 }]
     *                 }]
     *                 }
     * @throws InterruptedException Throw Exception
     * @throws SuspendExecution     Throw Exception
     * @throws ExecutionException   Throw Exception
     */
    void sendRoomCommand(String command, String jsonData)
            throws InterruptedException, SuspendExecution, ExecutionException;

    void sendLobbyCommand(String command, String jsonData)
            throws InterruptedException, SuspendExecution, ExecutionException;
}
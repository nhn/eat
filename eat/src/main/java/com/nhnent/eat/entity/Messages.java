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

import co.paralleluniverse.actors.ActorRef;

import java.io.Serializable;


/**
 * Message object between Actors.
 */
public final class Messages implements Serializable {

    //public static final int TestCount = 1;
    //public static final Messages StartTest = new Messages(1, "");
    //public static final Messages StopTest = new Messages(10000, "");

    private static final long serialVersionUID = 0L;

    /**
     * Actor which transfer message to Actor
     */
    public ActorRef sender;
    /**
     * Message type
     */
    public MessageType type;
    /**
     * Unique id of player
     */
    public String userId;
    /**
     * Scenario file name(except path, it just contains file name)
     */
    public String scenarioFile;

    /**
     * Sequential index no of Actor. It is different with ActorRef, it is used for connection
     */
    public int actorIndex;

    public ScenarioExecutionResult scenarioExecutionResult;

    /**
     * Default Constructor(For serialization)
     */
    public Messages() { }

    /**
     * Constructor of message
     * @param sender Message sender
     * @param type Message type
     */
    public Messages(final ActorRef sender, final MessageType type) {
        this.sender = sender;
        this.type = type;
    }

    /**
     * Constructor of message
     * @param sender Message sender
     * @param type Message type
     * @param userId Player's unique id(userId)
     * @param scenarioFile Scenario file name(except path, it just contains file name)
     */
    public Messages(final ActorRef sender, final MessageType type, final String userId,
                    final String scenarioFile, final int actorIndex) {
        this.sender = sender;
        this.type = type;
        this.userId = userId;
        this.scenarioFile = scenarioFile;
        this.actorIndex = actorIndex;
    }

    /**
     * Convert message to string(with sender, userId and message type)
     * @return Converted message string
     */
    @Override
    public String toString() {
        return "Messages(" + sender + "," + userId + "," + type + ")";
    }

    /**
     * Compare message with given message object
     * @param o Message object
     * @return Result of compare
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Messages messages = (Messages) o;

        return userId.equals(messages.userId);
    }

    /**
     * Generate hashCode
     * @return HashCode of this message
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        return prime * sender.hashCode() + type.getValue();
    }
}

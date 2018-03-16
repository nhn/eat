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

/**
 * Packet Type which represents Type of Scenario Packet.
 * 'Request' means execute packet to Server
 * 'Response' means receive response packet from server
 * 'Send' means send packet server to Server without expectation value
 * 'Receive' means receive packet from Serve, it will not evaluation for testing.
 *           it just wait and receive packet to process scenario.
 */
public final class ScenarioUnitType {
    /**
     * Request Packet
     */
    public static final String Request = "Request";

    /**
     * Send Packet
     */
    public static final String Send = "Send";

    /**
     * Expected Response packet
     */
    public static final String Response = "Response";

    /**
     * Receive packet
     */
    public static final String Receive = "Receive";

    /**
     * Ignore packet until receive given packet
     */
    public static final String ReceiveUntil = "ReceiveUntil";

    /**
     * Scenario unit for Sleep
     */
    public static final String Sleep = "Sleep";

    /**
     * Print given string as Logger.info
     */
    public static final String Print = "Print";

    /**
     * Disconnect with Server
     */
    public static final String Disconnect = "Disconnect";

    /**
     * Connect to Server
     */
    public static final String Connect = "Connect";

    /**
     * Extra function call
     */
    public static final String ExtraFunctionCall = "ExtraFunctionCall";

    /**
     * Set Card Deck
     */
    public static final String SetCardDeck = "SetCardDeck";

    /**
     * Set QA Command
     */
    public static final String SetQaCommand = "SetQaCommand";

    /**
     * Request rest api call (old version)
     * Set QA Command
     */
    public static final String SetJMXQaCommand = "SetJMXQaCommand";

    public static final String GetJMXQaCommand = "GetJMXQaCommand";

    /**
     * Request rest api call
     */
    public static final String RequestRestCall = "RequestRestCall";

    /**
     * Request RESTful call (fixed version)
     */
    public static final String RequestREST = "RequestREST";

    /**
     * Response RESTful call (fixed version)
     */
    public static final String ResponseREST = "ResponseREST";

    /**
     * None
     */
    public static final String None = "None";
}

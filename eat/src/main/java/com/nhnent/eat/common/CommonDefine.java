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

package com.nhnent.eat.common;

/**
 * Common definition
 */
public final class CommonDefine {

    /**
     * Constructor
     */
    private CommonDefine() { }

    /**
     * Const String of empty string
     */
    public static final String EmptyString = "";

    //File
    /**
     * File extension of Scenario file
     */
    public static final String ScenarioFileExtension = ".scn";
    /**
     * File extension of pre-compiled scenario file
     */
    public static final String PrecompiledScenarioFileExtension = ".compiled";
    /**
     * Delimiter for include other scenario file
     */
    public static final String IncludeDelimiter = "include";

    /**
     * Delimiter for begin set card deck
     */
    public static final String BeginSetCardDeckDelimiter = "SET_CARD_DECK_BEGIN";

    /**
     * Delimiter for begin QA command
     */
    public static String BeginQaCommandDelimiter = "QA_COMMAND_BEGIN";


    //Random
    /**
     * Delimiter for start of static random variable
     * When scenario is compiled, the random value will defined.
     */
    public static final String StaticRandomVariableStart = "@@STATIC_RANDOM:";

    /**
     * Delimiter for end of random variable
     */
    public static final String RandomVariableEnd = "@@";

    /**
     * Delimiter for start of static random variable
     * When scenario running, the random value will defined.
     */
    public static final String DynamicRandomVariableStart = "@@DYNAMIC_RANDOM:";

    /**
     * Delimiter of range of random variable.
     * (ex, 1~10 => random value between 1 to 10)
     */
    public static final String RandomVariableRangeDelimiter = "~";

    //Variable
    /**
     * Delimiter for declare common variable
     */
    public static final String DeclareVariableDelimiter = "@@CMM_VAR@@";

    /**
     * Delimiter for using common variable
     */
    public static final String UsingVariableDelimiter = "@@";

    //Packet Define
    /**
     * Delimiter for define Packet
     */
    public static final String PckDefDelimiter = "#";

    /**
     * Delimiter for define Packet Name
     */
    public static final String PckNameDelimiter = ":";

    /**
     * String for Request Packet Transfer
     */
    public static final String RequestPacketString = "Request";

    /**
     * String for Send Packet Transfer
     */
    public static final String SendPacketString = "Send";


    /**
     * String for Response Packet
     */
    public static final String ResponsePacketString = "Response";

    /**
     * String for Receive Packet
     */
    public static final String ReceivePacketString = "Receive";

    /**
     *  String for Request RESTful Call
     */
    public static final String RequestRESTString = "RestRequest";

    /**
     *  String for Response RSETful Call
     */
    public static final String ResponseRESTString = "RestResponse";

    /**
     * String for Wait until receive given Packet
     */
    public static final String ReceiveUntilPacketString = "ReceiveUntil";


    public static final String RequestJMXString = "RequestJMXMBean";

    public static final String ResponseJMXString = "ResponseJMXMBean";


    //Json
    /**
     * Json variable name indicator
     * All of json variable name is ended with ~~~":
     * So ": is indicator
     */
    public static final String JsonVarNameEndIndicator = "\":";

    /**
     * Delimiter of comment
     */
    public static final String CommentDelimiter = "--";

    //Print out
    /**
     * Delimiter for print string
     */
    public static final String PrintDelimiter = "PRINT";

    //Loop
    /**
     * Delimiter for start of Loop procedure
     */
    public static final String LoopStartDelimiter = "BEGIN_LOOP";
    /**
     * Delimiter for end of Loop procedure
     */
    public static final String LoopEndDelimiter = "END_LOOP";

    //Etc.

    public static final String ExtraFunction = "FUNCTION";

    /**
     * Delimiter for Sleep
     */
    public static final String SleepDelimiter = "SLEEP";

    /**
     * Delimiter for disconnection
     */
    public static final String DisconnectionDelimiter = "DISCONNECT";

    /**
     * Delimiter for connection
     */
    public static final String ConnectionDelimiter = "CONNECT";
}

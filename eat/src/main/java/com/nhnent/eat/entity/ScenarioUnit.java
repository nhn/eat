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

import java.util.List;

/**
 * Scenario unit is extracted from Scenario File. <br>
 * - It can be defined to transfer or expected packet. <br>
 * - It can be defined to loop command <br>
 * - It can be defined to sleep command <br>
 */
public class ScenarioUnit {

    /**
     * ScenarioType (Request / RequestREST / SLEEP ... etc)
     */
    public String type;

    /**
     * Communication Type (Request or Response)
     */
    public String communicationMethod;

    /**
     * Destination Sever(Game or Chat)
     */
    public String dest;

    /**
     * Package Name of packet
     */
    public String packageName;

    /**
     * Packet Name of Scenario
     */
    public String name;

    /**
     * sub Id for user
     */
    public String subId;

    /**
     * Packet Contents
     */
    public String json;

    /**
     * Loop type (Start Loop or End Loop)
     */
    public LoopType loopType;

    /**
     * IP of Loop, it can distinguish specific loop from nested loop.
     */
    public int loopDepth;

    /**
     * Count of loop
     */
    public int loopCount;

    /**
     * Sleep period(milliseconds)
     * Only when the type is 'Sleep', it will used.
     * If the value is -1, it means it will set on run-time instead of Scenario Compiling time.
     */
    public int sleepPeriod;

    /**
     * Name of extra function
     * Only when the type is ExtraFunctionCall, it will used.
     */
    public String extraFunctionName;

    /**
     * Name of extra function
     * Only when the type is ExtraFunctionCall and the function return some value, it will used.
     */
    public String returnVariableName;

    /**
     * It can be used for
     *  - Print
     */
    public String reservedField;

    /**
     * Parameters of extra function.
     * Only when the type is ExtraFunctionCall, it will initialized.
     */
    public List<String> extraFunctionParameter;

    /**
     * Initialize Scenario Unit
     */
    public ScenarioUnit() {
        type = ScenarioUnitType.None;
        loopType = LoopType.None;
        loopCount = 0;
    }
}

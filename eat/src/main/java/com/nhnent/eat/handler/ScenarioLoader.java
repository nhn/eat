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

import co.paralleluniverse.fibers.SuspendExecution;
import com.nhnent.eat.TesterActor;
import com.nhnent.eat.common.Config.Config;
import com.nhnent.eat.entity.ScenarioUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static com.nhnent.eat.common.CommonDefine.*;
import static com.nhnent.eat.common.Util.applyVariable;
import static com.nhnent.eat.common.Util.extractGlobalVariable;

/**
 * Scenario Loader extracts scenario packet from scenario file
 */
public class ScenarioLoader {
    private final Logger logger = LoggerFactory.getLogger(getClass());


    private ScenarioUnitParser scenarioUnitParser = new ScenarioUnitParser();
    /**
     * Pre-compile the given scenario file.
     * It will perform the following works.
     * - Remove comment
     * - Apply Global variable
     * - Apply Random variable
     *
     * @param scenarioFileName scenario file name
     * @param userId User ID(Player's unique id)
     * @return Precompiled scenario file name
     */
    private String preCompile(final String scenarioFileName, final String userId) {

        List<String> packageNames = Config.obj().getPacket().getPackageKeys();
        List<String> packageNameDelimiters = new LinkedList<>();
        for(String packageName : packageNames) {
            packageNameDelimiters.add("[" + packageName + "]");
        }

        String compiledScenarioFile = scenarioFileName.replace(ScenarioFileExtension, "_" + userId
                + PrecompiledScenarioFileExtension);

        //If already exist compiled scenario file, return it.
        File f = new File(compiledScenarioFile);
        if (f.exists() && !f.isDirectory()) {
            return compiledScenarioFile;
        }

        //If scenario file does not exist, try it to compile.
        StringBuilder compiledContents = new StringBuilder(EmptyString);
        try {
            BufferedReader in = new BufferedReader(new FileReader(scenarioFileName));
            logger.debug("Pre-compile scenario file.(" + scenarioFileName + ")");
            String s = EmptyString;

            while ((s = in.readLine()) != null) {
                if (s.trim().equals(EmptyString)) {
                    continue;
                }


                if (s.trim().startsWith(CommentDelimiter)) {
                    continue;
                }

                if (s.contains(StaticRandomVariableStart)) {
                    s = putRandomVariable(s);
                }
                if (s.trim().startsWith(PckDefDelimiter + LoopStartDelimiter)
                        || s.trim().startsWith(PckDefDelimiter + LoopEndDelimiter)
                        || s.trim().startsWith(PckDefDelimiter + BeginSetCardDeckDelimiter)
                        || s.trim().startsWith(PckDefDelimiter + IncludeDelimiter)
                        || s.trim().startsWith(PckDefDelimiter + DisconnectionDelimiter)
                        || s.trim().startsWith(PckDefDelimiter + ConnectionDelimiter)
                        || s.trim().startsWith(PckDefDelimiter + SleepDelimiter)
                        || s.trim().startsWith(PckDefDelimiter + PrintDelimiter)
                        || s.trim().startsWith(PckDefDelimiter + ExtraFunction)) {
                    compiledContents.append(s).append("\n");
                    continue;
                }
                if (s.contains(DeclareVariableDelimiter)) {
                    extractGlobalVariable(s);
                    continue;
                }
                while(s.contains(UsingVariableDelimiter)) {
                    String appliedString = applyVariable(TesterActor.globalVariable.get(), s);
                    if(s.trim().equals(appliedString.trim())) {
                        //if applied string is same with original, it means cannot find variable.
                        //it might be real-time variable such as available poker card
                        break;
                    }
                    s = applyVariable(TesterActor.globalVariable.get(), s);
                }

                //Packet name will like '[NGT]Base.CreateRoomReq'
                //So at first remove '[' and ']', and then extract Package Name.
                for(String packages : packageNameDelimiters) {
                    String packageType = packages.replace("[","").replace("]","");
                    String packetPackageName = "[" + Config.obj().getPacket().getPackage(packageType) + "]";
                    s = s.replace(packages,packetPackageName);
                }

                compiledContents.append(s).append("\n");
            }
            in.close();

            BufferedWriter out = new BufferedWriter(new FileWriter(compiledScenarioFile));
            out.write(compiledContents.toString());
            out.close();
        } catch (Exception e) {
            logger.error("Exception is raised", e);
        }

        return compiledScenarioFile;
    }


    /**
     * Load Scenario from scenario file
     * Create ScenarioUnit list
     *
     * @param scenarioFileName Scenario file name
     * @param userId User ID
     * @return ScenarioUnit list
     */
    public final List<ScenarioUnit> loadScenario(final String scenarioFileName, final String userId) throws SuspendExecution {

        if(!userId.equals(EmptyString))
        {
            TesterActor.globalVariable.get().put("userId", userId);
        }

        // Do precompile for 1.Remove comments 2.Apply Global variables 3.Apply Random variables
        String compiledScenarioFile = preCompile(scenarioFileName, userId);

        List<ScenarioUnit> listScenarioUnit = new ArrayList();

        try
        {

            BufferedReader br = new BufferedReader(new FileReader(compiledScenarioFile));

            StringBuilder entireScenarioString = new StringBuilder(EmptyString);
            String line;

            // Read a scenario file as a String
            while((line = br.readLine()) != null)
            {
                entireScenarioString.append(line);
            }

            entireScenarioString = new StringBuilder(entireScenarioString.toString().trim());

            // Deck Setting problem..
            entireScenarioString = new StringBuilder(entireScenarioString.toString().replaceAll("\\s+", EmptyString));


            // Split the entire scenario String by '#'
            String[] syllableUnits = entireScenarioString.toString().split("[#]");
            List<String> finalSyllableUnits = new LinkedList<>();


            for (String syllableUnit : syllableUnits) {
                if (syllableUnit.equals(EmptyString)) continue;

                finalSyllableUnits.add(syllableUnit);
            }

            // For example, A RestRequest syllable becomes
            // "RestRequest{    "Method" : "post",    "Url" : "http://10.161.68.84:8080/gia/msudda/gameMoney/exchange/deposit/",    "Body" :    {        "tno": "3212",        "msuddaMoney": 50000,        "pcPokerMoney": 5000    }}"

            for(String syllable : finalSyllableUnits)
            {
                String header;
                String jsonBody;

                ScenarioUnit scenarioUnit;


                // If the syllable has json body, split header and json body.
                // Example)
                // syllable == "RestRequest{    "Method" : "post",    "Url" : "http://10.161.68.84:8080/gia/msudda/gameMoney/exchange/deposit/",    "Body" :    {        "tno": "3212",        "msuddaMoney": 50000,        "pcPokerMoney": 5000    }}"
                // header == "RestRequest"
                // jsonBody == "{    "Method" : "post",    "Url" : "http://10.161.68.84:8080/gia/msudda/gameMoney/exchange/deposit/",    "Body" :    {        "tno": "3212",        "msuddaMoney": 50000,        "pcPokerMoney": 5000    }}""


                // If there is no json body, json body will be EmptyString("")
                // Example)
                // syllable == SLEEP 500
                // header == SLEEP 500
                // jsonBody == ""

                if(syllable.contains("{"))
                {
                    int headerEndIndex = syllable.indexOf('{');

                    header = syllable.substring(0, headerEndIndex);
                    jsonBody = syllable.substring(headerEndIndex);
                }
                else
                {
                    header = syllable;
                    jsonBody = EmptyString;
                }

                // If the syllable starts with "include", recursive the loadScenario function.
                if(header.startsWith(IncludeDelimiter))
                {
                    String scenarioPath = Config.obj().getScenario().getScenarioPath();
                    String includeFileName = scenarioPath + "/" + header.replace(" ", "")
                            .replace(IncludeDelimiter + "=", "");
                    listScenarioUnit.addAll(loadScenario(includeFileName, userId));
                }
                else
                {
                    // Create a ScenarioUnit by passing ScenarioUnit parser.
                    scenarioUnit = scenarioUnitParser.parse(header, jsonBody);

                    listScenarioUnit.add(scenarioUnit);
                }
            }

            br.close();

            // Delete temporary compiled file
            File f = new File(compiledScenarioFile);
            if (f.exists() && !f.isDirectory()) {
                if (f.delete()) {
                    logger.debug("All of compiled scenario files are deleted.");
                } else {
                    logger.debug("Failed to deletion of compiled scenario files.");
                }
            }


            return listScenarioUnit;
        }
        catch (Exception e)
        {
            logger.error("Exception raised :" + e);
        }



        return null;
    }


    /**
     * Generate random value and replace it to string
     *
     * @param s original string
     * @return replaced string
     */
    private String putRandomVariable(final String s) {
        Random generator = new Random();
        int posStart = s.indexOf(StaticRandomVariableStart);
        int posEnd = s.indexOf(RandomVariableEnd, posStart + 1) + RandomVariableEnd.length();
        String randomValString = s.substring(posStart, posEnd);
        String[] ranges = randomValString
                .replace(StaticRandomVariableStart, EmptyString)
                .replace(RandomVariableEnd, EmptyString)
                .trim()
                .split(RandomVariableRangeDelimiter);
        int rangeStart = Integer.parseInt(ranges[0].trim());
        int rangeEnd = Integer.parseInt(ranges[1].trim());
        int randomVal = generator.nextInt(rangeEnd - rangeStart) + rangeStart;
        return s.replace(randomValString, String.valueOf(randomVal));
    }

}

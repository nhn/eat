package com.nhnent.eat;

import com.nhnent.eat.common.Config.Config;
import com.nhnent.eat.handler.ScenarioLoader;
import org.junit.Test;

/**
 * Created by NHNEnt on 2016-08-09.
 */
public class ScenarioLoaderTest {
    @Test
    public void load() throws Exception {
            ScenarioLoader scenLoader = new ScenarioLoader();
            String scenPath = Config.obj().getScenario().getScenarioPath();
            String scenFile = scenPath + "\\chat_test.scn";
            System.out.println(scenPath);
            scenLoader.loadScenario(scenFile, "");
    }


}
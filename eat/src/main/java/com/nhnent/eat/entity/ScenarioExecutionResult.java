package com.nhnent.eat.entity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NHNEnt on 2017-03-27.
 */
public class ScenarioExecutionResult {
        public int succeedCount = 0;
        public int failureCount = 0;
        public List<Duration> listResponseTime = new ArrayList<>();

        public StatisticsResult statisticsResult;
}

package com.nhnent.eat.customScenario;

import co.paralleluniverse.fibers.SuspendExecution;
import com.nhnent.eat.common.PacketClassPool;
import com.nhnent.eat.common.Util;
import com.nhnent.eat.communication.communicator.IBaseCommunication;
import com.nhnent.eat.entity.ScenarioExecutionResult;
import com.nhnent.eat.entity.ScenarioUnit;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

/**
 * This class load Custom Scenario API and execute it.
 */
public class ApiLoader {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Class apiClass = null;
    private final HashMap<String, Object> apiInstances = new HashMap<>();

    private static final ApiLoader obj = new ApiLoader();
    public static ApiLoader obj() {
        return obj;
    }

    /**
     * Load class from JAR file
     * @param pathToJar path of JAR file
     * @param apiClassName class name to load
     */
    public void loadClass(final String pathToJar, final String apiClassName) {
        apiClass = Util.loadClassFromJarFile(pathToJar, apiClassName);
    }

    /**
     * Initialize class and store it to apiInstances(HashMap)
     * @param packetClassPool packet class pool
     * @param communicationList communication interface list
     * @param runtimeVar runtime variable which used by Scenario
     * @param userId User ID
     */
    public void initialize(final PacketClassPool packetClassPool,
                           final List<IBaseCommunication> communicationList,
                           final HashMap<String, String> runtimeVar,
                           final String userId) throws SuspendExecution {
        Object instance = null;
        try {
            instance = apiClass.newInstance();
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        apiInstances.put(userId, instance);
        ((BaseCustomAPI) apiInstances.get(userId)).initialize(packetClassPool, communicationList, runtimeVar, userId);
    }

    public Boolean executeExtraFunction(ScenarioExecutionResult result, final String userId, final ScenarioUnit scenario)
            throws SuspendExecution, InterruptedException {
        if(apiInstances.containsKey(userId)) {
            return ((BaseCustomAPI) apiInstances.get(userId)).executeExtraFunction(result, scenario);
        }
        return Boolean.TRUE;
    }
}

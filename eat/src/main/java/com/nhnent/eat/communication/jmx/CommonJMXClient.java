package com.nhnent.eat.communication.jmx;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nhnent.eat.common.Config.Config;
import com.nhnent.eat.common.Config.JMXConfig;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


class MBeanData {
    public MBeanData(String fullName, Class classType) {
        this.fullName = fullName;
        this.classType = classType;

        this.functions = new HashMap<>();
    }

    private final String fullName;
    public final Class classType;
    public Object instance;

    public final HashMap<String, Method> functions;
}


/**
 * JMX client to access QA Service of Game
 */
public class CommonJMXClient {

    private static final CommonJMXClient instance = new CommonJMXClient();

    public static CommonJMXClient obj() {
        return instance;
    }

    private JMXConnector jmxConnector;
    private final HashMap<String, MBeanData> classMap;

    ObjectName beanName;

    public void close() throws IOException {
        if (jmxConnector != null) {
            jmxConnector.close();
            jmxConnector = null;
        }
    }

    private CommonJMXClient() {
        classMap = new HashMap<>();

        if (jmxConnector == null) {
            try {

                JMXConfig conf = Config.obj().getJmxConfig();

                loadClass(conf.getmBeanFilePath());

                JMXServiceURL url =
                        new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + Config.obj().getJmxConfig().getIpAddress() + ":" +
                                Config.obj().getJmxConfig().getPort() + "/jmxrmi");
                jmxConnector = JMXConnectorFactory.connect(url);

                createProxy(Config.obj().getJmxConfig().getMBeanName(), Config.obj().getJmxConfig().getEndPointName());

            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }

    private boolean createProxy(String mBeanName, String endPointName) {
        MBeanData data = findData(mBeanName);

        try {
            if (data.instance == null) {
                MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
                data.instance = MBeanServerInvocationHandler.newProxyInstance(
                        mbeanServerConnection, new ObjectName(endPointName), data.classType, true);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return data.instance != null;
    }


    public Object qaCommand(String mBeanName, String functionName, String json) {
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(json);

        JsonObject jo = je.getAsJsonObject();

        Set<Map.Entry<String, JsonElement>> entrySet = jo.entrySet();

        List<String> parameters = new LinkedList<>();

        for (Map.Entry<String, JsonElement> entry : entrySet) {

            if (entry.getValue().toString().startsWith("\""))
                parameters.add(entry.getValue().getAsString());
            else
                parameters.add(entry.getValue().toString());
        }

        Object[] parametersArray = parameters.toArray();

        return executeFunction(mBeanName, functionName, parametersArray);
    }

    public Object executeFunction(String mBeanName, String callFunctionName, Object... parameters) {

        MBeanData data = findData(mBeanName);

        if (data.instance == null) {
            throw new RuntimeException(mBeanName + "'s instance is not exist");
        }

        return invokeFunction(data, callFunctionName, parameters);
    }

    private Method createMethod(MBeanData data, String callFunctionName, Object... parameters) {
        Method method = null;

        Class<?>[] classes = new Class[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            classes[i] = parameters[i].getClass();
        }
        try {
            method = data.classType.getMethod(callFunctionName, classes);
        } catch (Exception e) {
            System.out.println("createMethod : " + e.toString());
        }

        return method;
    }

    private Object invokeFunction(MBeanData data, String callFunctionName, Object... parameters) {
        try {
            Method method;
            if (!data.functions.containsKey(callFunctionName)) {
                method = createMethod(data, callFunctionName, parameters);
                data.functions.put(callFunctionName, method);
            } else {
                method = data.functions.get(callFunctionName);
            }

            return method.invoke(data.instance, parameters);
        } catch (Exception e) {
            System.out.println("invokeFunction : " + e.toString());
        }

        return null;
    }

    private MBeanData findData(String mBeanName) {
        if (!classMap.containsKey(mBeanName)) {
            throw new RuntimeException(mBeanName + " is not exist");
        }

        return classMap.get(mBeanName);
    }

    private void loadClass(String filePath) throws Exception {
        JarFile jarFile = new JarFile(filePath);
        Enumeration<JarEntry> entry = jarFile.entries();

        URL[] urls = {new URL("jar:file:" + filePath + "!/")};
        URLClassLoader cl = URLClassLoader.newInstance(urls);

        while (entry.hasMoreElements()) {
            JarEntry je = entry.nextElement();
            if (je.isDirectory()) {
                continue;
            }

            final int lengthOfExtension = 6;
            String fullClassName = je.getName().substring(0, je.getName().length() - lengthOfExtension);
            fullClassName = fullClassName.replace('/', '.');

            int lastDotIndex = fullClassName.lastIndexOf('.') + 1;
            int classNameLength = (fullClassName.length() - lastDotIndex);
            String className = fullClassName.substring(lastDotIndex, lastDotIndex + classNameLength);

            if (!className.endsWith("MBean")) {
                continue;
            }

            try {
                Class klass = cl.loadClass(fullClassName);
                classMap.put(className, new MBeanData(fullClassName, klass));
            } catch (Exception e) {
                System.out.println("loadClass : " + e.toString());
            }

        }
    }
}

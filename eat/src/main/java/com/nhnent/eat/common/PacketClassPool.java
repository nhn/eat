package com.nhnent.eat.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class manage class which comes from JAR
 */
public class PacketClassPool {

    final Logger logger = LoggerFactory.getLogger(getClass());
    HashMap<String, Class> classes;

    HashMap<String, Class> foundClasses;

    private static PacketClassPool instance = null;

    /**
     * Get singleton instance
     * @return singleton instance
     */
    public static PacketClassPool obj() {
        if (instance == null) {
            instance = new PacketClassPool();
        }
        return instance;
    }

    /**
     * Constructor
     */
    public PacketClassPool() {
        classes = new HashMap<>();
        foundClasses = new HashMap<>();
    }

    /**
     * Load classes information from jar file
     * @param pathToJar path to jar file
     * @param packageName package name
     * @throws Exception cannot find jar file
     */
    public final void loadClassInfoFromJarFile(final String packageName, final String pathToJar) throws Exception {

        JarFile jarFile = new JarFile(pathToJar);
        Enumeration<JarEntry> entry = jarFile.entries();

        URL[] urls = {new URL("jar:file:" + pathToJar + "!/")};
        URLClassLoader cl = URLClassLoader.newInstance(urls);

        while (entry.hasMoreElements()) {
            JarEntry je = entry.nextElement();
            if (je.isDirectory() || !je.getName().endsWith(".class")) {
                continue;
            }
            // -6 because of .class
            final int lengthOfExtension = 6;
            String className = je.getName().substring(0, je.getName().length() - lengthOfExtension);
            className = className.replace('/', '.');

            if(!className.startsWith(packageName)) {
                continue;
            }

            try {
                Class klass = cl.loadClass(className);
                classes.put(className,klass);
            }
            catch (Exception e) {
                logger.error("Failed to load class:" + className);
            }
        }
    }

    /**
     * Find class which match with given name
     * @param className class name which want to find
     * @return found class
     */
    public final Class findClassByName(String className) {
        return classes.get(className);
    }

    /**
     * Find class which end with given name
     * @param className class name which want to find
     * @return found class
     */
    public final Class findClassEndWithGivenName(String className) {

        //To reduce full search, use foundClasses
        if(foundClasses.containsKey(className)) {
            return foundClasses.get(className);
        }

        for (String k : classes.keySet()) {
            if(k.endsWith(className)) {
                foundClasses.put(k, classes.get(k));
                return classes.get(k);
            }
        }
        return null;
    }

    /**
     * Call function of class
     * @param clsName Class Name
     * @param instance instance of class
     * @param functionName Function Name
     * @param parameters Parameters of function
     * @return return value(s) of function
     * @throws Exception Handle exception
     */
    public final Object callFunction(final String clsName,
                                     final Object instance,
                                     final String functionName,
                                     final Object... parameters) throws Exception {
        return callFunction(true, clsName, instance, functionName, parameters);
    }

    /**
     * Call function of class
     * @param usePrimitiveType function function which use primitive type parameter or not
     * @param clsName Class Name
     * @param instance instance of class
     * @param functionName Function Name
     * @param parameters Parameters of function
     * @return return value(s) of function
     * @throws Exception Handle exception
     */
    public final Object callFunction(final Boolean usePrimitiveType,
                                     final String clsName,
                                     final Object instance,
                                     final String functionName,
                                     final Object... parameters) throws Exception {
        Class<?> klass = findClassByName(clsName);
        Method theMethod;
        if (parameters != null) {
            Class<?>[] parameterTypes = new Class[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                Class type = parameters[i].getClass();
                //In case of ProtoBuf, it just use primitive type.
                //So the following code change the type to find proper method of class.
                if (usePrimitiveType) {
                    if (type.getName().equals("java.lang.Boolean")) {
                        type = boolean.class;
                    }
                    else if (type.getName().equals("java.lang.Integer")) {
                        type = int.class;
                    }
                }
                parameterTypes[i] = type;
            }
            theMethod = klass.getMethod(functionName, parameterTypes);
        }
        else {
            theMethod = klass.getMethod(functionName);
        }
        return theMethod.invoke(instance, parameters);
    }
}

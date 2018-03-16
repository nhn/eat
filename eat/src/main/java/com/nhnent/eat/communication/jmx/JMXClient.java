package com.nhnent.eat.communication.jmx;

import co.paralleluniverse.fibers.SuspendExecution;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.nhnent.eat.common.Config.Config;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.nhnent.eat.common.CommonDefine.EmptyString;
import static com.nhnent.eat.common.JsonUtil.getValueOfVar;

/**
 * JMX client to access QA Service of Game
 */
public class JMXClient {

    private static final JMXClient instance = new JMXClient();

    public static JMXClient obj() {
        return instance;
    }

    private JMXConnector jmxConnector = null;
    private QaTestAPIMBean mBeanProxy;

    /**
     * Connect to QA Service
     *
     * @throws IOException                  Throw Exception
     * @throws MalformedObjectNameException Throw Exception
     * @throws InterruptedException         Throw Exception
     * @throws SuspendExecution             Throw Exception
     * @throws ExecutionException           Throw Exception
     */
    private void connect()
            throws IOException, MalformedObjectNameException,
            InterruptedException, SuspendExecution, ExecutionException {

        if (jmxConnector != null) {
            return;
        }

        JMXServiceURL url =
                new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + Config.obj().getQaService().getIpAddress() + ":" +
                        Config.obj().getQaService().getPort() + "/jmxrmi");

        jmxConnector = JMXConnectorFactory.connect(url);
        MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();
        //ObjectName should be same as your MBean name
        ObjectName mBeanName = new ObjectName(Config.obj().getQaService().getEndPointName());

        //Get MBean proxy instance that will be used to make calls to registered MBean
        mBeanProxy =
                MBeanServerInvocationHandler.newProxyInstance(
                        mbeanServerConnection, mBeanName, QaTestAPIMBean.class, true);
    }

    /**
     * Set Card Deck of Game
     *
     * @param json Card Deck
     * @throws InterruptedException Throw Exception
     * @throws SuspendExecution     Throw Exception
     * @throws ExecutionException   Throw Exception
     */
    public void setCardDeck(String json) throws InterruptedException, SuspendExecution, ExecutionException,
            IOException, MalformedObjectNameException {
        JMXClient.obj().connect();
        mBeanProxy.sendLobbyCommand("SetCardDeck", json);
    }

    public void setQaCommand(String json) throws InterruptedException, SuspendExecution, ExecutionException,
            IOException, MalformedObjectNameException {

        String command;
        String data;

        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(json);

        JsonElement jeCommand = getValueOfVar(je, "Command");
        if (jeCommand != null) {
            command = jeCommand.getAsString();
        } else {
            command = EmptyString;
        }

        JsonElement jeData = getValueOfVar(je, "Data");
        if (jeData != null) {
            data = jeData.toString();
        } else {
            data = EmptyString;
        }

        JMXClient.obj().connect();
        mBeanProxy.sendLobbyCommand(command, data);
    }

    /**
     * Disconnect JMX connection
     *
     * @throws IOException Throw Exception
     */
    public void disconnect() throws IOException {
        if (jmxConnector != null) {
            jmxConnector.close();
            jmxConnector = null;
        }
    }
}

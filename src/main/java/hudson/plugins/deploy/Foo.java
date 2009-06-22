package hudson.plugins.deploy;

import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.JMXConnector;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.MBeanInfo;
import javax.management.ObjectInstance;
import javax.management.MBeanServerInvocationHandler;
import javax.naming.Context;
import java.util.Hashtable;
import java.net.URLClassLoader;
import java.net.URL;

/**
 *
 * JMX documentation:
 *      http://edocs.bea.com/wls/docs100/wlsmbeanref/core/index.html
 *
 * @author Kohsuke Kawaguchi
 */
public class Foo {

    public static void main(String[] args) throws Exception {
        JMXServiceURL serviceURL = new JMXServiceURL("t3", "127.0.0.1", 7001, "/jndi/weblogic.management.mbeanservers.domainruntime");

        URLClassLoader cl = new URLClassLoader(new URL[]{
                new URL("file:///home/kohsuke/bea/wlserver_10.3/server/lib/weblogic.jar")});

        // otherwise JNDI fails to find an InitialContextFactory
        Thread.currentThread().setContextClassLoader(cl);

        Hashtable h = new Hashtable();
        h.put(Context.SECURITY_PRINCIPAL, "admin");
        h.put(Context.SECURITY_CREDENTIALS, "adminadmin");
        h.put(JMXConnectorFactory.PROTOCOL_PROVIDER_CLASS_LOADER,cl);
        h.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES,"weblogic.management.remote");
        
        JMXConnector connector = JMXConnectorFactory.connect(serviceURL, h);
        MBeanServerConnection con = connector.getMBeanServerConnection();

        
    }

    interface DeployerRuntime {
        ObjectName activate(String source, String name, String stagingMode, Object info, String id);
    }

    interface DeploymentTaskRuntimeMBean {

    }

    /**
     * Trying to access mbeans fail, apparently because CORBA fails to unmarshal a WL-specific exception class
     */
    public static void jmain(String[] args) throws Exception {
        // http://download.oracle.com/docs/cd/E11035_01/wls100/jmx/accessWLS.html#wp1118690
        String hostname = "localhost";
        int port = 7001;
        String protocol = "rmi";
        String jndiroot = new String("/jndi/iiop://" + hostname + ":" +
                port + "/");
        // weblogic.management.mbeanservers.domainruntime
        // weblogic.management.mbeanservers.runtime
        // weblogic.management.mbeanservers.edit
        String mserver = "weblogic.management.mbeanservers.domainruntime";

        JMXServiceURL serviceURL = new JMXServiceURL(protocol, hostname, port,
                jndiroot + mserver);

        Hashtable h = new Hashtable();
        h.put(Context.SECURITY_PRINCIPAL, "admin");
        h.put(Context.SECURITY_CREDENTIALS, "adminadmin");

        JMXConnector connector = JMXConnectorFactory.connect(serviceURL, h);
        MBeanServerConnection con = connector.getMBeanServerConnection();
//        for(ObjectName on : con.queryNames(null,null))
//            System.out.println(on);

        ObjectName on = new ObjectName("com.bea:Name=DeployerRuntime,Type=DeployerRuntime");
        DeployerRuntime dr = MBeanServerInvocationHandler.newProxyInstance(con, on, DeployerRuntime.class, false);
        System.out.println(con.invoke(on,"activate",new Object[]{"src/test/simple.war","simple",null,null,null},
                new String [] { "java.lang.String", "java.lang.String", "java.lang.String", "weblogic.management.deploy.DeploymentData", "java.lang.String" })); 

        // see http://90kts.com/blog/2008/monitoring-weblogic-using-jmx-in-sitescope/
        // the trick is:
        /*
Setting up IIOP access on monitored machines
SiteScope relies on the IIOP protocol to talk to the WL server. In order to get remote access with JMX via IIOP you need to provide a default username and password for the IIOP user.

This can be done via the WL admin console as in:
domServiceBus->Environment->Servers->server01_nn->Protocols [tab]\->IIOP->advanced

Then provide a default username and password for the IIOP user.

Note: IIOP is already enabled by default, but a username and pwd is not. This change requires a server restart for WL. You must make sure that the username and password is >= 8 digits, otherwise the CORBA connection will fail when using SiteScope.

        
         */
    }
}

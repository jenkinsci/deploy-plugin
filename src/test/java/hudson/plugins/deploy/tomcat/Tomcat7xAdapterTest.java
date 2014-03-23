package hudson.plugins.deploy.tomcat;

import hudson.EnvVars;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.property.RemotePropertySet;
import org.codehaus.cargo.container.tomcat.Tomcat7xRemoteContainer;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author soudmaijer
 */
public class Tomcat7xAdapterTest {

    private Tomcat7xAdapter adapter;
    private static final String url = "http://localhost:8080";
    private static final String username = "username";
    private static final String password = "password";

    @Before
    public void setup() {
        adapter = new  Tomcat7xAdapter(url, password, username,"");
    }

    @Test
    public void testContainerId() {
        Assert.assertEquals(adapter.getContainerId(), new Tomcat7xRemoteContainer(null).getId());
    }

    @Test
    public void testConfigure() {
        final DefaultConfigurationFactory configFactory = new DefaultConfigurationFactory();
        Configuration config = configFactory.createConfiguration(adapter.getContainerId(), ContainerType.REMOTE, ConfigurationType.RUNTIME);
        adapter.configure(config, new EnvVars());

        Assert.assertEquals(url + "/manager/text", config.getPropertyValue(RemotePropertySet.URI));
        Assert.assertEquals(username, config.getPropertyValue(RemotePropertySet.USERNAME));
        Assert.assertEquals(password, config.getPropertyValue(RemotePropertySet.PASSWORD));
    }

    @Test
    public void testEnvVars() {
        final String urlvar = "url";
        final String urlval = "https://localhost:8443";
        final String managervar = "man";
        final String managerval = "/super-manager";
        final String uservar = "user";
        final String userval = "secret";
        final String passvar = "pass";
        final String passval = "agent man";

        final Tomcat7xAdapter envadapter = new Tomcat7xAdapter("$" + urlvar, "$" + passvar, "$" + uservar, "$" + managervar);
        final DefaultConfigurationFactory configFactory = new DefaultConfigurationFactory();
        Configuration config = configFactory.createConfiguration(envadapter.getContainerId(), ContainerType.REMOTE, ConfigurationType.RUNTIME);
        envadapter.configure(config, new EnvVars(urlvar, urlval, managervar, managerval, uservar, userval, passvar, passval));

        Assert.assertEquals(urlval + managerval, config.getPropertyValue(RemotePropertySet.URI));
        Assert.assertEquals(userval, config.getPropertyValue(RemotePropertySet.USERNAME));
        Assert.assertEquals(passval, config.getPropertyValue(RemotePropertySet.PASSWORD));
    }
}

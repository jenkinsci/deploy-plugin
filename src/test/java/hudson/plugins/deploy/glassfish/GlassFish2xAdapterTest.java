package hudson.plugins.deploy.glassfish;

import hudson.EnvVars;
import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.glassfish.GlassFish2xInstalledLocalContainer;
import org.codehaus.cargo.generic.ContainerFactory;
import org.codehaus.cargo.generic.DefaultContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author soudmaijer
 */
public class GlassFish2xAdapterTest {

    private GlassFish2xAdapter adapter;
    private static final String home = "/dev/null";
    private static final String username = "username";
    private static final String password = "password";
    private static int port = 1234;

    @Before
    public void setup() {
        adapter = new GlassFish2xAdapter(home, password, username, port);
    }

    @Test
    public void testContainerId() {
        Assert.assertEquals(adapter.getContainerId(), new GlassFish2xInstalledLocalContainer(null).getId());
    }

    @Test
    public void testConfigure() {
        Assert.assertEquals(adapter.home, home);
     //   Assert.assertEquals(adapter.adminPort, port);
        Assert.assertEquals(adapter.userName, username);
        Assert.assertEquals(adapter.getPassword(), password);

        ConfigurationFactory configFactory = new DefaultConfigurationFactory();
        ContainerFactory containerFactory = new DefaultContainerFactory();

        Container container = adapter.getContainer(configFactory, containerFactory, adapter.getContainerId(), new EnvVars());
        Assert.assertNotNull(container);
    }
}

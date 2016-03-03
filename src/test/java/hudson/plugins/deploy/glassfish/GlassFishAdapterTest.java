package hudson.plugins.deploy.glassfish;

import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author soudmaijer
 */
public class GlassFishAdapterTest {

    private GlassFish3xAdapter glassFish3xAdapter;
    private static final String home = "/dev/null";
    private static final String username = "username";
    private static final String password = "password";
    private static final String port = "1234";

    @Before
    public void setup() {
        glassFish3xAdapter = new GlassFish3xAdapter(home, password, username, port, null);
    }

    @Test
    public void testConfigureGlassFish3x() {
        ConfigurationFactory configFactory = new DefaultConfigurationFactory();
        Configuration config = configFactory.createConfiguration(glassFish3xAdapter.getContainerId(), ContainerType.INSTALLED, ConfigurationType.STANDALONE, glassFish3xAdapter.home);
        Assert.assertNotNull(config);
    }
}

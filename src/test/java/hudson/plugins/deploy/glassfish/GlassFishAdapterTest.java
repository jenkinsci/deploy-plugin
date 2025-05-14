package hudson.plugins.deploy.glassfish;

import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author soudmaijer
 */
@WithJenkins
class GlassFishAdapterTest {

    private GlassFish3xAdapter glassFish3xAdapter;
    private static final String home = "/dev/null";
    private static final String port = "1234";

    private JenkinsRule jenkinsRule;

    @BeforeEach
    void setUp(JenkinsRule rule) {
        jenkinsRule = rule;

        glassFish3xAdapter = new GlassFish3xAdapter(home, "", port, "");
    }

    @Test
    void testConfigureGlassFish3x() {
        ConfigurationFactory configFactory = new DefaultConfigurationFactory();
        Configuration config = configFactory.createConfiguration(glassFish3xAdapter.getContainerId(), ContainerType.INSTALLED, ConfigurationType.STANDALONE, glassFish3xAdapter.home);
        assertNotNull(config);
    }
}

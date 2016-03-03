package hudson.plugins.deploy.glassfish;

import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.StreamBuildListener;
import hudson.model.FreeStyleProject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.glassfish.GlassFish2xInstalledLocalContainer;
import org.codehaus.cargo.generic.ContainerFactory;
import org.codehaus.cargo.generic.DefaultContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * @author soudmaijer
 */
public class GlassFish2xAdapterTest {

    private GlassFish2xAdapter adapter;
    private static final String home = "/dev/null";
    private static final String username = "username";
    private static final String password = "password";
    private static final String test = "test";
    private static final String port = "1234";
    
    @Rule public JenkinsRule jenkinsRule = new JenkinsRule();

    @Before
    public void setup() {
        adapter = new GlassFish2xAdapter(home, password, username, port);
    }

    @Test
    public void testContainerId() {
        Assert.assertEquals(adapter.getContainerId(), new GlassFish2xInstalledLocalContainer(null).getId());
    }

    @Test
    public void testConfigure() throws IOException, InterruptedException, ExecutionException {
        Assert.assertEquals(adapter.home, home);
     //   Assert.assertEquals(adapter.adminPort, port);
        Assert.assertEquals(adapter.userName, username);
        Assert.assertEquals(adapter.getPassword(), password);

        ConfigurationFactory configFactory = new DefaultConfigurationFactory();
        ContainerFactory containerFactory = new DefaultContainerFactory();

        FreeStyleProject project = jenkinsRule.createFreeStyleProject();
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        BuildListener listener = new StreamBuildListener(new ByteArrayOutputStream());

        Container container = adapter.getContainer(configFactory, containerFactory, adapter.getContainerId(), build.getEnvironment(listener), build.getBuildVariableResolver());
        Assert.assertNotNull(container);
    }
}

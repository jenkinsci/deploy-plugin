package hudson.plugins.deploy.glassfish;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.domains.Domain;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
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
    private static final String port = "1234";
    
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Before
    public void setup() throws Exception {
        UsernamePasswordCredentialsImpl c = new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL, "test", "sample", username, password);
        CredentialsProvider.lookupStores(jenkinsRule.jenkins).iterator().next().addCredentials(Domain.global(), c);

        adapter = new GlassFish2xAdapter(home, c.getId(), port);
        adapter.loadCredentials(/* temp project to avoid npe */ jenkinsRule.createFreeStyleProject());
    }

    @Test
    public void testContainerId() {
        Assert.assertEquals(adapter.getContainerId(), new GlassFish2xInstalledLocalContainer(null).getId());
    }

    @Test
    public void testConfigure() throws IOException, InterruptedException, ExecutionException {
        Assert.assertEquals(home, adapter.home);
        Assert.assertEquals(port, adapter.adminPort);
        Assert.assertEquals(username, adapter.getUsername());
        Assert.assertEquals(password, adapter.getPassword());

        ConfigurationFactory configFactory = new DefaultConfigurationFactory();
        ContainerFactory containerFactory = new DefaultContainerFactory();

        FreeStyleProject project = jenkinsRule.createFreeStyleProject();
        adapter.loadCredentials(project); // DeployPublisher would do this
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        BuildListener listener = new StreamBuildListener(new ByteArrayOutputStream());

        Container container = adapter.getContainer(configFactory, containerFactory, adapter.getContainerId(), build.getEnvironment(listener), build.getBuildVariableResolver());
        Assert.assertNotNull(container);
    }
}

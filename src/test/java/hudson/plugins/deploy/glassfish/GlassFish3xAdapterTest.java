package hudson.plugins.deploy.glassfish;

import hudson.FilePath;
import hudson.model.StreamBuildListener;
import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.glassfish.GlassFish3xInstalledLocalContainer;
import org.codehaus.cargo.generic.ContainerFactory;
import org.codehaus.cargo.generic.DefaultContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author soudmaijer
 */
public class GlassFish3xAdapterTest {

    private GlassFish3xAdapter adapter;
    private static final String home = "D:/development/server/glassfishv3";
    private static final String username = "adminadmin";
    private static final String password = "adminadmin";
    private static int port = 1234;

    @Before
    public void setup() {
        adapter = new GlassFish3xAdapter(home, password, username, port);
    }

    @Test
    public void testContainerId() {
        Assert.assertEquals(adapter.getContainerId(), new GlassFish3xInstalledLocalContainer(null).getId());
    }

    @Test
    public void testConfigure() {
        Assert.assertEquals(adapter.home, home);
        Assert.assertEquals(adapter.adminPort, port);
        Assert.assertEquals(adapter.userName, username);
        Assert.assertEquals(adapter.password, password);

        ConfigurationFactory configFactory = new DefaultConfigurationFactory();
        ContainerFactory containerFactory = new DefaultContainerFactory();

        Container container = adapter.getContainer(configFactory, containerFactory, adapter.getContainerId());
        Assert.assertNotNull(container);
    }

    /**
     * This test only runs in your local environment
     * @throws IOException
     * @throws InterruptedException
     */
    //@Test
    public void testDeploy() throws IOException, InterruptedException {
        adapter.redeploy(new FilePath(new File("D:/workspace/hudson/deploy-plugin/src/test/simple.war")), null, null, new StreamBuildListener(System.out));
    }
}

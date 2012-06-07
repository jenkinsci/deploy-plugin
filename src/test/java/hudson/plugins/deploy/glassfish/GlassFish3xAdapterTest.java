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
    private GlassFish3xAdapter remoteAdapter;
    private static final String home = "D:/development/server/glassfishv3";
    private static final String username = "admin";
    private static final String password = "";
    private static int port = 1234;
    private static final String hostname = "localhost";
    private static final int adminPort = 4848;

    @Before
    public void setup() {
        adapter = new GlassFish3xAdapter(home, password, username, port, null);
        remoteAdapter = new GlassFish3xAdapter(null, password, username, adminPort, hostname);
    }

    @Test
    public void testContainerId() {
        Assert.assertEquals(adapter.getContainerId(), new GlassFish3xInstalledLocalContainer(null).getId());
    }

    @Test
    public void testConfigure() {
        Assert.assertEquals(adapter.home, home);
    //    Assert.assertEquals(adapter.adminPort, port);
        Assert.assertEquals(adapter.userName, username);
        Assert.assertEquals(adapter.getPassword(), password);

        ConfigurationFactory configFactory = new DefaultConfigurationFactory();
        ContainerFactory containerFactory = new DefaultContainerFactory();

        Container container = adapter.getContainer(configFactory, containerFactory, adapter.getContainerId());
        Assert.assertNotNull(container);
    }

    @Test
    public void testConfigureRemote() {
        Assert.assertNull("Expexted adapter.home to be null", remoteAdapter.home);
   //     Assert.assertEquals(remoteAdapter.adminPort, adminPort);
        Assert.assertEquals(remoteAdapter.userName, username);
        Assert.assertEquals(remoteAdapter.getPassword(), password);
        Assert.assertEquals(remoteAdapter.hostname, hostname);

        ConfigurationFactory configFactory = new DefaultConfigurationFactory();
        ContainerFactory containerFactory = new DefaultContainerFactory();

        Container container = remoteAdapter.getContainer(configFactory, containerFactory, remoteAdapter.getContainerId());
        Assert.assertNotNull(container);
    }

    /**
     * This test only runs in your local environment
     * @throws IOException
     * @throws InterruptedException
     */
    //@Test
    public void testDeploy() throws IOException, InterruptedException {
        
        adapter.redeploy(new FilePath(new File("src/test/simple.war")), "contextPath", null, null, new StreamBuildListener(System.out));
    }
    
    //@Test
    public void testRemoteDeploy() throws IOException, InterruptedException {
       

        remoteAdapter.redeploy(new FilePath(new File("src/test/simple.war")), "contextPath", null, null, new StreamBuildListener(System.out));
    }
}

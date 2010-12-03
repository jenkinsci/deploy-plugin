package hudson.plugins.deploy.tomcat;

import org.codehaus.cargo.container.tomcat.Tomcat7xRemoteContainer;
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
        adapter = new  Tomcat7xAdapter(url, password, username);
    }

    @Test
    public void testContainerId() {
        Assert.assertEquals(adapter.getContainerId(), Tomcat7xRemoteContainer.ID);            
    }

    @Test
    public void testConfigure() {
        Assert.assertEquals(adapter.url,url);
        Assert.assertEquals(adapter.userName,username);
        Assert.assertEquals(adapter.password,password);
    }
}

package hudson.plugins.deploy;

import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.tomcat.TomcatPropertySet;
import org.codehaus.cargo.container.property.RemotePropertySet;

import java.net.URL;
import java.net.MalformedURLException;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class TomcatAdapter extends DefaultContainerAdapterImpl {
    @Property(RemotePropertySet.USERNAME)
    public final String userName;
    @Property(RemotePropertySet.PASSWORD)
    public final String password;
    /**
     * Top URL of Tomcat.
     */
    public final URL url;

    public TomcatAdapter(String userName, String password, URL url) {
        this.password = password;
        this.url = url;
        this.userName = userName;
    }

    public void configure(Configuration config) {
        super.configure(config);
        try {
            config.setProperty(TomcatPropertySet.MANAGER_URL,new URL(url,"/manager").toExternalForm());
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }
}

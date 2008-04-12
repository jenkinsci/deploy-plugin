package hudson.plugins.deploy;

import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.tomcat.TomcatPropertySet;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Base class for Tomcat adapters.
 * 
 * @author Kohsuke Kawaguchi
 */
public abstract class TomcatAdapter extends PasswordProtectedAdapterCargo {
    /**
     * Top URL of Tomcat.
     */
    public final URL url;

    public TomcatAdapter(String userName, String password, URL url) {
        super(userName, password);
        this.url = url;
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

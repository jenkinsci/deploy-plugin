package hudson.plugins.deploy.tomcat;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.tomcat.TomcatPropertySet;
import org.kohsuke.stapler.DataBoundConstructor;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Tomcat 6.x
 *
 * @author Kohsuke Kawaguchi
 */
public class Tomcat6xAdapter extends TomcatAdapter {

    @DataBoundConstructor
    public Tomcat6xAdapter(String url, String password, String userName) {
        super(url, password, userName);
    }

		public void configure(Configuration config) {
        super.configure(config);
        try {
            URL _url = new URL(url + "/manager/html");
            config.setProperty(TomcatPropertySet.MANAGER_URL,_url.toExternalForm());
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }

    public String getContainerId() {
        return "tomcat6x";
    }
    
    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        public String getDisplayName() {
            return "Tomcat 6.x";
        }
    }
}


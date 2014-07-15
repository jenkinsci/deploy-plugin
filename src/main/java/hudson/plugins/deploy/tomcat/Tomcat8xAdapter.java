package hudson.plugins.deploy.tomcat;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import org.codehaus.cargo.container.property.RemotePropertySet;
import org.codehaus.cargo.container.configuration.Configuration;
import org.kohsuke.stapler.DataBoundConstructor;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Tomcat 8.x
 *
 * @author soudmaijer
 */
public class Tomcat8xAdapter extends TomcatAdapter {

    /**
     * Tomcat 8 support
     * 
     * @param url Tomcat server location (for example: http://localhost:8080)
     * @param password tomcat manager password
     * @param userName tomcat manager username
     */
    @DataBoundConstructor
    public Tomcat8xAdapter(String url, String password, String userName) {
        super(url, password, userName);
    }
    
		public void configure(Configuration config) {
        super.configure(config);
        try {
            URL _url = new URL(url + "/manager/text");
            config.setProperty(RemotePropertySet.URI,_url.toExternalForm());
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Tomcat Cargo containerId
     * @return tomcat8x
     */
    public String getContainerId() {
        return "tomcat8x";
    }
    
    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        public String getDisplayName() {
            return "Tomcat 8.x";
        }
    }
}


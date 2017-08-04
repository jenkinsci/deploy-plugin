package hudson.plugins.deploy.tomcat;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Tomcat 5.x.
 * 
 * @author Kohsuke Kawaguchi
 */
public class Tomcat5xAdapter extends TomcatAdapter {

    @DataBoundConstructor
    public Tomcat5xAdapter(String url, String credentialsId) {
        super(url, credentialsId);
    }

    public String getContainerId() {
        return "tomcat5x";
    }

    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        public String getDisplayName() {
            return "Tomcat 5.x";
        }
    }
}

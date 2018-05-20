package hudson.plugins.deploy.tomcat;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Tomcat 5.x.
 * 
 * @author Kohsuke Kawaguchi
 */
public class Tomcat5xAdapter extends TomcatAdapter {

    @DataBoundConstructor
    public Tomcat5xAdapter(String url, String credentialsId, String context) {
        super(url, credentialsId, context);
    }

    public String getContainerId() {
        return "tomcat5x";
    }

    @Symbol("tomcat5")
    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        public String getDisplayName() {
            return "Tomcat 5.x";
        }
    }
}

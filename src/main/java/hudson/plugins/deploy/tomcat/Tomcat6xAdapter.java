package hudson.plugins.deploy.tomcat;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

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

    public String getContainerId() {
        return "tomcat6x";
    }
    
    @Extension
    @Symbol("tomcat6x")
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        public String getDisplayName() {
            return "Tomcat 6.x";
        }
    }
}


package hudson.plugins.deploy.tomcat;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Tomcat 4.x.
 *
 * @author Kohsuke Kawaguchi
 */
public class Tomcat4xAdapter extends TomcatAdapter {

    @DataBoundConstructor
    public Tomcat4xAdapter(String url, String password, String userName) {
        super(url, password, userName);
    }

    public String getContainerId() {
        return "tomcat4x";
    }

    @Extension
    @Symbol("tomcat4x")
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        public String getDisplayName() {
            return "Tomcat 4.x";
        }
    }
}

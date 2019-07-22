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
    private static final long serialVersionUID = 1558737368614036333L;

    @DataBoundConstructor
    public Tomcat6xAdapter(String url, String credentialsId) {
        super(url, credentialsId);
    }

    @Override
    public String getContainerId() {
        return "tomcat6x";
    }

    @Symbol("tomcat6")
    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        @Override
        public String getDisplayName() {
            return "Tomcat 6.x";
        }
    }
}


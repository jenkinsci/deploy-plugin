package hudson.plugins.deploy.tomcat;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import org.codehaus.cargo.container.tomcat.Tomcat6xRemoteContainer;
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
    public Tomcat6xAdapter(String url, String credentialsId, String alternativeDeploymentContext, String path) {
        super(url, credentialsId, alternativeDeploymentContext, path);
    }

    @Override
    public String getContainerId() {
        return Tomcat6xRemoteContainer.ID;
    }

    @Symbol("tomcat6")
    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        @Override
        public String getDisplayName() {
            return new Tomcat6xRemoteContainer(null).getName();
        }
    }
}


package hudson.plugins.deploy.tomcat;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import org.codehaus.cargo.container.tomcat.Tomcat4xRemoteContainer;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Tomcat 4.x.
 *
 * @author Kohsuke Kawaguchi
 */
public class Tomcat4xAdapter extends TomcatAdapter {
    private static final long serialVersionUID = -3577537993151201721L;

    @DataBoundConstructor
    public Tomcat4xAdapter(String url, String credentialsId, String alternativeDeploymentContext, String path) {
        super(url, credentialsId, alternativeDeploymentContext, path);
    }

    @Override
    public String getContainerId() {
        return Tomcat4xRemoteContainer.ID;
    }

    @Symbol("tomcat4")
    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        @Override
        public String getDisplayName() {
            return new Tomcat4xRemoteContainer(null).getName();
        }
    }
}

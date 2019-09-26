package hudson.plugins.deploy.tomcat;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import org.codehaus.cargo.container.tomcat.Tomcat9xRemoteContainer;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Tomcat 9.x
 *
 * @author m4ndr4ck
 */
public class Tomcat9xAdapter extends TomcatAdapter {
    private static final long serialVersionUID = -7479041536985095270L;

    /**
     * Tomcat 9 support
     *
     * @param url Tomcat server location (for example: http://localhost:8080)
     * @param credentialsId tomcat manager username password credentials
     * @param alternativeDeploymentContext alternative context
     * @param path an alternative manager context path
     */
    @DataBoundConstructor
    public Tomcat9xAdapter(String url, String credentialsId, String alternativeDeploymentContext, String path) {
        super(url, credentialsId, alternativeDeploymentContext, path);
    }

    @Override
    public String getContainerId() {
        return Tomcat9xRemoteContainer.ID;
    }

    @Symbol("tomcat9")
    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        @Override
        public String getDisplayName() {
            return new Tomcat9xRemoteContainer(null).getName();
        }
    }
}

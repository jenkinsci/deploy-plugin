package hudson.plugins.deploy.tomcat;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;

import org.codehaus.cargo.container.tomcat.Tomcat5xRemoteContainer;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Tomcat 5.x.
 * 
 * @author Kohsuke Kawaguchi
 */
public class Tomcat5xAdapter extends TomcatAdapter {
    private static final long serialVersionUID = 2441615146518612287L;

    @DataBoundConstructor
    public Tomcat5xAdapter(String url, String credentialsId, String path) {
        super(url, credentialsId, path);
    }

    @Override
    public String getContainerId() {
        return Tomcat5xRemoteContainer.ID;
    }

    @Symbol("tomcat5")
    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        @Override
        public String getDisplayName() {
            return new Tomcat5xRemoteContainer(null).getName();
        }
    }
}

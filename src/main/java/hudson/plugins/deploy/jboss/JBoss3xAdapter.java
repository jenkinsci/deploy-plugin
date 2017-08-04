package hudson.plugins.deploy.jboss;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * JBoss 3.x.
 * 
 * @author Kohsuke Kawaguchi
 */
public class JBoss3xAdapter extends JBossAdapter {

    @DataBoundConstructor
    public JBoss3xAdapter(String url, String credentialsId) {
        super(url, credentialsId);
    }

    @Override
    public String getContainerId() {
        return "jboss3x";
    }

    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        @Override
        public String getDisplayName() {
            return "JBoss AS 3.x";
        }
    }
}

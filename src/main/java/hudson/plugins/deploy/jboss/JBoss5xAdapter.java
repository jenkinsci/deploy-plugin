package hudson.plugins.deploy.jboss;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author Kohsuke Kawaguchi
 */
public class JBoss5xAdapter extends JBossAdapter {

    @DataBoundConstructor
    public JBoss5xAdapter(String url, String credentialsId) {
        super(url, credentialsId);
    }

    @Override
    public String getContainerId() {
        return "jboss5x";
    }


    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        @Override
        public String getDisplayName() {
            return "JBoss AS 5.x";
        }
    }
}

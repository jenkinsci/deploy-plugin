package hudson.plugins.deploy.jboss;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * JBoss 4.x.
 *
 * @author Kohsuke Kawaguchi
 */
public class JBoss4xAdapter extends JBossAdapter {

    @DataBoundConstructor
    public JBoss4xAdapter(String url, String password, String userName, String portOffset) {
        super(url, password, userName, portOffset);
    }

    @Override
    public String getContainerId() {
        return "jboss4x";
    }

    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {

        @Override
        public String getDisplayName() {
            return "JBoss AS 4.x";
        }
    }
}

package hudson.plugins.deploy.jboss;

import hudson.model.Descriptor;
import hudson.plugins.deploy.ContainerAdapter;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import hudson.Extension;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * JBoss 4.x.
 *
 * @author Kohsuke Kawaguchi
 */
public class JBoss4xAdapter extends JBossAdapter {
    @DataBoundConstructor
    public JBoss4xAdapter(String url, String password, String userName) {
        super(url, password, userName);
    }

    public String getContainerId() {
        return "jboss4x";
    }

    @Extension
	public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        public String getDisplayName() {
            return "JBoss 4.x";
        }
    }
}

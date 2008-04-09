package hudson.plugins.deploy;

import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * JBoss 4.x.
 *
 * @author Kohsuke Kawaguchi
 */
public class JBoss4xAdapter extends JBossAdapter {
    @DataBoundConstructor
    public JBoss4xAdapter(String userName, String password) {
        super(userName, password);
    }

    public String getContainerId() {
        return "jboss4x";
    }

    public Descriptor<ContainerAdapter> getDescriptor() {
        return DESCRIPTOR;
    }

    public static final Descriptor<ContainerAdapter> DESCRIPTOR = new Descriptor<ContainerAdapter>(JBoss4xAdapter.class) {
        public String getDisplayName() {
            return "JBoss 4.x";
        }
    };
}

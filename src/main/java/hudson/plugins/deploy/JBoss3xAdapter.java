package hudson.plugins.deploy;

import org.kohsuke.stapler.DataBoundConstructor;
import hudson.model.Descriptor;

/**
 * JBoss 3.x.
 * 
 * @author Kohsuke Kawaguchi
 */
public class JBoss3xAdapter extends PasswordProtectedAdapter {
    @DataBoundConstructor
    public JBoss3xAdapter(String userName, String password) {
        super(userName, password);
    }

    public String getContainerId() {
        return "jboss3x";
    }

    public Descriptor<ContainerAdapter> getDescriptor() {
        return DESCRIPTOR;
    }

    public static final Descriptor<ContainerAdapter> DESCRIPTOR = new Descriptor<ContainerAdapter>(JBoss3xAdapter.class) {
        public String getDisplayName() {
            return "JBoss 3.x";
        }
    };
}

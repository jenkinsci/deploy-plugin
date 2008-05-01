package hudson.plugins.deploy;

import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.net.URL;

/**
 * @author Kohsuke Kawaguchi
 */
public class JBoss5xAdapter extends JBossAdapter {
    @DataBoundConstructor
    public JBoss5xAdapter(URL url, String userName, String password) {
        super(url, userName, password);
    }

    public String getContainerId() {
        return "jboss5x";
    }

    public Descriptor<ContainerAdapter> getDescriptor() {
        return DESCRIPTOR;
    }

    public static final Descriptor<ContainerAdapter> DESCRIPTOR = new Descriptor<ContainerAdapter>(JBoss5xAdapter.class) {
        public String getDisplayName() {
            return "JBoss 5.x";
        }
    };
}

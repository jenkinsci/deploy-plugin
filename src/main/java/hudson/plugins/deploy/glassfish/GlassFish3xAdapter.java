package hudson.plugins.deploy.glassfish;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import org.codehaus.cargo.container.glassfish.GlassFishStandaloneLocalConfiguration;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * GlassFish 3.x support
 * 
 * @author soudmaijer
 */
public class GlassFish3xAdapter extends GlassFishAdapter {

    /**
     * GlassFish 3.x
     *
     * @param home GlassFish home directory
     * @param password glassfish admin password
     * @param userName glassfish admin username
     */
    @DataBoundConstructor
    public GlassFish3xAdapter(String home, String password, String userName, Integer adminPort) {
        super(home, password, userName, adminPort);
        GlassFishStandaloneLocalConfiguration conf;
    }

    /**
     * GlassFish Cargo containerId
     * @return glassfish3x
     */
    @Override
    protected String getContainerId() {
        return "glassfish3x";
    }

    /**
     * {@inheritDoc}
     */
    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        public String getDisplayName() {
            return "GlassFish 3.x";
        }
    }
}

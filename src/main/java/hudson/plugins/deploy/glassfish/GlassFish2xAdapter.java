package hudson.plugins.deploy.glassfish;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * GlassFish 2.x support.
 */
public class GlassFish2xAdapter extends GlassFishAdapter {
	
    /**
     * GlassFish 2.x
     *
     * @param hostname hostname of the GlassFish installation
     * @param home location of the GlassFish installation
     * @param password admin password
     * @param userName admin username
     * @param adminPort admin server port
     */	
    @DataBoundConstructor
    public GlassFish2xAdapter(String hostname, String home, String password, String userName, Integer adminPort) {
        super(hostname, home, password, userName, adminPort);
    }

    @Override
    protected String getContainerId() {
        return "glassfish2x";
    }

    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        public String getDisplayName() {
            return "GlassFish 2.x";
        }
    }
}

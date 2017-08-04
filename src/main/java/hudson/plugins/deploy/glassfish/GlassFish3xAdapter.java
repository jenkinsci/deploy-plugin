package hudson.plugins.deploy.glassfish;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;

import org.codehaus.cargo.container.glassfish.GlassFish3xStandaloneLocalConfiguration;
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
     * @param credentialsId the id of the username password credential
     * @param adminPort glassfish admin port
     */
    @DataBoundConstructor
    public GlassFish3xAdapter(String home, String credentialsId, String adminPort, String hostname) {
        super(home, credentialsId, adminPort, hostname);
        GlassFish3xStandaloneLocalConfiguration conf;
    }

    /**password, userName, adminPort, hostname);
     }
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

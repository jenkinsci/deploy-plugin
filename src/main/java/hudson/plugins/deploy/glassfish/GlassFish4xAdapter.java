package hudson.plugins.deploy.glassfish;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import org.codehaus.cargo.container.glassfish.GlassFish4xStandaloneLocalConfiguration;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * GlassFish 4.x support
 * 
 * @author frenout
 */
public class GlassFish4xAdapter extends GlassFishAdapter {

    /**
     * GlassFish 4.x
     *
     * @param home GlassFish home directory
     * @param credentialsId the id of the username password credential
     * @param adminPort glassfish admin port
     */
    @DataBoundConstructor
    public GlassFish4xAdapter(String home, String credentialsId, String adminPort, String hostname) {
        super(home, credentialsId, adminPort, hostname);
        GlassFish4xStandaloneLocalConfiguration conf;
    }

    /**password, userName, adminPort, hostname);
     }
     * GlassFish Cargo containerId
     * @return glassfish3x
     */
    @Override
    protected String getContainerId() {
        return "glassfish4x";
    }

    /**
     * {@inheritDoc}
     */
    @Symbol("glassfish3")
    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        public String getDisplayName() {
            return "GlassFish 4.x";
        }
    }
}

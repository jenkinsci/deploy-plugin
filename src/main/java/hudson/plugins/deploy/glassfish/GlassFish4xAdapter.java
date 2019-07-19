package hudson.plugins.deploy.glassfish;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;

/**
 * GlassFish 4.x support
 * 
 * @author frenout
 */
public class GlassFish4xAdapter extends GlassFishAdapter {
    private static final long serialVersionUID = 2137808194916814021L;

    /**
     * GlassFish 4.x
     *
     * @param home location of the GlassFish installation
     * @param credentialsId the id of the credential
     * @param adminPort admin server port
     * @param hostname hostname
     */
    @DataBoundConstructor
    public GlassFish4xAdapter(String home, String credentialsId, String adminPort, String hostname) {
        super(home, credentialsId, adminPort, hostname);
    }

    @Override
    protected String getContainerId() {
        return "glassfish4x";
    }

    /**
     * {@inheritDoc}
     */
    @Symbol("glassfish4")
    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        @Override
        public String getDisplayName() {
            return "GlassFish 4.x";
        }
    }
}

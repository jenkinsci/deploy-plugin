package hudson.plugins.deploy.glassfish;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;

/**
 * GlassFish 3.x support
 * 
 * @author soudmaijer
 */
public class GlassFish3xAdapter extends GlassFishAdapter {
    private static final long serialVersionUID = 425375750404662378L;

    /**
     * GlassFish 3.x
     *
     * @param home location of the GlassFish installation
     * @param credentialsId the id of the credential
     * @param adminPort admin server port
     * @param hostname hostname
     */
    @DataBoundConstructor
    public GlassFish3xAdapter(String home, String credentialsId, String adminPort, String hostname) {
        super(home, credentialsId, adminPort, hostname);
    }

    @Override
    protected String getContainerId() {
        return "glassfish3x";
    }

    /**
     * {@inheritDoc}
     */
    @Symbol("glassfish3")
    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        @Override
        public String getDisplayName() {
            return "GlassFish 3.x";
        }
    }
}

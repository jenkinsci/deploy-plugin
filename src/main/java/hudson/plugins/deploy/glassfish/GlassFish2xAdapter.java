package hudson.plugins.deploy.glassfish;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * GlassFish 2.x support.
 */
public class GlassFish2xAdapter extends GlassFishAdapter {
    private static final long serialVersionUID = 7227205877124435028L;

    /**
     * GlassFish 2.x
     *
     * @param home location of the GlassFish installation
     * @param credentialsId the id of the credential
     * @param adminPort admin server port
     * @param hostname hostname
     */
    @DataBoundConstructor
    public GlassFish2xAdapter(String home, String credentialsId, String adminPort) {
        super(home, credentialsId, adminPort, null);
    }

    @Override
    protected String getContainerId() {
        return "glassfish2x";
    }

    @Symbol("glassfish2")
    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        @Override
        public String getDisplayName() {
            return "GlassFish 2.x";
        }
    }
}

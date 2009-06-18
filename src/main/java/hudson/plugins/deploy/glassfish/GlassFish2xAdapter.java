package hudson.plugins.deploy.glassfish;

import hudson.model.Descriptor;
import hudson.plugins.deploy.ContainerAdapter;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import hudson.Extension;

import org.kohsuke.stapler.DataBoundConstructor;

public class GlassFish2xAdapter extends GlassFishAdapter {
	@DataBoundConstructor
	public GlassFish2xAdapter(String home, String password, String userName, Integer adminPort) {
		super(home, password, userName, adminPort);
	}

	@Override
	protected String getContainerId() {
		return "glassfish2";
	}

    @Extension
	public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        public String getDisplayName() {
            return "GlassFish 2.x";
        }
    }
        
    private static final long serialVersionUID = 2872067827725333149L;
}

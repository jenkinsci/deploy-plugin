package hudson.plugins.deploy.glassfish;

import hudson.model.Descriptor;
import hudson.plugins.deploy.ContainerAdapter;

import org.kohsuke.stapler.DataBoundConstructor;

public class GlassFish2xAdapter extends GlassFishAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2872067827725333149L;

	@DataBoundConstructor
	public GlassFish2xAdapter(String url, String password, String userName) {
		super(url, password, userName);
	}

	@Override
	protected String getContainerId() {
		return "glassfish2";
	}

	public Descriptor<ContainerAdapter> getDescriptor() {
		return DESCRIPTOR;
	}

	public static final Descriptor<ContainerAdapter> DESCRIPTOR = new Descriptor<ContainerAdapter>(GlassFish2xAdapter.class) {
        public String getDisplayName() {
            return "GlassFish 2.x";
        }
    };
        
}

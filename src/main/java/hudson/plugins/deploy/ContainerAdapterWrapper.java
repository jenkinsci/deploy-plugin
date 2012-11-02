package hudson.plugins.deploy;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * A wrapper for a single {@link ContainerAdapter}
 * 
 * @author Brandon Munroe
 */
public class ContainerAdapterWrapper {
	public final ContainerAdapter adapter;
	
	@DataBoundConstructor
	public ContainerAdapterWrapper(ContainerAdapter adapter) {
		this.adapter = adapter;
	}
}

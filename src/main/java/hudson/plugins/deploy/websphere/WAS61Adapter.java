package hudson.plugins.deploy.websphere;

import java.io.IOException;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.plugins.deploy.ContainerAdapter;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import org.kohsuke.stapler.DataBoundConstructor;
import org.jvnet.localizer.ResourceBundleHolder; 

/**
 * WebSphere Application Server 6.1
 *
 * @author Antonio Sanso
 */
public class WAS61Adapter extends WebSphereAdapter {
	
	@DataBoundConstructor
	public WAS61Adapter(String url) {
		super(url);	
	}

	public String getContainerId() {
		return "was6x";
	}

	@Extension
	public static final class DescriptorImpl extends ContainerAdapterDescriptor {
		public String getDisplayName() {
			return "WebSphere Application Server 6.1";
		}
	}

	@Override
	public boolean redeploy(FilePath war, AbstractBuild<?, ?> build, Launcher launcher, final BuildListener listener) throws IOException, InterruptedException {		 
		try {
			installApplication(war);
		} catch (Exception e) {  
			listener.fatalError(ResourceBundleHolder.get(WAS61Adapter.class).format("DeployExecutionFailed",e));
            return false;
 		}
		return true;
	}
}


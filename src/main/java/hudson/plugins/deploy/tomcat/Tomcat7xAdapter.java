package hudson.plugins.deploy.tomcat;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Tomcat 7.x
 */
public class Tomcat7xAdapter extends TomcatAdapter {

	

	/**
	 * Tomcat 7 support
	 * 
	 * @param url
	 *            Tomcat server location (for example: http://localhost:8080)
	 * @param password
	 *            tomcat manager password
	 * @param userName
	 *            tomcat manager username
	 */
	@DataBoundConstructor
	public Tomcat7xAdapter(String url, String password, String userName,
			String managerContext) {
		super(url, password, userName,managerContext);
	}

	@Override
	protected String getTomcatManagerSuffix() {
		if (managerContext.length() != 0) {
			return managerContext;
		}
		return "/manager/text";
	}

	/**
	 * Tomcat Cargo containerId
	 * 
	 * @return tomcat7x
	 */
	public String getContainerId() {
		return "tomcat7x";
	}

	@Extension
	public static final class DescriptorImpl extends ContainerAdapterDescriptor {
		public String getDisplayName() {
			return "Tomcat 7.x";
		}
	}
}

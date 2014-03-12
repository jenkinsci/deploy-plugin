package hudson.plugins.deploy.tomcat;

import hudson.EnvVars;
import hudson.plugins.deploy.PasswordProtectedAdapterCargo;

import org.codehaus.cargo.container.property.RemotePropertySet;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.tomcat.TomcatWAR;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Base class for Tomcat adapters.
 * 
 * @author Kohsuke Kawaguchi
 */
public abstract class TomcatAdapter extends PasswordProtectedAdapterCargo {
	/**
	 * Top URL of Tomcat.
	 */
	public final String url;

	protected String managerContext;

	public TomcatAdapter(String url, String password, String userName,
			String managerContext) {
		super(userName, password);
		this.url = url;
		this.managerContext = managerContext;
	}

	public void configure(Configuration config, EnvVars env) {
		super.configure(config, env);
		String containerUrl = env.expand(url);
		try {
			URL _url = new URL(containerUrl + env.expand(getTomcatManagerSuffix()));
			config.setProperty(RemotePropertySet.URI, _url.toExternalForm());
		} catch (MalformedURLException e) {
			throw new AssertionError(e);
		}
		String username = env.expand(userName);
		if(username != null) {
			config.setProperty(RemotePropertySet.USERNAME, username);
		}
		String password = env.expand(getPassword());
		if(password != null) {
			config.setProperty(RemotePropertySet.PASSWORD, password);
		}
	}

	protected String getTomcatManagerSuffix() {
		if (managerContext.length() != 0) {
			return managerContext;
		}
		return "/manager";
	}

	/**
	 * Create a Tomcat-specific Deployable object from the given file object.
	 * 
	 * @param deployableFile
	 *            The file to deploy.
	 * @return A Tomcat-specific Deployable object.
	 * @see hudson.plugins.deploy.CargoContainerAdapter#createWAR(java.io.File)
	 */
	@Override
	protected WAR createWAR(File deployableFile) {
		return new TomcatWAR(deployableFile.getAbsolutePath());
	}
}

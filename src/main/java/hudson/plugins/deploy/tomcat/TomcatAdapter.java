package hudson.plugins.deploy.tomcat;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Util;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import hudson.plugins.deploy.Messages;
import hudson.plugins.deploy.PasswordProtectedAdapterCargo;
import hudson.util.FormValidation;
import hudson.util.VariableResolver;

import org.codehaus.cargo.container.property.RemotePropertySet;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.tomcat.TomcatWAR;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;

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

    public TomcatAdapter(String url, String password, String userName) {
        super(userName, password);
        this.url = url;
    }

    public void configure(Configuration config) {
        super.configure(config);
        try {
            URL _url = new URL(config.getPropertyValue("expandedUrl") + "/manager");
            config.setProperty(RemotePropertySet.URI,_url.toExternalForm());
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }
    
    @Override
	protected void configure(Configuration config,
			VariableResolver<String> variableResolver, EnvVars envVars) {
    	String expandedUrl = Util.replaceMacro(envVars.expand(this.url), variableResolver);
		config.setProperty("expandedUrl", expandedUrl);
		configure(config);
	}

	/**
     * Create a Tomcat-specific Deployable object from the given file object.
     * @param deployableFile The file to deploy.
     * @return A Tomcat-specific Deployable object.
     * @see hudson.plugins.deploy.CargoContainerAdapter#createWAR(java.io.File)
     */
    @Override
    protected WAR createWAR(File deployableFile) {
        return new TomcatWAR(deployableFile.getAbsolutePath());
    }
    
}

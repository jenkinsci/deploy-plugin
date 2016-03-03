package hudson.plugins.deploy.tomcat;

import java.net.MalformedURLException;
import java.net.URL;

import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.property.RemotePropertySet;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.EnvVars;
import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import hudson.util.VariableResolver;

/**
 * Tomcat 8.x
 *
 * @author Matheus Salmi
 */
public class Tomcat8xAdapter extends TomcatAdapter {

  private static final long serialVersionUID = -8488646105111894289L;

    /**
     * Tomcat 8 support
     * 
     * @param url Tomcat server location (for example: http://localhost:8080)
     * @param password tomcat manager password
     * @param userName tomcat manager username
     */
    @DataBoundConstructor
    public Tomcat8xAdapter(String url, String password, String userName) {
        super(url, password, userName);
    }
    
    public void configure(Configuration config, EnvVars envVars, VariableResolver<String> resolver) {
        super.configure(config, envVars, resolver);
        try {
            URL _url = new URL(expandVariable(envVars, resolver, url) + "/manager/text");
            config.setProperty(RemotePropertySet.URI,_url.toExternalForm());
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Tomcat Cargo containerId
     * @return tomcat8x
     */
    public String getContainerId() {
        return "tomcat8x";
    }
    
    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        public String getDisplayName() {
            return "Tomcat 8.x";
        }
    }
}


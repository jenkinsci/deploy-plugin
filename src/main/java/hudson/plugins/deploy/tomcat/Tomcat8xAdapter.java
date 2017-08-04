package hudson.plugins.deploy.tomcat;

import hudson.EnvVars;
import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import hudson.util.VariableResolver;

import java.net.MalformedURLException;
import java.net.URL;

import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.property.RemotePropertySet;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Tomcat 8.x
 *
 * @author soudmaijer
 */
public class Tomcat8xAdapter extends TomcatAdapter {

    /**
     * Tomcat 8 support
     *
     * @param url Tomcat server location (for example: http://localhost:8080)
     * @param credentialsId tomcat manager username password credentials
     */
    @DataBoundConstructor
    public Tomcat8xAdapter(String url, String credentialsId) {
        super(url, credentialsId);
    }

    public void configure(Configuration config, EnvVars envVars, VariableResolver<String> resolver) {
        super.configure(config, envVars, resolver);
        try {
            URL _url = new URL(expandVariable(envVars, resolver, url) + "/manager/text");
            config.setProperty(RemotePropertySet.URI, _url.toExternalForm());
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

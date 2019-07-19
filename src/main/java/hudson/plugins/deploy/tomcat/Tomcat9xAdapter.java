package hudson.plugins.deploy.tomcat;

import hudson.EnvVars;
import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import hudson.util.VariableResolver;

import java.net.MalformedURLException;
import java.net.URL;

import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.property.RemotePropertySet;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Tomcat 9.x
 *
 * @author m4ndr4ck
 */
public class Tomcat9xAdapter extends TomcatAdapter {

    private static String path = "/manager/text";

    /**
     * Tomcat 9 support
     *
     * @param url Tomcat server location (for example: http://localhost:8080)
     * @param credentialsId tomcat manager username password credentials
     */
    @DataBoundConstructor
    public Tomcat9xAdapter(String url, String credentialsId) {
        super(url, credentialsId, path);
    }

    /**
     * Tomcat Cargo containerId
     * @return tomcat9x
     */
    public String getContainerId() {
        return "tomcat9x";
    }

    @Symbol("tomcat9")
    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        public String getDisplayName() {
            return "Tomcat 9.x";
        }
    }
}

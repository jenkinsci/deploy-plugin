package hudson.plugins.deploy.tomcat;

import hudson.Extension;
import hudson.plugins.deploy.ContainerAdapterDescriptor;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Tomcat 7.x
 *
 * @author soudmaijer
 */
public class Tomcat7xAdapter extends TomcatAdapter {
    private static final long serialVersionUID = -7404114022873678861L;

    private static final String PATH = "/manager/text";

    /**
     * Tomcat 7 support
     *
     * @param url Tomcat server location (for example: http://localhost:8080)
     * @param credentialsId the tomcat user credentials
     */
    @DataBoundConstructor
    public Tomcat7xAdapter(String url, String credentialsId) {
        super(url, credentialsId, PATH);
    }

    @Override
    public String getContainerId() {
        return "tomcat7x";
    }

    @Symbol("tomcat7")
    @Extension
    public static final class DescriptorImpl extends ContainerAdapterDescriptor {
        @Override
        public String getDisplayName() {
            return "Tomcat 7.x";
        }
    }
}

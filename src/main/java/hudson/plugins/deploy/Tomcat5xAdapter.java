package hudson.plugins.deploy;

import hudson.model.Descriptor;
import org.codehaus.cargo.container.property.RemotePropertySet;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.tomcat.TomcatPropertySet;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.DataBoundConstructor;
import net.sf.json.JSONObject;

import java.net.URL;
import java.net.MalformedURLException;

/**
 * Tomcat 5.x.
 * 
 * @author Kohsuke Kawaguchi
 */
public class Tomcat5xAdapter extends DefaultContainerAdapterImpl {

    @Property(RemotePropertySet.USERNAME)
    public final String userName;

    @Property(RemotePropertySet.PASSWORD)
    public final String password;

    /**
     * Top URL of Tomcat.
     */
    public final URL url;

    @DataBoundConstructor
    public Tomcat5xAdapter(String userName, String password, URL url) {
        this.userName = userName;
        this.password = password;
        this.url = url;
    }

    public void configure(Configuration config) {
        super.configure(config);
        try {
            config.setProperty(TomcatPropertySet.MANAGER_URL,new URL(url,"/manager").toExternalForm());
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }

    public String getContainerId() {
        return "tomcat5x";
    }

    public Descriptor<ContainerAdapter> getDescriptor() {
        return DESCRIPTOR;
    }

    public static final Descriptor<ContainerAdapter> DESCRIPTOR = new Descriptor<ContainerAdapter>(Tomcat5xAdapter.class) {
        public String getDisplayName() {
            return "Tomcat 5.x";
        }

        public ContainerAdapter newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return req.bindJSON(clazz,formData);
        }
    };
}

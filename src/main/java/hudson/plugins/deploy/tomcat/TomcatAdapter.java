package hudson.plugins.deploy.tomcat;

import hudson.plugins.deploy.PasswordProtectedAdapterCargo;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.tomcat.TomcatPropertySet;
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

    public TomcatAdapter(String url, String password, String userName) {
        super(userName, password);
        this.url = url;
    }

    public void configure(Configuration config) {
        super.configure(config);
        try {
            URL _url = new URL(url + "/manager");
            config.setProperty(TomcatPropertySet.MANAGER_URL,_url.toExternalForm());
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }
    
    /**
     * Create a Tomcat-specific Deployable object from the given file object.
     * @param deployableFile The file to deploy.
     * @return A Tomcat-specific Deployable object.
     * @see hudson.plugins.deploy.CargoContainerAdapter#createDeployable(java.io.File)
     */
    @Override
    protected Deployable createDeployable(File deployableFile) {
        return new TomcatWAR(deployableFile.getAbsolutePath());
    }
}

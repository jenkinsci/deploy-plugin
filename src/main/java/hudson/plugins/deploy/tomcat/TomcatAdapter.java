package hudson.plugins.deploy.tomcat;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.deploy.PasswordProtectedAdapterCargo;
import hudson.util.VariableResolver;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.RemotePropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.codehaus.cargo.container.tomcat.TomcatWAR;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Base class for Tomcat adapters.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class TomcatAdapter extends PasswordProtectedAdapterCargo {
    private static final long serialVersionUID = 4326184683787248367L;

    /**
     * Top URL of Tomcat.
     */
    public final String url;

    /**
     * Alternative context that override context defined in plugin main configuration
     */
    public final String alternativeDeploymentContext;

    private final String path;

    public TomcatAdapter(
        String url, String credentialsId, String alternativeDeploymentContext, String path) {
        super(credentialsId);
        this.url = url;
        this.path = path;
        this.alternativeDeploymentContext = alternativeDeploymentContext;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public String getPath() {
        return path;
    }

    @Override
    public void configure(Configuration config, EnvVars envVars, VariableResolver<String> resolver) {
        super.configure(config, envVars, resolver);
        try {
            if (StringUtils.isNotBlank(this.path)) {
                // set remote URI directly if alternative manager context path is specified
                URL managerUrl = new URL(expandVariable(envVars, resolver, this.url) + expandVariable(envVars, resolver, this.path));
                config.setProperty(RemotePropertySet.URI, managerUrl.toExternalForm());
            } else {
                // overwrite default values with current URL
                URL baseUrl = new URL(expandVariable(envVars, resolver, this.url));
                config.setProperty(GeneralPropertySet.PROTOCOL, baseUrl.getProtocol());
                config.setProperty(GeneralPropertySet.HOSTNAME, baseUrl.getHost());
                config.setProperty(ServletPropertySet.PORT, String.valueOf(baseUrl.getPort()));
            }
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
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

    @Override
    public void redeployFile(
        FilePath war, String aContextPath, Run<?, ?> run, Launcher launcher, TaskListener listener)
        throws IOException, InterruptedException {
        String finalContextPath =
            StringUtils.defaultIfBlank(this.alternativeDeploymentContext, aContextPath);
        super.redeployFile(war, finalContextPath, run, launcher, listener);
    }
}

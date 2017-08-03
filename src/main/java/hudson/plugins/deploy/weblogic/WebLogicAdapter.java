package hudson.plugins.deploy.weblogic;

import hudson.EnvVars;
import hudson.plugins.deploy.ContainerAdapterDescriptor;
import hudson.plugins.deploy.DefaultCargoContainerAdapterImpl;
import hudson.plugins.deploy.PasswordProtectedAdapterCargo;
import hudson.util.FormValidation;
import hudson.util.VariableResolver;

import java.io.File;

import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.generic.ContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.DoNotUse;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * Base class for WebLogic support.
 * 
 * @author Kohsuke Kawaguchi
 */
@Restricted(NoExternalUse.class)
public abstract class WebLogicAdapter extends PasswordProtectedAdapterCargo {

    // @Property(WebLogicPropertySet.SERVER)
    public final String server;

    // @Property(ServletPropertySet.PORT)
    public final Integer port;

    public final String home;

    //@DataBoundConstructor
    public WebLogicAdapter(String home, String credentialsId, String server, Integer port) {
        super(credentialsId);
        this.home = home;
        this.server = server;
        this.port = port;
    }

    @Override
    protected Container getContainer(ConfigurationFactory configFactory, ContainerFactory containerFactory, String id, EnvVars envVars, VariableResolver<String> resolver) {
        Configuration config = configFactory.createConfiguration(id, ContainerType.INSTALLED, ConfigurationType.EXISTING, home);
        configure(config, envVars, resolver);
        return containerFactory.createContainer(id, ContainerType.INSTALLED, config);
    }

    public static abstract class WebLogicAdapterDescriptor extends ContainerAdapterDescriptor {
        public FormValidation doCheckHome(@QueryParameter String value) {
            if(new File(new File(value),"autodeploy").isDirectory())
                return FormValidation.ok();
            return FormValidation.warning(value+" doesn't appear to have the autodeploy subdirectory");
        }
    }
}


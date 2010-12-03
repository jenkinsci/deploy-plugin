package hudson.plugins.deploy.weblogic;

import hudson.plugins.deploy.ContainerAdapterDescriptor;
import hudson.plugins.deploy.DefaultCargoContainerAdapterImpl;
import hudson.util.FormValidation;
import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.generic.ContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.io.File;

/**
 * Base class for WebLogic support.
 * 
 * @author Kohsuke Kawaguchi
 */
public abstract class WebLogicAdapter extends DefaultCargoContainerAdapterImpl {
//    @Property(WebLogicPropertySet.ADMIN_USER)
    public final String userName;

//    @Property(WebLogicPropertySet.ADMIN_PWD)
    public final String password;

//    @Property(WebLogicPropertySet.SERVER)
    public final String server;

//    @Property(ServletPropertySet.PORT)
    public final Integer port;

    public final String home;

    @DataBoundConstructor
    public WebLogicAdapter(String home, String userName, String password, String server, Integer port) {
        this.home = home;
        this.userName = userName;
        this.password = password;
        this.server = server;
        this.port = port;
    }

    @Override
    protected Container getContainer(ConfigurationFactory configFactory, ContainerFactory containerFactory, String id) {
        Configuration config = configFactory.createConfiguration(id, ContainerType.INSTALLED, ConfigurationType.EXISTING, home);
        configure(config);
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


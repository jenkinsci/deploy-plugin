package hudson.plugins.deploy.glassfish;

import hudson.EnvVars;
import hudson.plugins.deploy.PasswordProtectedAdapterCargo;
import hudson.util.VariableResolver;

import org.apache.commons.lang.StringUtils;
import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.glassfish.GlassFishPropertySet;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.RemotePropertySet;
import org.codehaus.cargo.container.spi.AbstractInstalledLocalContainer;
import org.codehaus.cargo.container.spi.AbstractRemoteContainer;
import org.codehaus.cargo.container.spi.configuration.AbstractRuntimeConfiguration;
import org.codehaus.cargo.container.spi.configuration.AbstractStandaloneLocalConfiguration;
import org.codehaus.cargo.generic.ContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;

/**
 * GlassFishAdapter, configures the cargo GlassFish container.
 */
public abstract class GlassFishAdapter extends PasswordProtectedAdapterCargo {

    /**
     * Property home is required for GlassFish local containers.
     */
    public final String home;
    @Property(GlassFishPropertySet.ADMIN_PORT)
    public final String adminPort;
    /**
     * Property hostname is required for GlassFish remote containers. (including "localhost")
     * <br/>
     * If this property is set, the property GlassFishAdaper.home will be ignored
     */
    @Property(GeneralPropertySet.HOSTNAME)
    public final String hostname;

    /**
     * GlassFishAdapter, supports local glassfish deployments.
     *
     * @param home location of the GlassFish installation
     * @param credentialsId the id of the credential
     * @param adminPort admin server port
     * @param hostname hostname
     */
    protected GlassFishAdapter(String home, String credentialsId, String adminPort, String hostname) {
        super(credentialsId);
        this.home = home;
        this.adminPort = adminPort;
        this.hostname = hostname;
    }

    @Override
    public String getUrl() {
        return "http://" + StringUtils.defaultIfBlank(hostname, "localhost") + ':' + adminPort;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Container getContainer(ConfigurationFactory configFactory, ContainerFactory containerFactory, String id, EnvVars envVars, VariableResolver<String> resolver) {

        if (hostname != null) {


            AbstractRuntimeConfiguration config = (AbstractRuntimeConfiguration) configFactory.createConfiguration(id, ContainerType.REMOTE, ConfigurationType.RUNTIME);
            configure(config, envVars, resolver);
            config.setProperty(RemotePropertySet.PASSWORD, getPassword());

            AbstractRemoteContainer container = (AbstractRemoteContainer) containerFactory.createContainer(id, ContainerType.REMOTE, config);

            return container;


        } else {
            AbstractStandaloneLocalConfiguration config = (AbstractStandaloneLocalConfiguration) configFactory.createConfiguration(id, ContainerType.INSTALLED, ConfigurationType.STANDALONE, home);
            configure(config, envVars, resolver);

            AbstractInstalledLocalContainer container = (AbstractInstalledLocalContainer) containerFactory.createContainer(id, ContainerType.INSTALLED, config);

            // Explicitly sets the home on the LocalContainer:
            container.setHome(expandVariable(envVars, resolver, home));

            return container;
        }

    }
}

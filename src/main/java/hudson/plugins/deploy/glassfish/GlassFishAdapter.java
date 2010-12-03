package hudson.plugins.deploy.glassfish;

import hudson.plugins.deploy.PasswordProtectedAdapterCargo;
import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.glassfish.GlassFishPropertySet;
import org.codehaus.cargo.container.spi.AbstractInstalledLocalContainer;
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
    public final Integer adminPort;

    /**
     * GlassFishAdapter, supports local glassfish deployments.
     *
     * @param home location of the GlassFish installation
     * @param password admin password
     * @param userName admin username
     * @param adminPort admin server port
     */
    protected GlassFishAdapter(String home, String password, String userName, Integer adminPort) {
        super(userName, password);        
        this.home = home;
        this.adminPort = adminPort;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Container getContainer(ConfigurationFactory configFactory, ContainerFactory containerFactory, String id) {

        AbstractStandaloneLocalConfiguration config = (AbstractStandaloneLocalConfiguration)configFactory.createConfiguration(id, ContainerType.INSTALLED, ConfigurationType.STANDALONE, home);
        configure(config);

        AbstractInstalledLocalContainer container = (AbstractInstalledLocalContainer)containerFactory.createContainer(id, ContainerType.INSTALLED, config);

        // Explicitly sets the home on the LocalContainer:
        container.setHome(home);

        return container;
    }
}

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
@   Property(GeneralPropertySet.HOSTNAME) 
    public final String hostname;
	
    @Property(GlassFishPropertySet.ADMIN_PORT)
    public final Integer adminPort;

    /**
     * GlassFishAdapter, supports local glassfish deployments.
     *
     * @param hostname hostname of the GlassFish installation
     * @param password admin password
     * @param userName admin username
     * @param adminPort admin server port
     */
    protected GlassFishAdapter(String hostname, String password, String userName, Integer adminPort) {
        super(userName, password);
        this.hostname = hostname;
        this.adminPort = adminPort;
    }
}

package hudson.plugins.deploy;

import hudson.FilePath;
import hudson.Launcher;
import hudson.FilePath.FileCallable;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.deployer.Deployer;
import org.codehaus.cargo.generic.ContainerFactory;
import org.codehaus.cargo.generic.DefaultContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.codehaus.cargo.generic.deployer.DefaultDeployerFactory;
import org.codehaus.cargo.generic.deployer.DeployerFactory;

/**
 * Provides container-specific glue code.
 *
 * <p>
 * To support remote operations as an inner class, marking the class as serializable.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class CargoContainerAdapter implements ContainerAdapter, Serializable {
    /**
     * Returns the container ID used by Cargo. 
     */
    protected abstract String getContainerId();

    /**
     * Fills in the {@link Configuration} object.
     */
    protected abstract void configure(Configuration config);
    
    protected Container getContainer(ConfigurationFactory configFactory, ContainerFactory containerFactory, String id) {    	
        Configuration config = configFactory.createConfiguration(id, ContainerType.REMOTE, ConfigurationType.RUNTIME);
        configure(config);
        return containerFactory.createContainer(id, ContainerType.REMOTE, config);
    }
    
    protected void deploy(DeployerFactory deployerFactory, final BuildListener listener, Container container, File f) {
    	Deployer deployer = deployerFactory.createDeployer(container);

        listener.getLogger().println("Deploying "+f);
        deployer.setLogger(new LoggerImpl(listener.getLogger()));
        deployer.redeploy(new WAR(f.getAbsolutePath()));
    }

    public boolean redeploy(FilePath war, AbstractBuild<?, ?> build, Launcher launcher, final BuildListener listener) throws IOException, InterruptedException {
        return war.act(new FileCallable<Boolean>() {
            public Boolean invoke(File f, VirtualChannel channel) throws IOException {
                if(!f.exists()) {
                    listener.error(Messages.DeployPublisher_NoSuchFile(f));
                    return true;
                }                
                final ConfigurationFactory configFactory = new DefaultConfigurationFactory();
                final ContainerFactory containerFactory = new DefaultContainerFactory();
                final DeployerFactory deployerFactory = new DefaultDeployerFactory();

                Container container = getContainer(configFactory, containerFactory, getContainerId());
                
                deploy(deployerFactory, listener, container, f);
                return true;
            }
        });
    }
}

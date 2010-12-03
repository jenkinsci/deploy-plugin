package hudson.plugins.deploy;

import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.remoting.VirtualChannel;
import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.deployer.Deployer;
import org.codehaus.cargo.generic.ContainerFactory;
import org.codehaus.cargo.generic.DefaultContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.codehaus.cargo.generic.deployer.DefaultDeployerFactory;
import org.codehaus.cargo.generic.deployer.DeployerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * Provides container-specific glue code.
 *
 * <p>
 * To support remote operations as an inner class, marking the class as serializable.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class CargoContainerAdapter extends ContainerAdapter implements Serializable {
    /**
     * Returns the container ID used by Cargo.
     * @return
     */
    protected abstract String getContainerId();

    /**
     * Fills in the {@link Configuration} object.
     * @param config
     */
    protected abstract void configure(Configuration config);

    protected Container getContainer(ConfigurationFactory configFactory, ContainerFactory containerFactory, String id) {
        Configuration config = configFactory.createConfiguration(id, ContainerType.REMOTE, ConfigurationType.RUNTIME);
        configure(config);
        return containerFactory.createContainer(id, ContainerType.REMOTE, config);
    }
    
    protected void deploy(DeployerFactory deployerFactory, final BuildListener listener, Container container, File f) {
        Deployer deployer = deployerFactory.createDeployer(container);

        listener.getLogger().println("Deploying "+f+" to container "+container.getName());

        deployer.setLogger(new LoggerImpl(listener.getLogger()));
        deployer.redeploy(createDeployable(f));
    }

    /**
     * Creates a Deployable object from the given file object.
     * @param deployableFile The deployable file to create the Deployable from.
     * @return A Deployable object.
     */
    protected Deployable createDeployable(File deployableFile) {
        return new WAR(deployableFile.getAbsolutePath());
    }

    public boolean redeploy(FilePath war, AbstractBuild<?, ?> build, Launcher launcher, final BuildListener listener) throws IOException, InterruptedException {
        return war.act(new FileCallable<Boolean>() {
            public Boolean invoke(File f, VirtualChannel channel) throws IOException {
                if(!f.exists()) {
                    listener.error(Messages.DeployPublisher_NoSuchFile(f));
                    return true;
                }
                ClassLoader cl = getClass().getClassLoader();
                final ConfigurationFactory configFactory = new DefaultConfigurationFactory(cl);
                final ContainerFactory containerFactory = new DefaultContainerFactory(cl);
                final DeployerFactory deployerFactory = new DefaultDeployerFactory(cl);

                Container container = getContainer(configFactory, containerFactory, getContainerId());
                
                deploy(deployerFactory, listener, container, f);
                return true;
            }
        });
    }
}

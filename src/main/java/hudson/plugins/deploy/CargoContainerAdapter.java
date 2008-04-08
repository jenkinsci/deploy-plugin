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

/**
 * Provides container-specific glue code.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class CargoContainerAdapter implements ContainerAdapter {
    /**
     * Returns the container ID used by Cargo. 
     */
    protected abstract String getContainerId();

    /**
     * Fills in the {@link Configuration} object.
     */
    protected abstract void configure(Configuration config);

    public boolean redeploy(FilePath war, AbstractBuild<?, ?> build, Launcher launcher, final BuildListener listener) throws IOException, InterruptedException {
        return war.act(new FileCallable<Boolean>() {
            public Boolean invoke(File f, VirtualChannel channel) throws IOException {
                if(!f.exists()) {
                    listener.error(Messages.DeployPublisher_NoSuchFile(f));
                    return true;
                }
                ConfigurationFactory configFactory = new DefaultConfigurationFactory();
                ContainerFactory containerFactory = new DefaultContainerFactory();
                DeployerFactory deployerFactory = new DefaultDeployerFactory();

                String id = getContainerId();
                Configuration config = configFactory.createConfiguration(id, ContainerType.REMOTE, ConfigurationType.RUNTIME);
                configure(config);
                Container container = containerFactory.createContainer(id, ContainerType.REMOTE, config);
                Deployer deployer = deployerFactory.createDeployer(container);

                listener.getLogger().println("Deploying "+f);
                deployer.setLogger(new LoggerImpl(listener.getLogger()));
                deployer.redeploy(new WAR(f.getAbsolutePath()));

                return true;
            }
        });
    }
}

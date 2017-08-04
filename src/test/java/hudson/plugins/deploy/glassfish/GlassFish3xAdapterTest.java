package hudson.plugins.deploy.glassfish;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.domains.Domain;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.StreamBuildListener;
import hudson.model.FreeStyleProject;
import hudson.model.Node;
import hudson.slaves.EnvironmentVariablesNodeProperty;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.glassfish.GlassFish3xInstalledLocalContainer;
import org.codehaus.cargo.container.glassfish.GlassFishPropertySet;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.RemotePropertySet;
import org.codehaus.cargo.generic.ContainerFactory;
import org.codehaus.cargo.generic.DefaultContainerFactory;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * @author soudmaijer
 */
public class GlassFish3xAdapterTest {

    private GlassFish3xAdapter adapter;
    private GlassFish3xAdapter remoteAdapter;
    private static final String home = "D:/development/server/glassfishv3";
    private static final String homeVariable = "home";
    private static final String username = "admin";
    private static final String usernameVariable = "uname";
    private static final String password = "";
    private static final String port = "1234";
    private static final String hostname = "localhost";
    private static final String hostnameVariable = "hostname";
    private static final String adminPort = "4848";
    private static final String adminPortVariable = "adminPort";
    private static final String variableStart = "${";
    private static final String variableEnd = "}";
    
    @Rule public JenkinsRule jenkinsRule = new JenkinsRule();

    @Before
    public void setup() throws Exception {
        UsernamePasswordCredentialsImpl c = new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL, "test", "sample", username, password);
        CredentialsProvider.lookupStores(jenkinsRule.jenkins).iterator().next().addCredentials(Domain.global(), c);

        adapter = new GlassFish3xAdapter(home, c.getId(), port, null);
        adapter.loadCredentials(/* temp project to avoid npe */ jenkinsRule.createFreeStyleProject());
        remoteAdapter = new GlassFish3xAdapter(null, c.getId(), adminPort, hostname);
        remoteAdapter.loadCredentials(/* temp project to avoid npe */ jenkinsRule.createFreeStyleProject());
    }

    @Test
    public void testContainerId() {
        Assert.assertEquals(adapter.getContainerId(), new GlassFish3xInstalledLocalContainer(null).getId());
    }

    @Test
    public void testConfigure() throws IOException, InterruptedException, ExecutionException {
        Assert.assertEquals(home, adapter.home);
        Assert.assertEquals(port, adapter.adminPort);
        Assert.assertEquals(username, adapter.getUsername());
        Assert.assertEquals(password, adapter.getPassword());
        Assert.assertEquals(null, adapter.hostname);

        ConfigurationFactory configFactory = new DefaultConfigurationFactory();
        ContainerFactory containerFactory = new DefaultContainerFactory();
        FreeStyleProject project = jenkinsRule.createFreeStyleProject();
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        BuildListener listener = new StreamBuildListener(new ByteArrayOutputStream());

        Container container = adapter.getContainer(configFactory, containerFactory, adapter.getContainerId(), build.getEnvironment(listener), build.getBuildVariableResolver());
        Assert.assertNotNull(container);
    }

    @Test
    public void testConfigureRemote() throws IOException, InterruptedException, ExecutionException {
        Assert.assertEquals(null, remoteAdapter.home);
        Assert.assertEquals(adminPort, remoteAdapter.adminPort);
        Assert.assertEquals(username, remoteAdapter.getUsername());
        Assert.assertEquals(password, remoteAdapter.getPassword());
        Assert.assertEquals(null, adapter.hostname);

        ConfigurationFactory configFactory = new DefaultConfigurationFactory();
        ContainerFactory containerFactory = new DefaultContainerFactory();
        FreeStyleProject project = jenkinsRule.createFreeStyleProject();
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        BuildListener listener = new StreamBuildListener(new ByteArrayOutputStream());

        Container container = remoteAdapter.getContainer(configFactory, containerFactory, remoteAdapter.getContainerId(), build.getEnvironment(listener), build.getBuildVariableResolver());
        Assert.assertNotNull(container);
    }

    /**
     * This test only runs in your local environment
     * @throws IOException
     * @throws InterruptedException
     */
    //@Test
    public void testDeploy() throws IOException, InterruptedException {
        
        adapter.redeploy(new FilePath(new File("src/test/simple.war")), "contextPath", null, null, new StreamBuildListener(System.out));
    }
    
    //@Test
    public void testRemoteDeploy() throws IOException, InterruptedException {
       

        remoteAdapter.redeploy(new FilePath(new File("src/test/simple.war")), "contextPath", null, null, new StreamBuildListener(System.out));
    }
    
    @Test
    public void testVariables() throws Exception {
        Node n = jenkinsRule.createSlave();
    	EnvironmentVariablesNodeProperty property = new EnvironmentVariablesNodeProperty();
    	EnvVars envVars = property.getEnvVars();
    	envVars.put(homeVariable, home);
    	envVars.put(usernameVariable, username);
    	envVars.put(adminPortVariable, adminPort);
    	envVars.put(hostnameVariable, hostname);
    	jenkinsRule.jenkins.getGlobalNodeProperties().add(property);

        FreeStyleProject project = jenkinsRule.createFreeStyleProject();
        project.setAssignedNode(n);
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        BuildListener listener = new StreamBuildListener(new ByteArrayOutputStream());


        UsernamePasswordCredentialsImpl c = new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL, null,
                "", getVariable(usernameVariable), password);
        CredentialsProvider.lookupStores(jenkinsRule.jenkins).iterator().next().addCredentials(Domain.global(), c);

        adapter = new  GlassFish3xAdapter(getVariable(homeVariable), c.getId(), getVariable(adminPortVariable), null);
        Configuration config = new DefaultConfigurationFactory().createConfiguration(adapter.getContainerId(), ContainerType.REMOTE, ConfigurationType.RUNTIME);
        adapter.migrateCredentials(Collections.EMPTY_LIST);
        adapter.loadCredentials(project);
        adapter.configure(config, project.getEnvironment(n, listener), build.getBuildVariableResolver());

        Assert.assertEquals(username, config.getPropertyValue(RemotePropertySet.USERNAME));
        Assert.assertEquals(adminPort, config.getPropertyValue(GlassFishPropertySet.ADMIN_PORT));

        remoteAdapter = new  GlassFish3xAdapter(null, c.getId(), getVariable(adminPortVariable), getVariable(hostnameVariable));
        config = new DefaultConfigurationFactory().createConfiguration(adapter.getContainerId(), ContainerType.REMOTE, ConfigurationType.RUNTIME);
        remoteAdapter.migrateCredentials(Collections.EMPTY_LIST);
        remoteAdapter.loadCredentials(project);
        remoteAdapter.configure(config, project.getEnvironment(n, listener), build.getBuildVariableResolver());

        Assert.assertEquals(username, config.getPropertyValue(RemotePropertySet.USERNAME));
        Assert.assertEquals(adminPort, config.getPropertyValue(GlassFishPropertySet.ADMIN_PORT));
        Assert.assertEquals(hostname, config.getPropertyValue(GeneralPropertySet.HOSTNAME));
    }

    private String getVariable(String variableName) {
    	return variableStart + variableName + variableEnd;
    }
}

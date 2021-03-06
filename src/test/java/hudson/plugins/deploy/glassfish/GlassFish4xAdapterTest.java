package hudson.plugins.deploy.glassfish;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.Domain;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.*;
import hudson.plugins.deploy.ContainerAdapter;
import hudson.plugins.deploy.DeployPublisher;
import hudson.slaves.EnvironmentVariablesNodeProperty;

import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.glassfish.GlassFish4xInstalledLocalContainer;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

/**
 * @author soudmaijer
 */
public class GlassFish4xAdapterTest {

    private GlassFish4xAdapter adapter;
    private GlassFish4xAdapter remoteAdapter;
    private static final String home = "D:/development/server/glassfishv4";
    private static final String homeVariable = "home";
    private static final String username = "admin";
    private static final String usernameVariable = "uname";
    private static final String password = "";
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

        adapter = new GlassFish4xAdapter(home, c.getId(), null, null);
        adapter.loadCredentials(/* temp project to avoid npe */ jenkinsRule.createFreeStyleProject());
        remoteAdapter = new GlassFish4xAdapter(null, c.getId(), adminPort, hostname);
        remoteAdapter.loadCredentials(/* temp project to avoid npe */ jenkinsRule.createFreeStyleProject());
    }

    @Test
    public void testContainerId() {
        Assert.assertEquals(adapter.getContainerId(), new GlassFish4xInstalledLocalContainer(null).getId());
    }

    @Test
    public void testConfigure() throws IOException, InterruptedException, ExecutionException {
        Assert.assertEquals(home, adapter.home);
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

    // This test only runs in your local environment
    //@Test
    public void testDeploy() throws Exception {
        FreeStyleProject project = this.jenkinsRule.createFreeStyleProject();
        FreeStyleBuild freeStyleBuild = project.scheduleBuild2(0).get(); // touch workspace

        FilePath workspace = freeStyleBuild.getWorkspace();
        new FilePath(new File("src/test/simple.war")).copyTo(workspace.child("simple.war"));

        ArrayList<ContainerAdapter> adapters = new ArrayList<ContainerAdapter>();
        adapters.add(this.adapter);
        project.getPublishersList().add(new DeployPublisher(adapters, "simple.war"));

        Run<?, ?> run = project.scheduleBuild2(0).get();
        this.jenkinsRule.assertBuildStatus(Result.SUCCESS, run);
    }

    // This test only runs in your remote environment
    //@Test
    public void testRemoteDeploy() throws Exception {
        FreeStyleProject project = this.jenkinsRule.createFreeStyleProject();
        FreeStyleBuild freeStyleBuild = project.scheduleBuild2(0).get(); // touch workspace

        FilePath workspace = freeStyleBuild.getWorkspace();
        new FilePath(new File("src/test/simple.war")).copyTo(workspace.child("simple.war"));

        ArrayList<ContainerAdapter> adapters = new ArrayList<ContainerAdapter>();
        adapters.add(this.remoteAdapter);
        project.getPublishersList().add(new DeployPublisher(adapters, "simple.war"));

        Run<?, ?> run = project.scheduleBuild2(0).get();
        this.jenkinsRule.assertBuildStatus(Result.SUCCESS, run);
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

        adapter = new  GlassFish4xAdapter(getVariable(homeVariable), c.getId(), getVariable(adminPortVariable), null);
        Configuration config = new DefaultConfigurationFactory().createConfiguration(adapter.getContainerId(), ContainerType.REMOTE, ConfigurationType.RUNTIME);
        adapter.migrateCredentials(Collections.<StandardUsernamePasswordCredentials>emptyList());
        adapter.loadCredentials(project);
        adapter.configure(config, project.getEnvironment(n, listener), build.getBuildVariableResolver());

        Assert.assertEquals(username, config.getPropertyValue(RemotePropertySet.USERNAME));
        Assert.assertEquals(adminPort, config.getPropertyValue(GlassFishPropertySet.ADMIN_PORT));

        remoteAdapter = new  GlassFish4xAdapter(null, c.getId(), getVariable(adminPortVariable), getVariable(hostnameVariable));
        config = new DefaultConfigurationFactory().createConfiguration(remoteAdapter.getContainerId(), ContainerType.REMOTE, ConfigurationType.RUNTIME);
        remoteAdapter.migrateCredentials(Collections.<StandardUsernamePasswordCredentials>emptyList());
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

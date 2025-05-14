package hudson.plugins.deploy.tomcat;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.domains.Domain;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import hudson.EnvVars;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Node;
import hudson.model.StreamBuildListener;
import hudson.slaves.EnvironmentVariablesNodeProperty;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.property.RemotePropertySet;
import org.codehaus.cargo.container.tomcat.Tomcat7xRemoteContainer;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * @author soudmaijer
 */
@WithJenkins
class Tomcat7xAdapterTest {

    private Tomcat7xAdapter adapter;
    private static final String url = "http://localhost:8080";
    private static final String managerContextPath = "/foo-manager/text";
    private static final String configuredUrl = url + managerContextPath;
    private static final String urlVariable = "URL";
    private static final String username = "usernm";
    private static final String usernameVariable = "user";
    private static final String password = "password";
    private static final String alternativeContext = "context";
    private static final String alternativeContextVariable = "altcontext";
    private static final String variableStart = "${";
    private static final String variableEnd = "}";

    private JenkinsRule jenkinsRule;

    @BeforeEach
    void setUp(JenkinsRule rule) throws Exception {
        jenkinsRule = rule;

        UsernamePasswordCredentialsImpl c = new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL, "test", "sample", username, password);
        CredentialsProvider.lookupStores(jenkinsRule.jenkins).iterator().next().addCredentials(Domain.global(), c);

        adapter = new  Tomcat7xAdapter(url, c.getId(), null, null);
        adapter.loadCredentials(/* temp project to avoid npe */ jenkinsRule.createFreeStyleProject());
    }

    @Test
    void testContainerId() {
        assertEquals(adapter.getContainerId(), new Tomcat7xRemoteContainer(null).getId());
    }

    @Test
    void testConfigure() {
        assertEquals(url, adapter.url);
        assertEquals(username, adapter.getUsername());
        assertEquals(password, adapter.getPassword());
    }

    @Test
    void testVariables() throws Exception {
        Node n = jenkinsRule.createSlave();
    	EnvironmentVariablesNodeProperty property = new EnvironmentVariablesNodeProperty();
    	EnvVars envVars = property.getEnvVars();
    	envVars.put(urlVariable, url);
    	envVars.put(usernameVariable, username);
    	envVars.put(alternativeContextVariable, alternativeContext);
    	jenkinsRule.jenkins.getGlobalNodeProperties().add(property);

        FreeStyleProject project = jenkinsRule.createFreeStyleProject();
        project.setAssignedNode(n);
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        BuildListener listener = new StreamBuildListener(new ByteArrayOutputStream(), StandardCharsets.UTF_8);


        UsernamePasswordCredentialsImpl c = new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL, null,
                "", getVariable(usernameVariable), password);
        CredentialsProvider.lookupStores(jenkinsRule.jenkins).iterator().next().addCredentials(Domain.global(), c);

        adapter =
            new Tomcat7xAdapter(
                getVariable(urlVariable), c.getId(), getVariable(alternativeContextVariable), managerContextPath);
        Configuration config = new DefaultConfigurationFactory().createConfiguration(adapter.getContainerId(), ContainerType.REMOTE, ConfigurationType.RUNTIME);
        adapter.migrateCredentials(Collections.emptyList());
        adapter.loadCredentials(project);
        adapter.configure(config, project.getEnvironment(n, listener), build.getBuildVariableResolver());

        assertEquals(configuredUrl, config.getPropertyValue(RemotePropertySet.URI));
        assertEquals(username, config.getPropertyValue(RemotePropertySet.USERNAME));
    }

    private String getVariable(String variableName) {
    	return variableStart + variableName + variableEnd;
    }
}

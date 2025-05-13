package hudson.plugins.deploy;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import hudson.model.AbstractProject;
import hudson.plugins.deploy.glassfish.GlassFish3xAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;
import org.jvnet.hudson.test.recipes.LocalData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Confirms that old adapters are serialized and deserialized correctly
 *
 * @author Alex Johnson
 */
@WithJenkins
class PasswordProtectedAdapterCargoTest {

    // these need to match what is configured in the @LocalData resource .zip
    private final String username0 = "admin";
    private final String password0 = "schoolbus";
    private final String username1 = "manager";
    private final String password1 = "lighthouse";

    private JenkinsRule j;

    @BeforeEach
    void setUp(JenkinsRule rule) {
        j = rule;
    }

    @Test
    @LocalData
    void testMigrateOldPLainPassword() {
        AbstractProject<?, ?> project = j.getInstance().getItemByFullName("plainPassword", AbstractProject.class);
        DeployPublisher deployer = project.getPublishersList().get(DeployPublisher.class);

        GlassFish3xAdapter adapter = (GlassFish3xAdapter) deployer.getAdapters().get(0);
        adapter.loadCredentials(project);

        // adapter returns correct username and password
        assertEquals(username0, adapter.getUsername());
        assertEquals(password0, adapter.getPassword());
    }

    @Test
    @LocalData
    void testMigrateOldScrambledPassword() {
        AbstractProject<?, ?> project = j.getInstance().getItemByFullName("scrambledPassword", AbstractProject.class);
        DeployPublisher deployer = project.getPublishersList().get(DeployPublisher.class);

        GlassFish3xAdapter adapter = (GlassFish3xAdapter) deployer.getAdapters().get(0);
        adapter.loadCredentials(project);

        // adapter returns correct username and password
        assertEquals(username1, adapter.getUsername());
        assertEquals(password1, adapter.getPassword());
    }

    @Test
    @LocalData
    void testMatchGeneratedCredentials() throws Exception {
        // create 2 projects and first build
        AbstractProject<?, ?> project0 = j.getInstance().getItemByFullName("scrambledPassword", AbstractProject.class);
        project0.scheduleBuild2(0).get();
        AbstractProject<?, ?> project1 = j.getInstance().getItemByFullName("samePassword", AbstractProject.class);
        project1.scheduleBuild2(0).get();
        AbstractProject<?, ?> project2 = j.getInstance().getItemByFullName("plainPassword", AbstractProject.class);
        project2.scheduleBuild2(0).get();

        StandardUsernamePasswordCredentials cred0 = extractCredentials(project0);
        StandardUsernamePasswordCredentials cred1 = extractCredentials(project1);
        StandardUsernamePasswordCredentials cred2 = extractCredentials(project2);

        assertEquals(cred0, cred1);
        assertNotEquals(cred0, cred2);
        assertNotEquals(cred1, cred2);
    }

    private StandardUsernamePasswordCredentials extractCredentials(AbstractProject<?, ?> project) {
        DeployPublisher publisher = project.getPublishersList().get(DeployPublisher.class);
        String id = ((PasswordProtectedAdapterCargo) publisher.getAdapters().get(0)).getCredentialsId();
        return CredentialsProvider.findCredentialById(id,
                StandardUsernamePasswordCredentials.class, project.getFirstBuild());
    }
}

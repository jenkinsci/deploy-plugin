package hudson.plugins.deploy;

import hudson.model.Label;
import hudson.model.Result;
import hudson.model.labels.LabelOperatorPrecedence;
import hudson.model.labels.LabelVisitor;
import hudson.util.VariableResolver;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests pipeline compatibility. Since there ia no Builder that sets the build status all of these tests
 * will ultimately result in a no-op.
 */
public class PipelineSyntaxTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    private String getFullScript (String func) {
        return "node {\n" +
                    "writeFile(file: 'readme.txt', text: 'this creates a workspace if one doesnt already exist')\n" +
                    func +
                "}";
    }

    @Test
    public void testNoAdapterDeploy() throws Exception {
        WorkflowJob p = j.getInstance().createProject(WorkflowJob.class, "DryRunTest");
        p.setDefinition(new CpsFlowDefinition(
                getFullScript("deploy(war: 'target/app.war', contextPath: 'app', onFailure: false)"),
                false));
        WorkflowRun b = j.assertBuildStatusSuccess(p.scheduleBuild2(0));
    }

    @Test
    public void testMockAdapterDeploy() throws Exception {
        WorkflowJob p = j.getInstance().createProject(WorkflowJob.class, "MockTest");
        p.setDefinition(new CpsFlowDefinition(
                getFullScript("deploy(adapters: [workflowAdapter()], war: 'target/app.war', contextPath: 'app')"),
                false));
        WorkflowRun b = j.assertBuildStatusSuccess(p.scheduleBuild2(0));
    }

    @Test
    public void testMockAdaptersDeploy() throws Exception {
        WorkflowJob p = j.getInstance().createProject(WorkflowJob.class, "MockTest");
        p.setDefinition(new CpsFlowDefinition(
                getFullScript("deploy(adapters: [workflowAdapter(), workflowAdapter(), workflowAdapter()], war: 'target/app.war', contextPath: 'app')"),
                false));
        WorkflowRun b = j.assertBuildStatusSuccess(p.scheduleBuild2(0));
    }

    @Test
    public void testGlassFishAdapter() throws Exception {
        WorkflowJob p = j.getInstance().createProject(WorkflowJob.class, "GlassfishTest");
        p.setDefinition(new CpsFlowDefinition(
                getFullScript(
                "def gf2 = glassfish2( " +
                    "home: 'FAKE', " +
                    "credentialsId: 'FAKE'" +
                    "adminPort: '1234', " +
                    "hostname: 'localhost') \n" +
                "def gf3 = glassfish3( " +
                    "home: 'FAKE', " +
                    "credentialsId: 'FAKE'" +
                    "adminPort: '1234', " +
                    "hostname: 'localhost') \n" +
                "deploy(adapters: [gf2, gf3], war: 'target/app.war', contextPath: 'app')"),
                false));

        WorkflowRun b = j.assertBuildStatusSuccess(p.scheduleBuild2(0));
    }

    @Test
    public void testTomcatAdapter() throws Exception {
        WorkflowJob p = j.getInstance().createProject(WorkflowJob.class, "TomcatTest");
        p.setDefinition(new CpsFlowDefinition(
                getFullScript(
                "def tc7 = tomcat7( " +
                    "url: 'FAKE', " +
                    "credentialsId: 'FAKE') \n" +
                "def tc8 = tomcat8( " +
                    "home: 'FAKE', " +
                    "credentialsId: 'FAKE') \n" +
                "deploy(adapters: [tc7, tc8], war: 'target/app.war', contextPath: 'app')"),
                false));
        WorkflowRun b = j.assertBuildStatusSuccess(p.scheduleBuild2(0));
    }

    @Test
    public void testLegacyAdapterThrows() throws Exception {
        WorkflowJob p = j.getInstance().createProject(WorkflowJob.class, "legacyTest");
        p.setDefinition(new CpsFlowDefinition(
                getFullScript(
                        "writeFile(file: 'target/app.war', text: '')\n" +
                        "deploy(adapters: [legacyAdapter()], war: 'target/app.war', contextPath: 'app', onFailure: true)"),
                false));
        WorkflowRun r = p.scheduleBuild2(0).get();
        j.assertBuildStatus(Result.FAILURE, r);
        assertTrue(r.getLog().contains("maintainer") && r.getLog().contains("update"));
    }

}

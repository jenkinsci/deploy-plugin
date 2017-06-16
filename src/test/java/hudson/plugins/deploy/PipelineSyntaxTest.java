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

/**
 * Tests pipeline compatibility. Since there are no *.war files in the workspace all of these scripts
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
        j.createOnlineSlave(Label.get("remote"));
        p.setDefinition(new CpsFlowDefinition(
                getFullScript("deploy(war: 'target/app.war', contextPath: 'app', onFailure: false)"),
                false));
        WorkflowRun b = j.assertBuildStatusSuccess(p.scheduleBuild2(0));
    }

    @Test
    public void testMockAdapterDeploy() throws Exception {
        j.jenkins.setNumExecutors(1);
        WorkflowJob p = j.getInstance().createProject(WorkflowJob.class, "MockTest");
        p.setDefinition(new CpsFlowDefinition(
                getFullScript("deploy(container: mock(), war: 'target/app.war', contextPath: 'app')"),
                false));
        WorkflowRun b = j.assertBuildStatusSuccess(p.scheduleBuild2(0));
    }

    @Test
    public void testMockAdaptersDeploy() throws Exception {
        WorkflowJob p = j.getInstance().createProject(WorkflowJob.class, "MockTest");
        j.createOnlineSlave(Label.get("remote"));
        p.setDefinition(new CpsFlowDefinition(
                getFullScript("deploy(containers: [mock(), mock(), mock()], war: 'target/app.war', contextPath: 'app')"),
                false));
        WorkflowRun b = j.assertBuildStatusSuccess(p.scheduleBuild2(0));
    }

    @Test
    public void testGlassFishAdapter() throws Exception {
        WorkflowJob p = j.getInstance().createProject(WorkflowJob.class, "GlassfishTest");
        j.createOnlineSlave(Label.get("remote"));
        p.setDefinition(new CpsFlowDefinition(
                getFullScript(
                "def gf2 = glassfish2( " +
                    "home: 'FAKE', " +
                    "password: 'FAKE', " +
                    "userName: 'FAKE', " +
                    "adminPort: '1234', " +
                    "hostname: 'localhost') \n" +
                "def gf3 = glassfish3( " +
                    "home: 'FAKE', " +
                    "password: 'FAKE', " +
                    "userName: 'FAKE', " +
                    "adminPort: '1234', " +
                    "hostname: 'localhost') \n" +
                "deploy(containers: [gf2, gf3], war: 'target/app.war', contextPath: 'app')"),
                false));

        WorkflowRun b = j.assertBuildStatusSuccess(p.scheduleBuild2(0));
    }

    @Test
    public void testTomcatAdapter() throws Exception {
        WorkflowJob p = j.getInstance().createProject(WorkflowJob.class, "TomcatTest");
        j.createOnlineSlave(Label.get("remote"));
        p.setDefinition(new CpsFlowDefinition(
                getFullScript(
                "def tc7 = tomcat7( " +
                    "url: 'FAKE', " +
                    "password: 'FAKE', " +
                    "userName: 'FAKE') \n" +
                "def tc8 = tomcat8( " +
                    "home: 'FAKE', " +
                    "password: 'FAKE', " +
                    "userName: 'FAKE') \n" +
                "deploy(containers: [tc7, tc8], war: 'target/app.war', contextPath: 'app')"),
                false));

        WorkflowRun b = j.assertBuildStatusSuccess(p.scheduleBuild2(0));
    }

}

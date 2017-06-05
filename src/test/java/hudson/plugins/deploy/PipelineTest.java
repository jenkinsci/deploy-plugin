package hudson.plugins.deploy;

import hudson.model.Label;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Test pipeline compat
 */
public class PipelineTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void testNoAdapterDeploy() throws Exception {
        WorkflowJob p1 = j.getInstance().createProject(WorkflowJob.class, "p1");
        j.createOnlineSlave(Label.get("remote"));
        p1.setDefinition(new CpsFlowDefinition("node {" +
                "deploy(adapters: [], war: '/target/app.war', contextPath: '/app', onFailure: false) \n"+
                "}"));
        j.assertBuildStatusSuccess(p1.scheduleBuild2(0));

        WorkflowJob p2 = j.getInstance().createProject(WorkflowJob.class, "p2");
        j.createOnlineSlave(Label.get("remote"));
        p2.setDefinition(new CpsFlowDefinition("node {" +
                "deploy(adapter: null, war: '/target/app.war', contextPath: '/app', onFailure: false) \n"+
                "}"));
        j.assertBuildStatusSuccess(p2.scheduleBuild2(0));
    }

    @Test
    public void testAdapterDeployNoContainer() throws Exception {
        WorkflowJob p = j.getInstance().createProject(WorkflowJob.class, "p3");
        j.createOnlineSlave(Label.get("remote"));
        p.setDefinition(new CpsFlowDefinition("node {" +
                "deploy(adapters: [], war: '/target/app.war', contextPath: '/app') \n"+
                "}"));

        WorkflowRun b = j.assertBuildStatusSuccess(p.scheduleBuild2(0));
    }
}

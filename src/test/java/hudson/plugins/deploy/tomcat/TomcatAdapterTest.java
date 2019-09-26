package hudson.plugins.deploy.tomcat;

import hudson.plugins.deploy.DeploymentContext;

/**
 * @author Julien Béti - julien.beti@cosium.com
 */
abstract class TomcatAdapterTest {
  private static final String VARIABLE_START = "${";
  private static final String VARIABLE_END = "}";

  DeploymentContext getDeploymentContextVariable(String variableName) {
    String variable = getVariable(variableName);
    if(variable != null) {
      return DeploymentContext.of(variable);
    }
    return null;
  }

  String getVariable(String variableName) {
    return VARIABLE_START + variableName + VARIABLE_END;
  }
}

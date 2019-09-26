package hudson.plugins.deploy;

/**
 * Deployment context of an application in an application server.
 * @author Julien Béti - julien.beti@cosium.com
 * @since 1.16
 */
public class DeploymentContext extends NonBlankString {
  private DeploymentContext(String value) {
    super(value);
  }

  public static DeploymentContext of(String context) {
    return new DeploymentContext(context);
  }
}

package hudson.plugins.deploy;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.Domain;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Job;
import hudson.Util;
import hudson.util.Scrambler;
import jenkins.model.Jenkins;
import org.codehaus.cargo.container.property.RemotePropertySet;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.DoNotUse;
import org.kohsuke.accmod.restrictions.NoExternalUse;

import javax.annotation.CheckForNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates credentials for the previously stored password.
 *
 * Historical precedent of the multiple password fields and why they should not be removed. Using
 * {@link hudson.plugins.deploy.tomcat.Tomcat7xAdapter} as an example, but applies to all.
 *
 * v1.0     Stored password as plain text
 *          <pre>{@code
 *              <Tomcat7xAdapter>
 *                  <userName>admin</userName>
 *                  <password>pw</password>
 *                  <url>http://example.com:8080</url>
 *              </Tomcat7xAdapter>
 *          }</pre>
 *
 * v1.9     Used {@link hudson.util.Scrambler} to base64 encode password. readResolve converted plaintext password
 *          to passwordScrambled.
 *          <pre>{@code
 *              <Tomcat7xAdapter>
 *                  <userName>admin</userName>
 *                  <passwordScrambled>cHcNCg==</passwordScrambled>
 *                  <url>http://example.com:8080</url>
 *              </Tomcat7xAdapter>
 *          }</pre>
 *
 * v1.11    Full support of credentials. To be backwards compatible and not break builds converts old configurations
 *          from password or passwordScrambled to credentials.
 *          <pre>{@code
 *              <Tomcat7xAdapter>
 *                  <credentialsId>aDjnKd4j-s66fnF53-2dmAS7PkqD4</credentialsId>
 *                  <url>http://example.com:8080</url>
 *              </Tomcat7xAdapter>
 *          }</pre>
 *
 * @author Alex Johnson
 * @author Kohsuke Kawaguchi
 */
public abstract class PasswordProtectedAdapterCargo extends DefaultCargoContainerAdapterImpl {
    @Deprecated // backwards compatibility
    private String passwordScrambled;

    @XStreamOmitField // do not store the password locally, but serialize for remoting
    public String userName;
    @XStreamOmitField
    private String password;
    @CheckForNull
    private String credentialsId;

    public PasswordProtectedAdapterCargo(String credentialsId) {
        this.credentialsId = Util.fixEmpty(credentialsId);
    }

    @Restricted(DoNotUse.class)
    @Deprecated
    public PasswordProtectedAdapterCargo(String userName, String password) {
        this.userName = userName;
        this.password = password;
        migrateCredentials(new ArrayList<StandardUsernamePasswordCredentials>());
    }

    @Override
    public boolean redeploy(FilePath war, String aContextPath, AbstractBuild<?,?> build, Launcher launcher,
                            final BuildListener listener) throws IOException, InterruptedException {
        loadCredentials(build.getParent());
        return super.redeploy(war, aContextPath, build, launcher, listener);
    }

    /**
     * Loads the credentials for a job.
     *
     * @param job the job to lookup the scope for
     */
    public void loadCredentials(Job job) {
        StandardUsernamePasswordCredentials credentials = ContainerAdapterDescriptor.lookupCredentials(job, getUrl(), credentialsId);
        if (credentials != null) {
            CredentialsProvider.track(job, credentials);
            userName = credentials.getUsername();
            password = credentials.getPassword().getPlainText();
        } else {
            Logger.getLogger(DeployPublisher.class.getName()).log(Level.WARNING, "Tried to load DeployPublisher credentials for credentials ID " + credentialsId +
                    " but couldn't find them!");
        }
    }

    public String getCredentialsId() {
        return credentialsId;
    }

    @Property(RemotePropertySet.USERNAME)
    public String getUsername() {
        return userName;
    }

    @Property(RemotePropertySet.PASSWORD)
    @Restricted(NoExternalUse.class)
    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return null;
    }

    /**
     * Migrates to credentials.
     * In case where migration fails, we retain the original username/password/passwordScrambled fields and should avoid
     * saving to disk until the user can help resolve the situation.
     * @return True if migration succeeded, false if we tried to create credentials and failed.
     */
    public boolean migrateCredentials(List<StandardUsernamePasswordCredentials> generated) {
        if (credentialsId == null) {
            if (passwordScrambled != null) {
                password = Scrambler.descramble(passwordScrambled);
            }

            StandardUsernamePasswordCredentials newCredentials = null;
            for (StandardUsernamePasswordCredentials c : generated) {
                if (c.getUsername().equals(userName) && c.getPassword().getPlainText().equals(password)) {
                    newCredentials = c;
                }
            }

            boolean validCredentials = newCredentials != null;
            if (!validCredentials) {
                newCredentials = new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL,
                        null, "Generated deploy-plugin credentials for " + getContainerId(),
                        userName, password);
                try {
                    CredentialsProvider.lookupStores(Jenkins.getInstance())
                            .iterator().next().addCredentials(Domain.global(), newCredentials);
                    generated.add(newCredentials);
                    validCredentials = true;
                    Logger.getLogger(DeployPublisher.class.getName()).log(Level.INFO, "credentials were " +
                            "generated and added to config");
                } catch (IOException e) {
                    Logger.getLogger(DeployPublisher.class.getName()).log(Level.SEVERE, "credentials were generated with id "+newCredentials.getId()+
                            " but could not be stored.  Please create valid credentials or fix this job.");
                    validCredentials = false;
                }
            }

            if (validCredentials) {  // Only blow away the userName and passWord if we successfully created credentials
                userName = null;
                password = null;
                passwordScrambled = null;
            }

            credentialsId = newCredentials.getId();
            return validCredentials;
        }
        return true;
    }
}

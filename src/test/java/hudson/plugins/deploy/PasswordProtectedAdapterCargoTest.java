package hudson.plugins.deploy;

import hudson.util.XStream2;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

public class PasswordProtectedAdapterCargoTest {
    @Test
    public void testDeserializeOldPlainPassword () {
        String plainPassword = "plain-password";
        String oldXml = "<hudson.plugins.deploy.glassfish.GlassFish3xAdapter><userName>manager</userName><password>"
            + plainPassword + "</password><home>/</home><hostname></hostname></hudson.plugins.deploy.glassfish.GlassFish3xAdapter>";
        XStream2 xs = new XStream2();

        PasswordProtectedAdapterCargo adapter = (PasswordProtectedAdapterCargo)xs.fromXML(oldXml);
        Assert.assertEquals(plainPassword, adapter.getPassword());
        
        String newXml = xs.toXML(adapter);
        Assert.assertThat("Password should be scrambled", newXml, CoreMatchers.not(JUnitMatchers.containsString(plainPassword)));
    }
}

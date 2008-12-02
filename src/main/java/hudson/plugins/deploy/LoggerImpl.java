package hudson.plugins.deploy;

import java.io.PrintStream;

import org.codehaus.cargo.util.internal.log.AbstractLogger;
import org.codehaus.cargo.util.log.LogLevel;
import org.codehaus.cargo.util.log.Logger;

/**
 * Adapter to Cargo {@link Logger}.
 *
 * Print out messages with some indentation.
 *
 * @author Kohsuke Kawaguchi
 */
public class LoggerImpl extends AbstractLogger {
    private final PrintStream out;

    public LoggerImpl(PrintStream out) {
        this.out = out;
    }

    protected void doLog(LogLevel level, String message, String category) {
        out.println("  "+message);
    }
}

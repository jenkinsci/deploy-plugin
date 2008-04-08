package hudson.plugins.deploy;

import org.apache.commons.beanutils.ConvertUtils;
import org.codehaus.cargo.container.configuration.Configuration;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.FIELD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class DefaultCargoContainerAdapterImpl extends CargoContainerAdapter {
    @Target(FIELD)
    @Retention(RUNTIME)
    @Documented
    @interface Property {
        /**
         * Property name.
         */
        String value();
    }

    /**
     * Default implementation that fills the configuration by using
     * fields annotated with {@link Property}.
     */
    public void configure(Configuration config) {
        for(Field f : getClass().getFields()) {
            Property p = f.getAnnotation(Property.class);
            if(p==null)     continue;

            try {
                config.setProperty(p.value(), ConvertUtils.convert(f.get(this)));
            } catch (IllegalAccessException e) {
                IllegalAccessError x = new IllegalAccessError();
                x.initCause(e);
                throw x;
            }
        }
    }
}

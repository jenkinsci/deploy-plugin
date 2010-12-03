package hudson.plugins.deploy;

import org.apache.commons.beanutils.ConvertUtils;
import org.codehaus.cargo.container.configuration.Configuration;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class DefaultCargoContainerAdapterImpl extends CargoContainerAdapter {
    @Target(FIELD)
    @Retention(RUNTIME)
    @Documented
    public @interface Property {
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
                String v = ConvertUtils.convert(f.get(this));
                if(v!=null)
                    config.setProperty(p.value(), v);
            } catch (IllegalAccessException e) {
                IllegalAccessError x = new IllegalAccessError();
                x.initCause(e);
                throw x;
            }
        }
    }
}

package fit.wenchao.propertyResolver.propertiesProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If a property method is annotated by this annotation, it must be present in the corresponded
 * properties file with a not-blank value. Otherwise, framework throws exception.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Mandatory {
}

package fit.wenchao.propertiesProcessor;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use @Default to provide a default value when a property is allowed missing in its properties
 * file.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Default {
    Class<? extends PropertyValueProducer> value() default DefaultPropertyValueProducer.class;
}

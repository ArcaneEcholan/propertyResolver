package fit.wenchao.propertyResolver.utils;

import java.lang.reflect.Field;

public class ReflectUtils {
    public static void setField(Field field, Object instance, Object value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

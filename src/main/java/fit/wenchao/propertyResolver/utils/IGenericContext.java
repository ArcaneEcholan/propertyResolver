package fit.wenchao.propertyResolver.utils;

import java.lang.reflect.Type;

public interface IGenericContext {
    boolean isGeneric();

    int count();

    Type getType(int i);

    IGenericContext get(int i);

    Class<?> getRawType();
}

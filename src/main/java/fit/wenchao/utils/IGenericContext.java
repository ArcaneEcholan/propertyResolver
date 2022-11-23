package fit.wenchao.utils;

import java.lang.reflect.Type;

public interface IGenericContext {
    boolean isGeneric();

    int count();

    Type getType(int i);

    GenericContext get(int i);

    Class<?> getRawType();
}

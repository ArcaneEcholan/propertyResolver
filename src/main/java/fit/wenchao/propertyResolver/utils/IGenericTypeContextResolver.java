package fit.wenchao.propertyResolver.utils;

import java.lang.reflect.Type;

public interface IGenericTypeContextResolver {
    IGenericContext resolve(Type type);
}

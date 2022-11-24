package fit.wenchao.utils;

import fit.wenchao.utils.IGenericContext;

import java.lang.reflect.Type;

public interface IGenericTypeContextResolver {
    IGenericContext resolve(Type type);
}

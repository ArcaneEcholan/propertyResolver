package fit.wenchao.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GenericTypeContextResolver implements IGenericTypeContextResolver {
    @Override
    public IGenericContext resolve(Type type) {
        GenericContext genericContext = new GenericContext();
        Type targetType = type;
        boolean isGeneric;
        Class<?> rawType;
        List<IGenericContext> nestedCtx;
        if (targetType instanceof ParameterizedType) {
            isGeneric = true;
            rawType = (Class<?>) ((ParameterizedType) type).getRawType();
            nestedCtx = new ArrayList<>();
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            for (Type actualTypeArgument : actualTypeArguments) {
                IGenericContext nestGenericContext = this.resolve(actualTypeArgument);
                nestedCtx.add(nestGenericContext);
            }
            genericContext.setGeneric(isGeneric);
            genericContext.setType(targetType);
            genericContext.setRawType(rawType);
            genericContext.setNestedCtx(nestedCtx);
            return genericContext;
        }
        rawType = (Class<?>) targetType;
        genericContext.setRawType(rawType);

        return genericContext;
    }
}

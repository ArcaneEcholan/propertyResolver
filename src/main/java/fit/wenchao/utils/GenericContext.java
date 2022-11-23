package fit.wenchao.utils;

import fit.wenchao.Factory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenericContext implements IGenericContext {

    Type type;

    boolean isGeneric;

    List<GenericContext> nestedCtx;

    Class<?> rawType;

    public GenericContext(Type genericType) {
        this.type = genericType;

        if (type instanceof ParameterizedType) {
            this.isGeneric = true;
            rawType = (Class<?>) ((ParameterizedType) type).getRawType();
            nestedCtx = new ArrayList<>();
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            for (Type actualTypeArgument : actualTypeArguments) {
                GenericContext genericContext = new GenericContext(actualTypeArgument);
                nestedCtx.add(genericContext);
            }
            return;
        }
        this.rawType = (Class<?>) type;
    }

    @Override
    public boolean isGeneric() {
        return isGeneric;
    }

    @Override
    public int count() {
        return this.nestedCtx.size();
    }

    @Override
    public Type getType(int i) {
        return this.nestedCtx.get(i).getType(i);
    }

    @Override
    public GenericContext get(int i) {
        return this.nestedCtx.get(i);
    }

    @Override
    public Class<?> getRawType() {
        return rawType;
    }

    private static class ForTest {
        List<Map<String, List<ForTest>>> list;
    }

    public static void main(String[] args) throws NoSuchFieldException {
        Field listField = ForTest.class.getDeclaredField("list");
        listField.setAccessible(true);

        Type genericType = listField.getGenericType();

        IGenericContext genericContext = Factory.getGenericContext(genericType);

        System.out.println(genericContext.isGeneric());
        System.out.println(genericContext.count());
        System.out.println(genericContext.get(0).get(1).get(0).getRawType());

    }
}
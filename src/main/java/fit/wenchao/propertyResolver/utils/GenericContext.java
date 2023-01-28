package fit.wenchao.propertyResolver.utils;

import fit.wenchao.propertyResolver.Factory;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class GenericContext implements IGenericContext {

    Type type;

    boolean isGeneric;

    List<IGenericContext> nestedCtx;

    Class<?> rawType;

    public GenericContext() {
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setGeneric(boolean generic) {
        isGeneric = generic;
    }

    public void setNestedCtx(List<IGenericContext> nestedCtx) {
        this.nestedCtx = nestedCtx;
    }

    public void setRawType(Class<?> rawType) {
        this.rawType = rawType;
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
    public IGenericContext get(int i) {
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

        IGenericContext genericContext = Factory.getGenericTypeContextResolver().resolve(genericType);

        System.out.println(genericContext.isGeneric());
        System.out.println(genericContext.count());
        System.out.println(genericContext.get(0).get(1).get(0).getRawType());

    }
}
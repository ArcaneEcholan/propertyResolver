package propertyResolver;

import fit.wenchao.constants.CommonConstants;
import fit.wenchao.example.TestProperty;
import fit.wenchao.propertyResolver.PropertyResolver;
import fit.wenchao.propertyResolver.ResettableFileInputStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TestPropertyResolver {

    @Test
    public void test_createConf_simple_string_conf() throws InstantiationException, IllegalAccessException, NoSuchFieldException, IOException {
        TestProperty conf = PropertyResolver.create(TestProperty.class);
        Assert.assertEquals(conf.getEncryptAll(), "true");
        System.out.println(conf.getClasspath());
        System.out.println(conf.getEncryptAll());
        System.out.println(conf.getKeep());
    }

    @Test
    public void test_createConf_nested_object_conf() throws InstantiationException, IllegalAccessException, IOException, NoSuchFieldException {
        TestProperty conf = PropertyResolver.create(TestProperty.class);
        System.out.println(conf.getSec().getAge());
        System.out.println(conf.getSec().getName());
        System.out.println(conf.getSec().getAdds());

        List<String> expectedAddsList = new ArrayList<>();
        expectedAddsList.add("add");
        expectedAddsList.add("cff");
        expectedAddsList.add("tff");

        Assert.assertNotEquals(conf.getSec(), null);
        Assert.assertEquals("12", conf.getSec().getAge());
        Assert.assertEquals("wc", conf.getSec().getName());
        Assert.assertEquals(expectedAddsList, conf.getSec().getAdds());

    }


    @Test
    public void test_createConf_nested_object_list_conf() throws InstantiationException, IllegalAccessException, NoSuchFieldException, IOException {
        TestProperty conf = PropertyResolver.create(TestProperty.class);
        System.out.println(conf.getSec().getAge());
        System.out.println(conf.getSec().getName());
        System.out.println(conf.getSec().getAdds());

        List<String> expectedAddsList = new ArrayList<>();
        expectedAddsList.add("add");
        expectedAddsList.add("cff");
        expectedAddsList.add("tff");

        Assert.assertNotEquals(conf.getSec(), null);
        Assert.assertEquals("12", conf.getSec().getAge());
        Assert.assertEquals("wc", conf.getSec().getName());
        Assert.assertEquals(expectedAddsList, conf.getSec().getAdds());

        System.out.println(conf.getSecs());

    }

    @Test
    public void test_createConf_null_object_conf() throws InstantiationException, IllegalAccessException, NoSuchFieldException, IOException {
        TestProperty conf = PropertyResolver.create(TestProperty.class);
        System.out.println(conf.getSecNull());
    }
}

package fit.wenchao.propertyResolver;

import fit.wenchao.constants.CommonConstants;
import fit.wenchao.example.TestProperty;
import fit.wenchao.propertiesProcessor.ConfPrefix;
import fit.wenchao.utils.RegexUtils;
import fit.wenchao.utils.VarCaseConvertUtils;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static fit.wenchao.constants.CommonConstants.DEFAULT_CONF_NAME;
import static fit.wenchao.constants.CommonConstants.PROPERTIES_SUFFIX;


public class PropertyResolver {

    public static <T> T create(Class<T> configClass) throws IOException {
        InputStream inputStream;
        inputStream = PropertyResolver.getConfInput();
        return PropertyResolver.createConf(inputStream,
                configClass, false, null, null, false, "");
    }

    public static String getLineValue(String line) {
        String result = null;
        line = line.trim();
        String[] split = line.split("=");
        if (split.length < 2) {
            return null;
        }
        String valuePart = split[1];
        return valuePart.trim();
    }

    public static String getConfigClassPrefix(Class<?> configClass) {
        String lowerHyphen = VarCaseConvertUtils.lowerCamel2LowerHyphen(configClass.getSimpleName());
        String prefix = lowerHyphen.replace("-", ".");
        ConfPrefix confPrefixAnno
                = configClass.getAnnotation(ConfPrefix.class);
        if (confPrefixAnno != null) {
            prefix = confPrefixAnno.value();
        }

        return prefix;
    }


    public static InputStream getConfInput() throws IOException {
        InputStream inputStream = PropertyResolver.class.getClassLoader().getResourceAsStream(CommonConstants.DEFAULT_CONF_NAME + CommonConstants.PROPERTIES_SUFFIX);
        System.out.println(PropertyResolver.class.getClassLoader());
        System.out.println(ClassLoader.getSystemClassLoader());
        Field in = null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) > -1 ) {
            baos.write(buffer, 0, len);
        }
        baos.flush();

        return new StringInputStream(baos.toString("UTF-8"));

    }

    public static <T> T createConf(InputStream inputStream,
                                   Class<T> configClass,
                                   boolean nested,
                                   Field parentField,
                                   String parentPrefix, boolean inList,
                                   String listElemId) throws IOException {
        inputStream.reset();
        T confInstance = null;
        try {
            confInstance = configClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("Properties Javabean class must have a public default constructor.");
        }

        List<Field> fields = Arrays.stream(configClass.getDeclaredFields()).collect(Collectors.toList());
        String prefix;

        if (!nested) {
            prefix = getConfigClassPrefix(configClass);
        } else if(!inList) {
            prefix = parentPrefix + "." + parentField.getName();
        } else {
            prefix = parentPrefix + "." + parentField.getName() + "["+listElemId+"]";
        }

        for (Field field : fields) {
            field.setAccessible(true);

            String fieldName = field.getName();

            String fullPropertyName = prefix + "." + fieldName;

            Class<?> fieldType = field.getType();
            inputStream.reset();
            if (fieldType.isAssignableFrom(String.class)) {
                if (inputStream == null) {
                    throw new IllegalStateException("PropertyStream is empty");
                }
                String result = null;
                BufferedReader propertyIn =
                        new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = propertyIn.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith(fullPropertyName + "=")) {
                        result = getLineValue(line);
                    }
                }
                try {
                    field.set(confInstance, result);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (List.class.isAssignableFrom(fieldType)) {
                Type genericFieldType = field.getGenericType();
                if (genericFieldType instanceof ParameterizedType) {
                    Type[] actualTypeArguments = ((ParameterizedType) genericFieldType).getActualTypeArguments();
                    Type innerType = actualTypeArguments[0];
                    if (innerType instanceof ParameterizedType) {
                        throw new IllegalStateException("Not support nested List: field " + field.getName());
                    } else {
                        Class<?> innerClass = (Class<?>) innerType;
                        if (innerClass.isAssignableFrom(String.class)) {
                            if (inputStream == null) {
                                throw new IllegalStateException("PropertyStream is empty");
                            }

                            List<String> result = new ArrayList<>();
                            BufferedReader propertyIn =
                                    new BufferedReader(new InputStreamReader(inputStream));
                            String line;
                            while ((line = propertyIn.readLine()) != null) {
                                line = line.trim();
                                boolean matches = Pattern.matches("^" + RegexUtils.escape(fullPropertyName) + "\\[.*\\]=.*",
                                        line);
                                if (matches) {
                                    result.add(getLineValue(line));
                                }
                            }
                            try {
                                field.set(confInstance, result);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        } else {

                            if (inputStream == null) {
                                throw new IllegalStateException("Property file not found in the classpath: " + DEFAULT_CONF_NAME + PROPERTIES_SUFFIX);
                            }

                            Map<String, List<String>> map = new LinkedHashMap<>();
                            BufferedReader propertyIn = new BufferedReader(new InputStreamReader(inputStream));
                            String line;
                            while ((line = propertyIn.readLine()) != null) {
                                line = line.trim();
                                String regex = "^(" + RegexUtils.escape(fullPropertyName) + "\\[.*\\])\\.";
                                RegexNestListPrefixContext regexNestListPrefixContext =
                                        regexNestListPrefix(line, fullPropertyName);
                                if (regexNestListPrefixContext == null) {
                                    continue;
                                }
                                String listId = regexNestListPrefixContext.getId();
                                List<String> groupLines = map.get(listId);
                                if (groupLines == null) {
                                    groupLines = new ArrayList<>();
                                    map.put(listId, groupLines);
                                }

                                groupLines.add(line);

                            }

                            Map<String, String> groupStringMap = new HashMap<>();


                            map.forEach((k, v) -> {
                                String lines = "";

                                String listId = k;
                                List<String> groupLines = v;
                                for (String l : groupLines) {
                                    lines+=l;
                                    lines+="\n";
                                }
                                groupStringMap.put(k, lines);
                            });

                            Set<String> groupIds = groupStringMap.keySet();
                            List<Object> results = new ArrayList<>();
                            for (String groupId : groupIds) {
                                String groupStrings = groupStringMap.get(groupId);
                                StringInputStream stringInputStream = new StringInputStream(groupStrings);
                                Object result = createConf(stringInputStream, innerClass, true, field, prefix, true, groupId);
                                results.add(result);
                            }
                            try {
                                field.set(confInstance, results);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }

                            //throw new IllegalStateException("Only support String List arg: field " + field.getName());
                        }
                    }
                }
            } else {
                   Object result = createConf(inputStream, fieldType, true, field, prefix, false, null);
                try {
                    field.set(confInstance, result);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return confInstance;
    }

    @Getter
    @Setter
    static class RegexNestListPrefixContext {
        String id;
        String line;
    }

    public static RegexNestListPrefixContext regexNestListPrefix(String line, String fullPropertyName) {
        String regex = "^(" + RegexUtils.escape(fullPropertyName) + "\\[(.*)\\])\\.";
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(line);
        RegexNestListPrefixContext regexNestListPrefixContext = new RegexNestListPrefixContext();
        while (matcher.find()) {
            regexNestListPrefixContext.setId(matcher.group(2).trim());
            regexNestListPrefixContext.setLine(line);
            return regexNestListPrefixContext;
        }
        return null;
    }

    @Test
    public void test_regex_prefix() {
        String regex = "^(" + RegexUtils.escape("test.property.sec") + "\\[(.*)\\])\\.";
        Pattern compile = Pattern.compile(regex);
        String src = "test.property.sec[1].adds[]=add\n\r" +
                "test.property.sec[1].adds[]=cff\n\r" +
                "test.property.sec[1].adds[]=tff";
        Matcher matcher = compile.matcher(src);
        int c = 0;
        while (matcher.find()) {
            c++;
            System.out.println(c);
            System.out.println(matcher.group(0).trim());
            System.out.println(matcher.group(1).trim());
            System.out.println(matcher.group(2).trim());
            System.out.println();
        }


    }


}

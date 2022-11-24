package fit.wenchao.propertyResolver;

import fit.wenchao.Factory;
import fit.wenchao.propertiesProcessor.ConfPrefix;
import fit.wenchao.utils.*;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static fit.wenchao.constants.CommonConstants.DEFAULT_CONF_NAME;
import static fit.wenchao.constants.CommonConstants.PROPERTIES_SUFFIX;
import static fit.wenchao.utils.StrUtils.ft;


public class PropertyResolver implements IPropertyResolver {

    private final IGenericTypeContextResolver genericTypeContextResolver;

    public PropertyResolver(IGenericTypeContextResolver genericTypeContextResolver) {
        this.genericTypeContextResolver = genericTypeContextResolver;
    }

    @Override
    public <T> T create(Class<T> configClass) throws IOException {
        ResettableInputStream resettableInputStream = getResettableConfInput();
        resettableInputStream.mark();
        return createConf(resettableInputStream,
                configClass, false, null, null, false, "");
    }

    public String getLineValue(String line) {
        String result = null;
        line = line.trim();
        String[] split = line.split("=");
        if (split.length < 2) {
            return null;
        }
        String valuePart = split[1];
        return valuePart.trim();
    }

    public String getConfigClassPrefix(Class<?> configClass) {
        String lowerHyphen = VarCaseConvertUtils.lowerCamel2LowerHyphen(configClass.getSimpleName());
        String prefix = lowerHyphen.replace("-", ".");
        ConfPrefix confPrefixAnno
                = configClass.getAnnotation(ConfPrefix.class);
        if (confPrefixAnno != null) {
            prefix = confPrefixAnno.value();
        }

        return prefix;
    }


    public ResettableInputStream getResettableConfInput() throws IOException {
        return Factory.getStringResettableInputStreamFromResource(DEFAULT_CONF_NAME + PROPERTIES_SUFFIX);
    }


    public Object processListWithNotGenericObjectField(ResettableInputStream resettableInputStream,
                                                       String fullPropertyName,
                                                       Class<?> configClass,
                                                       Field field,
                                                       String prefix) throws IOException {

        Object result;
        Type genericFieldType = field.getGenericType();

        IGenericContext genericContext = genericTypeContextResolver.resolve(genericFieldType);

        if (!genericContext.isGeneric()) {
            throw new RuntimeException(ft("Raw List not support"));
        }

        int typeArgCount = genericContext.count();
        if (typeArgCount > 1) {
            throw new RuntimeException("Property parse error");
        }

        IGenericContext firstNestedCtx = genericContext.get(0);
        if (firstNestedCtx.isGeneric()) {
            throw new IllegalStateException("Not support nested List or other generic Types: field " + field.getName());
        }

        Class<?> innerClass = firstNestedCtx.getRawType();
        if (innerClass.isAssignableFrom(String.class)) {
            result = processStringField(resettableInputStream, fullPropertyName, true);
        } else {
            result = processNotGenericObjectNestedInListField(resettableInputStream, fullPropertyName, innerClass,
                    field, prefix);
            //throw new IllegalStateException("Only support String List arg: field " + field.getName());
        }
        return result;
    }

    public Object processNotGenericObjectField(ResettableInputStream resettableInputStream,
                                               String fullPropertyName,
                                               Class<?> configClass,
                                               Field field,
                                               String prefix) throws IOException {
        return createConf(resettableInputStream, configClass, true, field, prefix, false, null);
    }

    public Object processStringField(ResettableInputStream resettableInputStream,
                                     String fullPropertyName, boolean nestedInList) throws IOException {
        if (resettableInputStream == null) {
            throw new IllegalStateException("PropertyStream is empty");
        }

        if (nestedInList) {
            List<String> result = new ArrayList<>();
            BufferedReader propertyIn =
                    resettableInputStream.getBufferedReader();
            String line;
            while ((line = propertyIn.readLine()) != null) {
                line = line.trim();
                boolean matches = Pattern.matches("^" + RegexUtils.escape(fullPropertyName) + "\\[.*\\]=.*",
                        line);
                if (matches) {
                    result.add(getLineValue(line));
                }
            }

            return result;
        }


        String result = null;
        BufferedReader propertyIn =
                resettableInputStream.getBufferedReader();
        String line;
        while ((line = propertyIn.readLine()) != null) {
            line = line.trim();
            if (line.startsWith(fullPropertyName + "=")) {
                result = getLineValue(line);
            }
        }
        return result;
    }

    public Object processNotGenericObjectNestedInListField(ResettableInputStream resettableInputStream,
                                                           String fullPropertyName,
                                                           Class<?> configClass,
                                                           Field field,
                                                           String prefix) throws IOException {
        if (resettableInputStream == null) {
            throw new IllegalStateException("Property file not found in the classpath: " + DEFAULT_CONF_NAME + PROPERTIES_SUFFIX);
        }

        Map<String, List<String>> map = new LinkedHashMap<>();
        BufferedReader propertyIn =
                resettableInputStream.getBufferedReader();
        String line;
        while ((line = propertyIn.readLine()) != null) {
            line = line.trim();
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
                lines += l;
                lines += "\n";
            }
            groupStringMap.put(k, lines);
        });

        Set<String> groupIds = groupStringMap.keySet();
        List<Object> results = new ArrayList<>();

        for (String groupId : groupIds) {
            String groupStrings = groupStringMap.get(groupId);
            Object result = createConf(Factory.getStringResettableInputStreamFromString(groupStrings),
                    configClass, true, field, prefix, true, groupId);
            results.add(result);
        }

        return results;
    }


    public <T> T createConf(ResettableInputStream resettableInputStream,
                            Class<T> configClass,
                            boolean nested,
                            Field parentField,
                            String parentPrefix, boolean inList,
                            String listElemId) throws IOException {
        if (resettableInputStream == null) {
            throw new NullPointerException(ft("ResettableInputStream argument of createConf cannot be null"));
        }

        resettableInputStream.reset();
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
        } else if (!inList) {
            prefix = parentPrefix + "." + parentField.getName();
        } else {
            prefix = parentPrefix + "." + parentField.getName() + "[" + listElemId + "]";
        }

        // traverse fields of target Class
        for (Field field : fields) {
            field.setAccessible(true);

            String fieldName = field.getName();

            // full property name in properties file, e.g. system.config.name
            String fullPropertyName = prefix + "." + fieldName;

            Class<?> fieldType = field.getType();
            resettableInputStream.reset();
            Object result = null;
            if (fieldType.isAssignableFrom(String.class)) {
                result = processStringField(resettableInputStream,
                        fullPropertyName,
                        false);
            } else if (List.class.isAssignableFrom(fieldType)) {
                result = processListWithNotGenericObjectField(resettableInputStream,
                        fullPropertyName,
                        fieldType,
                        field,
                        prefix);
            } else {
                result = processNotGenericObjectField(resettableInputStream,
                        fullPropertyName,
                        fieldType,
                        field,
                        prefix);
            }

            ReflectUtils.setField(field, confInstance, result);
        }

        return confInstance;
    }

    @Getter
    @Setter
    static class RegexNestListPrefixContext {
        String id;
        String line;
    }

    public RegexNestListPrefixContext regexNestListPrefix(String line, String fullPropertyName) {
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

    public static void main(String[] args) {
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

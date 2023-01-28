package fit.wenchao.propertyResolver.utils;

public class VarCaseConvertUtils {
    public static String lowerCamel2LowerHyphen(String name) {
        String separator = "-";
        name = name.replaceAll("([a-z])([A-Z])", "$1"+separator+"$2").toLowerCase();
        return name;
        //return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, name);
    }

    public static void main(String[] args) {
        System.out.println(lowerCamel2LowerHyphen("helloWorld"));
        System.out.println(lowerCamel2LowerHyphen("HelloWorld"));
        System.out.println(lowerCamel2LowerHyphen("hello"));
        System.out.println(lowerCamel2LowerHyphen("Hello"));
    }
}

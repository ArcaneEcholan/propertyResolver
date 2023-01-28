package fit.wenchao.propertyResolver.utils;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    public static String escape(String raw) {
        return Pattern.quote(raw);
    }

    public static void main(String[] args) {
        String[] split = "test.name".split("\\.");
        System.out.println(split.length);

        split = "test".split("\\.");
        System.out.println(split.length);

        split = "test.".split("\\.");
        System.out.println(split.length);
    }

    public static String regexFirstWord(String src, String pat) {
        Pattern compile = Pattern.compile(pat);
        Matcher matcher = compile.matcher(src);
        boolean matchFound = matcher.find();
        if (!matchFound) {
            return null;
        }
        return matcher.group();
    }

    public static Long regexFirstInt(String src) {
        return regexFirstNumber(src, "([-|\\+]?[1-9]\\d*|0)");
    }

    public static Long regexFirstPosInt(String src) {
        return regexFirstNumber(src, "([1-9]\\d*|0)");
    }

    private static Long regexFirstNumber(String src, String pat) {
        String value = regexFirstWord(src, pat);
        if (value == null) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @Test
    public void testRegexFirstInt() {
        Long aLong = regexFirstInt("-12");
        System.out.println(aLong);
        aLong = regexFirstPosInt("0");
        System.out.println(aLong);
    }

    @Test
    public void testRegexFirstPosInt() {
        Long aLong = regexFirstPosInt("-12");
        System.out.println(aLong);
        aLong = regexFirstPosInt("0");
        System.out.println(aLong);
    }

}
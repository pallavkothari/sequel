package util;

/**
 * Created by pkothari on 7/14/16.
 */
public class TextUtil {
    public static String simplifyString(String s) {
        return s.toUpperCase().replaceAll("[^A-Za-z0-9]", "");
    }
}

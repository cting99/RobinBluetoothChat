package cting.com.robin.support.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cting on 2018/2/27.
 */

public class StringHelper {

    public static int getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            try {
                return Integer.valueOf(matcher.group(0));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
}

package org.mendora.string;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author menfre
 * @version 1.0
 * date: 2018/9/27
 * desc: 字符串工具
 */
public class StringUtil {
    private static Pattern linePattern = Pattern.compile("_(\\w)");

    /**
     * 下划线转驼峰
     *
     * @param str 待转换字符串
     * @return 转换完毕字符串
     */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 驼峰转下划线(简单写法，效率低于{@link #humpToLine2(String)})
     *
     * @param str 待转换字符串
     * @return 转换完毕字符串
     */
    public static String humpToLine(String str) {
        return str.replaceAll("[A-Z]", "_$0").toLowerCase();
    }

    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    /**
     * 驼峰转下划线,效率比上面高
     *
     * @param str 待转换字符串
     * @return 转换完毕字符串
     */
    public static String humpToLine2(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 首字母大写
     *
     * @param str 带转换字符串
     * @return 转换完毕字符串
     */
    public static String firstLetterUpper(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}


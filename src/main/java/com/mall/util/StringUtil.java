package com.mall.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 通用字符串校验、参数解析与业务单号生成工具类。
 *
 * @author 张三
 * @studentId 20240301
 */
public class StringUtil {

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static int parseInt(String value, int defaultVal) {
        if (isEmpty(value)) {
            return defaultVal;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static double parseDouble(String value, double defaultVal) {
        if (isEmpty(value)) {
            return defaultVal;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /**
     * 生成唯一订单编号，格式：ORD + yyyyMMddHHmmss + 4位随机数
     */
    public static String generateOrderId() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int rand = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "ORD" + timestamp + rand;
    }
}

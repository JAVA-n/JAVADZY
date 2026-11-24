package com.mall.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * MD5 消息摘要加密工具类，用于用户与管理员密码的不可逆加密存储与校验。
 *
 * @author 张三
 * @studentId 20240301
 */
public class MD5Util {

    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

    /**
     * 将明文字符串转换为 32 位小写十六进制 MD5 摘要
     *
     * @param input 原始明文密码
     * @return 32 位 MD5 字符串
     */
    public static String md5(String input) {
        if (input == null) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(HEX_DIGITS[(b >> 4) & 0x0f]);
                sb.append(HEX_DIGITS[b & 0x0f]);
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 加密失败", e);
        }
    }
}

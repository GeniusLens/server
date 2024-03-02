package xyz.thuray.geniuslens.server.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHAUtil {
    /**
     * 传入文本内容，返回 SHA-256 串
     *
     * @param str 文本内容
     * @return SHA-256 串
     */
    public static String SHA256(final String str) {
        return SHA(str, "SHA-256");
    }

    /**
     * 传入文本内容，返回 SHA-512 串
     *
     * @param str 文本内容
     * @return SHA-512 串
     */
    public static String SHA512(final String str) {
        return SHA(str, "SHA-512");
    }

    /**
     * md5加密
     *
     * @param str 需要加密的字符串
     * @return 加密后的字符串
     */
    public static String MD5(String str) {
        return SHA(str, "MD5");
    }

    /**
     * 字符串 SHA 加密
     *
     * @param str 需要加密的字符串
     * @return 返回加密串
     */
    private static String SHA(final String str, final String strType) {
        // 返回值
        String result = null;

        // 是否是有效字符串
        if (str != null && !str.isEmpty()) {
            try {
                // SHA 加密开始
                // 创建加密对象 并傳入加密類型
                MessageDigest messageDigest = MessageDigest.getInstance(strType);
                // 传入要加密的字符串
                messageDigest.update(str.getBytes());
                // 得到 byte 類型结果
                byte[] byteBuffer = messageDigest.digest();

                // 將 byte 轉換爲 string
                StringBuilder hexBuilder = new StringBuilder();
                // 遍歷 byte buffer
                for (byte b : byteBuffer) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) {
                        hexBuilder.append('0');
                    }
                    hexBuilder.append(hex);
                }
                // 得到返回結果
                result = hexBuilder.toString();
            } catch (NoSuchAlgorithmException e) {
                return null;
            }
        }

        return result;
    }
}
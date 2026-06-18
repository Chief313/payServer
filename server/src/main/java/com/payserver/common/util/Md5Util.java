package com.payserver.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 密码工具类
 * 注册时将明文密码 MD5 后入库；登录时对输入密码 MD5 后与库中值比对
 */
public final class Md5Util {

    private Md5Util() {
    }

    /**
     * 将明文密码转为 MD5 十六进制字符串（小写）
     *
     * @param rawPassword 明文密码
     * @return MD5 哈希值
     */
    public static String encrypt(String rawPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 算法不可用", e);
        }
    }

    /**
     * 校验明文密码与 MD5 密文是否匹配
     *
     * @param rawPassword     明文密码
     * @param encryptedPassword 数据库中的 MD5 密文
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encryptedPassword) {
        return encrypt(rawPassword).equals(encryptedPassword);
    }
}

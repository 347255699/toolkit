package org.mendora;

import java.security.MessageDigest;

public class Md5Enc {
    // 字符编码
    private static final String ENC = "UTF-8";
    // 算法
    private static final String SEC_NORMALIZE_ALG = "SHA-256";

    // 加密
    public static String encrypt(String data) throws Exception {
        MessageDigest dig = MessageDigest.getInstance(SEC_NORMALIZE_ALG);
        byte[] key = dig.digest(data.getBytes(ENC));
        return new String(key, ENC);
    }
}

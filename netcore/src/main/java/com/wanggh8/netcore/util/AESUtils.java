package com.wanggh8.netcore.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;

/**
 * @author huhuan
 * @version 2019/4/8
 */
public class AESUtils {

    public final static String AES_KEY = "aidht1234567890q";

    private static AESUtils aesUtils;

    private static AES aes;

    public static AESUtils get() {
        if (aesUtils == null) {
            aesUtils = new AESUtils();
            aes = SecureUtil.aes(AES_KEY.getBytes());
        }
        return aesUtils;
    }


    /**
     * 加密
     */
    public String encrypt(String data) {
        return StrUtil.isBlank(data) ? "" : aes.encryptBase64(data);
    }


    /**
     * 解密
     */
    public String decrypt(String data) {
        return StrUtil.isBlank(data) ? "" : aes.decryptStr(data);
    }

}

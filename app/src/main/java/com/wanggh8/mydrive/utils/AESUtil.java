package com.wanggh8.mydrive.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密工具
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/9/21
 */
public class AESUtil {

    public final static String AES_KEY = "ZL5dd3GoBXgwFv1e";

    private AESUtil() {
        throw new UnsupportedOperationException("Error...");
    }

    /**
     * 加密
     *
     * @param content 需要加密的内容
     * @return 密文
     */
    public static String encryptString(String content) {
        if (content == null) {
            return null;
        }
        return byte2HexStr(encrypt(content.getBytes(), AES_KEY));
    }

    /**
     * 解密
     *
     * @param content 待解密内容
     * @return 原文
     */
    public static String decryptString(String content) {
        byte[] bytes = decrypt(hexStr2Byte(content), AES_KEY);
        if (bytes == null) {
            return null;
        }
        return new String(bytes);
    }

    /**
     * 加密
     *
     * @param bytes    需要加密的内容
     * @param password 加密密码
     * @return 密文byte[]
     */
    private static byte[] encrypt(byte[] bytes, String password) {
        if (bytes == null) {
            return null;
        }
        try {
            SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            return cipher.doFinal(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param bytes    待解密内容
     * @param password 解密密钥
     * @return 原文byte
     */
    private static byte[] decrypt(byte[] bytes, String password) {
        if (bytes == null) {
            return null;
        }
        try {
            SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            return cipher.doFinal(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将2进制转换成16进制
     *
     * @param buf 2进制byte[]
     * @return 16进制String
     */
    private static String byte2HexStr(byte buf[]) {
        try {
            if (buf == null || buf.length < 1) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            for (byte b : buf) {
                String hex = Integer.toHexString(b & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                sb.append(hex.toUpperCase());
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将16进制转换为2进制
     *
     * @param hexStr 16进制String
     * @return 16进制byte[]
     */
    private static byte[] hexStr2Byte(String hexStr) {
        try {
            if (hexStr == null || hexStr.length() < 1) {
                return null;
            }
            byte[] result = new byte[hexStr.length() / 2];
            for (int i = 0; i < hexStr.length() / 2; i++) {
                int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
                int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
                result[i] = (byte) (high * 16 + low);
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }


}

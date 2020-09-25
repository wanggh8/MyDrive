package com.wanggh8.mydrive.utils;

import java.io.UnsupportedEncodingException;

/**
 * utf-8 工具类
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/9/23
 */
public class UTF8Util {
    public static String urlEncodeUTF8(String source) {
        String result = source;
        try {
            result = java.net.URLEncoder.encode(source, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String urlDecodeUTF8(String source) {
        String result = source;
        try {
            result = java.net.URLDecoder.decode(source, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

}

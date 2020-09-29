package com.wanggh8.netcore.bean;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.StringWriter;

import okhttp3.MediaType;

public class XMLBean {


    /** 操作成功或失败：
     	成功：0
     	失败：非0
     */
    @Element(required = false)
    public String resultCode;

    /** 对返回码的说明*/
    @Element(required = false)
    public String resultDesc;

    /**
     * 构建xml
     * @return 类序列化xml字符串
     */
    public String toXML(){
        Serializer serializer = new Persister();
        StringWriter writer = new StringWriter();
        String xml = "";
        try {
            serializer.write(this, writer);
            xml = writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }

    /**
     * 获取数据类型
     * @return
     */
    public MediaType getMediaType(){
        return MediaType.parse("application/xml; charset=utf-8");
    }

}
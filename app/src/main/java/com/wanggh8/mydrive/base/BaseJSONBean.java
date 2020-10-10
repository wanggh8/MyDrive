package com.wanggh8.mydrive.base;

import com.google.gson.GsonBuilder;

import java.io.Serializable;

/**
 * @author wanggh8
 * @version V1.0
 * @date 2020/10/10
 */
public class BaseJSONBean implements Serializable {
    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
}

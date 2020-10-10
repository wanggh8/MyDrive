package com.wanggh8.mydrive.bean;

import com.wanggh8.mydrive.base.BaseJSONBean;

/**
 * 网盘名和类型
 * @see com.wanggh8.mydrive.config.DriveType
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/10/10
 */

public class DriveBean extends BaseJSONBean {
    // 网盘名
    private String name;
    // 网盘类型
    private String type;
    // 网盘图标id
    private int iconId;

    public DriveBean() {
    }

    public DriveBean(String name, String type, int iconId) {
        this.name = name;
        this.type = type;
        this.iconId = iconId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }
}

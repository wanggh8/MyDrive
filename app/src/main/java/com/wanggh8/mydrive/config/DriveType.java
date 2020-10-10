package com.wanggh8.mydrive.config;

import com.wanggh8.mydrive.R;

/**
 * 网盘类别枚举
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/10/10
 */
public enum DriveType {
    oneDrive("OneDrive", R.drawable.ic_onedrive),
    googleDrive("Google Drive", R.drawable.ic_googledrive);

    private String typeName;
    private int typeIconId;

    DriveType(String typeName, int typeIconId) {
        this.typeName = typeName;
        this.typeIconId = typeIconId;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getTypeIconId() {
        return typeIconId;
    }
}

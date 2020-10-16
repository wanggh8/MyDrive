package com.wanggh8.mydrive.base;

import com.wanggh8.mydrive.netapi.MyDriveService;
import com.wanggh8.netcore.model.CoreNetModel;

/**
 * @author wanggh8
 * @version V1.0
 * @date 2020/10/16
 */
public class BaseModel extends CoreNetModel {

    protected MyDriveService myDriveService;

    public BaseModel() {
        this.myDriveService = getService(MyDriveService.class);
    }
}

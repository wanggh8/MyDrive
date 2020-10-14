package com.wanggh8.mydrive.bean;

import com.microsoft.identity.client.IAccount;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 网盘名和类型
 * @see com.wanggh8.mydrive.config.DriveType
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/10/13
 */

/*
(一) @Entity 定义实体
@nameInDb 在数据库中的名字，如不写则为实体中类名
@indexes 索引
@createInDb 是否创建表，默认为true,false时不创建
@schema 指定架构名称为实体
@active 无论是更新生成都刷新
(二) @Id
(三) @NotNull 不为null
(四) @Unique 唯一约束
(五) @ToMany 一对多
(六) @OrderBy 排序
(七) @ToOne 一对一
(八) @Transient 不存储在数据库中
(九) @generated 由greendao产生的构造函数或方法
 */
@Entity
public class DriveBean {

    // 网盘名
    private String name;
    // 网盘自定义名
    private String myName;
    // 网盘类型
    private String type;
    // 网盘图标id
    private int iconId;
    // 网盘用户唯一索引id
    @Id
    private String id;

    private String accessToken;

    public DriveBean() {
    }

    public DriveBean(String name, String type, int iconId) {
        this.name = name;
        this.type = type;
        this.iconId = iconId;
    }

    @Generated(hash = 581516904)
    public DriveBean(String name, String myName, String type, int iconId, String id,
            String accessToken) {
        this.name = name;
        this.myName = myName;
        this.type = type;
        this.iconId = iconId;
        this.id = id;
        this.accessToken = accessToken;
    }

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}

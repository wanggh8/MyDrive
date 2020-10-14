package com.wanggh8.mydrive.utils;

import com.wanggh8.mydrive.base.BaseApplication;
import com.wanggh8.mydrive.bean.DriveBean;
import com.wanggh8.mydrive.db.DaoSession;
import com.wanggh8.mydrive.db.DriveBeanDao;

import java.util.List;

/**
 * @author wanggh8
 * @version V1.0
 * @date 2020/10/14
 */
public class DriveDBUtil {

    private static DaoSession daoSession;

    public static int UNDO = 0;
    public static int INSERT = 1;
    public static int UPDATE = 2;
    public static int DELETE = 3;

    static {
        daoSession = BaseApplication.getInstance().getDaoSession();
    }

    /**
     * 插入DriveBean
     * @param bean DriveBean
     * @return int
     */
    public static int insert(DriveBean bean){
        if(!queryExist(bean)){
            daoSession.insert(bean);
            return INSERT;
        }
        return UNDO;
    }

    /**
     * 更新DriveBean
     * 如果不存在则新建
     *
     * @param bean
     * @return int
     */
    public static int update(DriveBean bean) {
        if (queryExist(bean)) {
            daoSession.update(bean);
            return UPDATE;
        }
        else {
            daoSession.insert(bean);
            return INSERT;
        }
    }

    /**
     * 删除
     *
     * @param bean DriveBean
     * @return int
     */
    public static int delete(DriveBean bean){
        if(queryExist(bean)){
            daoSession.delete(bean);
            return DELETE;
        }
        return UNDO;
    }

    public static int deleteById(String id){
        DriveBean bean = queryById(id);
        if(bean != null){
            daoSession.delete(bean);
            return DELETE;
        }
        return UNDO;
    }

    /**
     * 查询是否存在
     * @param bean DriveBean
     * @return
     */
    public static boolean queryExist(DriveBean bean){
        DriveBean unique = daoSession.queryBuilder(DriveBean.class)
                .where(DriveBeanDao.Properties.Id.eq(bean.getId()))
                .unique();
        return unique == null ? false : true;
    }

    /**
     * 根据DriveBean id查询单个是否存在
     *
     * @param id DriveBean id
     * @return DriveBean
     */
    public static boolean queryExistById(String id){
        DriveBean bean = daoSession.queryBuilder(DriveBean.class)
                .where(DriveBeanDao.Properties.Id.eq(id))
                .unique();
        return bean == null ? false : true;
    }

    /**
     * 根据DriveBean id查询单个
     *
     * @param id DriveBean id
     * @return DriveBean
     */
    public static DriveBean queryById(String id){
        DriveBean bean = daoSession.queryBuilder(DriveBean.class)
                .where(DriveBeanDao.Properties.Id.eq(id))
                .unique();
        return bean;
    }

    /**
     * 查询所有 DriveBean
     * @return
     */
    public static List<DriveBean> queryAll(){
        return daoSession.loadAll(DriveBean.class);
    }
}

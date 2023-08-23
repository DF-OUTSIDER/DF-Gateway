package com.easyusing.gateway.cache;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBFactory;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author outsider
 * @date 2023/8/1
 */
public class LevelDbCache {

    private static DB db = null;
    private static String dbFolder="leveldb/test.db";
    private static String charset = "utf-8";
    private static DBFactory factory = null;

    public static void initDbCache() {
        if (null == factory) {
            factory = new Iq80DBFactory();
            Options options = new Options();
            options.createIfMissing(true);
            try {
                db = factory.open(new File(dbFolder), options);
            } catch (IOException e) {
                System.out.println("levelDB启动异常");
                e.printStackTrace();
            }
        }
    }

    /**
     * 存放数据
     *
     * @param key
     * @param val
     */
    public static void put(String key, String val) {
        try {
            db.put(key.getBytes(charset), val.getBytes());
        } catch (UnsupportedEncodingException e) {
            System.out.println("编码转化异常");
            e.printStackTrace();
        }
    }

    /**
     * 根据key获取数据
     *
     * @param key
     * @return
     */
    public static String get(String key) {
        byte[] val = null;
        try {
            val = db.get(key.getBytes(charset));
        } catch (Exception e) {
            System.out.println("levelDB get error");
            e.printStackTrace();
            return null;
        }
        if (val == null) {
            return null;
        }
        return new String(val);
    }

    /**
     * 根据key删除数据
     *
     * @param key
     */
    public void delete(String key) {
        try {
            db.delete(key.getBytes(charset));
        } catch (Exception e) {
            System.out.println("levelDB delete error");
            e.printStackTrace();
        }
    }


    /**
     * 关闭数据库连接
     * 每次只要调用了initDB方法，就要在最后调用此方法
     */
    public void closeDB() {
        if (db != null) {
            try {
                db.close();
            } catch (IOException e) {
                System.out.println("levelDB 关闭异常");
                e.printStackTrace();
            }
        }
    }

}

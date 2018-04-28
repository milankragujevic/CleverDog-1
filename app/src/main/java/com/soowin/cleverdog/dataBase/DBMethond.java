package com.soowin.cleverdog.dataBase;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.soowin.cleverdog.utlis.PublicApplication;

import java.io.File;
import java.io.IOException;

/**
 * SD卡数据库的增、删、改、查方法
 * Created by hexiaotao on 2016/10/14.
 */
public class DBMethond {
    // ----以下两个成员变量是针对在SD卡中存储数据库文件使用----
    File externalDir = Environment.getExternalStorageDirectory();
    //    private File path = new File(MyApplication.databasesPath); // 数据库文件目录
    private File path;  // 数据库文件目录
    private File f = new File(PublicApplication.databasesPath + "/CleverDog.db"); // 数据库文件
    DBHelper myOpenHelper;
    SQLiteDatabase sqlitedb;

    /**
     * @return 返回值描述
     * @throws <异常类型> {@inheritDoc} 异常描述
     * @author 作者:  方法描述:创建数据库数据表如果这些数据表不存在
     * <p/>
     * 参数描述
     */
    @SuppressWarnings("static-access")
    public void onCreatAllTable() {
        path = new File(externalDir.getAbsolutePath() + "/CleverDog/DataBases");
        // 实例化默认数据库辅助操作对象
        myOpenHelper = new DBHelper(PublicApplication.getContext());
        // ----如要在SD卡中创建数据库文件，先做如下的判断和创建相对应的目录和文件----
        if (!path.exists()) { // 判断目录是否存在
            if (path.mkdirs()) {
                Log.e("dbMethond", "mkdirs-->true");
            }// 创建目录
        }
        if (!f.exists()) { // 判断文件是否存在
            try {
                f.createNewFile(); // 创建文件
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sqlitedb = SQLiteDatabase.openOrCreateDatabase(f, null);

        sqlitedb.execSQL(DBHelper.main_table);
    }

    /**
     * 插入一条数据
     * <p/>
     * tableName表名
     * values插入的键值对
     * 如：ContentValues values = new ContentValues();
     * values.put("firstPackageSeq", "1");
     */
    public void insert(String tableName, ContentValues values) {
        sqlitedb.insert(tableName, null, values);
    }

    /**
     * 初始化数据图像视频数据表
     */
    public boolean add(String tableName, String main_id, String item_name, String path,
                       String functionName) {
        Cursor c = sqlitedb.query(tableName, null,
                "main_id=? and item_name =? and path = ? and functionName = ?",
                new String[]{main_id, item_name, path, functionName}, null, null,
                null);
        if (c.getCount() != 0) {
            c.close();
            return true;
        } else {
            ContentValues values = new ContentValues();
            values.put("main_id", main_id);
            values.put("item_name", item_name);
            values.put("path", path);
            values.put("functionName", functionName);
            sqlitedb.insert(tableName, null, values);
            return true;
        }
    }

    /**
     * 插入数据
     * <p/>
     * tableName表名
     * sql
     */
    public void exexSql(String sql) {
        sqlitedb.execSQL(sql);
    }

    /**
     * 删除一掉数据
     * <p/>
     * tableName表名
     * key条件的名称
     * "key = ? and key2 = ?"
     * date条件的值
     * new String[]{data,data2}
     */
    public void delete(String tableName, String WhereKey, String[] WhereDate) {
        sqlitedb.delete(tableName, WhereKey, WhereDate);
    }

    /**
     * 删除所有数据
     * <p/>
     * tableName表名
     */
    public void deleteAllData(String tableName) {
        sqlitedb.delete(tableName, null, null);
    }

    /**
     * 更新一条数据
     * <p/>
     * tableName表名
     * values
     * 键名和新的数据值
     * key
     * Where条件的名称
     * keyValue
     * Where条件的旧值
     */
    public void update(String tableName, ContentValues values, String key,
                       String keyValue) {
        sqlitedb.update(tableName, values, key + "=?",
                new String[]{keyValue});
    }

    /**
     * 更新一条数据
     * <p/>
     * tableName表名
     * values
     * 键名和新的数据值
     * key
     * Where条件的名称
     * keyValue
     * Where条件的旧值
     */
    public void updates(String tableName, ContentValues values, String key,
                        String[] keyValue) {

        sqlitedb.update(tableName, values, key, keyValue);
    }

    /**
     * 查找数据
     * <p/>
     *
     * @param tableName 表名
     * @param WhereKey  条件的键名
     *                  如："key=? and key2 =?"
     * @param WhereDate 条件的键值
     *                  如：new String[] { data1,data2 }
     * @return cursor返回类型
     */
    public Cursor findDatas(String tableName, String WhereKey,
                            String[] WhereDate) {
        return sqlitedb.query(tableName, null, WhereKey, WhereDate,
                null, null, null);
    }

    /**
     * @return 返回值描述
     * @throws <异常类型> {@inheritDoc} 异常描述
     * @author 作者:  方法描述:具体功能描述
     * <p/>
     * sql语句
     */
    public Cursor find(String sql, String[] selectionArgs) {
        return sqlitedb.rawQuery(sql, selectionArgs);
    }

    /**
     * 查找数据表中的所有数据
     * <p/>
     * tableName表名
     *
     * @return cursor返回类型
     */
    public Cursor findDatasFromDatabase(String tableName) {
        return sqlitedb.query(tableName, null, null, null, null, null,
                null);
    }

    /**
     * 查询数据库版本号
     */
    public int getVersion() {
        return sqlitedb.getVersion();
    }

    /**
     * 设置数据库版本号
     */
    public void setVersion(int version) {
        sqlitedb.setVersion(version);
    }
}

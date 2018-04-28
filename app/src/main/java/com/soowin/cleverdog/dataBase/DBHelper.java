package com.soowin.cleverdog.dataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hexiaotao on 2016/10/14.
 */
public class DBHelper extends SQLiteOpenHelper {
    private final static int DATABASE_VERSION = 1;
    public static final String DB_NAME = "CleverDog.db";

    private static DBHelper mInstance = null;
    /**
     * 违章主表
     **/
    public static final String MAIN_TABLE = "main_table";
    /**
     * 违章主表
     **/
    public static final String main_table = "CREATE TABLE IF NOT EXISTS main_table"
            + "(id integer PRIMARY KEY autoincrement,latitude varchar(20),"
            + "longitude varchar(20),denoter_name varchar(20),end_time datetime,"
            + "denoter_type integer,denoter_id integer,star_time datetime,"
            + "direction integer)";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    /**
     * 单例模式
     **/
    public static synchronized DBHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBHelper(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(main_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (newVersion != oldVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS main_table");
            onCreate(sqLiteDatabase);
        }
    }

    public Cursor query(String sql, String[] args) {// 返回操作结果指针
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, args);
        return cursor;
    }
}
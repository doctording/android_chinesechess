package com.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

	private static final String DATABASE_NAME = "chess.db";
	private static final int DATABASE_VERSION = 1;

	//自带的构造方法
	 public DBHelper(Context context, String name, CursorFactory factory,
	   int version) {
	   super(context, name, factory, version);
	}
		 
	public DBHelper(Context context) {
		this(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		//创建用户信息表
		String usertable = "create table if not exists Chessmov" +  
            "(id INTEGER," +
            "fromId INTEGER," +
            "fromX INTEGER," +
            "fromY INTEGER," +
		   "toId INTEGER," +
		  "toX INTEGER," +
		  "toY INTEGER )" ;
		db.execSQL(usertable);
	}
	
	// 如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("ALTER TABLE chessmov ADD COLUMN other STRING");
	
	}
	
}
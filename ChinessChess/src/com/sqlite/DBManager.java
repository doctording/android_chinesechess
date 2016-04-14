package com.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

	private DBHelper helper;
	private  SQLiteDatabase db;

	/**
	 * 构造函数
	 * @param context
	 */
	public DBManager(Context context) {
		helper = new DBHelper(context); // 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,mFactory);
		// 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
		db = helper.getWritableDatabase();
		db = helper.getReadableDatabase();
	}

	/**
	 * 清空数据库中的内容
	 * @return
	 */
	public boolean chessmov_deleteAll(){
		boolean b = false;
		try {
			db.execSQL("delete from Chessmov");
			b =  true;
		} catch (SQLException e) {
			b =false ;
		}
		return b;
	}
	
	/**
	 * 插入数据
	 * @param Chessmov
	 * @return
	 */
	public boolean chessmov_insertChessmov(ModelChessmov Chessmov) {
		boolean b = false;
		db.beginTransaction();
		try {
			
			Cursor c = db.rawQuery(
					"select *from Chessmov where id=?" , new String[] { 
							Chessmov.getId() 
							});
			if (c == null || c.getCount() <= 0) {
				db.execSQL("insert into Chessmov values(?, ?,?,?, ?,?,?)", new Object[] {
						Chessmov.getId(),
						Chessmov.getFromId(),
						Chessmov.getFromX(),
						Chessmov.getFromY(),
						Chessmov.getToId(),
						Chessmov.getToX(),
						Chessmov.getToY()  });
				b = true;
			}
			c.close();
			db.setTransactionSuccessful(); // 这个事务成功的也必须加上
		}catch (SQLException e) {
			db.endTransaction();
			b = false ;
		}finally{
			db.endTransaction(); // 最后事务一定要结束？？
		}
		return b;
	} 
	
	/**
	 * 删除一个Chessmov对象
	 * @param s
	 * @return
	 */
	public boolean Chessmov_deleteById(int id) {
		boolean b = false;
		try {
			db.execSQL("delete *from Chessmov where id=?",
					new Object[] { id });
			b =  true;
		}catch (SQLException e) {
			b =false ;
		}
		return b;
	}
	
	/**
	 * 查询得到一个用户
	 * @param tel
	 * @return
	 */
	public ModelChessmov Chessmov_getById(int id) {
		ModelChessmov u = null ;
		Cursor c = db.rawQuery("select * from Chessmov where id=?" , 
				new String[] { }) ;
		while (c.moveToNext()) {
			u = new ModelChessmov();
			u.id = c.getString(c.getColumnIndex("id"));
			u.fromId = c.getInt( c.getColumnIndex("fromId") );
			u.fromX = c.getInt( c.getColumnIndex("fromX") );
			u.fromY = c.getInt( c.getColumnIndex("fromY") );
			u.toId = c.getInt( c.getColumnIndex("toId") );
			u.toX = c.getInt( c.getColumnIndex("toX") );
			u.toY = c.getInt( c.getColumnIndex("toY") );
			
		}
		c.close() ;
		return u ;
	}
	
	/**
	 * 直接查表得到所有对象
	 * @return
	 */
	public List<ModelChessmov> Chessmov_getAll() {
		List<ModelChessmov> models = new ArrayList<ModelChessmov>();
		ModelChessmov u = null ;
		Cursor c = db.rawQuery("select * from Chessmov" , null) ;
		while (c.moveToNext()) {
			u = new ModelChessmov();
			u.id = c.getString(c.getColumnIndex("id"));
			u.fromId = c.getInt( c.getColumnIndex("fromId") );
			u.fromX = c.getInt( c.getColumnIndex("fromX") );
			u.fromY = c.getInt( c.getColumnIndex("fromY") );
			u.toId = c.getInt( c.getColumnIndex("toId") );
			u.toX = c.getInt( c.getColumnIndex("toX") );
			u.toY = c.getInt( c.getColumnIndex("toY") );
			models.add(u);
		}
		c.close() ;
		return models ;
	}
	
}
package com.example.ppmusic.utils;

import java.util.ArrayList;
import java.util.List;

import com.example.ppmusic.bean.MusicInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class DBUtils {

	// 新建db
	public static void createDB(Context context) {
		// 创建StuDBHelper对象
		StuDBHelper dbHelper = new StuDBHelper(context, "stu_db", null, 1);
		// 得到一个可读的SQLiteDatabase对象
		SQLiteDatabase db = dbHelper.getReadableDatabase();
	}

	public static void clearTable(Context context) {
		StuDBHelper dbHelper = new StuDBHelper(context, "stu_db", null, 1);
		// 得到一个可写的数据库
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		// 删除表示例：
		// db.execSQL("DROP TABLE CUSTOMERS")
		// 清除表中所有记录：
		db.execSQL("DELETE FROM stu_table");
	}

	public static boolean insertDB(Context context, MusicInfo musicInfo) {
		StuDBHelper dbHelper = new StuDBHelper(context, "stu_db", null, 1);
		// 得到一个可写的数据库
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		List<MusicInfo> mlist = queryAllDB(context);
		if (mlist.size() >= 1) {
			for (int i = 0; i < mlist.size(); i++) {
				if (mlist.get(i).getTitle().equals(musicInfo.getTitle())
						&& !musicInfo.getTitle().isEmpty()) {
					db.close();
					return false;
				}
			}
		}

		// 生成ContentValues对象 //key:列名，value:想插入的值
		ContentValues cv = new ContentValues();
		// 往ContentValues对象存放数据，键-值对模式
		// cv.put("id", Math.random()*20);
		cv.put("title",
				musicInfo.getTitle() == null ? "" : musicInfo.getTitle());
		cv.put("artist", musicInfo.getArtist());
		cv.put("album", musicInfo.getAlbum());
		cv.put("duration", musicInfo.getDuration());
		cv.put("url", musicInfo.getUrl());
		cv.put("favourite", musicInfo.isFavourite()?1:0);
		// 调用insert方法，将数据插入数据库
		db.beginTransaction();
		db.insert("stu_table", null, cv);
		db.setTransactionSuccessful();
		db.endTransaction();
		// 关闭数据库
		db.close();
		return true;
	}

	public static boolean insertAllDB(Context context, List<MusicInfo> musiclist) {
		StuDBHelper dbHelper = new StuDBHelper(context, "stu_db", null, 1);
		// 得到一个可写的数据库
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String sql = "insert into stu_table(title,artist,album,duration,url,favourite) values(?,?,?,?,?,?)";

		List<MusicInfo> mlist = queryAllDB(context);
		db.beginTransaction();
		for (MusicInfo line : musiclist) {
			boolean isExit = false;
			for (int i = 0; i < mlist.size(); i++) {
				if (mlist.get(i).getTitle().equals(line.getTitle())) {
					isExit = true;
					break;
				}
			}
			if (!isExit) {

				SQLiteStatement stat = db.compileStatement(sql);
				stat.bindString(1, line.getTitle());
				stat.bindString(2, line.getArtist());
				// stat.bindLong(3, line.getAlbum());
				stat.bindString(3, line.getAlbum());
				stat.bindLong(4, line.getDuration());
				stat.bindString(5, line.getUrl());
				stat.bindLong(6, line.isFavourite()? 1:0);
				stat.executeInsert();
			}
			/*
			 * Cursor cursor = db.query("stu_table", new String[] { "title",
			 * "artist", "album", "duration" }, "title=?", new String[] {
			 * line.getTitle() }, null, null, null); if (cursor.getCount() == 0)
			 * {
			 * 
			 * }
			 */

		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		return true;

	}

	public static MusicInfo queryDB(Context context) {
		StuDBHelper dbHelper = new StuDBHelper(context, "stu_db", null, 1);
		// 得到一个可写的数据库
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		// 参数1：表名
		// 参数2：要想显示的列
		// 参数3：where子句
		// 参数4：where子句对应的条件值
		// 参数5：分组方式
		// 参数6：having条件
		// 参数7：排序方式
		Cursor cursor = db.query("stu_table", new String[] { "id", "title",
				"artist", "album", "duration","favourite" }, "title=?",
				new String[] { "1" }, null, null, null);
		MusicInfo musicInfo = new MusicInfo();
		if (cursor.getCount() == 0) {
			// 关闭数据库
			db.close();
			return null;
		}
		while (cursor.moveToNext()) {
			String name = cursor.getString(cursor.getColumnIndex("title"));
			musicInfo.setTitle(name);
			int duration = cursor.getColumnIndex("duration");
			musicInfo.setDuration(duration);
			musicInfo.setArtist("TFboy");
			musicInfo.setAlbum("leftandright");
		}
		// 关闭数据库
		db.close();
		return musicInfo;
	}

	public static MusicInfo queryById(Context context, int id) {
		StuDBHelper dbHelper = new StuDBHelper(context, "stu_db", null, 1);
		// 得到一个可写的数据库
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		// 参数1：表名
		// 参数2：要想显示的列
		// 参数3：where子句
		// 参数4：where子句对应的条件值
		// 参数5：分组方式
		// 参数6：having条件
		// 参数7：排序方式
		Cursor cursor = db.query("stu_table", new String[] { "id", "title",
				"artist", "album", "duration" ,"url","favourite" }, "id=?",
				new String[] { "" + id }, null, null, null);
		MusicInfo musicInfo = new MusicInfo();
		if (cursor.getCount() < 1) {
			// 关闭数据库
			db.close();
			return null;
		} else {
			while (cursor.moveToNext()) {
				musicInfo.setId(id);
				String name = cursor.getString(cursor.getColumnIndex("title"));
				musicInfo.setTitle(name);
				String artist = cursor.getString(cursor
						.getColumnIndex("artist"));
				musicInfo.setArtist(artist);
				int duration = cursor.getInt(cursor.getColumnIndex("duration"));
				musicInfo.setDuration(duration);
				String album = cursor.getString(cursor.getColumnIndex("album"));
				musicInfo.setAlbum(album);
				String url = cursor.getString(cursor.getColumnIndex("url"));
				musicInfo.setUrl(url);
				boolean favourite = cursor.getInt(cursor.getColumnIndex("favourite"))==1?true:false;
				musicInfo.setFavourite(favourite);
			}
		}
		// 关闭数据库
		db.close();
		return musicInfo;
	}

	public static MusicInfo queryDB(Context context, MusicInfo mInfo) {
		StuDBHelper dbHelper = new StuDBHelper(context, "stu_db", null, 1);
		// 得到一个可写的数据库
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		// 参数1：表名
		// 参数2：要想显示的列
		// 参数3：where子句
		// 参数4：where子句对应的条件值
		// 参数5：分组方式
		// 参数6：having条件
		// 参数7：排序方式
		Cursor cursor = db.query("stu_table", new String[] { "id", "title",
				"artist", "album", "duration","favourite" }, "title=?",
				new String[] { "1" }, null, null, null);
		MusicInfo musicInfo = new MusicInfo();
		if (cursor.getCount() == 0) {
			// 关闭数据库
			db.close();
			return null;
		}
		while (cursor.moveToNext()) {
			String name = cursor.getString(cursor.getColumnIndex("title"));
			musicInfo.setTitle(name);
			int duration = cursor.getColumnIndex("duration");
			musicInfo.setDuration(duration);
			musicInfo.setArtist("TFboy");
			musicInfo.setAlbum("leftandright");
		}
		// 关闭数据库
		db.close();
		return musicInfo;
	}

	public static List<MusicInfo> queryAllDB(Context context) {
		StuDBHelper dbHelper = new StuDBHelper(context, "stu_db", null, 1);
		// 得到一个可写的数据库
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		// 参数1：表名
		// 参数2：要想显示的列
		// 参数3：where子句
		// 参数4：where子句对应的条件值
		// 参数5：分组方式
		// 参数6：having条件
		// 参数7：排序方式
		Cursor cursor = db.query("stu_table", new String[] { "title", "artist",
				"album", "duration", "url","favourite" }, null, null, null, null, null);
		cursor = db.rawQuery("SELECT* FROM stu_table", null);
		List<MusicInfo> mList = new ArrayList<MusicInfo>();
		while (cursor.moveToNext()) {
			MusicInfo musicInfo = new MusicInfo();
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			musicInfo.setId(id);
			String name = cursor.getString(cursor.getColumnIndex("title"));
			musicInfo.setTitle(name);
			String artist = cursor.getString(cursor.getColumnIndex("artist"));
			musicInfo.setArtist(artist);
			int duration = cursor.getInt(cursor.getColumnIndex("duration"));
			musicInfo.setDuration(duration);
			String album = cursor.getString(cursor.getColumnIndex("album"));
			musicInfo.setAlbum(album);
			String url = cursor.getString(cursor.getColumnIndex("url"));
			musicInfo.setUrl(url);
			boolean favourite = cursor.getInt(cursor.getColumnIndex("favourite"))==1?true:false;
			musicInfo.setFavourite(favourite);
			mList.add(musicInfo);
			System.out.println("query------->");
		}
		// 关闭数据库
		db.close();
		return mList;
	}
	public static List<MusicInfo> queryAllFavouriteList(Context context, int favourite) {
		StuDBHelper dbHelper = new StuDBHelper(context, "stu_db", null, 1);
		// 得到一个可写的数据库
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		// 参数1：表名
		// 参数2：要想显示的列
		// 参数3：where子句
		// 参数4：where子句对应的条件值
		// 参数5：分组方式
		// 参数6：having条件
		// 参数7：排序方式
		Cursor cursor = db.query("stu_table", new String[] { "id", "title",
				"artist", "album", "duration" ,"url", "favourite"}, "favourite=?",
				new String[] { "" + favourite }, null, null, null);

		List<MusicInfo> mList = new ArrayList<MusicInfo>();
		if (cursor.getCount() < 1) {
			// 关闭数据库
			db.close();
			return mList;
		} else {
			while (cursor.moveToNext()) {
				MusicInfo musicInfo = new MusicInfo();
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				musicInfo.setId(id);
				String name = cursor.getString(cursor.getColumnIndex("title"));
				musicInfo.setTitle(name);
				String artist = cursor.getString(cursor
						.getColumnIndex("artist"));
				musicInfo.setArtist(artist);
				int duration = cursor.getInt(cursor.getColumnIndex("duration"));
				musicInfo.setDuration(duration);
				String album = cursor.getString(cursor.getColumnIndex("album"));
				musicInfo.setAlbum(album);
				String url = cursor.getString(cursor.getColumnIndex("url"));
				musicInfo.setUrl(url);
				musicInfo.setFavourite(favourite==1?true:false);
				mList.add(musicInfo);
			}
		}
		// 关闭数据库
		db.close();
		return mList;
	}
	
	public static boolean modifyDB(Context context, MusicInfo mInfo) {
		StuDBHelper dbHelper = new StuDBHelper(context, "stu_db", null, 1);
		// 得到一个可写的数据库
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put("title",
				mInfo.getTitle() == null ? "" : mInfo.getTitle());
		cv.put("artist", mInfo.getArtist());
		cv.put("album", mInfo.getAlbum());
		cv.put("duration", mInfo.getDuration());
		cv.put("url", mInfo.getUrl());
		cv.put("favourite", mInfo.isFavourite()?1:0);
		// where 子句 "?"是占位符号，对应后面的"1",
		String whereClause = "id=?";
		String[] whereArgs = { String.valueOf(mInfo.getId()) };
		// 参数1 是要更新的表名
		// 参数2 是一个ContentValeus对象
		// 参数3 是where子句
		db.update("stu_table", cv, whereClause, whereArgs);
		// 关闭数据库
		db.close();
		return true;
	}
}

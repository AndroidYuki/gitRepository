package com.mykj.code.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.mykj.code.R;
import com.mykj.code.util.Common;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private final static String DATABASE_NAME = Common.DATABASE_NAME;
	private final static String DATABASE_PATH = Common.ROOT_PATH;
	private final static int DATABASE_VERSION = 1;
	public Context ct;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_PATH + DATABASE_NAME, null, DATABASE_VERSION);
		ct = context;
//
//		File dbFile = new File(DATABASE_PATH, DATABASE_NAME);
//		if (!dbFile.exists()) {
//			copyDBFromRaw2SD(context);
//		}
	}

	public static void copyDBFromRaw2SD(Context context) {

		File dbFile = new File(DATABASE_PATH, DATABASE_NAME);
		if (dbFile.exists()) {
			return;
		}

		// 数据库文件在SDCard中的目录
		File dir = null;
		FileOutputStream fos = null;
		InputStream is = null;
		try {
			dir = new File(DATABASE_PATH);
			// 如果目录不存在则创建
			if (!dir.exists())
				dir.mkdir();

			File file = new File(DATABASE_PATH, DATABASE_NAME);
			// 如果文件不存在则复制文件
			if (!file.exists()) {
				// 打开应用中raw目录下的数据库文件
				is = context.getResources().openRawResource(R.raw.mykj);
				fos = new FileOutputStream(file);

				byte[] buffer = new byte[8192];
				int count = 0;

				// 开始复制
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 虽然通过SqliteFactory类导入一个数据库，但是没有journal文件，
	 * 所以，仍然会执行OnCreate方法，执行完后，数据库版本变成当前类中的版本
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		onUpgrade(db, connectionSource, 0, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int oldVersion, int newVersion) {
		
	}
//	@Override
//	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
//		
//			try {
//				TableUtils.createTable(connectionSource, PACS.class);
//				TableUtils.createTable(connectionSource, HIS.class);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//	
//		
//	}
//
//	@Override
//	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion,
//			int newVersion) {
//		try {
//			TableUtils.dropTable(connectionSource, PACS.class, true);
//			TableUtils.dropTable(connectionSource, HIS.class, true);
//			onCreate(db, connectionSource);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}

}

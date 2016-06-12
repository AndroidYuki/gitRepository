package com.mykj.code.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.KeyEvent;
import android.widget.Toast;
import cn.pedant.SweetAlert.widget.SweetAlertDialog;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.mykj.code.R;
import com.mykj.code.db.DatabaseHelper;
import com.mykj.code.util.Common;

public class CommonActivity extends Activity {
	private DatabaseHelper databaseHelper = null;
	CommonActivity activity;
	public DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this,
					DatabaseHelper.class);
		}
		return databaseHelper;
	}
	 // 定义一个变量，来标识是否退出
    private static boolean isExit = false;

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		/*
		 * 释放资源
		 */
		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
	}

	/**
	 * 获取当前时间
	 * */
	public static String getNow() {
		String time;
		Date mydate = new Date(); // 获取当前日期Date对象
		String format = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
		time = sdf.format(mydate);
		return time;
	}

	/**
	 * BASE64加密
	 */
	public String encode(String code) {

		byte[] encode = null;
		String encodeString = null;
		try {
			encode = Base64.encode(code.trim().getBytes("GB2312"),
					Base64.NO_WRAP);
			encodeString = new String(encode, "GB2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodeString;
	}

	/**
	 * 将日期格式由yyyy-MM-dd改为yyyyMMdd
	 * 
	 * @author YuKi 2015年12月21日 11:43:47
	 */
	public String initTime(String time) {
		String timeY = time.substring(0, 4);
		String timeM = time.substring(5, 7);
		String timeD = time.substring(8, 10);
		StringBuffer timeSub = new StringBuffer();
		timeSub.append(timeY).append(timeM).append(timeD);
		String timeS = timeSub.toString();
		return timeS;
	}

	/**
	 * 日期格式转换
	 * */
	public String tranTime(String time) {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		try {
			time = sdf2.format(sdf1.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}

	/**
	 * His 编码逻辑
	 * */

	public String hisCode(String name, String time) {
		String nameCode = encode(name);
		String timeCode = encode(time);
		String sumCode = nameCode.concat(timeCode);

		/*
		 * 客户名称与到期时间的ASCII码总和
		 */
		char[] sumChars = sumCode.toCharArray();// ASCII码转换
		int sumTemp = 0;
		for (int i = 0; i < sumChars.length; i++) {
			if (i != sumChars.length - 1) {
				sumTemp += sumChars[i];
			} else {
				sumTemp += sumChars[i];
			}
		}
		String sumASC = String.valueOf(sumTemp);

		/*
		 * 到期时间的ASCII码 4位一组连接
		 */
		StringBuffer sbt = new StringBuffer();
		String timeSub = initTime(time);
		char[] timeChars = timeSub.toCharArray();

		for (int i = 0; i < timeChars.length; i++) {

			if (i != timeChars.length - 1) {
				sbt.append((int) timeChars[i]);
			} else {
				sbt.append((int) timeChars[i]);
			}
		}
		sbt.insert(4, "-").insert(9, "-").insert(14, "-");
		String timeASC = sbt.toString();
		StringBuffer sbCode = new StringBuffer();
		sbCode.append(sumASC).append("-").append(timeASC);
		return sbCode.toString();

	}

	/**
	 * Pacs 编码逻辑
	 * */

	public String pacsCode(String name, String time) {
		String workStationCount = "0505";
		String vision = "网络版PACS";// 版本类型
		time = initTime(time);
		String nameCode = encode(name.trim());// 客户名的加密字符串
		String timeCode = encode(time.trim());// 到期时间的加密字符串
		String visionCode = encode(vision.trim());// 版本类型的加密字符串
		String workStationCountCode = encode(workStationCount.trim());

		StringBuffer sb = new StringBuffer();
		sb.append(nameCode).append(visionCode).append(timeCode)
				.append(workStationCountCode);
		String sum = sb.toString().trim();

		/*
		 * 所有字符的ASCII码的总和
		 */
		char[] sumChars = sum.toCharArray();// ASCII码转换
		int sumTemp = 0;
		for (int i = 0; i < sumChars.length; i++) {
			if (i != sumChars.length - 1) {
				sumTemp += sumChars[i];
			} else {
				sumTemp += sumChars[i];
			}
		}
		String sumASC = String.valueOf(sumTemp);

		/*
		 * 版本类型的ASCII码
		 */
		String visions = name.concat(vision);
		String visionsCode = encode(visions.trim());

		char[] visionsChars = visionsCode.toCharArray();
		int visionsTemp = 0;
		for (int i = 0; i < visionsChars.length; i++) {
			if (i != visionsChars.length - 1) {
				visionsTemp += visionsChars[i];
			} else {
				visionsTemp += visionsChars[i];
			}
		}

		String visionACS = String.valueOf(visionsTemp);

		/*
		 * 到期时间的ASCII码 4位一组连接
		 */
		StringBuffer sbt = new StringBuffer();
		char[] timeChars = time.toCharArray();

		for (int i = 0; i < timeChars.length; i++) {

			if (i != timeChars.length - 1) {
				sbt.append((int) timeChars[i]);
			} else {
				sbt.append((int) timeChars[i]);
			}
		}

		sbt.insert(4, "-").insert(9, "-").insert(14, "-");
		String timeASC = sbt.toString();
		/*
		 * 站点数的ASCII码
		 */
		StringBuffer sbc = new StringBuffer();
		char[] stationChars = workStationCount.toCharArray();
		for (int i = 0; i < stationChars.length; i++) {
			if (i != stationChars.length - 1) {
				sbc.append((int) stationChars[i]);
			} else {
				sbc.append((int) stationChars[i]);
			}
		}
		sbc.insert(4, "-");
		String countASC = sbc.toString();
		StringBuffer sbCode = new StringBuffer();
		sbCode.append(sumASC).append("-").append(visionACS).append("-")
				.append(timeASC).append("-").append(countASC);
		return sbCode.toString();

	}

	/**
	 * 复制内容到剪贴板
	 * */
	public void copyToBoard(final String content) {

		try {
			ClipboardManager clipboardManager = (ClipboardManager) this
					.getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData myClip;
			myClip = ClipData.newPlainText("code", content);
			clipboardManager.setPrimaryClip(myClip);
			new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
			.setTitleText("复制成功").setContentText("已成功复制到系统剪贴板")
			.show();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}
	
	 /**
		 * 将raw目录下的数据库文件copy到相应目录下
		 * @param context
		 */
		public static void copyDBFromRaw2SD(Context context) {
		
			
			 File dbFile = new File(Common.ROOT_PATH, Common.DATABASE_NAME);
			 if(dbFile.exists()){
				 return;
			 }
			 
			// 数据库文件在SDCard中的目录
			File dir = null;
			FileOutputStream fos = null;
			InputStream is = null;
			try {
				dir = new File(Common.ROOT_PATH);
				// 如果目录不存在则创建
				System.out.println("文件是否存在： " + dir.exists());
				if (!dir.exists())
					dir.mkdir();

				File file = new File(Common.ROOT_PATH, Common.DATABASE_NAME);
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

}

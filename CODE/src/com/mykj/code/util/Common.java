package com.mykj.code.util;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Environment;
import android.util.Base64;



public class Common {
	public final static String ROOT_PATH = Environment.getExternalStorageDirectory().getPath() + "/com.mykj.code/";
	public final static String DATABASE_NAME = "mykj.db3";
	
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
		SimpleDateFormat sdf2 =  new SimpleDateFormat("yyyyMMdd");
		try {
			time = sdf2.format(sdf1.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}
	
	
}

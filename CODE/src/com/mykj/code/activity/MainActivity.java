package com.mykj.code.activity;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.pedant.SweetAlert.widget.CustomDialog;
import cn.pedant.SweetAlert.widget.SweetAlertDialog;

import com.mykj.code.R;
import com.mykj.code.dao.CodeDao;
import com.mykj.code.vo.CODE;

public class MainActivity extends CommonActivity implements OnClickListener {
	private EditText nameEditText;
	private TextView endTime;
	private Button createButton;// 生成按钮
	private Button clearButton;// 清空
	private RelativeLayout exit;// 退出
	private RelativeLayout history;// 历史
	private String name, time, his, pacs;// 客户名称，到期时间，生成的注册码
	private int year, month, day;

	private CODE bean = new CODE();

	private CustomDialog dialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		copyDBFromRaw2SD(this);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		nameEditText = (EditText) findViewById(R.id.name);
		endTime = (TextView) findViewById(R.id.time);
		createButton = (Button) findViewById(R.id.create);
		clearButton = (Button) findViewById(R.id.btn_clear);
		exit = (RelativeLayout) findViewById(R.id.btn_exit);
		history = (RelativeLayout) findViewById(R.id.btn_history);

		endTime.setOnClickListener(this);
		createButton.setOnClickListener(this);
		clearButton.setOnClickListener(this);
		exit.setOnClickListener(this);
		history.setOnClickListener(this);

		// 初始化Calendar日历对象
		Calendar mycalendar = Calendar.getInstance(Locale.CHINA);
		Date mydate = new Date(); // 获取当前日期Date对象
		mycalendar.setTime(mydate);// //为Calendar对象设置时间为当前日期
		year = mycalendar.get(Calendar.YEAR); // 获取Calendar对象中的年
		month = mycalendar.get(Calendar.MONTH);// 获取Calendar对象中的月
		day = mycalendar.get(Calendar.DAY_OF_MONTH);// 获取这个月的第几天

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/*
		 * 到期时间
		 */
		case R.id.time:
			// 创建DatePickerDialog对象
			DatePickerDialog dpd = new DatePickerDialog(this, Datelistener,
					year, month, day);
			dpd.show();// 显示DatePickerDialog组件
			break;
		/*
		 * 生成
		 */
		case R.id.create:
			name = nameEditText.getText().toString().trim();
			time = endTime.getText().toString().trim();
			if (name == null || name.equals("")) {
				new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
						.setTitleText("请输入客户名称！").setContentText("客户名称未填写")
						.show();
				nameEditText.requestFocus();
				return;
			} else if (time == null || time.equals("")) {
				new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
						.setTitleText("请输入到期时间！").setContentText("到期时间未填写")
						.show();
				endTime.requestFocus();
				return;
			} else {
				his = hisCode(name, time);
				pacs = pacsCode(name, time);
				Boolean isSucess = saveData();
				if (isSucess) {
					CustomDialog.Builder customBuilder = new CustomDialog.Builder(
							MainActivity.this);
					customBuilder
							.setTitle(name + "注册码")
							.setMessage("体检: " + his + "\n" + "Pacs: " + pacs)
							.setPositiveButton("复制",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											Log.i("-----------",
													"ClickPositiveButton");
											// 复制逻辑

											String content = name + "\n"
													+ "体检: " + his + "\n"
													+ "Pacs: " + pacs;
											dialog.dismiss();
											copyToBoard(content);

										}

									})
							.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											Log.i("-----------",
													"ClickNagativeButton");
											dialog.dismiss();
										}
									});

					dialog = customBuilder.create();
					dialog.show();

				} else {
					new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
							.setTitleText("保存失败").setContentText("再试一次吧")
							.show();
				}
			}

			break;
		// 清空按钮
		case R.id.btn_clear:
			nameEditText.setText("");
			endTime.setText("");
			break;
		// 退出按钮
		case R.id.btn_exit:
			MainActivity.this.finish();
			break;

		// 历史按钮
		case R.id.btn_history:
			Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
			startActivity(intent);

			break;
		default:
			break;
		}
	}

	private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener() {
		/**
		 * params：view：该事件关联的组件 params：myyear：当前选择的年 params：monthOfYear：当前选择的月
		 * params：dayOfMonth：当前选择的日
		 */
		@Override
		public void onDateSet(DatePicker view, int myyear, int monthOfYear,
				int dayOfMonth) {

			// 修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
			year = myyear;
			month = monthOfYear;
			day = dayOfMonth;
			// 更新日期
			updateDate();

		}

		// 当DatePickerDialog关闭时，更新日期显示
		private void updateDate() {
			// 在TextView上显示日期
			endTime.setText(String.format("%d-%02d-%02d", year, month + 1, day));
		}
	};

	/**
	 * 保存数据
	 * */
	private Boolean saveData() {
		try {
			bean.setNAME(name);
			bean.setTIME(time);
			bean.setHIS(his);
			bean.setPACS(pacs);

			CodeDao dao = new CodeDao(this);
			dao.save(bean);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

}

package com.mykj.code.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import cn.pedant.SweetAlert.widget.SweetAlertDialog;

import com.mykj.code.R;
import com.mykj.code.adapter.MyListAdapter;
import com.mykj.code.dao.CodeDao;
import com.mykj.code.vo.CODE;

/**
 * 历史记录界面
 * */
public class HistoryActivity extends CommonActivity implements OnClickListener {
	private RelativeLayout exit;// 返回
	private RelativeLayout select;// 查询
	private Button delete;// 删除
	private Button seleteAll;// 全选
	private ListView listView;// 列表
	private MyListAdapter adapter;
	private AlertDialog mAlertDialog;
	private AlertDialog sAlertDialog;// 点击查询按妞的对话框
	private AlertDialog selectAlertDialog;// 长按查询结果显示的对话框

	private CodeDao dao;
	private List<CODE> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);

		exit = (RelativeLayout) findViewById(R.id.btnExit);
		select = (RelativeLayout) findViewById(R.id.btnSelect);
		delete = (Button) findViewById(R.id.btnDelete);
		seleteAll = (Button) findViewById(R.id.btnSelectAll);
		listView = (ListView) findViewById(R.id.lvListView);

		exit.setOnClickListener(this);
		select.setOnClickListener(this);
		delete.setOnClickListener(this);
		seleteAll.setOnClickListener(this);
		/*
		 * 列表的点击事件
		 */

		initData();
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				final int index = position;
				/*
				 * 初始化数据
				 */
				String strHis = list.get(index).getHIS();// 体检注册码
				String strPacs = list.get(index).getPACS();// Pacs注册码
				final String strName = list.get(index).getNAME();// 客户名称
				String strTime = list.get(index).getTIME();// 到期时间
				final String str = strName + "\n" + "体检注册码:" + strHis + "\n"
						+ "Pacs注册码:" + strPacs;// 全部
				final String strNameHis = strName + "\n" + "体检注册码:" + strHis;// 体检
				final String strNamePacs = strName + "\n" + "Pacs注册码:"
						+ strPacs;// Pacs
				/*
				 * 弹出对话框，包括四个选项：1.全部 2.体检注册码 3.Pacs注册码 4. 客户名称
				 */
				String[] content = new String[] { "体检注册码", "Pacs注册码", "客户名称",
						"复制全部" };
				ListAdapter listAdapter = new ArrayAdapter<String>(
						HistoryActivity.this, R.layout.dialog_item, content);

				LayoutInflater inflater = LayoutInflater
						.from(HistoryActivity.this);
				View layout = inflater.inflate(R.layout.my_dialog, null);

				ListView lv = (ListView) layout.findViewById(R.id.lv_content);
				TextView tv = (TextView) layout.findViewById(R.id.tv_title);
				tv.setText("请选择您要复制的内容");
				lv.setAdapter(listAdapter);

				lv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						switch (position) {
						case 0:
							copyToBoard(strNameHis);
							mAlertDialog.dismiss();
							break;
						case 1:
							copyToBoard(strNamePacs);
							mAlertDialog.dismiss();
							break;
						case 2:
							copyToBoard(strName);
							mAlertDialog.dismiss();
							break;
						case 3:
							copyToBoard(str);
							mAlertDialog.dismiss();
							break;
						default:
							break;
						}
					}
				});
				AlertDialog.Builder builder = new AlertDialog.Builder(
						HistoryActivity.this);
				mAlertDialog = builder.create();
				mAlertDialog.show();
				mAlertDialog.getWindow().setContentView(layout);
				mAlertDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				return true;
			}

		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/*
		 * 返回
		 */
		case R.id.btnExit:
			HistoryActivity.this.finish();
			break;

		/*
		 * 查询
		 */
		case R.id.btnSelect:
			dao = new CodeDao(this);
			LayoutInflater inflater = LayoutInflater.from(HistoryActivity.this);
			View view = inflater.inflate(R.layout.select_dialog, null);
			TextView title = (TextView) view.findViewById(R.id.title);
			title.setText("查询");

			final EditText editText = (EditText) view.findViewById(R.id.edit);
			Button nagButton = (Button) view.findViewById(R.id.negativeButton);// 取消
			Button posButton = (Button) view.findViewById(R.id.positiveButton);// 查询
			nagButton.setText("取消");
			posButton.setText("查询");

			posButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// FIXME
					//首先判断用户是否输入
					if (editText.getText().toString().trim() != null && !((editText.getText().toString().trim()).equals(""))) {
						List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
						final List<CODE> codes = dao.queryByString(editText
								.getText().toString().trim());// 模糊查询
						LayoutInflater inflater = LayoutInflater
								.from(HistoryActivity.this);
						View layout = inflater.inflate(R.layout.my_dialog, null);
						/*
						 * 将查询到的数据遍历出来存放到map list中
						 */
						if (codes != null && codes.size() > 0) {

							for (CODE code : codes) {
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("NAME", code.getNAME());
								map.put("TIME", code.getTIME());
								map.put("HIS", code.getHIS());
								map.put("PACS", code.getPACS());
								maps.add(map);
							}
							// 对话框弹窗提示 点击item 定位到相应的列表项中

							ListView lv = (ListView) layout
									.findViewById(R.id.lv_content);
							TextView tv = (TextView) layout
									.findViewById(R.id.tv_title);
							tv.setText("查询结果");
							ListAdapter adapter = new SimpleAdapter(
									HistoryActivity.this, maps,
									R.layout.select_item, new String[] { "NAME",
											"TIME", "HIS", "PACS" }, new int[] {
											R.id.title, R.id.time, R.id.his_code,
											R.id.pacs_code });
							lv.setAdapter(adapter);
							/*
							 * 长按弹出对话框
							 */
							lv.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {

									final int index = position;
									/*
									 * 初始化数据
									 */
									String strHis = list.get(index).getHIS();// 体检注册码
									String strPacs = list.get(index).getPACS();// Pacs注册码
									final String strName = list.get(index)
											.getNAME();// 客户名称
									String strTime = list.get(index).getTIME();// 到期时间
									final String str = strName + "\n" + "体检注册码:"
											+ strHis + "\n" + "Pacs注册码:" + strPacs;// 全部
									final String strNameHis = strName + "\n"
											+ "体检注册码:" + strHis;// 体检
									final String strNamePacs = strName + "\n"
											+ "Pacs注册码:" + strPacs;// Pacs
									/*
									 * 弹出对话框，包括四个选项：1.全部 2.体检注册码 3.Pacs注册码 4. 客户名称
									 */
									String[] content = new String[] { "体检注册码",
											"Pacs注册码", "客户名称", "复制全部" };
									ListAdapter listAdapter = new ArrayAdapter<String>(
											HistoryActivity.this,
											R.layout.dialog_item, content);

									LayoutInflater inflater = LayoutInflater
											.from(HistoryActivity.this);
									View layout = inflater.inflate(
											R.layout.my_dialog, null);

									ListView lv = (ListView) layout
											.findViewById(R.id.lv_content);
									TextView tv = (TextView) layout
											.findViewById(R.id.tv_title);
									tv.setText("请选择您要复制的内容");
									lv.setAdapter(listAdapter);

									lv.setOnItemClickListener(new OnItemClickListener() {

										@Override
										public void onItemClick(
												AdapterView<?> parent, View view,
												int position, long id) {
											switch (position) {
											case 0:
												copyToBoard(strNameHis);
												selectAlertDialog.dismiss();
												sAlertDialog.dismiss();
												mAlertDialog.dismiss();
												break;
											case 1:
												copyToBoard(strNamePacs);
												selectAlertDialog.dismiss();
												sAlertDialog.dismiss();
												mAlertDialog.dismiss();
												break;
											case 2:
												copyToBoard(strName);
												selectAlertDialog.dismiss();
												sAlertDialog.dismiss();
												mAlertDialog.dismiss();
												break;
											case 3:
												copyToBoard(str);
												selectAlertDialog.dismiss();
												sAlertDialog.dismiss();
												mAlertDialog.dismiss();
												break;
											default:
												break;
											}
										}
									});
									AlertDialog.Builder builder = new AlertDialog.Builder(
											HistoryActivity.this);
									selectAlertDialog = builder.create();
									selectAlertDialog.show();
									selectAlertDialog.getWindow().setContentView(
											layout);
									selectAlertDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT,
											LayoutParams.WRAP_CONTENT);

								}
							});
							AlertDialog.Builder builder = new AlertDialog.Builder(
									HistoryActivity.this);
							sAlertDialog = builder.create();
							sAlertDialog.show();
							sAlertDialog.setContentView(layout);
						} else {
							/*
							 * 当查询结果为空时提示对话框
							 */
							new SweetAlertDialog(HistoryActivity.this,
									SweetAlertDialog.ERROR_TYPE)
									.setTitleText("查询结果为空")
									.setContentText("请更换关键词").show();
							editText.requestFocus();
							editText.setText("");
						}
					}
					else {
						/*
						 * 当输入结果为空时 提示
						 */
						new SweetAlertDialog(HistoryActivity.this,
								SweetAlertDialog.ERROR_TYPE)
								.setTitleText("请输入要查询的内容!")
								.setContentText("关键词未填写").show();
						editText.requestFocus();
					}
					

				}
			});
			nagButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mAlertDialog.dismiss();
				}
			});
			AlertDialog.Builder builder = new AlertDialog.Builder(
					HistoryActivity.this);

			mAlertDialog = builder.create();
			mAlertDialog.show();
			mAlertDialog.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
							| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
			mAlertDialog.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE); // 显示dialog的时候就显示软键盘
			mAlertDialog.getWindow().setContentView(view);
			mAlertDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			break;
		/*
		 * 删除
		 */
		case R.id.btnDelete:

			dao = new CodeDao(this);

			/*
			 * 得到checkBox选择寄存map
			 */
			Map<Integer, Boolean> map = adapter.getCheckMap();
			// 获取当前的数据数量
			int count = adapter.getCount();
			Boolean isChecked = false;

			// 进行遍历
			for (int i = 0; i < count; i++) {
				if (map.get(i) != null && map.get(i)) {
					isChecked = true;
				}
			}
			// 如果有选中的对象
			if (isChecked) {
				new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
						.setTitleText("是否删除")
						.setContentText("真的要删除吗？")
						.setCancelText("取消")
						.setConfirmText("删除")
						.showCancelButton(true)
						.setCancelClickListener(null)
						.setConfirmClickListener(
								new SweetAlertDialog.OnSweetClickListener() {

									@Override
									public void onClick(
											SweetAlertDialog sweetAlertDialog) {
										/*
										 * 得到checkBox选择寄存map
										 */
										Map<Integer, Boolean> map = adapter
												.getCheckMap();
										// 获取当前的数据数量
										int count = adapter.getCount();
										// 进行遍历
										for (int i = 0; i < count; i++) {

											// 因为List的特性,删除了2个item,则3变成2,所以这里要进行这样的换算,才能拿到删除后真正的position
											int position = i
													- (count - adapter
															.getCount());

											if (map.get(i) != null
													&& map.get(i)) {
												CODE bean = (CODE) adapter
														.getItem(position);
												dao.delete(bean);// 从数据库中删除
												map.put(position, false);
											}
										}
										initData();
										adapter.notifyDataSetChanged();
										sweetAlertDialog
												.setTitleText("删除成功")
												.setContentText("您的记录已成功删除！")
												.setConfirmText("OK")
												.showCancelButton(false)
												.setCancelClickListener(null)
												.setConfirmClickListener(null)
												.changeAlertType(
														SweetAlertDialog.SUCCESS_TYPE);
									}

								}).show();
			} else {
				new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
						.setTitleText("请您选择要删除的内容！").setConfirmText("OK")
						.show();
			}

			break;

		case R.id.btnSelectAll:// 全选
			if (seleteAll.getText().toString().trim().equals("全选")) {

				// 所有项目全部选中
				adapter.configCheckMap(true);

				adapter.notifyDataSetChanged();

				seleteAll.setText("全不选");
			} else {

				// 所有项目全部不选中
				adapter.configCheckMap(false);

				adapter.notifyDataSetChanged();

				seleteAll.setText("全选");
			}

			break;

		default:
			break;
		}
	}

	/**
	 * 初始化数据
	 * */
	private void initData() {
		CodeDao dao = new CodeDao(this);
		list = new ArrayList<CODE>();
		list = dao.query();

		adapter = new MyListAdapter(this, list);

		listView.setAdapter(adapter);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			HistoryActivity.this.finish();
		}
		return false;
	}

}

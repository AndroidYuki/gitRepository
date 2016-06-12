package com.mykj.code.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.mykj.code.R;
import com.mykj.code.vo.CODE;

public class MyListAdapter extends BaseAdapter{

	private Context context;
	
	private List<CODE> list = null;

	
	
	/**
	 *  CheckBox 是否选择的存储集合,key 是 position , value 是该position是否选中
	 * */
	private Map<Integer, Boolean> isCheckMap = new HashMap<Integer, Boolean>();
	
	public MyListAdapter(Context context,List<CODE> list) {
		this.context = context;
		this.list = list;
		configCheckMap(false);
		
	}
	/**
	 * 首先,默认情况下,所有项目都是没有选中的.这里进行初始化
	 * */
	public void configCheckMap(boolean bool){
		for (int i = 0; i < list.size(); i++) {
			isCheckMap.put(i, bool);
		}
	}
	@Override
	public int getCount() {
		return list == null ?0:list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;

		/**
		 * 进行ListView 的优化
		 */
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.list_item, parent, false);
			viewHolder.tv_title = (TextView)convertView.findViewById(R.id.title);
			viewHolder.tv_time= (TextView)convertView.findViewById(R.id.time);
			viewHolder.tv_his= (TextView)convertView.findViewById(R.id.his_code);
			viewHolder.tv_pacs= (TextView)convertView.findViewById(R.id.pacs_code);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		CODE tempBean = list.get(position);
		
		viewHolder.tv_title.setText(tempBean.getNAME());
		viewHolder.tv_time.setText(tempBean.getTIME());
		viewHolder.tv_his.setText(tempBean.getHIS());
		viewHolder.tv_pacs.setText(tempBean.getPACS());
		
		CheckBox cbCheck = (CheckBox) convertView.findViewById(R.id.cbCheckBox);
		
		cbCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				/*
				 * 将选择项加载到map里面寄存
				 */
				isCheckMap.put(position, isChecked);
			}
		});
		
		if (isCheckMap.get(position) == null) {
			isCheckMap.put(position, false);
		}

		cbCheck.setChecked(isCheckMap.get(position));
                        
		
		
		return convertView;
	
	}
	/**
	 * 获得该项是否被选中
	 * */
	public Map<Integer, Boolean> getCheckMap() {
		return this.isCheckMap;
	}
	
	public static class ViewHolder{
		TextView tv_title;
		TextView tv_time;
		TextView tv_his;
		TextView tv_pacs;
	}

}

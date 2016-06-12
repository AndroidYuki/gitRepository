package com.mykj.code.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.mykj.code.db.DatabaseHelper;
import com.mykj.code.vo.CODE;

public class CodeDao {
	private Context context;
	private DatabaseHelper helper;
	private Dao<CODE, Integer> dao;
	
	public CodeDao(Context context) {
		this.context = context;
		helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
		try {
			dao = helper.getDao(CODE.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public CodeDao(Context context, DatabaseHelper helper){
		this.context = context;
		this.helper  = helper;
		try {
			dao = helper.getDao(CODE.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据名称，若名称存在则更新，不存在则保存
	 * */
	public void save(CODE bean) {
		try {
			String name = bean.getNAME();
			List<CODE> list = new ArrayList<CODE>();
			list = queryCodeByName(name);
			if (list.size() > 0) {
				bean.setID(list.get(0).getID());
				dao.update(bean);
			}
			else {
				dao.create(bean);
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 根据名称查询
	 * */
	public List<CODE>  queryCodeByName(String name) {
		List<CODE> list = new ArrayList<CODE>();
		try {
			list = dao.queryForEq("NAME", name);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
		
	}
	/**
	 * 查询全部数据
	 * */
	public List<CODE> query() {
		List<CODE> list = new ArrayList<CODE>();
		try {
			list = dao.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 根据Id删除数据
	 * */
	public void deleteById(int id) {
		try {
			dao.deleteById(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 删除
	 * */
	public void delete(CODE bean) {
		try {
			dao.delete(bean);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 模糊查询
	 * @param string
	 * */
	public List<CODE> queryByString(String str) {
		List<CODE> lists = new ArrayList<CODE>();
		try {
			String queryString = "%" + str + "%";
			lists = dao.queryBuilder().where().like("NAME", queryString).query();
			if (lists != null && lists.size() > 0) {
				return lists;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return lists;
	}
}

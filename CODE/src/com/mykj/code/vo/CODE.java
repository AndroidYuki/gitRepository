package com.mykj.code.vo;
import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
/** 默认实现Serializable接口，方便intent传值 */ 
@SuppressWarnings("serial")
public class CODE implements Serializable{ 
/** 默认String类型，数据如需计算，再单独转换类型 */ 

@DatabaseField(id = true)
public String ID; 
@DatabaseField
public String NAME; 
@DatabaseField
public String TIME; 
@DatabaseField
public String HIS; 
@DatabaseField
public String PACS; 





public String getID() {
	 return this.ID;
}
public void setID(String id) {
	 this.ID = id;
}


public String getNAME() {
	 return this.NAME;
}
public void setNAME(String name) {
	 this.NAME = name;
}


public String getTIME() {
	 return this.TIME;
}
public void setTIME(String time) {
	 this.TIME = time;
}


public String getHIS() {
	 return this.HIS;
}
public void setHIS(String his) {
	 this.HIS = his;
}


public String getPACS() {
	 return this.PACS;
}
public void setPACS(String pacs) {
	 this.PACS = pacs;
}

} 

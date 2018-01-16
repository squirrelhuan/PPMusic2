package com.example.ppmusic.bean.KuWo;

import java.util.List;

public class Result_KuWo {

	private int RN;
	private int PN;
	private int HIT;
	private int TOTAL;
	private int SHOW;
	private int NEW;
	
	private int MSHOW;
	private String HITMODE;
	private String ARTISTPIC;
	private String HIT_BUT_OFFLINE;
	private List<abslist> absList;
	public int getRN() {
		return RN;
	}
	public void setRN(int rN) {
		RN = rN;
	}
	public int getPN() {
		return PN;
	}
	public void setPN(int pN) {
		PN = pN;
	}
	public int getHIT() {
		return HIT;
	}
	public void setHIT(int hIT) {
		HIT = hIT;
	}
	public int getTOTAL() {
		return TOTAL;
	}
	public void setTOTAL(int tOTAL) {
		TOTAL = tOTAL;
	}
	public int getSHOW() {
		return SHOW;
	}
	public void setSHOW(int sHOW) {
		SHOW = sHOW;
	}
	public int getNEW() {
		return NEW;
	}
	public void setNEW(int nEW) {
		NEW = nEW;
	}
	public int getMSHOW() {
		return MSHOW;
	}
	public void setMSHOW(int mSHOW) {
		MSHOW = mSHOW;
	}
	public String getHITMODE() {
		return HITMODE;
	}
	public void setHITMODE(String hITMODE) {
		HITMODE = hITMODE;
	}
	public String getARTISTPIC() {
		return ARTISTPIC;
	}
	public void setARTISTPIC(String aRTISTPIC) {
		ARTISTPIC = aRTISTPIC;
	}
	public String getHIT_BUT_OFFLINE() {
		return HIT_BUT_OFFLINE;
	}
	public void setHIT_BUT_OFFLINE(String hIT_BUT_OFFLINE) {
		HIT_BUT_OFFLINE = hIT_BUT_OFFLINE;
	}
	public List<abslist> getAbsList() {
		return absList;
	}
	public void setAbsList(List<abslist> absList) {
		this.absList = absList;
	}
	
}

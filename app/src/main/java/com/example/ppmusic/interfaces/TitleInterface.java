package com.example.ppmusic.interfaces;

import android.view.View.OnClickListener;

public interface TitleInterface extends BaseInterface {
	public String getTitleText();

	public boolean getTitleLeftHide();

	public String getTitleRightText();
	
	public int getBackgroundColor();

	public OnClickListener getTitleRightClick();
}

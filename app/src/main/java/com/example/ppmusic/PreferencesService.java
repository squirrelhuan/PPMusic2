package com.example.ppmusic;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesService {
private Context context;
	public PreferencesService(Context context) {
	super();
	this.context = context;
}
	/**
	 * �������
	 * **/
	public void save(String username, String usercontent ) {
		SharedPreferences preferences=context.getSharedPreferences("usercenter", Context.MODE_PRIVATE);
		Editor editor= preferences.edit();
		editor.putString("username", username);
		editor.putString("usercontent", usercontent);
		editor.commit();
	}/**
	��ȡ����
	**/

	public Map<String, String> getPreferences() {
		Map<String,String>params=new HashMap<String, String>();
		SharedPreferences preferences=context.getSharedPreferences("usercenter", Context.MODE_PRIVATE);
		params.put("username", preferences.getString("username", "moren"));
		params.put("usercontent", preferences.getString("usercontent", "morenc"));
		return params;
	}
	public void savecurrentmusic(Integer currentID, Integer currentTIME) {
		// TODO Auto-generated method stub
		SharedPreferences preferences=context.getSharedPreferences("currentmusic", Context.MODE_PRIVATE);
		Editor editor= preferences.edit();
		editor.putInt("currentID", currentID);
		editor.putInt("currentTIME", currentTIME);
		editor.commit();
		
	}
	public Map<String, String> getcurrentmusic() {
		Map<String,String>params=new HashMap<String, String>();
		SharedPreferences preferences=context.getSharedPreferences("currentmusic", Context.MODE_PRIVATE);
		params.put("currentID", String.valueOf(preferences.getInt("currentID", 0)));
		params.put("currentTIME", String.valueOf(preferences.getInt("currentTIME", 0)));
		return params;
	}
}

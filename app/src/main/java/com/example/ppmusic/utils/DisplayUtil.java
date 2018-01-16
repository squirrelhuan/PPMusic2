package com.example.ppmusic.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

/**
 * dp、sp 转换为 px 的工具类
 */
public class DisplayUtil {
	/**
	 * 将px值转换为dip或dp值，保证尺寸大小不变
	 * 
	 * @param pxValue
	 * @param scale
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 * 
	 * @param dipValue
	 * @param scale
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * 代码中实现背景选择的selector
	 * 
	 * @param context
	 * @param idNormal
	 * @param idPressed
	 * @return
	 */
	public static StateListDrawable addStateDrawable(Context context, int idNormal, int idPressed) {
		StateListDrawable sd = new StateListDrawable();
		Drawable normal = idNormal == -1 ? null : context.getResources().getDrawable(idNormal);
		Drawable pressed = idPressed == -1 ? null : context.getResources().getDrawable(idPressed);
		//	    Drawable focus = idFocused == -1 ? null : context.getResources().getDrawable(idFocused);
		/**
		 * 注意该处的顺序，只要有一个状态与之相配，背景就会被换掉
		 * 所以不要把大范围放在前面了，如果sd.addState(new[]{},normal)放在第一个的话，就没有什么效果了
		 **/
		//	    sd.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, focus);
		//	    sd.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
		//	    sd.addState(new int[]{android.R.attr.state_focused}, focus);
		sd.addState(new int[] { android.R.attr.state_pressed }, pressed);
		sd.addState(new int[] { android.R.attr.state_enabled }, normal);
		sd.addState(new int[] {}, normal);
		return sd;
	}
}

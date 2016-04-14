package com.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;


public class ActivityHelper {
	
	private Context context;

	public ActivityHelper(Context context) {
		this.context = context;
	}

	/**
	 *
	 * 获取屏幕宽度
	 */
	public int getScreenWidth() {
		WindowManager wm = (WindowManager) context
				.getSystemService(context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getWidth();
	}

	/**
	 * 
	 * 获取屏幕高度
	 */
	public int getScreenHeight() {
		WindowManager wm = (WindowManager) context
				.getSystemService(context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getHeight();
	}

	/**
	 * 
	 * dp到px转换
	 */
	public static int convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return (int) px;
	}

	/**
	 * 
	 * px到dp转换
	 */
	public static int convertPixelsToDp(float px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return (int) dp;
	}
	
	public int getStatusBarHeight() {
		  int result = 0;
		  int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		  if (resourceId > 0) {
		      result = context.getResources().getDimensionPixelSize(resourceId);
		  }
		  return result;
		}
	
	public void openDialogWithDefaultStyle(String title, String msg,
			String btn1, String btn2, View view,
			final onDialogCallBack dialogcallback) {
		
		AlertDialog.Builder build;
		build = new AlertDialog.Builder(context).setTitle(title)
				.setIcon(android.R.drawable.ic_dialog_info).setView(view)
				.setCancelable(true)
				.setPositiveButton(btn1, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (dialogcallback != null)
							dialogcallback.onBtn1CallBack();
					}
				})
				.setNegativeButton(btn2, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						if (dialogcallback != null)
							dialogcallback.onBtn2CallBack();
					}
				});
	
		if (!TextUtils.isEmpty(msg))
			build.setMessage(msg);

		build.show();
	}

	public interface onDialogCallBack {
		public void onBtn1CallBack();

		public void onBtn2CallBack();
	}
}

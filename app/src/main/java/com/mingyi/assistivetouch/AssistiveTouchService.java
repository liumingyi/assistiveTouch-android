package com.mingyi.assistivetouch;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

/**
 * assistiveTouch服务
 * Created by liumingyi on 3/17/16.
 */
public class AssistiveTouchService extends Service {

	private static final String TAG = "AssistiveTouchService";

	WindowManager windowManager;
	WindowManager.LayoutParams params;
	AssistiveView assistiveView;

	boolean isAdded = false;

	@Nullable @Override public IBinder onBind(Intent intent) {
		return null;
	}

	@Override public void onCreate() {
		Log.d(TAG, "-----> onCreate");
		windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		params = new WindowManager.LayoutParams();
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;// 系统提示window
		params.format = PixelFormat.TRANSLUCENT;// 支持透明
		params.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 焦点
		params.width = 160;//窗口的宽和高
		params.height = 160;
		params.x = 0;//窗口位置的偏移量
		params.y = 0;
		Log.d(TAG, ">>> " + params.x + " , " + params.y);
		params.alpha = 0.6f;//窗口的透明度

		assistiveView = new AssistiveView(this);
		//assistiveView.setOnClickListener(this);
		//assistiveView.setOnTouchListener(this);
		super.onCreate();
	}

	@Override public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "-----> onStartCommand");
		if (!isAdded) {
			isAdded = true;
			windowManager.addView(assistiveView, params);
		}
		//return super.onStartCommand(intent, flags, startId);
		return START_NOT_STICKY;
	}
}

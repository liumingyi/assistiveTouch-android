package com.mingyi.assistivetouch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * assistiveTouchService 在task被杀后尝试重新添加assistiveTouchView
 * Created by liumingyi on 3/29/16.
 */
public class AssistiveTouchService extends Service {

	@Nullable @Override public IBinder onBind(Intent intent) {
		return null;
	}

	@Override public void onCreate() {
		new AssistiveView(this);
		super.onCreate();
	}
}

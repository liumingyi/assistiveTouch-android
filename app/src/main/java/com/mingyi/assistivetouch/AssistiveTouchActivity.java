package com.mingyi.assistivetouch;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

public class AssistiveTouchActivity extends Activity {

	private static final String TAG = "AssistiveTouch";

	public static int OVERLAY_PERMISSION_REQ_CODE = 1234;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "Start AssistiveTouchActivity");

		checkPermission();
	}

	@TargetApi(Build.VERSION_CODES.M) private void checkPermission() {
		if (!Settings.canDrawOverlays(this)) {
			Intent intent =
				new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
			startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
		} else {
			startAssistiveTouchService();
		}
	}

	@TargetApi(Build.VERSION_CODES.M) @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
			if (!Settings.canDrawOverlays(this)) {
				// SYSTEM_ALERT_WINDOW permission not granted...
				Log.e(TAG, "----------not granted-------------");
			} else {
				startAssistiveTouchService();
			}
		}
	}

	private void startAssistiveTouchService() {
		new AssistiveView(this);
		finish();
	}
}

package com.mingyi.assistivetouch;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * 鼠标view
 * Created by liumingyi on 3/28/16.
 */
public class MouseView extends View {

	private static final String TAG = "MouseView";

	private Paint paint;

	public MouseView(Context context) {
		super(context);
		init();
	}

	public MouseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MouseView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public MouseView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	private void init() {

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.GREEN);
	}

	@Override protected void onDraw(Canvas canvas) {
		canvas.drawCircle(30, 30, 30, paint);
	}
}

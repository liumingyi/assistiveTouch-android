package com.mingyi.assistivetouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * assistiveTouch控件
 * Created by liumingyi on 3/17/16.
 */
public class AssistiveView extends View {

	private static final String TAG = "AssistiveView";

	Paint paint;
	Paint blackPaint;

	Point center = new Point();

	int width;
	int height;
	int normalRadius;
	int pressedRadius;

	int initialX;
	int initialY;

	boolean isLongPressed;

	WindowManager windowManager;
	WindowManager.LayoutParams params;

	GestureDetectorCompat gestureDetector;
	GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

		@Override public boolean onSingleTapUp(MotionEvent e) {
			Log.d(TAG, "Gesture >> SingleTapUp -----> 展开动画");
			return super.onSingleTapUp(e);
		}

		@Override public void onLongPress(MotionEvent e) {
			Log.d(TAG, "Gesture >> LongPress ----> 标记状态 ----> 下一个Up触发点击");
			super.onLongPress(e);
			isLongPressed = true;
			invalidate();
		}
	};

	public AssistiveView(Context context) {
		super(context);
		init(context);
	}

	public AssistiveView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AssistiveView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		initWindowManager(context);
		initGestureDetector(context);
		initPaints();
	}

	private void initWindowManager(Context context) {
		windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		params = new WindowManager.LayoutParams();
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;// 系统提示window
		params.format = PixelFormat.TRANSLUCENT;// 支持透明
		params.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 焦点
		// FIXME: 3/28/16 改为pd
		params.width = 160;//窗口的宽和高
		params.height = 160;
		params.x = calculateOffsetX(0);//窗口位置的偏移量
		params.y = calculateOffsetY(0);
		params.alpha = 0.6f;//窗口的透明度
		windowManager.addView(this, params);
	}

	private void initGestureDetector(Context context) {
		gestureDetector = new GestureDetectorCompat(context, gestureListener);
	}

	private void initPaints() {
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.RED);

		blackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		blackPaint.setColor(Color.BLACK);
	}

	private int calculateOffsetX(int initialX) {
		return 0;
	}

	private int calculateOffsetY(int initialY) {
		return 0;
	}

	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (height == 0 || width == 0) {
			height = getMeasuredHeight();
			width = getMeasuredWidth();
			center.x = width / 2;
			center.y = height / 2;
			normalRadius = (int) (width / 2 * 0.8);
			pressedRadius = width / 2;
			invalidate();
		}
		Log.d(TAG, "onMeasure >>> " + width + " , " + height);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override protected void onDraw(Canvas canvas) {
		Log.d(TAG, "==== onDraw = " + width + " , " + height);
		if (isLongPressed) {
			canvas.drawCircle(center.x, center.y, pressedRadius, blackPaint);
		}
		canvas.drawCircle(center.x, center.y, normalRadius, paint);
	}

	@Override public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Log.d(TAG, "-Down--------" + x + " , " + y);
				break;
			case MotionEvent.ACTION_MOVE:
				Log.d(TAG, "-Move--------" + x + " , " + y);
				params.x = x;
				params.y = y;
				windowManager.updateViewLayout(this, params);
				break;
			case MotionEvent.ACTION_UP:
				Log.d(TAG, "-Up--------" + x + " , " + y);
				if (isLongPressed) {
					isLongPressed = false;
					invalidate();
				}
				break;
		}
		return gestureDetector.onTouchEvent(event);
	}

	@Override public boolean callOnClick() {
		Log.d(TAG, "callOnClick");
		return false;
	}
}

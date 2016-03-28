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
import android.view.Gravity;
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
	int statusBarHeight;
	int normalRadius;
	int pressedRadius;

	int initialX = 0;
	int initialY = 0;

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

		//Display display = windowManager.getDefaultDisplay();
		//Point point = new Point();
		//display.getSize(point);
		//screenWidth = point.x;
		//screenHeight = point.y;
		//Log.d(TAG, "Screen --> w : " + point.x + " , h : " + point.y);

		params = new WindowManager.LayoutParams();
		params.gravity = Gravity.TOP | Gravity.START;
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;// 系统提示window
		params.format = PixelFormat.TRANSLUCENT;// 支持透明
		params.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 焦点
		params.alpha = 0.6f;//窗口的透明度

		// FIXME: 3/28/16 改为pd
		width = params.width = 160;//窗口的宽和高
		height = params.height = 160;

		statusBarHeight = getStatusBarHeight();
		center.x = width / 2;
		center.y = height / 2;
		normalRadius = (int) (width / 2 * 0.8);
		pressedRadius = width / 2;

		//params.x = calculateOffsetX(0, point.x);//窗口位置的偏移量
		//params.y = calculateOffsetY(0, point.y);
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

	public int getStatusBarHeight() {
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	private int calculateOffsetX(int initialX, int screenWidth) {
		return initialX + screenWidth / 2;
	}

	// TODO: 3/28/16
	private int calculateOffsetY(int initialY, int screenHeight) {
		// FIXME: 3/28/16 72
		return initialY + (screenHeight - statusBarHeight - height) / 2;
	}

	@Override protected void onDraw(Canvas canvas) {
		Log.d(TAG, "==== onDraw = " + width + " , " + height);
		if (isLongPressed) {
			canvas.drawCircle(center.x, center.y, pressedRadius, blackPaint);
		}
		canvas.drawCircle(center.x, center.y, normalRadius, paint);
	}

	int lastX;
	int lastY;
	int startX;
	int startY;
	//boolean touchConsumedByMove = false;

	@Override public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getRawX();
		int y = (int) event.getRawY();
		//Log.d(TAG, "touch >>> " + x + " , " + y + " ------ " + event.getX() + " , " + event.getY());
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				startX = lastX = x;
				startY = lastY = y;
				break;
			case MotionEvent.ACTION_MOVE:
				int offsetX = x - lastX;
				int offsetY = y - lastY;
				lastX = x;
				lastY = y;
				params.x += offsetX;
				params.y += offsetY;
				windowManager.updateViewLayout(this, params);
				//if (Math.abs(lastX - startX) >= 5 || Math.abs(lastY - startY) >= 5) {
				//	if (event.getPointerCount() == 1) {
				//		params.x += offsetX;
				//		params.y += offsetY;
				//		touchConsumedByMove = true;
				//		windowManager.updateViewLayout(this, params);
				//	} else {
				//		touchConsumedByMove = false;
				//	}
				//} else {
				//	touchConsumedByMove = false;
				//}
				break;
			case MotionEvent.ACTION_UP:
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

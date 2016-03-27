package com.mingyi.assistivetouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

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
	int radius;

	GestureDetectorCompat gestureDetector;
	GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

		@Override public boolean onSingleTapUp(MotionEvent e) {
			Log.d(TAG, "Gesture >> SingleTapUp -----> 展开动画");
			return super.onSingleTapUp(e);
		}

		@Override public void onLongPress(MotionEvent e) {
			Log.d(TAG, "Gesture >> LongPress ----> 标记状态 ----> 下一个Up触发点击");
			super.onLongPress(e);
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

	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (height == 0 || width == 0) {
			height = getMeasuredHeight();
			width = getMeasuredWidth();
			center.x = width / 2;
			center.y = height / 2;
			radius = width / 2;
			invalidate();
		}
		Log.d(TAG, "onMeasure >>> " + width + " , " + height);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	private void init(Context context) {
		gestureDetector = new GestureDetectorCompat(context, gestureListener);

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.RED);

		blackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		blackPaint.setColor(Color.BLACK);
	}

	@Override protected void onDraw(Canvas canvas) {
		Log.d(TAG, "==== onDraw = " + width + " , " + height);
		canvas.drawRect(0, 0, width, height, blackPaint);
		canvas.drawCircle(center.x, center.y, radius, paint);
	}

	@Override public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Log.d(TAG, "------Down--------");
				break;
			case MotionEvent.ACTION_MOVE:
				Log.d(TAG, "------Move--------" + x + " , " + y);
				break;
			case MotionEvent.ACTION_UP:
				Log.d(TAG, "------Up--------");
				//params.x = x;
				//params.y = y;
				//windowManager.updateViewLayout(assistiveView, params);
				break;
		}
		return gestureDetector.onTouchEvent(event);
	}

	@Override public boolean callOnClick() {
		Log.d(TAG, "callOnClick");
		return false;
	}
}

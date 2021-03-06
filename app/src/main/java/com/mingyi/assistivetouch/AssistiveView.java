package com.mingyi.assistivetouch;

import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.SystemClock;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import java.io.DataOutputStream;
import java.io.OutputStream;

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

	int screenWidth;
	int screenHeight;

	boolean isLongPressed;

	WindowManager windowManager;
	WindowManager.LayoutParams touchParams;

	MouseView mouseView;
	WindowManager.LayoutParams mouseParams;

	GestureDetectorCompat gestureDetector;
	GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

		@Override public boolean onSingleTapUp(MotionEvent e) {
			Log.d(TAG, "Gesture >> SingleTapUp -----> 展开动画");
			//toggleDesk();
			//boolean isAdmin = devicePolicyManager.isAdminActive(mComponentName);
			//if (isAdmin) {
			//	devicePolicyManager.lockNow();
			//}
			//execShellCmd("input tap " + e.getRawX() + " " + e.getRawY());
			return super.onSingleTapUp(e);
		}

		@Override public void onLongPress(MotionEvent e) {
			Log.d(TAG, "Gesture >> LongPress ----> 标记状态 ----> 下一个Up触发点击");
			super.onLongPress(e);
			isLongPressed = true;
			windowManager.addView(mouseView, mouseParams);
			invalidate();
		}
	};

	//private DevicePolicyManager devicePolicyManager;
	//ComponentName mComponentName;

	public AssistiveView(Context context) {
		super(context);
		init(context);
		//devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		//mComponentName = new ComponentName(context, MyAdminReceiver.class);
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
		initViews(context);
		initGestureDetector(context);
		initPaints();
	}

	private void initViews(Context context) {
		windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		touchParams = new WindowManager.LayoutParams();
		touchParams.gravity = Gravity.TOP | Gravity.START;
		touchParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		touchParams.format = PixelFormat.TRANSLUCENT;
		touchParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_FULLSCREEN;
		touchParams.alpha = 0.6f;

		width = touchParams.width = 160;
		height = touchParams.height = 160;

		statusBarHeight = getStatusBarHeight();
		center.x = width / 2;
		center.y = height / 2;
		normalRadius = (int) (width / 2 * 0.8);
		pressedRadius = width / 2;

		//可利用偏移量设置初始位置
		Display display = windowManager.getDefaultDisplay();
		Point point = new Point();
		display.getSize(point);
		screenWidth = point.x;
		screenHeight = point.y;
		//touchParams.x = calculateOffsetX(0, point.x);
		touchParams.y = calculateOffsetY(0);
		windowManager.addView(this, touchParams);

		createMouseView(context);
	}

	private void createMouseView(Context context) {
		mouseParams = new WindowManager.LayoutParams();
		mouseParams.gravity = Gravity.TOP | Gravity.START;
		mouseParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;// 系统提示window
		mouseParams.format = PixelFormat.TRANSLUCENT;
		mouseParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		mouseParams.alpha = 0.6f;
		mouseParams.width = 60;
		mouseParams.height = 60;
		mouseView = new MouseView(context);
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

	private int calculateOffsetX(int initialX) {
		return initialX + screenWidth / 2;
	}

	private int calculateOffsetY(int initialY) {
		return initialY + (screenHeight - statusBarHeight - height) / 2;
	}

	@Override protected void onDraw(Canvas canvas) {
		//changeSize();
		drawContent(canvas);
		//update();
	}

	private void drawContent(Canvas canvas) {
		//canvas.drawRect(0, 0, touchParams.width, touchParams.height, blackPaint);
		if (isLongPressed) {
			canvas.drawCircle(center.x, center.y, pressedRadius, blackPaint);
		}
		canvas.drawCircle(center.x, center.y, normalRadius, paint);
	}

	private int lastX;
	private int lastY;
	private int startX;
	private int startY;

	@Override public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getRawX();
		int y = (int) event.getRawY();
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
				touchParams.x += offsetX;
				touchParams.y += offsetY;
				//if (Math.abs(lastX - startX) >= 5 || Math.abs(lastY - startY) >= 5) {
				//	if (event.getPointerCount() == 1) {
				//		touchParams.x += offsetX;
				//		touchParams.y += offsetY;
				//		windowManager.updateViewLayout(this, touchParams);
				//	}
				//}
				windowManager.updateViewLayout(this, touchParams);

				if (isLongPressed) {
					mouseParams.x += offsetX * 8;
					mouseParams.y += offsetY * 8;
					windowManager.updateViewLayout(mouseView, mouseParams);
				}
				break;
			case MotionEvent.ACTION_UP:
				if (isLongPressed) {
					isLongPressed = false;
					// FIXME: 3/29/16 not work
					//dispatchMouseClickEvent2();
					removeMouseView();
					invalidate();
				}
				break;
		}
		return gestureDetector.onTouchEvent(event);
	}

	private void removeMouseView() {
		mouseParams.x = 0;
		mouseParams.y = 0;
		windowManager.removeViewImmediate(mouseView);
	}

	/////////////////////////////以下为待处理代码/////////////////////////////////////////////////

	/**
	 * 执行shell命令
	 */
	private void execShellCmd(String cmd) {
		try {
			// 申请获取root权限，这一步很重要，不然会没有作用
			Process process = Runtime.getRuntime().exec("su");
			// 获取输出流
			OutputStream outputStream = process.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
			dataOutputStream.writeBytes(cmd);
			dataOutputStream.flush();
			dataOutputStream.close();
			outputStream.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	boolean isOpenDesk;
	boolean isCloseDesk;

	enum state {
		OPENNING, OPEN, CLOSING, CLOSE
	}

	private void changeSize() {
		if (isOpenDesk) {
			isOpenDesk = scale(5);
		} else if (isCloseDesk) {
			isCloseDesk = scale(-5);
			if (!isCloseDesk) {
				resetWindowSize(160);
			}
		}
	}

	private void update() {
		if (isOpenDesk || isCloseDesk) {
			invalidate();
		}
	}

	// FIXME: 3/29/16
	private void toggleDesk() {
		if (touchParams.width == 160) {
			resetWindowSize(400);
			isOpenDesk = true;
		} else {

			isCloseDesk = true;
		}
		invalidate();
	}

	private void resetWindowSize(int size) {
		touchParams.width = size;
		touchParams.height = size;
		pressedRadius = size / 2;
		windowManager.updateViewLayout(AssistiveView.this, touchParams);
	}

	private boolean scale(int speed) {
		center.x += speed;
		center.y += speed;
		normalRadius += speed * 0.8f;

		if (center.x > 200) {
			center.x = 200;
		}
		if (center.x < 80) {
			center.x = 80;
		}
		if (center.y > 200) {
			center.y = 200;
		}
		if (center.y < 80) {
			center.y = 80;
		}
		if (normalRadius > 160) {
			normalRadius = 160;
		}
		if (normalRadius < 64) {
			normalRadius = 64;
		}
		return center.x != 200 && center.x != 80;
	}

	private void dispatchMouseClickEvent() {
		Log.d(TAG, "-performClick------------");
		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis() + 100;
		float x = 0.0f;
		float y = 0.0f;
		// List of meta states found here: developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
		int metaState = 0;
		MotionEvent eventDown = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, x, y, metaState);
		mouseView.dispatchTouchEvent(eventDown);
		MotionEvent eventUp = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x, y, metaState);
		// Dispatch touch event to view
		mouseView.dispatchTouchEvent(eventUp);
		eventDown.recycle();
		eventUp.recycle();
	}

	private void dispatchMouseClickEvent2() {
		new Thread(new Runnable() {
			@Override public void run() {
				Instrumentation inst = new Instrumentation();
				inst.sendPointerSync(
					MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN,
						200, 500, 0));
				inst.sendPointerSync(
					MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP,
						200, 500, 0));
			}
		}).start();
	}
}

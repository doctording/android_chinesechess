package com.example.view;

import com.example.cchess.MainActivity;
import com.example.cchess.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 
 * 该类是帮助界面 只绘制一张图片以及一个按钮即可， 通过对按钮的监听实现返回操作
 */
public class ViewHelp extends SurfaceView implements SurfaceHolder.Callback {
	
	MainActivity activity;// Activity的引用
	private TutorialThread thread;// 刷帧的线程
	Bitmap back;// 返回按钮
	Bitmap helpBackground;// 背景图片

	int backBtnX ;
	int backBtnY ;
	
	public ViewHelp(Context context){
		super(context);
	}
			
	public ViewHelp(Context context, MainActivity activity) {// 构造器
		super(context);
		this.activity = activity;// 得到activity引用
		getHolder().addCallback(this);
		this.thread = new TutorialThread(getHolder(), this);// 初始化重绘线程
		initBitmap();// 初始化图片资源
	}

	public void initBitmap() {// 初始化所用到的图片
		back = BitmapFactory.decodeResource(getResources(), R.drawable.menu_back);// 返回按钮
		helpBackground = BitmapFactory.decodeResource(getResources(),R.drawable.help3);// 初始化背景图片
	}

	public void oonDraw(Canvas canvas) {	// 自己写的绘制方法
		int screenH = activity.screenH ;
		int screenW = activity.screenW;
		int bW = helpBackground.getWidth();
		int bH = helpBackground.getHeight();
		int backH = back.getHeight();
		int backW = back.getWidth();
		
		Rect s1 = new Rect(0, 0, bW , bH);
		Rect s2 = new Rect(screenW/15,screenH/15,screenW *9/10 ,screenH*2/3);
		canvas.drawBitmap(helpBackground, s1,s2, null);// 绘制背景图片
		backBtnX = screenW/2 - backW/2 ;
		backBtnY = screenH*2/3+backH/2;
		canvas.drawBitmap(back, backBtnX , backBtnY , new Paint());// 绘制按钮
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {// 被创建时启动刷帧线程
		this.thread.setFlag(true);// 设置循环标志位
		this.thread.start();// 启动刷帧线程
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {// 被摧毁时停止刷帧线程
		boolean retry = true;// 循环标志位
		thread.setFlag(false);// 设置循环标志位
		while (retry) {
			try {
				thread.join();// 等待线程结束
				retry = false;// 停止循环
			} catch (InterruptedException e) {
			}// 不断地循环，直到刷帧线程结束
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {// 屏幕监听
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (event.getX() > backBtnX && event.getX() < backBtnX + back.getWidth()
					&& event.getY() > backBtnY && event.getY() < backBtnY + back.getHeight()) {// 点击了返回按钮
				activity.myHandler.sendEmptyMessage(1);// 发送Handler消息
			}
		}
		return super.onTouchEvent(event);
	}

	class TutorialThread extends Thread {	// 刷帧线程
		private int span = 1000;// 睡眠的毫秒数
		private SurfaceHolder surfaceHolder;// SurfaceHolder的引用
		private ViewHelp helpView;// 父类的引用
		private boolean flag = false;// 循环标记位

		public TutorialThread(SurfaceHolder surfaceHolder, ViewHelp helpView) {// 构造器
			this.surfaceHolder = surfaceHolder;// 得到surfaceHolder引用
			this.helpView = helpView;// 得到helpView引用
		}

		public void setFlag(boolean flag) {// 设置循环标记位
			this.flag = flag;
		}

		public void run() {// 重写的run方法
			Canvas c;// 画布
			while (this.flag) {// 循环
				c = null;
				try {
					c = this.surfaceHolder.lockCanvas(null);
					synchronized (this.surfaceHolder) {// 同步
						helpView.oonDraw(c);// 调用绘制方法
					}
				} finally {// 用finally语句保证下面的代码一定会被执行
					if (c != null) {// 更新屏幕显示内容
						this.surfaceHolder.unlockCanvasAndPost(c);
					}
				}
				try {
					Thread.sleep(span);// 睡眠指定毫秒数
				} catch (Exception e) {// 捕获异常
					e.printStackTrace();// 打印异常堆栈信息
				}
			}
		}
	}
}
package com.example.view;

import com.example.cchess.MainActivity;
import com.example.cchess.R;
import com.util.ActivityCollector;
import com.util.DrawUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ViewWelcome extends SurfaceView implements SurfaceHolder.Callback {
	MainActivity activity;// 总Activity的引用
	private TutorialThread thread;// 刷帧的线程
	
	Bitmap logo;
	Bitmap menu;//菜单图片
	Bitmap exit;// 退出图片
	
	int menuX , menuY;
	int exitX , exitY;
	
	public ViewWelcome(Context context){
		super(context);
	}
	
	public ViewWelcome(Context context, MainActivity activity) {// 构造器
		super(context);
		this.activity = activity;// 得到activity引用
		getHolder().addCallback(this);
		this.thread = new TutorialThread(getHolder(), this);// 启动刷帧线程
		initBitmap();// 初始化图片资源
	}

	public void initBitmap() {// 初始化图片资源图片
		logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
		menu = BitmapFactory.decodeResource(getResources(), R.drawable.menu2);// 菜单按钮
		exit = BitmapFactory.decodeResource(getResources(), R.drawable.menu_exit);// 退出按钮
	}

	public void oonDraw(Canvas canvas) {// 自己写的绘制方法
		canvas.drawColor(Color.BLACK);// 清屏
		
//		Rect s1 = new Rect(0, 0, logo.getWidth(),logo.getHeight() );
//		Rect s2 = new Rect(activity.screenW/11,activity.screenW*10/11,activity.screenH/20,activity.screenH / 2 );
		
		int x = activity.screenW / 2 - logo.getWidth()/2 ;
		int y = activity.screenH / 5 ;
		canvas.drawBitmap(logo,x,y, null);
		
		menuX = activity.screenW / 2 - menu.getWidth()/2 ;
		menuY = activity.screenH / 2 + activity.screenH / 20; 
		canvas.drawBitmap(menu, menuX, menuY, null);// 绘制菜单按钮
		
		exitX = activity.screenW / 2 - exit.getWidth()/2 ;
		exitY = menuY + menu.getHeight() + activity.screenH / 20 ;
		canvas.drawBitmap(exit, exitX, exitY, null);// 绘制退出按钮
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {// 创建时启动刷帧
		this.thread.setFlag(true);// 设置循环标志位
		this.thread.start();// 启动线程
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {// 摧毁时释放刷帧线程
		boolean retry = true;// 循环标志位
		thread.setFlag(false);// 设置循环标志位
		while (retry) {// 循环
			try {
				thread.join();// 等待线程结束
				retry = false;// 停止循环
			} catch (InterruptedException e) {
			}// 不断地循环，直到刷帧线程结束
		}
	}

	public boolean onTouchEvent(MotionEvent event) {// 屏幕监听
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (new DrawUtil().isClick(event,menuX, menuY,menu)) {// 点击的是帮助按钮
				activity.myHandler.sendEmptyMessage(1);	// 向activity发送Hander消息通知切换View
			}else if (new DrawUtil().isClick(event,exitX, exitY,exit)) {// 点击的是退出游戏
				ActivityCollector.finishAll();
				System.exit(0);// 直接退出游戏
			}
		}
		return super.onTouchEvent(event);
	}

	class TutorialThread extends Thread {// 刷帧线程
		private int span = 500;// 睡眠的毫秒数
		private SurfaceHolder surfaceHolder;// SurfaceHolder的引用
		private ViewWelcome welcomeView;// MenuView的引用
		private boolean flag = false;// 循环标记位

		public TutorialThread(SurfaceHolder surfaceHolder, ViewWelcome menuView) {// 构造器
			this.surfaceHolder = surfaceHolder;// 得到surfaceHolder引用
			this.welcomeView = menuView;// 得到menuView引用
		}

		public void setFlag(boolean flag) {// 设置循环标记位
			this.flag = flag;
		}

		public void run() {// 重写的run方法
			Canvas c;// 画布
			while (this.flag) {// 循环
				c = null;
				try {
					// 锁定整个画布，在内存要求比较高的情况下，建议参数不要为null
					c = this.surfaceHolder.lockCanvas(null);
					synchronized (this.surfaceHolder) {// 同步锁
						welcomeView.oonDraw(c);// 调用绘制方法
					}
				} finally {// 使用finally保证下面代码一定被执行
					if (c != null) {
						// 更新屏幕显示内容
						this.surfaceHolder.unlockCanvasAndPost(c);
					}
				}
				try {
					Thread.sleep(span);// 睡眠指定毫秒数
				} catch (Exception e) {// 捕获异常
					e.printStackTrace();// 有异常时打印异常堆栈信息
				}
			}
		}
	}
}
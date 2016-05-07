package simple.game.chess;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.util.ActivityHelper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * 定义游戏界面
 * 
 * @author Administrator
 * 
 */
public class ChessView extends SurfaceView implements SurfaceHolder.Callback {
	private Context context;
	private SurfaceHolder holder;// 定义surfacehold
	private MyThread myThread;// 自定义线程
	
	static boolean isgo;// 线程是否开启
	static int timer;// 定义刷新时间

	int oldx, oldy;// 保存上次点击的坐标
	int width;// 界面宽度（右边界）
	int height;// 界面高度（下边界）
	int rectWidth, rectHeight, itemwidth; // 棋盘最终宽度
	int left, top, itop, ileft ;
	int clickx = -1, clicky = -1 ;
	float dpvalue;
	private onStepLisenner lisenner;
	private List<Chess_Item_Base> items;
	private boolean canClick;
	
	private Chess_Item_Base lastitem,newitem;
	
	public ChessView(Context context) {
		this(context, null);
	}
	
	public static void setIsgo(boolean isgo) {
		ChessView.isgo = isgo;
	}
	
	public boolean getIsgo(){
		return isgo;
	}
	
	public void setLastAction(Chess_Item_Base lastitem,Chess_Item_Base newitem)
	{
		this.newitem = newitem;
		this.lastitem = lastitem;
	}

	public boolean isCanClick() {
		return canClick;
	}

	public void setCanClick(boolean canClick) {
		this.canClick = canClick;
	}

	public void setLisenner(onStepLisenner lisenner) {
		this.lisenner = lisenner;
	}

	public ChessView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		holder = this.getHolder();
		holder.addCallback(this);
		items = new ArrayList<Chess_Item_Base>();//初始化所有的棋子
		items.add(new Chess_Item_Base(0, 0, 1, true));//1 车
		items.add(new Chess_Item_Base(1, 0, 2, true));//2 马
		items.add(new Chess_Item_Base(2, 0, 3, true));//3 象
		items.add(new Chess_Item_Base(3, 0, 4, true));//4 士
		items.add(new Chess_Item_Base(4, 0, 5, true)); // 5 将 或 帅
		items.add(new Chess_Item_Base(5, 0, 4, true));
		items.add(new Chess_Item_Base(6, 0, 3, true));
		items.add(new Chess_Item_Base(7, 0, 2, true));
		items.add(new Chess_Item_Base(8, 0, 1, true));
		items.add(new Chess_Item_Base(1, 2, 6, true));// 6 炮
		items.add(new Chess_Item_Base(7, 2, 6, true));
		items.add(new Chess_Item_Base(0, 3, 0, true));
		items.add(new Chess_Item_Base(2, 3, 0, true));
		items.add(new Chess_Item_Base(4, 3, 0, true));
		items.add(new Chess_Item_Base(6, 3, 0, true));
		items.add(new Chess_Item_Base(8, 3, 0, true));

		items.add(new Chess_Item_Base(0, 9, 1, false));
		items.add(new Chess_Item_Base(1, 9, 2, false));
		items.add(new Chess_Item_Base(2, 9, 3, false));
		items.add(new Chess_Item_Base(3, 9, 4, false));
		items.add(new Chess_Item_Base(4, 9, 5, false));
		items.add(new Chess_Item_Base(5, 9, 4, false));
		items.add(new Chess_Item_Base(6, 9, 3, false));
		items.add(new Chess_Item_Base(7, 9, 2, false));
		items.add(new Chess_Item_Base(8, 9, 1, false));
		items.add(new Chess_Item_Base(1, 7, 6, false));
		items.add(new Chess_Item_Base(7, 7, 6, false));
		items.add(new Chess_Item_Base(0, 6, 0, false));
		items.add(new Chess_Item_Base(2, 6, 0, false));
		items.add(new Chess_Item_Base(4, 6, 0, false));
		items.add(new Chess_Item_Base(6, 6, 0, false));
		items.add(new Chess_Item_Base(8, 6, 0, false));
		myThread = new MyThread(holder);// 创建一个绘图线程
	}
	
	// 就是对所有的棋子改变，红黑方
	public void change()
	{
		for(Chess_Item_Base item :items)
			item.changeFirst();
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {

	}

	/**
	 * 当创建完成时初始化
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
		initdata();// 初始化数据
		//isrun = true;// 停止跑动
		isgo = true;// 线程开启
		timer = 50;//刷新的时间
		if(!myThread.isAlive())
			try {
				myThread.start();// 开启线程
			} catch (Exception e) {
				e.printStackTrace();
				try {
					holder = this.getHolder();
					holder.addCallback(this);
					myThread = new MyThread(holder);// 创建一个绘图线程
					myThread.start();
					
					if(lastitem != null && newitem != null)
					{
						setCanClick(false);
						setClick(lastitem.getCentx(),lastitem.getCenty());
						
						try {
							Thread.sleep(200);
						} catch (InterruptedException e3) {
							e.printStackTrace();
						}

						setClick(newitem.getCentx(), newitem.getCenty());
						try {
							Thread.sleep(200);
						} catch (InterruptedException e3) {
							e.printStackTrace();
						}

						setClick(-1, -1);
						setCanClick(true);
					}
				} catch (Exception e2) {
				}
			}
	}

	/**
	 * 当窗口注销的时候调用
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isgo = false;
	}

	/**
	 * 初始化数据
	 */
	private void initdata() {
		width = this.getRight();// 获取控件右方坐标
		height = this.getBottom();// 获取控件下方坐标
		/**
		 * 也可以用下面两个方法代替
		 */
		ActivityHelper helper = new ActivityHelper(context);
		dpvalue = helper.convertDpToPixel(1, context);
		left = helper.convertDpToPixel(20, context);// 最小留空区域
		int minl = width > height ? height : width;// 获取宽高中最小项

		rectWidth = minl - left * 2;
		itemwidth = (rectWidth - left / 2) / 8;
		rectHeight = itemwidth * 9 + left / 2;
		top = (height - rectHeight) / 2;
		itop = top + left / 4;
		ileft = left + left / 4;
	}

	/**
	 * 通过坐标返回对应位置的矩形
	 * 
	 * @param x
	 *            矩形左上顶点的x坐标
	 * @param y
	 *            矩形左上顶点的y坐标
	 * @return 对应的矩形
	 */
	/*private Rect getRectByXY(int x, int y) {
		return new Rect(x + 1, y + 1, x + 18, y + 18);
	}*/

	/**
	 * 自定义线程
	 * 
	 * @author Administrator
	 * 
	 */
	class MyThread extends Thread {
		private SurfaceHolder hold; // 新建surfaceholder对象

		/**
		 * 构造函数，初始化数据
		 * 
		 * @param hold
		 */
		public MyThread(SurfaceHolder hold) {
			this.hold = hold;
			ChessView.isgo = true;
		}

		@Override
		public void run() {
			Canvas c = null;// 创建画布
			oodraw(c);// 绘制
			while (ChessView.isgo)// 只要游戏未结束，就一直画图
				try {
					c = holder.lockCanvas();// 锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了。

					oodraw(c);// 绘图
					Thread.sleep(ChessView.timer);// 睡眠一段时间
				} catch (Exception ex) {
				} finally {
				}
		}
		
		private void oodraw(Canvas c) { //自定义绘图

			if (c == null)
				c = holder.lockCanvas();// 如过画布为空，获取画布
			c.drawColor(Color.WHITE);// 设置画布背景颜色黑色
			Paint p = new Paint();// 设置画笔
			p.setAntiAlias(true);// 设置取消锯齿效果 
			p.setStrokeWidth(dpvalue * 3); // 设置宽度
			p.setStyle(Paint.Style.STROKE); // 设置空心
			
			/* 设置颜色及绘制矩形 */
			p.setColor(Color.RED);
			c.drawRect(left, top, left + rectWidth, top + rectHeight, p);// 绘制外框

			p.setStrokeWidth(dpvalue * 1); // 设置宽度
			c.drawRect(ileft, itop, ileft + itemwidth * 8,
					itop + 9 * itemwidth, p);// 绘制内框
	
			for (int i = 0; i < 9; i++) // 横线
				c.drawLine(ileft, itop + i * itemwidth, ileft + itemwidth * 8 , itop + i * itemwidth, p);
			for (int i = 0; i < 8; i++) {// 竖线  楚河-汉界隔开 了，所以画两条
				c.drawLine(ileft + i * itemwidth, itop, ileft + i * itemwidth,itop + 4 * itemwidth, p);
				c.drawLine(ileft + i * itemwidth, itop + 5 * itemwidth, ileft + i * itemwidth, itop + 9 * itemwidth, p);
			}
			p.setTextSize(itemwidth / 2);
			float textWidth = p.measureText("楚河");
			float textHeight = textWidth / 2;
			int centy = (top * 2 + rectHeight) / 2;
			int centx = (ileft + itemwidth * 2);
			int centx2 = (ileft + itemwidth * 6);
			p.setStyle(Paint.Style.FILL);
			c.drawText("楚河", centx - textWidth / 2, centy + textHeight / 3, p); // 画出文字
			c.drawText("汉界", centx2 - textWidth / 2, centy + textHeight / 3, p); // 画出文字
			p.setStyle(Paint.Style.FILL_AND_STROKE);
			
			// 斜线
			c.drawLine(ileft + 3 * itemwidth, itop + 0 * itemwidth, ileft + 5 * itemwidth, itop + 2 * itemwidth, p);
			c.drawLine(ileft + 3 * itemwidth, itop + 2 * itemwidth, ileft + 5 * itemwidth, itop + 0 * itemwidth, p);

			c.drawLine(ileft + 3 * itemwidth, itop + 7 * itemwidth, ileft + 5* itemwidth, itop + 9 * itemwidth, p);
			c.drawLine(ileft + 3 * itemwidth, itop + 9 * itemwidth, ileft + 5* itemwidth, itop + 7 * itemwidth, p);
			// 折线
			drawItemKH(c, 0, 3, p);
			drawItemKH(c, 1, 2, p);
			drawItemKH(c, 7, 2, p);
			drawItemKH(c, 2, 3, p);
			drawItemKH(c, 4, 3, p);
			drawItemKH(c, 6, 3, p);
			drawItemKH(c, 8, 3, p);

			drawItemKH(c, 0, 6, p);
			drawItemKH(c, 1, 7, p);
			drawItemKH(c, 7, 7, p);
			drawItemKH(c, 2, 6, p);
			drawItemKH(c, 4, 6, p);
			drawItemKH(c, 6, 6, p);
			drawItemKH(c, 8, 6, p);

			//画棋子
			drawChessItems(c, p);
			holder.unlockCanvasAndPost(c);
		}

	}

	private void drawChessItems(Canvas c, Paint p) {

		p.setStyle(Paint.Style.FILL);
		// 画出所有的棋子
		for (Chess_Item_Base item : items) {
			p.setARGB(255, 220, 200, 200);
			c.drawCircle(ileft + item.getCentx() * itemwidth,itop + item.getCenty() * itemwidth, itemwidth / 2, p);
			// 设置棋子的颜色
			if (item.isFirst()) {
				p.setColor(Color.RED);
			} else {
				p.setColor(Color.BLACK);
			}
			p.setTextSize(itemwidth / 2);
			float textWidth = p.measureText("字");
			// 画棋子
			c.drawText(item.getText(), ileft + item.getCentx() * itemwidth
					- textWidth / 2, itop + item.getCenty() * itemwidth
					+ textWidth / 3, p); 
		}
		if (items.get(0).isFirst()) {
			p.setColor(Color.RED);
		} else {
			p.setColor(Color.BLACK);
		}
		p.setStyle(Paint.Style.STROKE);

		// 设置棋子走动后的圆圈和颜色
		p.setStrokeWidth(dpvalue * 2);
		if(lastitem!=null&&newitem!=null)
		{
			c.drawCircle(ileft + lastitem.getCentx() * itemwidth,
					itop + lastitem.getCenty() * itemwidth, itemwidth/2 , p);
			c.drawCircle(ileft + newitem.getCentx() * itemwidth,
				itop + newitem.getCenty() * itemwidth, itemwidth/2 , p);
			
			if (items.get(0).isFirst()) {
				p.setColor(Color.BLACK);
			} else {
				p.setColor(Color.RED);
			}
		}

		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(dpvalue * 1); // 设置宽度
		
		if (clickx != -1 && clicky != -1) {
			if (instep()) {
				remove_or_step();
			} else {
				for (Chess_Item_Base item : items) {
					if (item.inside(clickx, clicky)) {  // 绘制选中
						if(canClick)
						{
							if(item.isRed())
								break;
						}
						c.drawCircle(ileft + clickx * itemwidth, itop + clicky
								* itemwidth, itemwidth/2, p);
						drawSteps(c, p, item);

						temp = item;
						break;
					} else {
						temp = null;
					}
				}
			}
		}
	}
	
	private boolean instep()
	{
		if(temp!=null)
		{
			for(Chess_Item_Base item :steps)
			{
				
				if(item.inside(clickx, clicky))
					return true;
			}

			//Log.e("err", "temp no inside");
			return false;
		}
		//Log.e("err", "temp is null");
		return false;
	}
	
	private void remove_or_step()
	{
		Log.e("err", "instep remove");
		for(int i=0;i<items.size();i++ )
		{
			Chess_Item_Base item=items.get(i);
			if (item.inside(clickx, clicky))
			{
				if(!item.isSelf(temp)) // 如果是对方 ， 表示吃对方的棋子，items清除掉这个棋子
				{
					items.remove(item);
					i--;
					break;
				}
			}
		}
		if(lisenner != null && canClick)
		{
			lisenner.onStep(temp.getCentx(), temp.getCenty(), clickx, clicky);
		}
		// clickx clicky是 点击的要走动的棋子
		temp.setCentx(clickx); 
		temp.setCenty(clicky);
		
		clickx = -1;
		clicky = -1;
		temp = null;
	}
	private List<Chess_Item_Base> steps = new ArrayList<Chess_Item_Base>();
	private Chess_Item_Base temp;

	private void drawSteps(Canvas c, Paint p, Chess_Item_Base item) {

		steps = new ArrayList<Chess_Item_Base>();

		item.addSteps(items, steps);
		for (Chess_Item_Base step : steps) {
			c.drawCircle(ileft + step.getCentx() * itemwidth,itop + step.getCenty() * itemwidth, itemwidth / 4, p);
		}

	}
	
	// 绘制折线
	private void drawItemKH(Canvas c, int li, int ti, Paint p) {
		// 获取中心坐标
		int x = ileft + li * itemwidth;
		int y = itop + ti * itemwidth;
		// 线长
		int len = itemwidth / 4;
		// 距离中心
		int dc = itemwidth / 10;

		// left
		if (li != 0) {
			c.drawLine(x - dc, y + dc, x - dc - len, y + dc, p);
			c.drawLine(x - dc, y - dc, x - dc - len, y - dc, p);
			c.drawLine(x - dc, y + dc, x - dc, y + dc + len, p);
			c.drawLine(x - dc, y - dc, x - dc, y - dc - len, p);
		}
		// right
		if (li != 8) {
			c.drawLine(x + dc, y + dc, x + dc + len, y + dc, p);
			c.drawLine(x + dc, y - dc, x + dc + len, y - dc, p);
			c.drawLine(x + dc, y + dc, x + dc, y + dc + len, p);
			c.drawLine(x + dc, y - dc, x + dc, y - dc - len, p);
		}
	}

	/**
	 * 触摸事件
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if(!canClick){
			return true;
		}
		// 定义变量存储获取的值
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
/****over****/			
			if(isOver(items) != 0){
				setIsgo(false);
				if(isOver(items) == 1)
					Toast.makeText(getContext(), "红方胜利", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(getContext(), "黑方胜利", Toast.LENGTH_SHORT).show();
			}
			
			// 屏幕按下事件触发
			// 保存为全局
			int x = (int) event.getX();
			int y = (int) event.getY();
			if (itop <= y + itemwidth / 2 && y <= itop + itemwidth * 9 + itemwidth / 2)// y满足
			{
				if (ileft <= x + itemwidth / 2 && x <= ileft + itemwidth * 8 + itemwidth / 2)// x满足
				{
					clicky = (y + itemwidth / 2 - itop) / itemwidth;
					clickx = (x + itemwidth / 2 - ileft) / itemwidth;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			// 屏幕松开事件触发
			break;
		case MotionEvent.ACTION_MOVE:
			// 屏幕移动事件触发

			break;
		default:
			// 其他事件触发
			break;
		}
		return true;
	}
	public void setClick(int x,int y)
	{
		this.clicky=y;
		this.clickx=x;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
	
	public interface onStepLisenner{
		public void onStep(int oldx,int oldy,int cx,int cy);
	}
	
	public int isOver(List<Chess_Item_Base> items){
		int cnt = 0,cnt1 = 0 ;
		for(Chess_Item_Base item : items){
			if(item.getType() == 5){
				cnt++;
			}
			if(item.getText() == "帅"){
				cnt1++;
			}
		}
		if(cnt == 2)
			return 0; // 游戏未结束
		else if(cnt1 == 1){
			return 1 ; // 红方胜利
		}
		return 2 ;//黑方胜利
	}
}

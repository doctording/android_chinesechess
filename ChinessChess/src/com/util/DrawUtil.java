package com.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

public class DrawUtil {
	
	/**
	 * 画棋子
	 * @param c
	 * @param qizi
	 * @param qizibackground
	 * @param x
	 * @param y
	 * @param radius
	 * @param paint
	 */
	public void drawChess(Canvas c ,Bitmap qizi,Bitmap qizibackground,
			int x , int y ,int radius,Paint paint){
		
		Rect s1 = new Rect(0, 0, qizibackground.getWidth(),qizibackground.getHeight() );
		Rect s2 = new Rect(x-radius,y-radius,x+radius ,y+radius);
		c.drawBitmap(qizibackground, s1,s2, null);
	
		Rect sr1 = new Rect(0, 0, qizi.getWidth(),qizi.getHeight() );
		Rect sr2 = new Rect(x-radius*2/3,y-radius*2/3,x+radius*2/3,y+radius*2/3);
		c.drawBitmap(qizi,sr1,sr2,paint);
	}

	public void drawWan(Canvas c,Paint paint,int x ,int y ,int r){
		int le = r / 3 ;
		//左
		c.drawLine(x - 2*le, y - le, x - le, y - le, paint);
		c.drawLine(x - 2*le, y + le, x - le, y + le, paint);
		c.drawLine(x - le, y - 2*le, x - le, y - le, paint);
		c.drawLine(x - le, y + le, x - le, y + 2*le, paint);
		//右
		c.drawLine(x + 2*le, y - le, x + le, y - le, paint);
		c.drawLine(x + 2*le, y + le, x + le, y + le, paint);
		c.drawLine(x + le, y - 2*le, x + le, y - le, paint);
		c.drawLine(x + le, y + le, x + le, y + 2*le, paint);
	}
	
	public void drawRightWan(Canvas c,Paint paint,int x ,int y ,int r){
		int le = r / 3 ;
		//右
		c.drawLine(x + 2*le, y - le, x + le, y - le, paint);
		c.drawLine(x + 2*le, y + le, x + le, y + le, paint);
		c.drawLine(x + le, y - 2*le, x + le, y - le, paint);
		c.drawLine(x + le, y + le, x + le, y + 2*le, paint);
	}
	
	public void drawLeftWan(Canvas c,Paint paint,int x ,int y ,int r){
		int le = r / 3 ;
		//左
		c.drawLine(x - 2*le, y - le, x - le, y - le, paint);
		c.drawLine(x - 2*le, y + le, x - le, y + le, paint);
		c.drawLine(x - le, y - 2*le, x - le, y - le, paint);
		c.drawLine(x - le, y + le, x - le, y + 2*le, paint);
	}
	
	// 是否点击了bitmap
	public boolean isClick(MotionEvent event , int posX ,int posY , Bitmap bm){
		float x = event.getX();
		float y = event.getY();
		if(x > posX && x < posX + bm.getWidth() && y > posY && y < posY + bm.getHeight())
			return true;
		return false;
	}
	
}
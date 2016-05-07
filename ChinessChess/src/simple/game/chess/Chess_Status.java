package simple.game.chess;


import java.io.Serializable;
import java.util.List;

import com.util.JsonBeanUtil;
import com.util.MapAnnotation.Map;

public class Chess_Status implements Serializable {

	@Map(key = "oldx")
	private int oldx;
	@Map(key = "oldy")
	private int oldy;
	@Map(key = "cx")
	private int cx;
	@Map(key = "cy")
	private int cy;
	@Map(key = "ischange")
	boolean ischange=false;

	@Map(key = "isfirst")
	boolean isfirst=false;
	

	@Map(key = "conn")
	boolean conn=false;
	@Map(key = "message")
	private String message;
	
	
	public boolean isConn() {
		return conn;
	}
	public void setConn(boolean conn) {
		this.conn = conn;
	}
	public boolean isIsfirst() {
		return isfirst;
	}
	public void setIsfirst(boolean isfirst) {
		this.isfirst = isfirst;
	}
	public int getOldx() {
		return oldx;
	}
	public void setOldx(int oldx) {
		this.oldx = oldx;
	}
	public int getOldy() {
		return oldy;
	}
	public void setOldy(int oldy) {
		this.oldy = oldy;
	}
	public int getCx() {
		return cx;
	}
	public void setCx(int cx) {
		this.cx = cx;
	}
	public int getCy() {
		return cy;
	}
	public void setCy(int cy) {
		this.cy = cy;
	}
	public boolean isIschange() {
		return ischange;
	}
	public void setIschange(boolean ischange) {
		this.ischange = ischange;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}

package com.sqlite;

public class ModelChessmov{
	
	public String id;
	public int fromId;
	public int fromX;
	public int fromY;
	public int toId;
	public int toX;
	public int toY;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getFromId() {
		return fromId;
	}
	public void setFromId(int fromId) {
		this.fromId = fromId;
	}
	public int getFromX() {
		return fromX;
	}
	public void setFromX(int fromX) {
		this.fromX = fromX;
	}
	
	public int getFromY() {
		return fromY;
	}
	public void setFromY(int fromY) {
		this.fromY = fromY;
	}
	public int getToId() {
		return toId;
	}
	public void setToId(int toId) {
		this.toId = toId;
	}
	public int getToX() {
		return toX;
	}
	public void setToX(int toX) {
		this.toX = toX;
	}
	public int getToY() {
		return toY;
	}
	public void setToY(int toY) {
		this.toY = toY;
	}
	
	public ModelChessmov(){
		
	}
	
	public ModelChessmov(String id, int fromId, int fromX, int fromY, int toId,
			int toX, int toY) {
		super();
		this.id = id;
		this.fromId = fromId;
		this.fromX = fromX;
		this.fromY = fromY;
		this.toId = toId;
		this.toX = toX;
		this.toY = toY;
	}
	
	@Override
	public String toString() {
		return "ModelChessmov [id=" + id + ", fromId=" + fromId + ", fromX="
				+ fromX + ", fromY=" + fromY + ", toId=" + toId + ", toX="
				+ toX + ", toY=" + toY + "]";
	}

}
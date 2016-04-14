package com.algorithm;

public class Chessutil {
	
	public static boolean isBlack(int chessId){
		if(chessId >= 1 && chessId <=7){
			return true;
		}
		return false;
	}
	
	public static boolean isRed(int chessId){
		if(chessId >= 8 && chessId <= 14){
			return true;
		}
		return false;
	}
	
	public static boolean isSameSide(int chessId1,int chessId2){
		if( (isRed(chessId1) && isRed(chessId2)) || (isBlack(chessId1) && isBlack(chessId2))){
			return true;
		}
		return false;
	}
	
}
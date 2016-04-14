package com.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
	
	public String getNowime(){
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = format.format(date);
		return time;
	}
	
	public int[] getHMS(int time){
		int hour = time / 3600;
		int minite = time / 60 - hour * 60;
		int second = time % 60;
		int[] time2 = new int[3] ;
		time2[0] = hour;
		time2[1] = minite;
		time2[2] = second;
		return time2;
	}
	
	public String time2Str(int hour , int minite , int second){
		String h="" , m="" , s="" ;
		if(hour < 10){
			h += "0"+hour;
		}else{
			h += hour;
		}
		if(minite < 10){
			m += "0"+minite;
		}else{
			m += hour;
		}
		if(second < 10){
			s += "0"+second;
		}else{
			s += second;
		}
		return h +":"+m+":"+s ;
	}
}

package com.util;


public class IPUtils {
	public static boolean ping(String ip) {
		boolean result = false;
		Process p;
		try {
			System.out.println("start ping:"+ip);
			p = Runtime.getRuntime().exec(
					"ping -c 1 " + ip);
			int status = p.waitFor(); // status 只能获取是否成功，无法获取更多的信息
			if (status == 0) {
				result = true;
			} else {
				result = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}// m_strForNetAddress是输入的网址或者Ip地址
		return result;
	}
}

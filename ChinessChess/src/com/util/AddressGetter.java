package com.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

/**
 * 得到IP地址
 * @author dingwenlong
 *
 */
public class AddressGetter {
	
	public static String GetIp() {
		try {

			for (Enumeration<NetworkInterface> en = NetworkInterface

			.getNetworkInterfaces(); en.hasMoreElements();) {

				NetworkInterface intf = en.nextElement();

				for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr

				.hasMoreElements();) {

					InetAddress inetAddress = ipAddr.nextElement();
					// ipv4地址
					if (!inetAddress.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(inetAddress
									.getHostAddress())) {
						if (inetAddress.getHostAddress().contains("10.0.2"))
							continue;
						else
							return inetAddress.getHostAddress();

					}

				}

			}

		} catch (Exception ex) {

		}

		return getLocalIpAddress();

	}

	private static String getLocalIpAddress() {
		String ipaddress = "";
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						ipaddress = inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return ipaddress;
	}
}

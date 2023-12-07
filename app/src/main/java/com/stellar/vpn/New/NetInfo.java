package com.stellar.vpn.New;

import static android.content.Context.WIFI_SERVICE;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class NetInfo {

	private ConnectivityManager connManager = null;
	private WifiManager wifiManager = null;
	private WifiInfo wifiInfo = null;
	
	public NetInfo(Context context){
		connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
		wifiInfo = wifiManager.getConnectionInfo();
	}
	
	public int getCurrentNetworkType(){
		if(null == connManager){
			return 0;
		}
		NetworkInfo netInfo = connManager.getActiveNetworkInfo();
		return netInfo.getType();
	}


	public String getCurrentNetworkTypeName(){
		if(null == connManager){
			return "";
		}
		NetworkInfo netInfo = connManager.getActiveNetworkInfo();
		return netInfo.getTypeName();
	}
	
	public String getWifiIpAddress(){
		if(null == wifiManager || null == wifiInfo)
			return "";
		
		int ipAddress = wifiInfo.getIpAddress();
		return String.format("%d.%d.%d.%d",
					(ipAddress & 0xff),
					(ipAddress >> 8 & 0xff),
					(ipAddress >> 16 & 0xff),
					(ipAddress >> 24 & 0xff));
	}
	
	public String getWifiMACAddresds(){
		if(null == wifiManager || null == wifiInfo)
			return "";
		return wifiInfo.getMacAddress();
	}
	
	
	public String getWifiSSID(){
		if(null == wifiManager || null == wifiInfo)
			return "";
		return wifiInfo.getSSID();
	}
	
	public String getIPAddress(){
		String ipAddress = "";
		try{
			Enumeration<NetworkInterface> enument = NetworkInterface.getNetworkInterfaces();
			NetworkInterface netinterface = null;
			while(enument.hasMoreElements()){
				netinterface = enument.nextElement();
				
				for (Enumeration<InetAddress> enumip = netinterface.getInetAddresses();
					 enumip.hasMoreElements();){
						InetAddress inetAddress = enumip.nextElement();
						if(!inetAddress.isLoopbackAddress()){
							ipAddress = String.valueOf(inetAddress.getHostAddress());
							break;
						}
					}
			}
		}catch(SocketException e){
			e.printStackTrace();
		}
		
		return ipAddress;
	}

	public String getMobileIPAddress() {
		try {
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						return  addr.getHostAddress();
					}
				}
			}
		} catch (Exception ex) { } // for now eat exceptions
		return "";
	}

	public String getDeviceIPAddress() {
		try {
			List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface networkInterface : networkInterfaces) {
				List<InetAddress> inetAddresses = Collections.list(networkInterface.getInetAddresses());
				for (InetAddress inetAddress : inetAddresses) {
					if (!inetAddress.isLoopbackAddress()) {
						String sAddr = inetAddress.getHostAddress().toUpperCase();
								int delim = sAddr.indexOf('%');
								return delim < 0 ? sAddr : sAddr.substring(0, delim);
							}

				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}
}

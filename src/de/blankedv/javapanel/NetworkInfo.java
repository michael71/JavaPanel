package de.blankedv.javapanel;

import java.net.InetAddress;

public class NetworkInfo {
	 InetAddress inetAddress ;
	 String mac;
	 String hostname;
	 
	 NetworkInfo(InetAddress inet, String m) {	 
		 inetAddress = inet;
		 mac = new String(m);
		 hostname = inet.getHostName();
		 int n = hostname.indexOf('.');
		 if (n != -1) {
			 hostname = hostname.substring(0,n);
		 }
	}

	public String getIP() {
		return inetAddress.toString();
	}

	public String getMac() {
		return mac;
	}
	
	public String getHostname() {
		return hostname;
	}
	 
}

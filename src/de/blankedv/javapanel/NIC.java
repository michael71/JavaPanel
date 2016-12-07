package de.blankedv.javapanel;

import java.net.*;
import java.util.*;

class NIC {

	public static List<NetworkInfo> getMyIPAndMac() {

		List<NetworkInfo> addrList = new ArrayList<NetworkInfo>();
		Enumeration<NetworkInterface> interfaces = null;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			e.printStackTrace();
                        return null;
		}

		InetAddress localhost = null;

		try {
			localhost = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e) {
			e.printStackTrace();
                        return null;
		}

		while (interfaces.hasMoreElements()) {
			NetworkInterface ifc = interfaces.nextElement();
			Enumeration<InetAddress> addressesOfAnInterface = ifc.getInetAddresses();

			while (addressesOfAnInterface.hasMoreElements()) {
				InetAddress address = addressesOfAnInterface.nextElement();
                // look for IPv4 addresses which are not==127.0.0.1
				if (!address.equals(localhost) && !address.toString().contains(":")) {
					
				 	System.out.println("found network interface: " + address.getHostAddress());
				 	byte[] mac;
					try {
						mac = ifc.getHardwareAddress();
						//System.out.print("Current MAC address : ");

				        StringBuilder sb = new StringBuilder();
				        for (int i = 0; i < mac.length; i++) {
				            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));        
				        }
				        //System.out.println(sb.toString());
				        addrList.add(new NetworkInfo(address,sb.toString()));
					} catch (SocketException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 	
				}
			}
		}
		return addrList;
	}
}

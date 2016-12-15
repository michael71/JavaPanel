/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.blankedv.javapanel;

import static de.blankedv.javapanel.Defines.DEBUG;
import java.net.InetAddress;

/**
 *
 * @author mblank
 */
public class SXServer {
    public String role;
    public String service;
    public int    port;
    public String ip;
    
    SXServer(String r, String s, int p, String ip) {
        role = r;
        service = s;
        port = p;
        this.ip = ip;
        if (DEBUG) {
            System.out.println("new SX server found, service="+service+" ip="+ip.toString()+" port="+port);
        }
    }
    
    
}

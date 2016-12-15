/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.blankedv.javapanel;

import static de.blankedv.javapanel.Defines.DEBUG;

import java.awt.Dialog;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import javax.swing.JOptionPane;

import static de.blankedv.javapanel.Defines.servers;

/**
 *
 * @author mblank
 */
public class ResolveSXServices {

    private static int nServer = 0;
    private static ArrayList<SXServer> foundServers = new ArrayList<>();

    private static class SampleListener implements ServiceListener {

        @Override
        public void serviceAdded(ServiceEvent event) {
            System.out.println("Service added: " + event.getInfo());
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            System.out.println("Service removed: " + event.getInfo());
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            //System.out.println("Service resolved: " + event.getInfo());
            String app = event.getInfo().getApplication();
            String[] ips = event.getInfo().getHostAddresses();
            int port = event.getInfo().getPort();
            switch (ips.length) {
                case 0:
                    System.out.println("ResolveJmDNSService: ERROR, ip is empty");
                    break;
                case 1:
                    nServer++;
                    foundServers.add(new SXServer(app, app, port, ips[0]));
                    break;
                default:
                    if (DEBUG) {
                        System.out.println("ResolveJmDNSService: Warning, multiple ips, using=" + ips[0]);
                    }
                    nServer++;
                    foundServers.add(new SXServer(app, app, port, ips[0]));

            }

        }
    }

    public static ArrayList<SXServer> init(String[] services) throws InterruptedException {

        try {
            // Create a JmDNS instance
            JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

            for (String s : services) {
                // Add a service listener
                jmdns.addServiceListener(s, new SampleListener());
            }

            long t0 = System.currentTimeMillis();

            if (Utils.autoConfigEnabled()) {
                // in case we use "autoConfig" we have to wait until we have 
                // identified the server (max waiting time = 5secs)
                while ((nServer < services.length)
                        && ((System.currentTimeMillis() - t0) < 5000)) {
                    Thread.sleep(100);
                }
            }
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return foundServers;
    }

}

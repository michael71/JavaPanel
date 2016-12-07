package de.blankedv.javapanel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.prefs.Preferences;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

import static de.blankedv.javapanel.Defines.*;
import static de.blankedv.javapanel.JavaPanel.lbData;

/**
 * this classes run method is called every 2 seconds by a timer - it sends
 * announcement messages via UDP to announce the capabilities (i.e. lanbahn
 * channels it is listening to and the connected accessories like turnouts or
 * signals) every 20 seconds
 *
 * a UDP server is initialized in the init() method which listens to the lanbahn
 * UDP multicast port and interprets commands like "S 800 1" (set channel 800 to
 * value 1) for the accessory decoders
 *
 * the listening network interface (if there are more than 1 !) can be
 * configured in a preference file /root/.java/.userPrefs/de/blankedv/jlanbahn
 *
 * @author Michael Blank (C) 2014
 *
 */
public class LanbahnInterface extends TimerTask {

    // TODO Lanbahn Protocol doku
    public static String TEXT_ENCODING = "UTF8";
    public static InetAddress mgroup;
    public static MulticastSocket multicastsocket;
    protected int useInterface; // only necessary for multihomed raspi.

    public static LanbahnServer lbServer;

    public static boolean running = true;
    protected Thread t;
    Timer timer;
    protected static int announceCount = 0;
    protected static String announceString = "A JavaPanel";

    // Preferences prefs = Preferences.userNodeForPackage(this.getClass());
    Preferences prefs = Preferences.userRoot();
    // can be found at

    private List<NetworkInfo> myip = new ArrayList<NetworkInfo>();

    public void init() throws Exception {
        //myip = NIC.getMyIPAndMac();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(Calendar.getInstance().getTime());
        prefs.put("time", timeStamp);

        loadPrefs();

        try {
            multicastsocket = new MulticastSocket(LANBAHN_PORT);
            mgroup = InetAddress.getByName(LANBAHN_GROUP);
            multicastsocket.joinGroup(mgroup);
            System.out.println("new lanbahn multicast socket " + ":" + LANBAHN_PORT);
            
            multicastsocket.setLoopbackMode(false);  // receive message sent be this computer
            
            if (myip.size() > 1) {
                System.out
                        .println("warning: more than 1 network interface!");
            }

        } catch (IOException ex) {
            System.out
                    .println("could not open server socket on interface #"
                            + useInterface + " port=" + LANBAHN_PORT
                            + " - closing lanbahn window. "
                            + ex.getMessage());
            return;
        }
        startLanbahnServer(); // for receiving multicast messages

        timer = new Timer();
        timer.scheduleAtFixedRate(this, 100, 100); // for sending multicast messages

    }

    private void startLanbahnServer() {
        if (lbServer == null) {
            lbServer = new LanbahnServer();
            t = new Thread(lbServer);
            t.start();
        }
    }

    private void loadPrefs() throws Exception {

        // on the raspberry pi, the prefs.xml file is in
        // /root/.java/.userPrefs/de/blankedv/jlanbahn/prefs.xml
        System.out.println("reading config from " + prefs.toString());

        useInterface = prefs.getInt("interface", 0);
        //System.out.println("preferences: use interface #" + useInterface);

    }

    class LanbahnServer implements Runnable {

        public void run() {
            try {

                byte[] bytes = new byte[65536];
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length);

                while (running) {
                    // Warten auf Nachricht
                    multicastsocket.receive(packet);
                    String message = new String(packet.getData(), 0,
                            packet.getLength(), TEXT_ENCODING);
                    message = message.replaceAll("\\s+", " ").trim();
                    // replace multiple spaces by one only
                    if (DEBUG_COMM) {
                        System.out.println("rec:  " + message + " ");
                    }
                    interpretCommand(message);

                }
                System.out.println("lanbahn Server closing.");
                multicastsocket.leaveGroup(mgroup);
                multicastsocket.close();

            } catch (IOException ex) {
                System.out.println("lanbahnServer error:" + ex);
            }

        }

        /**
         * interpret a lanbahn command
         *
         * "S 802 1" means set channel 802 to value 1 only listens to "S" (set)
         * and "R" (read) commands
         *
         * @param msg
         * @return
         */
        public boolean interpretCommand(String msg) {

            // split by spaces, use "\s+" if double spaces and tabs
            // were not removed beforehand
            String[] command = msg.split(" ");

            if ((command.length == 3)
                    && (command[0].trim().substring(0, 1)
                    .equalsIgnoreCase("s"))) {
                // this is a SET commmand

                // check for addresses (2nd argument)
                int addr = convertToInt(command[1]);

                // get data value from command (3rd argument)
                int data = convertToInt(command[2]);

                // check for matching panel element
                for (PanelElement pe : panelElements) {
                    if (pe.hasAdrX(addr)) {
                        pe.setState(data);
                    }
                }
                if (simulation) {
                    // update value if in simulation mode, i.e.
                    // without actual accessory decoders
                    if (lbData.containsKey(addr)) {
                        lbData.put(addr, data);
                    }
                }

            } else if ((command.length == 3)
                    && (command[0].trim().substring(0, 2)
                    .equalsIgnoreCase("fb"))) {
                // this is a Feedback command

                // check for addresses (2nd argument)
                int addr = convertToInt(command[1]);

                // get data value from command (3rd argument)
                int data = convertToInt(command[2]);

                // check for matching panel element
                for (PanelElement pe : panelElements) {
                    if (pe.hasAdrX(addr)) {
                        pe.setState(data);
                    }
                }

            } else if ((command.length == 2)
                    && (command[0].trim().equalsIgnoreCase("r"))) {
                // then this is a READ command

                // check for addresses (2nd argument)
                int addr = convertToInt(command[1]);

                if (simulation) {
                    // echo the state back - in case we have no acc.decoders attached
                    if (lbData.containsKey(addr) && (lbData.get(addr) != INVALID_INT)) {
                        sendQ.offer("FB " + addr + " " + lbData.get(addr));
                    }
                }

            }

            // could not interpret this command or no decoder found with this address
            return false;
        }

        /**
         * helper function to convert an integer in string format to an int
         *
         * @param s
         * @return
         */
        private int convertToInt(String s) {
            // check for addresses
            int param = INVALID_INT;
            try {
                param = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out
                        .println("ERROR: command parameter is no valid integer, lanbahn channel must be 1..1024: "
                                + s + " ?");

            }
            return param;
        }

    }

    /**
     * send an announcement string (what channels are controlled by this system)
     * every few seconds
     *
     */
    @Override
    public void run() {

        announceCount++;
        if (announceCount == 200) {
            announceCount = 0;
            sendLanbahn(announceString);
            // update lanbahn panel elements
            for (PanelElement e : panelElements) {
                if ((e.isExpired()) && (e.getAdr() != INVALID_INT)) {
                    sendLanbahn("R " + e.getAdr());
                }
            }
        }
        while (!sendQ.isEmpty()) {
            String command = sendQ.poll();
            sendLanbahn(command);
        }
    }

    /**
     * send a string as a UDP datagram to lanbahn
     *
     * @param msg (lanbahn message)
     */
    private void sendLanbahn(String msg) {
        byte[] buf = msg.toUpperCase().getBytes();
        DatagramPacket packet;
        packet = new DatagramPacket(buf, buf.length, mgroup, LANBAHN_PORT);
        try {
            multicastsocket.send(packet);
            if (DEBUG_COMM) {
                System.out.println("sent: " + msg);
            }
        } catch (IOException ex) {
            System.out.println("ERROR when sending to lanbahn "
                    + ex.getMessage());
        }

    }

}

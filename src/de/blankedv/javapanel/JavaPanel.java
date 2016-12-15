/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor. 
 */
package de.blankedv.javapanel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.prefs.Preferences;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import static de.blankedv.javapanel.Defines.*;
import static de.blankedv.javapanel.LanbahnInterface.running;
import static java.awt.Frame.MAXIMIZED_BOTH;

import java.awt.Toolkit;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

@SuppressWarnings("serial")
public class JavaPanel extends JPanel implements ActionListener,
        ItemListener {

    //static final String open = "open Panel Definition File";
    static final String quit = "Quit Program";
    static final String settings = "Settings";

    static JFrame frame;
    static SettingsUI sett;

    static JMenuBar menuBar;
    JMenu menu;
    JMenuItem menuItem, menuExit;
    JMenu menu2;
    
    public static StatusBar statusBar = new StatusBar();

    public static HashMap<Integer, Integer> lbData = new HashMap<Integer, Integer>();

    final public static FramePositionMemory fm = new FramePositionMemory("javapanel");

    public JavaPanel() {

        System.out.println("JavaPanel, version = " + VERSION);
        prefs = Preferences.userNodeForPackage(this.getClass());
        loadPrefs();

        initMenues();

    }

    private void initMenues() {
        //Create the menu bar.
        menuBar = new JMenuBar();

        //Build the first menu.
        menu = new JMenu("File");
        menuBar.add(menu);
        //menuItem = new JMenuItem(open);
        //menu.add(menuItem);
        //menu.addSeparator();
        menuItem = new JMenuItem(settings);
        menu.add(menuItem);
        menu.addSeparator();
        menuExit = new JMenuItem(quit);
        menu.add(menuExit);

        menu2 = new JMenu("Info");
        menuBar.add(menu2);

        /* not needed, no keyboard input
               addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				racquet.keyReleased(e);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				racquet.keyPressed(e);
			}
		});
		setFocusable(true);  */
        menuItem.addActionListener(this);
        menuExit.addActionListener(this);
        menu2.addMenuListener(new MenuListener() {

            @Override
            public void menuSelected(MenuEvent e) {
                About aboutWindow = new About();
                aboutWindow.setVisible(true);
            }

            @Override
            public void menuDeselected(MenuEvent e) {
                System.out.println("menuDeselected");
            }

            @Override
            public void menuCanceled(MenuEvent e) {
                System.out.println("menuCanceled");
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setBackground(cBackground);
        g2d.clearRect(0, 0, frame.getWidth(), frame.getHeight());
        g2d.scale(scale, scale);
        //g2d.translate(10, 10); TODO center the panel
        for (PanelElement e : panelElements) {
            e.doDraw(g2d);
        }
    }

    public static void error(String errorTitle, String errorMessage) {
        //Custom button text
        int n;
        //System.exit(ABORT);
        n = JOptionPane.showConfirmDialog(frame,
                errorTitle + ": " + errorMessage + "\n\nWould you like to select a different Config File?",
                errorTitle,
                JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.NO_OPTION) {
            System.exit(ABORT);
        }
        settings();
    }

    public static void settings() {
        sett = new SettingsUI(panelName);
        sett.setVisible(true);
    }

    public static void main(String[] args) throws InterruptedException {

        try {   // Set system look and feel
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException 
                | InstantiationException | IllegalAccessException e) {
        }

        frame = new JFrame("Panel");
        JavaPanel javapanel = new JavaPanel();
        
        frame.add(javapanel);
         frame.setMinimumSize(new Dimension(400, 200));
        frame.setJMenuBar(menuBar);      
        frame.getContentPane().add(statusBar, java.awt.BorderLayout.SOUTH);      
        fm.loadPosition();
        frame.pack();

        frame.setVisible(true);
        frame.addComponentListener(new FrameListener());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        statusBar.setMessage("Verbinde mit SX Server...");       
        String status = resolveServers();     
        statusBar.setMessage(status);      

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                running = false;
                fm.storePosition();
                try {
                    Thread.sleep(200);  // waiting for network thread to close
                } catch (InterruptedException ex) {
                    Logger.getLogger(JavaPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("shutdown of javapanel");
            }
        });

        readAndParseConfigFromFile();

        String title = "Panel Name: " + panelName;
        sendStartOfDay = prefs.getBoolean("startofday", false);
        if (sendStartOfDay) {
            // "start of day" = set all elements to state 0
            for (PanelElement pe : panelElements) {
                pe.setState(0);
            }
        }
        if (simulation) {
            title += " - Simulation Mode";
            // store all data in hashmap
            // initialize here (after "SOD" has potentially been set)
            for (PanelElement pe : panelElements) {
                if (pe.getAdr() != INVALID_INT) {
                    lbData.putIfAbsent(pe.getAdr(), pe.getState());
                }
            }
        }

        frame.setTitle(title);
 
        lb = new LanbahnInterface();
        try {
            lb.init();
        } catch (Exception ex) {
            System.out.println(TAG + " ERROR: could not init LanbahnInterface " + ex.getMessage());
        }

        javapanel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                // e.getX/Y() is scaled
                int x = (int) (e.getX() / scale);
                int y = (int) (e.getY() / scale);
                if (DEBUG) {
                    System.out.println("touch x,y=(" + x + "," + y + ")");
                }

                for (PanelElement pe : panelElements) {
                    if (pe.isSelected(x, y)) {
                        // System.out.println("selected:"+pe.toString());
                        pe.toggle();
                    }
                }
            }

        });

        // repaint loop
        while (true) {
            if (recalcScaleFlag) {
                getPanelScale();
                recalcScaleFlag = false;
            }
            if (reloadConfigFlag) {
                reloadConfig();
            }
            javapanel.repaint();
            Route.auto();  // clear routes after some seconds
            Thread.sleep(200);  // allow some time for other processes/programs
        }

    }
    
    private static void reloadConfig() {
        // TODO
    }

    private static void readAndParseConfigFromFile() {
        String workingdirectory = System.getProperty("user.dir");
        System.out.println("dir=" + workingdirectory);

        String fname = prefs.get("configfilename", LOCAL_DIRECTORY + "/" + CONFIG_FILENAME);
        panelName = ParseConfig.readConfigFromFile(fname);
        WriteConfig.writeToXML(); // save with new, calculated elements

        if (panelName.toLowerCase().contains("error filenotfound")) {
            // Config file was not found
            frame.setPreferredSize(new Dimension(500, 300));
            error(panelName, fname);
            while (sett.isVisible()) {
                try {
                    // exit program when settings window is closed
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    Logger.getLogger(JavaPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.exit(0);
        }
    }

    private static String resolveServers() {
        // search via jmDNS for the following services
        String[] services = {"_lanbahn._udp.local.", "_sxnet._tcp.local.", "_sxconfig._tcp.local."};
        
        try {
            servers = ResolveSXServices.init(services);  // wait for servers (max 10 secs)
        } catch (InterruptedException ex) {
            Logger.getLogger(JavaPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        String msg = "kein config server.";
        for (SXServer s: servers) {
            if (s.service.equalsIgnoreCase("sxconfig")) {
                msg = "SX-Config Server: "+s.ip;
            }
        }       
        return msg;     
    }

    private static void getPanelScale() {
        String s = prefs.get("scale", "auto");
        if (s.toLowerCase().contains("auto")) {
            scale = autoscalePanel();
            if (scale < 1.0f) {
                scale = 1.0f;
            }
        } else if (s.toLowerCase().contains("max")) {
            frame.setExtendedState(MAXIMIZED_BOTH);
            scale = autoscalePanel();
            if (scale < 1.0f) {
                scale = 1.0f;
            }
        } else {
            scale = Float.parseFloat(s);
        }
        if (DEBUG) {
            System.out.println("scale=" + scale);
        }

        prefs.putFloat("scale-auto", scale);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case quit:
                System.exit(0);
                break;
            case settings:
                settings();
                break;
        }
        System.out.println("action " + e.getActionCommand());
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   
    private static float autoscalePanel() {
        int wi = frame.getWidth();
        int he = frame.getHeight();
        if ((wi < 400) || (he < 200)) {
            frame.setSize(new Dimension(400, 200));
            wi = frame.getWidth();
            he = frame.getHeight();
        }
        if (DEBUG) {
            System.out.println("autoscalePanel, w=" + wi + " h=" + he);
        }
        
        return Utils.calcPanelScale(wi, he);
 
    }

    /**
     * to catch the window resize event (if resized by hand) only has effect of
     * scale setting is "auto"
     */
    private static class FrameListener implements ComponentListener {

        public void componentHidden(ComponentEvent arg0) {
        }

        public void componentMoved(ComponentEvent arg0) {
        }

        public void componentResized(ComponentEvent arg0) {

            String s = prefs.get("scale", "auto");
            if (s.equalsIgnoreCase("auto")) {
                recalcScaleFlag = true;
            }
        }

        public void componentShown(ComponentEvent arg0) {
        }
    }
    
     private static void loadPrefs() {
        simulation = prefs.getBoolean("simulation", false);
        sendStartOfDay = prefs.getBoolean("startOfDay", false);
        drawAddresses = prefs.getBoolean("showAddresses", false);
        drawAddresses2 = prefs.getBoolean("showAddresses", false);
        routesEnabled = prefs.getBoolean("routesEnabled", false);
        String ac = prefs.get("autocleartime", "30s");
        autoClearTimeRoutes = Integer.parseInt(ac.substring(0, ac.length() - 1));

        String scaleString = prefs.get("scale", "2.0");
        if (scaleString.toLowerCase().contains("auto")) {
            scale = 2.0f;
            recalcScaleFlag = true; // calculate actual scale later
        } else if (scaleString.toLowerCase().contains("max")) {
            scale = 2.0f;
            recalcScaleFlag = true; // calculate actual scale later
        } else {
            scale = Float.parseFloat(scaleString); // get scale from setting
        }
        
        style = prefs.get("style","DE");
        Defines.setStyle(style);
        
        if (DEBUG) {
            System.out.println("loading preferences");
            System.out.println("simulation=" + simulation);
            System.out.println("sendStartOfDay=" + sendStartOfDay);
            System.out.println("showAddresses=" + drawAddresses);
            System.out.println("scale=" + scaleString);
        }

    }

}

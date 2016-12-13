/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.blankedv.javapanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.prefs.Preferences;

/**
 *
 * @author mblank
 */
public class Defines {

    public static final boolean DEBUG = true; // enable or disable debugging
    // with file
    public static final boolean DEBUG_COMM = true; // debugging of all lanbahn
    // msgs
    public static boolean comm1;

    public static final int LANBAHN_PORT = 27027;
    public static final String LANBAHN_GROUP = "239.200.201.250";
    public static final int MAX_LANBAHN_ADDRESS = 10000;
    public static LanbahnInterface lb = null;

    public static final String TAG = "JavaPanel";
    public static String selectedStyle = "US"; // German style or USS style

    public static ArrayList<PanelElement> panelElements = new ArrayList<>();
    public static ArrayList<Route> routes = new ArrayList<>();
    public static ArrayList<CompRoute> compRoutes = new ArrayList<>();
    public static ArrayList<SXMapping> sxmap = new ArrayList<>();
    
    public static final int MAX_LAMP_BUTTONS = 4;

    public static String panelName = "";
    public static float scale;
    public static boolean recalcScaleFlag = false;

    public static boolean drawAddresses = true; //false;
    public static boolean drawAddresses2 = true; //false;
    public static boolean flipUpsideDown = false;  //display all panel element from "other side"
    public static boolean saveStates;
    public static boolean sendStartOfDay = false;
    public static boolean simulation = false;
    public static boolean routesEnabled = false;
    public static int autoClearTimeRoutes = 30;    // clear routes automatically after 30secs


    // preferences
    // public static final String KEY_LOCO_ADR = "locoAdrPref";
    public static final String KEY_DRAW_ADR = "drawAddressesPref";
    public static final String KEY_DRAW_ADR2 = "drawAddressesPref2";
    public static final String KEY_STYLE_PREF = "selectStylePref";
    public static final String KEY_ENABLE_ZOOM = "enableZoomPref";
    public static final String KEY_ENABLE_EDIT = "enableEditPref";
    public static final String KEY_SAVE_STATES = "saveStatesPref";
    public static final String KEY_ROUTES = "routesPref";
    public static final String KEY_FLIP = "flipPref";
    public static final String KEY_ENABLE_ALL_ADDRESSES = "enableAllAddressesPref";
    public static final String KEY_XOFF = "xoffPref";
    public static final String KEY_YOFF = "yoffPref";
    public static final String KEY_SCALE = "scalePref";
    
    public static Preferences prefs;

    // connection state
    //public static LanbahnClientThread client;
    private static long timeOfLastReceivedMessage = 0;
    public static final BlockingQueue<String> sendQ = new ArrayBlockingQueue<String>(200);
    //
    public static final int TYPE_STATUS_MSG = 0;
    public static final int TYPE_ROUTE_MSG = 1;
    public static final int TYPE_FEEDBACK_MSG = 2;
    public static final int TYPE_ERROR_MSG = 3;

    public static String connString = "";

    public static final String LOCAL_DIRECTORY = "/home/pi"; // with trailing
    // slash

    public static final String CONFIG_FILENAME = "demo-panel.xml";

    public static final String DEMO_FILE = "demo-panel.xml"; // demo data in raw
    // assets dir.

    public static boolean configHasChanged = false; // store info whether config
    // has changed
    // if true, then a new config file is written at the end of the Activity

    public static final int INVALID_INT = -9999;
    public static final int INVALID_LANBAHN_DATA = 999;


    public static int RASTER = 20; // raster points
    // with xx pixels
    public static final int TURNOUT_LENGTH = 8; // NOT to be prescaled
    public static final int TURNOUT_LENGTH_LONG = (int) (TURNOUT_LENGTH * 1.4f);


    public static boolean enableEdit = false;

    public static final BasicStroke STROKE_SOLID = new BasicStroke(5.0f , BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
    public static final BasicStroke STROKE_LITE = new BasicStroke(3.0f , BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
    public static final float[] DASH = {8.0f};
    public static final BasicStroke STROKE_DASH = new BasicStroke(3.5f , BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, DASH, 0.0f);

    // TODO make different "looks"
    public static Color cBackground = Color.WHITE;
    public static Color cUnknown = Color.LIGHT_GRAY;
    public static Color cTrack = Color.BLACK;
    public static Color cSignal = Color.BLACK;
    public static Color cFree = Color.LIGHT_GRAY;
    public static Color cOccupied = Color.RED;
    public static Color cInRoute = Color.YELLOW;

    public static final Font ADDRESS_FONT = new Font("Dialog", Font.BOLD, 6);
    public static final Color ADDRESS_COLOR = Color.MAGENTA;
    
    public static final String VERSION = "1.1 - 16 May 2016";
}

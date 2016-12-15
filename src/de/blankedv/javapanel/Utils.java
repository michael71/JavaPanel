/*
 * some Util functions like min and max of 3 integers
 */
package de.blankedv.javapanel;

import static de.blankedv.javapanel.Defines.*;
import javax.swing.JFrame;

/**
 *
 * @author mblank
 */
public class Utils {

    /**
     * calculate minimum of 3 integers, the first one is always a valid number,
     * the other can if INVALID_INT (=>not taken into account) or valid
     * integers, then they are evaluated
     */
    public static int min(int x, int xt, int x2) {
        int m = x;  // is always defined.
        if (x == INVALID_INT) {
            System.out.println(TAG + "  Utils.min: x is undefined.");
        }
        if ((xt != INVALID_INT) && (xt < m)) {
            m = xt;
        }
        if ((x2 != INVALID_INT) && (x2 < m)) {
            m = x2;
        }
        return m;
    }

    /**
     * calculate maximum of 3 integers, the first one is always a valid number,
     * the other can if INVALID_INT (=>not taken into account) or valid
     * integers, then they are evaluated
     */
    public static int max(int x, int xt, int x2) {
        int m = x;
        if (x == INVALID_INT) {
            System.out.println(TAG + "Utils.min: x is undefined.");
        }
        if ((xt != INVALID_INT) && (xt > m)) {
            m = xt;
        }
        if ((x2 != INVALID_INT) && (x2 > m)) {
            m = x2;
        }
        return m;
    }

    public static float calcPanelScale(int frameWidth, int frameHeight) {

        int minx = 1000;
        int maxx = 0;
        int miny = 1000;
        int maxy = 0;

        for (PanelElement pe : panelElements) {
            int peMaxx = Utils.max(pe.x, pe.xt, pe.x2);
            int peMaxy = Utils.max(pe.y, pe.yt, pe.y2);

            int peMinx = Utils.min(pe.x, pe.xt, pe.x2);
            int peMiny = Utils.min(pe.y, pe.yt, pe.y2);

            if (peMaxx > maxx) {
                maxx = peMaxx;
            }
            if (peMaxy > maxy) {
                maxy = peMaxy;
            }

            if (peMinx < minx) {
                minx = peMinx;
            }
            if (peMiny < miny) {
                miny = peMiny;
            }
        }
        if (DEBUG) {
            System.out.println("minx=" + minx + " maxx=" + maxx
                    + " miny=" + miny + " maxy=" + maxy);
        }
        float px = (float) frameWidth / (float) (maxx - minx + 40);
        float py = (float) frameHeight / (float) (maxy - miny + 40);
        if (DEBUG) {
            System.out.println("px=" + px + " py=" + py);
        }

        float s = Math.min(px, py);

        prefs.putFloat("auto-scale", s);
        prefs.put("scale", "auto");

        return s;
    }
    
    static boolean autoConfigEnabled() {
        String ac = prefs.get("autoconfig","Ja");
        if ( ac.equalsIgnoreCase("ja") || ac.equalsIgnoreCase("yes")) {
            return true;
        } else {
            return false;
        }
    }
}

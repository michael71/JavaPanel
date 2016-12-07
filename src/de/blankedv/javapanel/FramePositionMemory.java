/*
 * see
 * http://stackoverflow.com/questions/6593322/why-does-the-jframe-setsize-method-not-set-the-size-correctly
 */
package de.blankedv.javapanel;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.prefs.Preferences;

import static de.blankedv.javapanel.Defines.prefs;
import static de.blankedv.javapanel.Defines.DEBUG;
import static de.blankedv.javapanel.JavaPanel.frame;
import javax.swing.JFrame;

/**
 * stores size and position of panel frame
 * 
 * TODO: do autoscale and store scale also
 * TODO: store different values for each individual panel (id by name)
 * 
 * @author mblank
 */
public class FramePositionMemory {
    public static final String WIDTH_PREF = "-width";

    public static final String HEIGHT_PREF = "-height";

    public static final String XPOS_PREF = "-xpos";

    public static final String YPOS_PREF = "-ypos";
    private String prefix;;
    // Class<?> cls;

    public FramePositionMemory(String prefix) {
        this.prefix = prefix;

    }
    

    public void loadPosition() {
       
    //  Restore the most recent mainframe size and location
        int width = prefs.getInt(prefix + WIDTH_PREF, frame.getWidth());
        int height = prefs.getInt(prefix + HEIGHT_PREF, frame.getHeight());
        if (width < 400) width = 400;
        if (height < 200) height = 200;
        if (DEBUG) System.out.println("loaded w=" + width + " h=" + height);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int xpos = (screenSize.width - width) / 2;
        int ypos = (screenSize.height - height) / 2;
        xpos = prefs.getInt(prefix + XPOS_PREF, xpos);
        ypos = prefs.getInt(prefix + YPOS_PREF, ypos);
        frame.setPreferredSize(new Dimension(width, height));  
        frame.setLocation(xpos, ypos);
 
    }

    public void storePosition() {
       
        int w = frame.getWidth();
        int h = frame.getHeight();
        if (w < 400) w= 400;
        if (h < 200) h =200;
        prefs.putInt(prefix + WIDTH_PREF, w+10 );
        prefs.putInt(prefix + HEIGHT_PREF, h+10);
        Point loc = frame.getLocation();
        prefs.putInt(prefix + XPOS_PREF, (int)loc.getX());
        prefs.putInt(prefix + YPOS_PREF, (int)loc.getY());
        if (DEBUG) System.out.println("saving position: w=" + w + " h=" + h + " x=" + loc.getX() + " y=" + loc.getY());
    }
}
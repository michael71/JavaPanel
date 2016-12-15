/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.blankedv.javapanel;

import de.blankedv.javapanel.PanelElement;
import de.blankedv.javapanel.SensorElement;
import de.blankedv.javapanel.SignalElement;
import de.blankedv.javapanel.Utils;
import static de.blankedv.javapanel.Defines.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * all active panel elements, like turnouts, signals, trackindicators (=sensors)
 * are derviced from this class. These elements have a "state" which is exactly
 * the same number as the "data" of the lanbahn messages "SET 810 2" => set
 * state of panel element with address=810 to state=2
 *
 * a panel element has only 1 address (=> double slips are 2 panel elements)
 *
 * @author mblank
 *
 */
public abstract class ActivePanelElement extends PanelElement {

    // these constants are defined just for easier understanding of the
    // methods of the classes derived from this class
    // turnouts
    protected static final int STATE_CLOSED = 0;
    protected static final int STATE_THROWN = 1;
    protected static final int N_STATES_TURNOUTS = 2;

    // signals
    protected static final int STATE_RED = 0;
    protected static final int STATE_GREEN = 1;
    protected static final int STATE_YELLOW = 2;
    protected static final int STATE_YELLOW_FEATHER = 3;
    protected static final int STATE_SH1 =3;
    protected static final int N_STATES_SIGNALS = 4;

    // buttons
    protected static final int STATE_NOT_PRESSED = 0;
    protected static final int STATE_PRESSED = 1;

    // sensors
    protected static final int STATE_FREE = 0;
    protected static final int STATE_OCCUPIED = 1;
    protected static final int STATE_INROUTE = 2;
    protected static final int N_STATES_SENSORS = 3;

    protected static final int STATE_UNKNOWN = INVALID_INT;

    protected int state;
    protected int adr = INVALID_INT;
    protected long lastToggle = 0L;
    protected long lastUpdateTime = 0L;

    public ActivePanelElement() {
        super(null, 0, 0);
    }

    /**
     * constructor for an ACTIVE panel element with 1 address default state is
     * "CLOSED" (="RED")
     *
     * @param type
     * @param x
     * @param y
     * @param name
     * @param adr
     */
    public ActivePanelElement(String type, int x, int y, String name, int adr) {
        super(null, x, y);
        this.type = type;
        this.state = STATE_UNKNOWN;
        this.adr = adr;
        lastUpdateTime = System.currentTimeMillis();
    }

    @Override
    public int getAdr() {
        return adr;
    }

    @Override
    public int getState() {
        return state;
    }
    
    @Override
    public boolean setState(int a) {
        return false;
    }

    @Override
    public boolean hasAdrX(int address) {
        if (adr == address) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setAdr(int adr) {
        this.adr = adr;
        this.state = STATE_UNKNOWN;
        this.lastUpdateTime = System.currentTimeMillis();
        if (adr != INVALID_INT) {
            //TODO sendQ.add("READ " + adr); // request update for this element
        }
    }

    

    @Override
    public boolean isSelected(int xs, int ys) {
        // check only for active elements
        // search x for range in x..(x+/-w)
        // search y for range in y..(y+/-h)

        Rectangle rect = getRect();

        // the touchpoint should be within rectangle of panel element
        // similar  rect.contains() methods, BUT the lines of the rect are
        // both included in the allowed area
        if ((xs >= rect.x) && (xs <= (rect.x + rect.width)) && (ys >= rect.y)
                && (ys <= (rect.y + rect.height))) {

            if (DEBUG) {
                System.out.println(TAG + "selected adr=" + adr + " " + type + "  (" + x + ","
                        + y + ") in rect=" + rect.toString());
            }
            return true;
        } else {
            // if (DEBUG) Log.d(TAG, "NO sel.  adr=" + adr + " " + type +
            // " not in rect="+ rect.toString());
            return false;
        }
    }

    // in contrast to android, the java rectangle is defined by
    // Rectangle(x,y,w,h)
    protected Rectangle getRect() {
        if (this instanceof SignalElement) {
            return new Rectangle(x - RASTER / 5, y - RASTER / 7, 2*RASTER / 5, 2 * RASTER / 7);
        } else if (this instanceof SensorElement) {
            if (x2 == INVALID_INT) {  // dot type sensor
                return new Rectangle(x - RASTER / 5, y - RASTER / 7, 2*RASTER / 5, 2 * RASTER / 7);
            } else {   // line type sensor
                return new Rectangle((x + x2) / 2 - RASTER / 5, (y + y2) / 2 - RASTER / 7, (x2 - x) ,
                        (y2 - y));

            }
        } else {
            // Rect rect = new Rect(left, top, right, bottom)
            int minx = Utils.min(x, xt, x2);
            int maxx = Utils.max(x, xt, x2);

            //noinspection SuspiciousNameCombination
            int miny = Utils.min(y, yt, y2);
            int maxy = Utils.max(y, yt, y2);

            // Rect rect = new Rect(left, top, right, bottom)
            return new Rectangle(minx, miny, (maxx-minx), (maxy-miny));
        }
    }
    
    protected void doDrawAddresses(Graphics2D g) {
        g.setFont(addressFont);
        g.setColor(addressColor);
        //Rectangle bounds = getRect();
        String txt;
        if (adr == INVALID_INT) {
            txt = "???";
        } else {
            txt = "" + adr;
        }
      
        if (x2 >= x) {
        //g.drawString(txt, (x+2)*scale, (y-2)*scale);
        g.drawString(txt, (x+2), (y-2));
        } else {
        //g.drawString(txt, (x-14)*scale, (y-2)*scale);
        g.drawString(txt, (x-14), (y-2));
        }
            

    }

    /**
     *
     * @return true, if the state of this element has not been communicated
     * during in the last 20 seconds
     */
    @Override
    public boolean isExpired() {
        return ((System.currentTimeMillis() - lastUpdateTime) > 20 * 1000);
    }

}

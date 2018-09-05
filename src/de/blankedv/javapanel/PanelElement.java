package de.blankedv.javapanel;

import static de.blankedv.javapanel.Defines.*;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * generic panel element - this can be a passive (never changing) panel element
 * or an active (lanbahn status dependent) element.
 *
 * @author mblank
 *
 */
public class PanelElement {

    protected String name = "";
    protected String type = "";
    protected int x; // starting point
    protected int y;
    protected int x2 = INVALID_INT; // endpoint - x2 always >x
    protected int y2 = INVALID_INT;
    protected int xt = INVALID_INT; // "thrown" position for turnout
    protected int yt = INVALID_INT;
    protected int state = 0;
    protected int adr = INVALID_INT;
    protected int adr2 = INVALID_INT;  // needed for DCC sensors with 2 addresses (1=occ/free, 2=in-route)
    protected String route = "";

    public PanelElement(String type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        name = "";
    }

    public PanelElement(String type, Point poi) {
        this.type = type;
        this.x = poi.x;
        this.y = poi.y;
        name = "";
    }

    public PanelElement(String type, Point poi, Point closed, Point thrown) {
        this.type = type;
        this.x = poi.x;
        this.y = poi.y;
        this.x2 = closed.x;
        this.y2 = closed.y;
        this.xt = thrown.x;
        this.yt = thrown.y;
        name = "";
    }

    public PanelElement() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void doDraw(Graphics2D g) {
        g.setStroke(STROKE_SOLID);
        g.setColor(cTrack);
        g.drawLine(x, y, x2, y2 );
    }

    public boolean isSelected(int lastTouchX, int lastTouchY) {
        return false;
    }

    public void toggle() {
        // do nothing for non changing element
    }

    public int getAdr() {
        return INVALID_INT;
    }
    
    public int getAdr2() {
        return INVALID_INT;
    }

    public void setAdr(int a) {

    }
    
    public void setAdr2(int a) {

    }

    public boolean hasAdrX(int address) {
        return false;
    }

    /** search for a panel element when only the address is known
     * 
     * @param address
     * @return 
     */
    public static PanelElement getPeByAddress(int address) {
		for (PanelElement pe : panelElements) {
			if (pe.getAdr() == address) {
				return pe;
			}
		}
		return null;
	}

    public int getState() {
        return 0;
    }
    
    public boolean setState(int a) {
        return false;
    }

    
    public boolean isExpired() {
        return false;  // a non-active element is never expired
    }

    /**
     * scale all panel elements for better fit on display and for possible
     * "upside down" display (=view from other side of the layout) currently
     * only called from readXMLConfigFile (i.e. NOT when flipUpsideDown is
     * changed in the prefs)
     */
    public static void scaleAll() {

        // in WriteConfig the NEW values are written !!
        int xmin = INVALID_INT;
        int xmax = INVALID_INT;
        int ymin = INVALID_INT;
        int ymax = INVALID_INT;
        boolean first = true;
        for (PanelElement pe : panelElements) {
            if (first) {
                xmin = xmax = pe.x;
                ymin = ymax = pe.y;
                first = false;
            }

            if ((pe.x != INVALID_INT) && (pe.x < xmin)) {
                xmin = pe.x;
            }
            if ((pe.x != INVALID_INT) && (pe.x > xmax)) {
                xmax = pe.x;
            }
            if ((pe.x2 != INVALID_INT) && (pe.x2 < xmin)) {
                xmin = pe.x2;
            }
            if ((pe.x2 != INVALID_INT) && (pe.x2 > xmax)) {
                xmax = pe.x2;
            }
            if ((pe.xt != INVALID_INT) && (pe.xt < xmin)) {
                xmin = pe.xt;
            }
            if ((pe.xt != INVALID_INT) && (pe.xt > xmax)) {
                xmax = pe.xt;
            }

            if ((pe.y != INVALID_INT) && (pe.y < ymin)) {
                ymin = pe.y;
            }
            if ((pe.y != INVALID_INT) && (pe.y > ymax)) {
                ymax = pe.y;
            }
            if ((pe.y2 != INVALID_INT) && (pe.y2 < ymin)) {
                ymin = pe.y2;
            }
            if ((pe.y2 != INVALID_INT) && (pe.y2 > ymax)) {
                ymax = pe.y2;
            }
            if ((pe.yt != INVALID_INT) && (pe.yt < ymin)) {
                ymin = pe.yt;
            }
            if ((pe.yt != INVALID_INT) && (pe.yt > ymax)) {
                ymax = pe.yt;
            }

        }

        // now move origin to (20,20)
        for (PanelElement pe : panelElements) {
            if (!flipUpsideDown) {
                if (pe.x != INVALID_INT) {
                    pe.x = 20 + (pe.x - xmin);
                }
                if (pe.x2 != INVALID_INT) {
                    pe.x2 = 20 + (pe.x2 - xmin);
                }
                if (pe.xt != INVALID_INT) {
                    pe.xt = 20 + (pe.xt - xmin);
                }
                if (pe.y != INVALID_INT) {
                    pe.y = 20 + (pe.y - ymin);
                }
                if (pe.y2 != INVALID_INT) {
                    pe.y2 = 20 + (pe.y2 - ymin);
                }
                if (pe.yt != INVALID_INT) {
                    pe.yt = 20 + (pe.yt - ymin);
                }
            } else {
                if (pe.x != INVALID_INT) {
                    pe.x = 20 + (xmax - pe.x);
                }
                if (pe.x2 != INVALID_INT) {
                    pe.x2 = 20 + (xmax - pe.x2);
                }
                if (pe.xt != INVALID_INT) {
                    pe.xt = 20 + (xmax - pe.xt);
                }
                if (pe.y != INVALID_INT) {
                    pe.y = 20 + (ymax - pe.y);
                }
                if (pe.y2 != INVALID_INT) {
                    pe.y2 = 20 + (ymax - pe.y2);
                }
                if (pe.yt != INVALID_INT) {
                    pe.yt = 20 + (ymax - pe.yt);
                }
            }

        }

        if (DEBUG) {
            System.out.println(TAG + " xmin=" + xmin + " xmax=" + xmax + " ymin=" + ymin
                    + " ymax=" + ymax);
        }

        //configHasChanged = true;   ==> will not saved in xml file

    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.blankedv.javapanel;

import static de.blankedv.javapanel.Defines.*;
import java.awt.Color;
import java.awt.Graphics2D;

public class SignalElement extends ActivePanelElement {

    // for signals which can be interactivly set from panel
    public SignalElement(String type, int x, int y, String name, int adr) {
        super(type, x, y, name, adr);
    }

    public SignalElement() {
        adr = INVALID_INT;
        state = STATE_UNKNOWN;
    }

    public SignalElement(PanelElement signal) {
        type = signal.type;
        x = signal.x;
        y = signal.y;
        adr = INVALID_INT;
        state = STATE_UNKNOWN;
    }

    public static SignalElement findSignalByAddress(int address) {
        for (PanelElement pe : panelElements) {
            if (pe instanceof SignalElement) {
                if (pe.getAdr() == address) {
                    return (SignalElement) pe;
                }
            }
        }

        return null;

    }

    @Override
    public void doDraw(Graphics2D g) {

        // read data from SX bus and paint position of turnout accordingly
        // draw a line and not a bitmap
        g.setColor(cSignal);
        g.setStroke(strokeLite);
        g.drawLine(x, y, x2, y2); //(int) (x * scale), (int) (y * scale), (int) (x2 * scale), (int) (y2 * scale));

        g.drawLine(x2, (y2 - 2), x2, (y2 + 2));

        g.setColor(cSignal);

        g.setStroke(strokeSolid);
        // TODO finetune position of color indicator
        int r = 8;
        int r2 = 6;
        
        int xd = x - r2/2;
        int yd = y - r2/2;

        g.fillOval(x - r/2, y - r/2, r, r);
        if ((enableEdit) || (adr == INVALID_INT)) {
            g.setColor(Color.RED); //cUnknown);
        } else if (state == STATE_RED) {
            g.setColor(Color.RED);
        } else if (state == STATE_GREEN) {
            g.setColor(Color.GREEN);

        } else if ((state == STATE_YELLOW) || (state == STATE_YELLOW_FEATHER)) {
            g.setColor(Color.YELLOW);

        } else if (state == STATE_UNKNOWN) {
            g.setColor(Color.RED); //cUnknown);

        }
        g.fillOval(xd, yd, r2, r2);
        if (drawAddresses) {
            doDrawAddresses(g);
        }
    }

    @Override
    public boolean setState(int state) {
        if (state < N_STATES_SIGNALS) {
            this.state = state;
            lastUpdateTime = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void toggle() {
        if (routesEnabled) return; // do not set signals by hand if routes are enabled

        if (adr == INVALID_INT) {
            return; // do nothing if no address defined.
        }
        if ((System.currentTimeMillis() - lastToggle) < 250) {
            return;  // do not toggle twice within 250msecs
        }
        lastToggle = System.currentTimeMillis();  // reset toggle timer

        // only for a SIMPLE SIGNAL RED / GREEN
        if (state != STATE_GREEN) {
            state = STATE_GREEN;
        } else {
            state = STATE_RED;
        }

        // state = STATE_UNKNOWN; // until updated via lanbahn message
        sendQ.add("SET " + adr + " " + state);  // ==> send changed data over network turnout interface
        if (DEBUG) {
            System.out.println(TAG + " toggle(adr=" + adr + ") new state=" + state);
        }
    }

    @Override
    public boolean isSelected(int xs, int ys) {
        // for signal check radius = RASTER/5 around signal center		
        int minx = x - RASTER / 5;
        int maxx = x + RASTER / 5;
        int miny = y - RASTER / 5;
        int maxy = y + RASTER / 5;

        // the touchpoint should be within rectangle of panel element
        if ((xs >= minx) && (xs <= maxx) && (ys >= miny) && (ys <= maxy)) {
            if (DEBUG) {
                System.out.println(TAG + " selected adr=" + adr + " " + type + "  (" + x + "," + y + ")");
            }
            return true;
        } else {
            // if (DEBUG) Log.d(TAG, "No Signal selection");
            return false;
        }
    }
}

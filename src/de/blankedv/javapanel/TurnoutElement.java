package de.blankedv.javapanel;

import static de.blankedv.javapanel.Defines.*;
import java.awt.Color;
import java.awt.Graphics2D;

public class TurnoutElement extends ActivePanelElement {

    // for turnouts which can be interactivly set from panel
    public TurnoutElement(String type, int x, int y, String name, int adr) {
        super(type, x, y, name, adr);
    }

    public TurnoutElement() {
        adr = INVALID_INT;
        state = STATE_UNKNOWN;
    }

    public TurnoutElement(PanelElement turnout) {
        type = turnout.type;
        x = turnout.x;
        y = turnout.y;
        x2 = turnout.x2;
        y2 = turnout.y2;
        xt = turnout.xt;
        yt = turnout.yt;
        adr = INVALID_INT;
        state = STATE_UNKNOWN;
    }

    @Override
    public void doDraw(Graphics2D g) {

         // draw a line and not a bitmap
        g.setStroke(strokeSolid);
 /*       if (enableEdit) {
            g.setColor(Color.GREEN);
            g.drawLine((int) (x * scale), (int) (y * scale), (int) (x2 * scale), (int) (y2 * scale));
            g.setColor(Color.RED);
            g.drawLine((int) (x * scale), (int) (y * scale), (int) (xt * scale), (int) (yt * scale));
        } else if (adr == INVALID_INT) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine((int) (x * scale), (int) (y * scale), (int) (x2 * scale), (int) (y2 * scale));
            g.drawLine((int) (x * scale), (int) (y * scale), (int) (xt * scale), (int) (yt * scale));
        } else if (state == STATE_CLOSED) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine((int) (x * scale), (int) (y * scale), (int) (xt * scale), (int) (yt * scale));
            //canvas.drawLine(x * prescale, y * prescale, xt * prescale, yt * prescale, bgPaint);
            g.setColor(Color.BLACK);
            g.drawLine((int) (x * scale), (int) (y * scale), (int) (x2 * scale), (int) (y2 * scale));
            //canvas.drawLine(x * prescale, y * prescale, x2 * prescale, y2 * prescale, linePaint2);
        } else if (state == STATE_THROWN) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine((int) (x * scale), (int) (y * scale), (int) (x2 * scale), (int) (y2 * scale));
            // canvas.drawLine(x * prescale, y * prescale, x2 * prescale, y2 * prescale, bgPaint);
            g.setColor(Color.BLACK);
            g.drawLine((int) (x * scale), (int) (y * scale), (int) (xt * scale), (int) (yt * scale));
            //canvas.drawLine(x * prescale, y * prescale, xt * prescale, yt * prescale, linePaint2);
        } else if (state == STATE_UNKNOWN) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine((int) (x * scale), (int) (y * scale), (int) (xt * scale), (int) (yt * scale));
            g.drawLine((int) (x * scale), (int) (y * scale), (int) (x2 * scale), (int) (y2 * scale));
        }  */
 
   if (enableEdit) {
            g.setColor(Color.GREEN);
            g.drawLine(x, y,  x2, y2);
            g.setColor(Color.RED);
            g.drawLine(x, y, xt, yt); 
        } else if (adr == INVALID_INT) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(x, y, x2, y2);
            g.drawLine(x, y, xt, yt);
        } else if (state == STATE_CLOSED) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(x, y, xt, yt); 
            g.setColor(Color.BLACK);
             g.drawLine(x, y,  x2, y2);
        } else if (state == STATE_THROWN) {
            g.setColor(Color.LIGHT_GRAY);
             g.drawLine(x, y,  x2, y2);
            g.setColor(Color.BLACK);
            g.drawLine(x, y, xt, yt);
        } else if (state == STATE_UNKNOWN) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(x, y, xt, yt);
            g.drawLine(x, y,  x2, y2);
        }   

        if (drawAddresses) {
            doDrawAddresses(g);
        }
    }
    
    @Override
    public boolean setState(int state) {
        if (state < N_STATES_TURNOUTS) {
            this.state = state;
            lastUpdateTime = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
       
    }

    @Override
    public void toggle() {
        	if (routesEnabled) return; // do not set turnouts by hand if routes are enabled
		
		if (adr == INVALID_INT) return; // do nothing if no sx address defined.
		
		if ((System.currentTimeMillis() - lastToggle) < 250) return;  // do not toggle twice within 250msecs
		
		lastToggle = System.currentTimeMillis();  // reset toggle timer

		
		// only for a SIMPLE turnout
                switch (state) {
                    case STATE_CLOSED:
                        state = STATE_THROWN;
                        break;
                    case STATE_THROWN:
                        state = STATE_CLOSED;
                        break;
                    case STATE_UNKNOWN:
                        state = STATE_CLOSED;
                        break;          
                }
		
		// state = STATE_UNKNOWN; // until updated via lanbahn message
		sendQ.add("SET "+adr+" "+state);  // ==> send changed data over network turnout interface
		if (DEBUG) System.out.println(TAG+" toggle(adr="+adr+") new state="+state);
        
    }

    public static TurnoutElement findTurnoutByAddress(int address) {
        for (PanelElement pe : panelElements) {
            if (pe instanceof TurnoutElement) {
                if (pe.getAdr() == address) {
                    return (TurnoutElement) pe;
                }
            }
        }
        return null;
    }

}

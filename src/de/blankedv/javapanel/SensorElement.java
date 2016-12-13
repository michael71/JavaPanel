/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.blankedv.javapanel;

import static de.blankedv.javapanel.Defines.*;
import java.awt.Color;
import java.awt.Graphics2D;

public class SensorElement extends ActivePanelElement {

    public SensorElement(String type, int x, int y, String name, int adr) {
        super(type, x, y, name, adr);
    }

    public SensorElement() {
        super();
    }

    @Override
    public boolean setState(int state) {
        if (state < N_STATES_SENSORS) {
            this.state = state;
            lastUpdateTime = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void doDraw(Graphics2D g) {
        g.setStroke(STROKE_DASH);
        // TODO make sensor appearance dependend on US or EU style
        if (x2 != INVALID_INT) {  // draw dashed line as sensor
            // set color according to state
            switch (state) {
                case STATE_FREE:
                    g.setColor(cFree);
                    break;
                case STATE_OCCUPIED:
                    g.setColor(cOccupied);
                    break;
                case STATE_INROUTE:
                    g.setColor(cInRoute);
                    break;
                case STATE_UNKNOWN:
                    g.setColor(cUnknown);
                    break;
            }
            g.drawLine(x, y, x2, y2);
        } else {
            // draw lamp type of sensor

            // read data from SX bus and set bitmap accordingly
            /* TODO	int h, w;
			Bitmap bm;
			StringBuilder bmName = new StringBuilder(type);
			// TODO proper handling of "unknown"
			if ( (state == STATE_FREE)  || (state == STATE_UNKNOWN)){
				bmName.append("_off");
			} else {
				bmName.append("_on");
			}

			bm = bitmaps.get(bmName.toString());
			if (bm == null) {
                Log.e(TAG,
                        "error, bitmap not found with name="
                                + bmName.toString());  
            } else {
                h = bm.getHeight() / 2;
                w = bm.getWidth() / 2;
                canvas.drawBitmap(bm, x * prescale - w, y * prescale - h, null); // center
                // bitmap
            }  */
        }
        if (drawAddresses2) {
            doDrawAddresses(g);
        }
    }

}

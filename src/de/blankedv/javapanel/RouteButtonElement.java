package de.blankedv.javapanel;

import static de.blankedv.javapanel.Defines.*;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * button for selecting routes
 *
 * this buttons are local to the device and the state is NOT sent via LANBAHN
 * messages
 *
 */
public class RouteButtonElement extends ActivePanelElement {

    // display a route button 
    long blink = System.currentTimeMillis();
    boolean toggleBlink = false;
    private long timeSet;
    public static boolean clearRouteButtonActive = false;
    
    // dimensions for button circle
    private final int RAD = 4;  // radius of route button
    private final int WID = 2 * RAD;

    public RouteButtonElement(String type, int x, int y, String name, int adr) {
        super(type, x, y, name, adr);
    }

    public RouteButtonElement() {
        adr = INVALID_INT;
        state = STATE_UNKNOWN;
    }

    public static RouteButtonElement findRouteButtonByAddress(int address) {
        for (PanelElement pe : panelElements) {
            if (pe instanceof RouteButtonElement) {
                if (pe.getAdr() == address) {
                    return (RouteButtonElement) pe;
                }
            }
        }

        return null;

    }

    public static boolean checkForRoute(int adrSecondBtn) {

        if (DEBUG) {
            System.out.println(TAG + " checkForRoute called, adrSecondBtn=" + adrSecondBtn);
        }
        // check if a route needs to be cleared first
        if (clearRouteButtonActive) {
            if (DEBUG) {
                System.out.println(TAG + " clearRouteButtonActive:true");
            }
            // find route with adrSecondBtn and clear it
            for (Route rt : routes) {
                if (DEBUG) {
                    System.out.println(TAG + " checking route id=" + rt.id);
                }
                if (rt.isActive()
                        && ((rt.btn1 == adrSecondBtn) || (rt.btn2 == adrSecondBtn))) {
                    if (DEBUG) {
                        System.out.println(TAG + " found route matching to btn. clearing route=" + rt.id);
                    }
                    // we found a route with this button, new clear it
                    // now set 
                    rt.clear();
                } else if (DEBUG) {
                    System.out.println(TAG + " route not active route for btn=" + adrSecondBtn);
                }
            }
            clearRouteButtonActive = false;
            findRouteButtonByAddress(adrSecondBtn).reset();  // clear the button also
            return true;
        }

        int nPressed = 0;
        int adrFirstBtn = 0;

        if (DEBUG) {
            System.out.println(TAG + " checking, if a route can be activated");
        }
        // now check if a route can be activated
        for (PanelElement pe : panelElements) {
            if (pe instanceof RouteButtonElement) {
                if (((RouteButtonElement) pe).isPressed()) {
                    nPressed++;
                    if (pe.getAdr() != adrSecondBtn) {
                        // if this is not the "checking" button, then it must be the first button
                        adrFirstBtn = pe.getAdr();
                    }
                }
            }
        }
        if (DEBUG) {
            System.out.println(TAG + " btns pressed total=" + nPressed);
        }
        if (nPressed == 2) {
            // this could be a route, 2 buttons are active
            // iterate over all possible routes 
            // we must know which button was pressed first!!
            if (DEBUG) {
                System.out.println(TAG + " checking for a route from btn-" + adrFirstBtn + " turnout btn-" + adrSecondBtn);
            }
            boolean routeFound = false;
            for (Route rt : routes) {
                if (DEBUG) {
                    System.out.println(TAG + " checking route id=" + rt.id);
                }
                if ((rt.btn1 == adrFirstBtn) && (rt.btn2 == adrSecondBtn)) {
                    // we found a route connecting these buttons,
                    // now set 
                    routeFound = true;
                    if (DEBUG) {
                        System.out.println(TAG + " found the route with id=" + rt.id);
                    }
                    // reset buttons
                    findRouteButtonByAddress(adrFirstBtn).reset();
                    findRouteButtonByAddress(adrSecondBtn).reset();

                    // set the route (i.e. sensors and turnouts)
                    rt.set();
                    break;  // no need to search further
                }
            }
            for (CompRoute cr : compRoutes) {
                if (DEBUG) {
                    System.out.println(TAG + " checking composite route id=" + cr.id);
                }
                if ((cr.btn1 == adrFirstBtn) && (cr.btn2 == adrSecondBtn)) {
                    // we found a route connecting these buttons,
                    // now set 
                    routeFound = true;
                    if (DEBUG) {
                        System.out.println(TAG + " found the composite route with id=" + cr.id);
                    }
                    // reset buttons
                    findRouteButtonByAddress(adrFirstBtn).reset();
                    findRouteButtonByAddress(adrSecondBtn).reset();

                    // set the route (i.e. sensors and turnouts)
                    cr.set();
                    break;  // no need to search further
                }
            }
            if (!routeFound) {
                //ControlArea.dispErrorMsg("keine passende Fahrstrasse.");
                System.out.println(TAG + "Error keine passende Fahrstrasse.");
                findRouteButtonByAddress(adrFirstBtn).reset();  // clear the button also
                findRouteButtonByAddress(adrSecondBtn).reset();  // clear the button also
            }

        } else if (nPressed > 2) {
            if (DEBUG) {
                System.out.println(TAG + " too many routeButtons pressed, clearing all");
            }
            // makes no sense, deselect all
            for (PanelElement pe : panelElements) {
                if (pe instanceof RouteButtonElement) {
                    ((RouteButtonElement) pe).reset();
                }
            }
            //ControlArea.dispErrorMsg("zu viele Buttons.");
        }

        return true;
    }

    /**
     * draw a route button
     *
     * @param g
     */
    @Override
    public void doDraw(Graphics2D g) {
        // do not draw rout buttons when "routes" not activated.
        if (!routesEnabled) {
            return;
        }

        g.setColor(Color.DARK_GRAY); //(Color.WHITE);
        g.fillOval(x - (RAD+1), y - (RAD+1), (WID+2), (WID+2));

        if ((enableEdit) || (adr == INVALID_INT)) {
            g.setColor(Color.LIGHT_GRAY);
            g.fillOval(x - RAD, y - RAD, WID, WID);
        } else if (state == STATE_PRESSED) {
            if ((System.currentTimeMillis() - blink) > 500) {
                toggleBlink = !toggleBlink;
                blink = System.currentTimeMillis();
            }
            if (toggleBlink) {
                g.setColor(Color.DARK_GRAY);
                g.fillOval(x - (RAD-1), y - (RAD-1), (WID-2), (WID-2));
            } else {
                g.setColor(Color.WHITE);
                g.fillOval(x - (RAD), y - (RAD), (WID), (WID));
            }
        } else if (state == STATE_NOT_PRESSED) {
            g.setColor(Color.WHITE);
            g.fillOval(x - RAD, y - RAD, WID, WID);
        } else if (state == STATE_UNKNOWN) {
            g.setColor(Color.WHITE);
            g.fillOval(x - RAD, y - RAD, WID, WID);
        }

        if (drawAddresses2) {
            doDrawAddresses(g);
        }
    }

    @Override
    public void toggle() {
        if (!routesEnabled) {
            return; // do not enable route keys if not routes are enabled
        }
        if (adr == INVALID_INT) {
            return; // do nothing if no address defined.
        }
        if ((System.currentTimeMillis() - lastToggle) < 500) {
            System.out.println(TAG + " - last toggle less than 500ms ago");
            return;  // do not toggle twice within 250msecs
        }

        lastToggle = System.currentTimeMillis();  // reset toggle timer

        if ((state == STATE_NOT_PRESSED) | (state == STATE_UNKNOWN)) {
            state = STATE_PRESSED;
            timeSet = System.currentTimeMillis();
            checkForRoute(adr);

        } else {
            state = STATE_NOT_PRESSED;
        }

        // state = STATE_UNKNOWN; // until updated via lanbahn message
        // sendQ.add("SET "+adr+" "+state);  // ==> send changed data over network
        if (DEBUG) {
            System.out.println(TAG + " toggle(adr=" + adr + ") new state=" + state + " time=" + lastToggle);
        }
    }

    /**
     * checks if button is being selected with a touch at point (xs, ys)
     */
    @Override
    public boolean isSelected(int xs, int ys) {
        // for route button check radius = RASTER/3 around center	!! slightly larger than for turnout/signal	
        int minx = x - RASTER / 3;
        int maxx = x + RASTER / 3;
        int miny = y - RASTER / 3;
        int maxy = y + RASTER / 3;

        // the touchpoint should be within rectangle of panel element
        if ((xs >= minx) && (xs <= maxx) && (ys >= miny) && (ys <= maxy)) {
            if (DEBUG) {
                if (DEBUG) {
                    System.out.println(TAG + " selected adr=" + adr + " " + type + "  (" + x + "," + y + ")");
                }
            }
            return true;
        } else {
            // if (DEBUG) Log.d(TAG, "No Route key selection");
            return false;
        }
    }

    /**
     *
     * @return true if the button is currently pressed, else false
     */
    public boolean isPressed() {
        if (state == STATE_PRESSED) {
            return true;
        } else {
            return false;
        }
    }

    public void reset() {
        state = STATE_NOT_PRESSED;
    }

    public static void autoReset() {
        for (PanelElement pe : panelElements) {
            if (pe instanceof RouteButtonElement) {
                if ((((RouteButtonElement) pe).state == STATE_PRESSED)
                        && ((System.currentTimeMillis() - ((RouteButtonElement) pe).timeSet) > 20 * 1000L)) {
                    ((RouteButtonElement) pe).state = STATE_NOT_PRESSED;
                }
            }
        }

    }
}

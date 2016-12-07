package de.blankedv.javapanel;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import static de.blankedv.javapanel.Defines.*;


/**
 * WriteConfig - Utility turnout save Panel Config
 *
 * @author Michael Blank
 *
 * @version 1.0
 */
public class WriteConfig {

    static StringWriter writer;
    
    /**
     * writeConfigToXML
     *
     * saves all PanelElements (including deducted elements) to an XML file
     * configFilename will have ".(date)" appended
     *
     * @param
     * @return true, if succeeds - false, if not.
     */
    public static boolean writeToXML() {

        FileWriter fWriter = null;
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_hhmmss");
            String suffix = df.format(new Date());
            String configFilename = prefs.get("configfilename", "javapanel-config.xml");
            // String fname = Paths.get(configFilename).getFileName().toString();
            fWriter = new FileWriter(configFilename + "." + suffix);
            fWriter.write(writeXml());
            fWriter.flush();
            fWriter.close();

            if (DEBUG) {
                System.out.println(TAG + " Config File " +configFilename + "." + suffix + " saved! ");
            }
            configHasChanged = false; // reset flag

        } catch (Exception e) {
            System.out.println(TAG + " ERROR: " + e.getMessage());
            return false;
        } finally {
            if (fWriter != null) {
                try {
                    fWriter.close();
                } catch (IOException e) {
                    System.out.println(TAG + " ERROR: could not close output file!");
                }
            }
        }

        return true;
    }

    
    private static void writeStart(String tagname) {
       writer.write("<"+tagname);
    }
    
    private static void writeClose() {
       writer.write(" />\n");
    }
    
    private static void writeCloseTag(String tagname) {
       writer.write("</"+tagname+">\n");
    }
     
    private static void writeAttribute(String name, int val) {
       writer.write(" "+name+"=\""+val+"\"");
    }
    
    private static void writeAttribute(String name, String sval) {
       writer.write(" "+name+"=\""+sval+"\"");
    }
     
    /**
     * writeConfigToXML
     *
     * saves all PanelElements (including deducted elements) to an XML file
     * (= simple XMLSerializer for lanbahn panel elements and routes)
     *
     * @param
     * @return true, if succeeds - false, if not.
     */
    private static String writeXml() {
        
            writer = new StringWriter();

            writer.write("<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n");
            writer.write("<layout-config>\n");
            writer.write("<panel name=\""+panelName+"\">\n");


            // now write all panel elements to the file
            for (PanelElement pe : panelElements) {
                if (DEBUG) {
                  //  System.out.println(TAG + " writing panel element " + pe.toString());
                }
                writeStart(pe.getType());                
                if (pe.name.length() > 0) {
                    writeAttribute("name=",pe.name);
                }
                writeAttribute("x", pe.x);
                writeAttribute("y", pe.y);
                if (pe.x2 != INVALID_INT) { // save only valid attributes
                    writeAttribute("x2", pe.x2);
                    writeAttribute("y2", pe.y2);
                }
                if (pe.xt != INVALID_INT) {
                    writeAttribute("xt", pe.xt);
                    writeAttribute("yt", pe.yt);
                }
                if (pe.getAdr() != INVALID_INT) {
                    writeAttribute("adr", pe.getAdr());
                }
                writeClose();
            }

            // write the routes
            for (Route rt : routes) {
                
                writeStart("route");

                writeAttribute("id",rt.id);
                writeAttribute("btn1",rt.btn1);
                writeAttribute("btn2",rt.btn2);
                writeAttribute("route",rt.routeString);
                writeAttribute("sensors",rt.sensorsString);
                writeAttribute("offending",rt.offendingString);

                writeClose();
            }

            // write the composite routes
            for (CompRoute rt : compRoutes) {

                writeStart("comproute");

                writeAttribute("id", rt.id);
                writeAttribute("btn1", rt.btn1);
                writeAttribute("btn2", rt.btn2);
                writeAttribute("routes", rt.routesString);

                writeClose();
            }

            writer.write("</panel>\n");  
            writer.write("</layout-config>\n");
           

                    
            return writer.toString();
       
    }


   
}

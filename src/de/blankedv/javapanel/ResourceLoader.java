/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.blankedv.javapanel;

import java.awt.Image;
import java.awt.Toolkit;

/**
 *
 * @author mblank
 */
public class ResourceLoader {
    static ResourceLoader rl = new ResourceLoader();
    
    public static Image getImage (String fname) {
        Image i;
        
        return Toolkit.getDefaultToolkit().getImage(fname);
    }
}

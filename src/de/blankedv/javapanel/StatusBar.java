/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.blankedv.javapanel;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;

public class StatusBar extends JLabel {

    /** Creates a new instance of StatusBar */
    public StatusBar() {
        super();
        super.setPreferredSize(new Dimension(100, 16));
        Font f = new Font("Ubuntu", Font.PLAIN, 12);
        this.setFont(f);
        setMessage("Ready");
    }

    public void setMessage(String message) {
        setText(" "+message);        
    }        
}
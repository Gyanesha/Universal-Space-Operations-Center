/*
 * The MIT License
 *
 * Copyright 2017 KSat e.V.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ksatstuttgart.usoc.gui;

import com.ksatstuttgart.usoc.controller.DataController;
import com.ksatstuttgart.usoc.data.MailEvent;
import com.ksatstuttgart.usoc.data.SerialEvent;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

      
/**
* <h1>MainFrame</h1>
* This class acts as the main component for the ground station GUI. It allows
* to display all relevant information.
*
* @author  Valentin Starlinger
* @version 1.0
*/
public class MainFrame extends JFrame {

    GridBagConstraints gbc;

    IridiumPanel ip;
    CurrentDataPanel dp;
    SerialPanel sp;

    public MainFrame() {
        super();
        initialize();
    }

    private void initialize(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //MenuBar
        //Panels
        JPanel mainPanel = new JPanel();

        dp = new CurrentDataPanel();//true,1600,900);
        dp.setMinimumSize(new java.awt.Dimension(500,dp.getHeight()));
        ip = new IridiumPanel();
        sp = new SerialPanel();
        
        JTabbedPane bottomPanel = new JTabbedPane();
        bottomPanel.addTab("Iridium", ip);
        bottomPanel.addTab("Serial", sp);
        
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(dp, BorderLayout.WEST);
        mainPanel.add(bottomPanel, BorderLayout.CENTER);

        this.getContentPane().add(mainPanel);
        this.setResizable(true);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
    public void updateData(DataController dc){
        dp.updateData(dc);
    }

    public void updateIridiumLog(MailEvent e) {
        ip.updateData(e);
    }

    public void updateSerialLog(SerialEvent e) {
        sp.updateData(e);
    }

    public void updateIridiumError(String msg) {
        ip.updateError(msg);
    }

    public void updateSerialError(String msg) {
        sp.updateError(msg);
    }

}
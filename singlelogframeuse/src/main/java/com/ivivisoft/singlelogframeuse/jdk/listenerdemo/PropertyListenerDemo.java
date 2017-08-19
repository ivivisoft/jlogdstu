/*
 *  Copyright (c) 2016, 张威, ivivisoft@gmail.com
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ivivisoft.singlelogframeuse.jdk.listenerdemo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JFrame;


public class PropertyListenerDemo {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Button Sample");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JButton button1 = new JButton("Press me");
        final JButton button2 = new JButton("Press me");
        ActionListener actionListener = actionEvent -> {
            JButton jButton = (JButton) actionEvent.getSource();
            int r = (int) (Math.random() * 100);
            int g = (int) (Math.random() * 100);
            int b = (int) (Math.random() * 100);
            jButton.setText("hello");
        };
        PropertyChangeListener propChangeListn = event -> {
            String property = event.getPropertyName();
            if ("text".equals(property)) {
                button2.setText((String) event.getNewValue());
            }
        };

        button1.addActionListener(actionListener);
        button1.addPropertyChangeListener(propChangeListn);
//        button2.addActionListener(actionListener);
        Container cPane = frame.getContentPane();
        cPane.add(button1, BorderLayout.NORTH);
        cPane.add(button2, BorderLayout.SOUTH);
        frame.setSize(500, 300);
        frame.setVisible(true);
    }

}

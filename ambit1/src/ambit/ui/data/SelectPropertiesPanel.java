/* SelectPropertiesPanel.java
 * Author: Nina Jeliazkova
 * Date: Nov 11, 2006 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit.ui.data;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;

import ambit.data.molecule.PropertyTranslator;

public class SelectPropertiesPanel extends JPanel {
    protected PropertyTranslator properties;
    protected SelectionPanel[] panels;
    public SelectPropertiesPanel(PropertyTranslator properties) {
        this(properties,properties.getTypes());
    }
    public SelectPropertiesPanel(PropertyTranslator properties, String[] types) {
        this(properties,types,null);
    }
    
    public SelectPropertiesPanel(PropertyTranslator properties, String[] types,ArrayList<String> selected) {        
        super(new BorderLayout());
        this.properties = properties;
        add(createPanes(types,selected),BorderLayout.CENTER);
        setPreferredSize(new Dimension(500,400));
    }
    protected JComponent createPanes(String[] titles,ArrayList<String> selected) {
        if (titles == null) return null;
            
        JTabbedPane p = new JTabbedPane();
        panels = new SelectionPanel[titles.length];
        for (int i=0; i < titles.length;i++) {
            panels[i] = new SelectionPanel(properties,titles[i],selected);
           p.addTab(titles[i],panels[i]);
        }   
        p.setPreferredSize(new Dimension(300,300));
        return p;
    }   
    public ArrayList<String> getFields() {
        ArrayList<String> list = new ArrayList<String>();
        for (int i=0; i < panels.length;i++) 
            panels[i].getFields(list);
        return list;
    }
}

class SelectionPanel extends JPanel {
    protected  HashtableModel model;
    public SelectionPanel(PropertyTranslator properties,String type) {
        this(properties,type,null);
    }
    public SelectionPanel(PropertyTranslator properties,String type,ArrayList<String> selected) {
        super(new BorderLayout());
        model = new HashtableModel(properties.getProperties(type),false);
        add(new JScrollPane(new JTable(model)),BorderLayout.CENTER);
        
        JButton bSelect = new JButton(new AbstractAction("Select all") {
           public void actionPerformed(ActionEvent arg0) {
                select(true);
            } 
        });
        JButton bUnselect = new JButton(new AbstractAction("Unselect all") {
            public void actionPerformed(ActionEvent arg0) {
                 select(false);
             } 
         });
        JButton bInvert = new JButton(new AbstractAction("Invert selection") {
            public void actionPerformed(ActionEvent arg0) {
                 invert();
             } 
         });            
        JToolBar bar = new JToolBar();
        bar.add(bSelect);
        bar.add(bUnselect);
        bar.add(bInvert);
        add(bar,BorderLayout.NORTH);
        select(selected);
    }
    public void invert() {
        
        for (int i= 0; i < model.getRowCount();i++)
            model.setValueAt(!(Boolean)(model.getValueAt(i,0)),i,0);
        model.fireTableDataChanged();
    }    
    public void select(boolean enable) {
        Boolean b = new Boolean(enable);
        for (int i= 0; i < model.getRowCount();i++)
            model.setValueAt(b,i,0);
        model.fireTableDataChanged();
    }
    public void select(ArrayList<String> selected) {
        for (int i= 0; i < model.getRowCount();i++)
            if (selected.indexOf(model.getValueAt(i,1).toString()) > -1)
                model.setValueAt(true,i,0);
            else
                model.setValueAt(false,i,0);
        model.fireTableDataChanged();
    }    
    public void getFields(ArrayList<String> list) {
        for (int i= 0; i < model.getRowCount();i++)
            if ((Boolean)(model.getValueAt(i,0)))
                list.add(model.getValueAt(i,1).toString());
    }
}

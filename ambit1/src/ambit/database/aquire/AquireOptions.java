/* AquireOptions.java
 * Author: Nina Jeliazkova
 * Date: Mar 20, 2007 
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

package ambit.database.aquire;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SpringLayout;

import ambit.database.ConnectionPool;
import ambit.database.data.AmbitDatabaseToolsData;
import ambit.ui.SpringUtilities;

public class AquireOptions extends JPanel {
    protected JComboBox endpointBox;
    protected JComboBox speciesBox;
    protected boolean useSimpletemplate = true;
    protected AmbitDatabaseToolsData dbaData;
    
    /*
     * dbaData.getAquire_endpoint_description(),
            dbaData.getAquire_species(),
            dbaData.isAquire_simpletemplate(),
     */
    public AquireOptions(String endpoint, AmbitDatabaseToolsData dbaData) {
        super();
        this.dbaData = dbaData;
        this.useSimpletemplate = dbaData.isAquire_simpletemplate();
        SpringLayout sl = new SpringLayout();
        setLayout(sl);
        Dimension d = new Dimension(Integer.MAX_VALUE,24);
        endpointBox = new JComboBox();
        endpointBox.setMaximumSize(d);
        
        endpointBox.setPreferredSize(d);
        speciesBox = new JComboBox();
        speciesBox.setMaximumSize(d);
        speciesBox.setPreferredSize(d);
        retrieveEndpoints();
        endpointBox.setSelectedItem(dbaData.getAquire_endpoint_description());
        speciesBox.setSelectedItem(dbaData.getAquire_species());        
        
        
        add(new JLabel("Endpoint:"));
        add(endpointBox);
        add(new JLabel("Species (latin name):"));
        add(speciesBox);
        
		ButtonGroup g = new ButtonGroup();
		JRadioButton boxSimple = new JRadioButton("Retrieve main fields only");		
		JRadioButton boxAll = new JRadioButton("Retrieve all available data per measurement");
		boxSimple.setSelected(useSimpletemplate);
        boxAll.setSelected(!useSimpletemplate);
		
		boxSimple.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Object source = e.getItemSelectable();
				JRadioButton box = (JRadioButton)source;
				setUseSimpletemplate(box.isSelected()); 
			}
		});
		boxAll.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Object source = e.getItemSelectable();
				JRadioButton box = (JRadioButton)source;
				setUseSimpletemplate(!box.isSelected()); 
			}
		});
		g.add(boxAll);
		g.add(boxSimple);
		
		add(boxSimple);
		add(boxAll);
        SpringUtilities.makeCompactGrid(this, 3,2,5,5,2,2);
    }
    public String getEndpoint() {
        Object o = endpointBox.getSelectedItem();
        if (o != null) 
       		if ("ALL".equals(o)) return null;
       		else {
       	        String e = o.toString();
       	        int p = e.indexOf(" (");
       	        
       	        return e.substring(0,p);

       		}
        else return null;
        
    }
    public String getEndpointDescription() {
        Object o = endpointBox.getSelectedItem();
        if (o ==null) return null;
        else return o.toString();
    }
    
    public String getSpecies() {
        Object o = speciesBox.getSelectedItem();
        if (o != null) 
       		if ("ALL".equals(o)) return null;
       		else return o.toString();
        /*{
       	        String e = o.toString();
       	        int p = e.indexOf(" (");
       	        
       	        return e.substring(p+2,e.length()-1);

       		}
       		*/
        else return null;
        
    }    
    protected void retrieveEndpoints() {
        try {
            ConnectionPool aquire_pool = new ConnectionPool(
            		dbaData.getHost(),dbaData.getPort(),"aquire",dbaData.getUser().getName(),dbaData.getUser().getPassword(),1,1);
            Connection aquire_connection = aquire_pool.getConnection();
            Statement stmt = aquire_connection.createStatement();
            ResultSet rs = stmt.executeQuery("select code, definition from endpoints order by code");
            endpointBox.setSelectedIndex(-1);
            endpointBox.removeAllItems();
            endpointBox.addItem("ALL");
            while (rs.next()) {
                Object o = rs.getString(1);
                if ("".equals(o)) continue;
                if ("/".equals(o)) continue;
                if ("Code".equals(o)) continue;
                endpointBox.addItem(rs.getString(1) +  " (" + rs.getString(2) + ")");                  
            }
            rs.close();
            
            rs = stmt.executeQuery("select SpeciesID, LatinName from species_data order by LatinName");
            
            speciesBox.setSelectedIndex(-1);
            speciesBox.removeAllItems();
            speciesBox.addItem("ALL");
            while (rs.next()) {
                speciesBox.addItem(rs.getString(2)); // +  " (" + rs.getString(1) + ")");         
                 
            }
            rs.close();
            
            stmt.close();
            aquire_connection.close();
            aquire_pool.returnConnection(aquire_connection);
            aquire_pool = null;
        } catch (Exception x) {
        	System.err.println(x.getMessage());
            //x.printStackTrace();
        }

    }
	public boolean isUseSimpletemplate() {
		return useSimpletemplate;
	}
	public void setUseSimpletemplate(boolean useSimpletemplate) {
		this.useSimpletemplate = useSimpletemplate;
	}
}
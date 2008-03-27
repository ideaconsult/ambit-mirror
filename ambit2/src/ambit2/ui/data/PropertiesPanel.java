/*
Copyright (C) 2005-2006  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.ui.data;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import org.openscience.cdk.CDKConstants;

import ambit2.config.AmbitCONSTANTS;
import ambit2.data.molecule.PropertyTranslator;



public class PropertiesPanel extends JPanel {
    protected PropertyTranslator properties;
	/**
	 * 
	 */
	private static final long serialVersionUID = -412257033711963996L;

	public PropertiesPanel(PropertyTranslator properties) {
		this(properties,properties.getTypes(),null);
	}
	/*
	public PropertiesPanel(PropertyTranslator properties, String[] types , Hashtable<String,String[]> options) {
		super(new BorderLayout());
	    this.properties = properties;
		
        if (options ==null) {
            options = new Hashtable<String,String[]>();
            options.put(PropertyTranslator.type_identifiers, getOptions());
        }
		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                createLeftPane(),createTranslatedPane(types,options));
		pane.setDividerLocation(200);
		add(pane,BorderLayout.CENTER);
		pane.setPreferredSize(new Dimension(500,400));
	}
	*/
	protected static Hashtable<String,String[]> dictionaryToOptions(PropertyTranslator dictionary) {
        Hashtable<String,String[]> t = new Hashtable<String,String[]>(); 
        t.put(PropertyTranslator.type_identifiers,new String[] {CDKConstants.CASRN,CDKConstants.NAMES,AmbitCONSTANTS.SMILES,AmbitCONSTANTS.INCHI,""});
        
        if (dictionary != null) {
            Hashtable h = dictionary.getDescriptorProperties();
            String[] option = new String[h.values().size()];
            Iterator values = h.values().iterator();
            int i=0;
            while (values.hasNext()) {
                option[i] = values.next().toString();
                i++;
            }
            t.put(PropertyTranslator.type_descriptors, option);
        }
        return t;
    }
	public PropertiesPanel(PropertyTranslator properties, String[] types , PropertyTranslator dictionary) {
		super(new BorderLayout());
	    this.properties = properties;
	    properties.translate(dictionary);
	    Hashtable<String,String[]>  options;
        if (dictionary ==null) {
            options = new Hashtable<String,String[]>();
            options.put(PropertyTranslator.type_identifiers, getOptions());
        } else options = dictionaryToOptions(dictionary);
		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                createLeftPane(),createTranslatedPane(types,options));
		pane.setDividerLocation(200);
		add(pane,BorderLayout.CENTER);
		pane.setPreferredSize(new Dimension(500,400));
	}
	protected JPanel createLeftPane() {
		return new HashtablePanel(properties,null,"Available properties",null);
	}
	public String[] getOptions() {
		return new String[] {CDKConstants.CASRN,CDKConstants.NAMES,AmbitCONSTANTS.SMILES,AmbitCONSTANTS.INCHI,""};
	}
	protected JComponent createTranslatedPane(String[] titles,Hashtable<String,String[]> options) {
		if (titles == null) return null;
            
		JTabbedPane p = new JTabbedPane();
		for (int i=0; i < titles.length;i++)
            p.addTab(titles[i],new HashtablePanel(properties,titles[i],titles[i],options.get(titles[i])));

		p.setPreferredSize(new Dimension(300,300));
		return p;
	}	

}




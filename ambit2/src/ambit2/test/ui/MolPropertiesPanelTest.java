/* MolPropertiesPanelTest.java
 * Author: Nina Jeliazkova
 * Date: 2006-6-2 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
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

package ambit2.test.ui;

import javax.swing.JOptionPane;

import junit.framework.TestCase;

import org.openscience.cdk.CDKConstants;

import ambit2.ui.data.MolPropertiesPanel;
import ambit2.ui.data.PropertiesPanel;
import ambit2.ui.data.QSARMolPropertiesPanel;
import ambit2.data.molecule.MolProperties;
import ambit2.data.molecule.MolPropertiesFactory;
import ambit2.data.molecule.PropertyTranslator;

/**
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-6-2
 */
public class MolPropertiesPanelTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(MolPropertiesPanelTest.class);
    }
    public void test() {
        /*
        Hashtable t = new Hashtable();
        t.put("SMILES","c1ccccc1");
        t.put("CAS","50-00-0");
        t.put("LogP","1.4");
        t.put("LC50","3");
        t.put("Predicted","3.1");
        t.put("Observed","3.11");
        t.put("Equation","a+b=c");
        */
        try {
        MolProperties props = MolPropertiesFactory.createDebnathModelProperties();
        MolPropertiesPanel p = new MolPropertiesPanel(props);
        JOptionPane.showMessageDialog(null,p,"",JOptionPane.PLAIN_MESSAGE);
        
        JOptionPane.showMessageDialog(null,new PropertiesPanel(props),"",JOptionPane.PLAIN_MESSAGE);
        
        p = new QSARMolPropertiesPanel(props);
        JOptionPane.showMessageDialog(null,p,"",JOptionPane.PLAIN_MESSAGE);        

        JOptionPane.showMessageDialog(null,props.getQsarModel().editor().getJComponent(),"",JOptionPane.PLAIN_MESSAGE);
        } catch (Exception x) {
            x.printStackTrace();
            fail();
        }
    }
    
    public void testPropertyTranslator() {
        PropertyTranslator p = new PropertyTranslator();
        p.addProperty(CDKConstants.CASRN, "50-00-0");
        p.addProperty("descriptors","XLogP", "LogP");
        p.addProperty("identifiers","Chemical name", CDKConstants.NAMES);
        
        PropertyTranslator p1 = new PropertyTranslator();
        p1.addProperty(CDKConstants.CASRN, "11-11-1");
        p1.addProperty("descriptors","CLogP", "LogP");
        p1.addProperty("identifiers","InChi", "InCHi");
        
        p.add(p1);
        
        assertEquals("11-11-1",p.getProperties().get(CDKConstants.CASRN));
        assertEquals("LogP",p.getProperty("descriptors", "CLogP"));
        
    }
}

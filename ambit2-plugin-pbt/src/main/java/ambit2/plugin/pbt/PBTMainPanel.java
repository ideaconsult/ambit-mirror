/* PBTMainPanel.java
 * Author: Nina Jeliazkova
 * Date: Oct 5, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
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

package ambit2.plugin.pbt;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.io.InputStream;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nplugins.shell.INPluginUI;
import nplugins.shell.INanoPlugin;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.microworkflow.ui.WorkflowContextListenerPanel;

public class PBTMainPanel extends WorkflowContextListenerPanel implements INPluginUI<INanoPlugin>  {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2943141896545107613L;
	protected JTabbedPane tabbedPane;
    protected PBTTableModel[] models;
    protected static String[] defs = {
    	"ambit2/plugin/pbt/xml/substance_page.xml",
    	"ambit2/plugin/pbt/xml/p_page.xml",
    	"ambit2/plugin/pbt/xml/b_page.xml",
    	"ambit2/plugin/pbt/xml/t_page.xml",
    	"ambit2/plugin/pbt/xml/result_page.xml"
    };
    public PBTMainPanel() {
        tabbedPane = new JTabbedPane();
        add(tabbedPane);
        /*

        */

        try {
			String file = "2008-12-03_REACH PBT Screening Tool_V0.99b_U N P R O T E C T E D.xls";
			InputStream in = PBTMainPanel.class.getClassLoader().getResourceAsStream("ambit2/plugin/pbt/xml/"+file);
			POIFSFileSystem poifsFileSystem = new POIFSFileSystem(in);
			
			HSSFWorkbook workbook = new HSSFWorkbook(poifsFileSystem);
			
			final PBTWorksheet[] pbt_worksheets = new PBTWorksheet[6];
			pbt_worksheets[0] = new PBTWorksheet(workbook,"TERMS & CONDITIONS",27,6);
			pbt_worksheets[1]  = new PBTWorksheet(workbook,"SUBSTANCE",28,8);
			pbt_worksheets[2]  = new PBTWorksheet(workbook,"P-Sheet",20,6);
			pbt_worksheets[3] = new PBTWorksheet(workbook,"B-Sheet",22,6);
			pbt_worksheets[4]  = new PBTWorksheet(workbook,"T-Sheet",19,6);
			pbt_worksheets[5] = new PBTWorksheet(workbook,"Result",15,5);
			
			for (int i=0; i < pbt_worksheets.length;i++) 
				tabbedPane.add(pbt_worksheets[i].getWorkbook().getSheetName(i),
						new JScrollPane(PBTPageBuilder.buildPanel(pbt_worksheets[i],1,1)));	
	        tabbedPane.addChangeListener(new ChangeListener() {
	        	public void stateChanged(ChangeEvent e) {
	           		pbt_worksheets[((JTabbedPane)e.getSource()).getSelectedIndex()].notifyCells(-1,-1);
	        	}
	        	
	        });
        } catch (Exception x) {
            models = new PBTTableModel[defs.length];
            for (int i=0; i < defs.length;i++) {
    	        try {
    	        	models[i] = new PBTTableModel();
    	        	models[i].setDefinition(defs[i]);
    		        tabbedPane.add(models[i].getValueAt(2,1).toString(),
    		        		new JScrollPane(PBTPageBuilder.buildPanel(models[i],-2,0)));	        	
    	        } catch (Exception xx) {
    	        	x.printStackTrace();
    	        	tabbedPane.add("Error",new JLabel(xx.getMessage()));
    	        }

            }
        }
    }
    @Override
    protected void animate(PropertyChangeEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub

    }

    public Component getComponent() {
        return this;
    }

    public INanoPlugin getPlugin() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setPlugin(INanoPlugin plugin) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public String toString() {
    	return "PBT SCREENING TOOL FOR REACH";
    }
}

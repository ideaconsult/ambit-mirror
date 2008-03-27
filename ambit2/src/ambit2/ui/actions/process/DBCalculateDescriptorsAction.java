/* DBCalculateDescriptorsAction.java
 * Author: Nina Jeliazkova
 * Date: 2006-5-14 
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

package ambit2.ui.actions.process;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.modeling.builder3d.TemplateHandler3D;

import ambit2.ui.editors.DescriptorsHashtableEditor;
import ambit2.data.descriptors.DescriptorFactory;
import ambit2.data.descriptors.DescriptorsHashtable;
import ambit2.database.data.AmbitDatabaseToolsData;
import ambit2.database.data.ISharedDbData;
import ambit2.database.processors.ReadStructureProcessor;
import ambit2.database.readers.DbStructureMissingDescriptorsReader;
import ambit2.database.writers.CDKDescriptorWriter;
import ambit2.exceptions.AmbitException;
import ambit2.io.batch.DefaultBatchStatistics;
import ambit2.io.batch.IBatchStatistics;
import ambit2.processors.IAmbitProcessor;
import ambit2.processors.ProcessorsChain;
import ambit2.processors.descriptors.CalculateDescriptors;
import ambit2.ui.UITools;
import ambit2.ui.actions.BatchAction;

/**
 * Reads structures without specified descriptors from the database, calculates descriptors and writes back to the database.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-5-14
 */
public class DBCalculateDescriptorsAction extends BatchAction {
    protected DescriptorsHashtable descriptors;
    /**
     * @param userData
     * @param mainFrame
     */
    public DBCalculateDescriptorsAction(Object userData, JFrame mainFrame) {
        this(userData, mainFrame,"Calculate descriptors");
    }

    /**
     * @param userData
     * @param mainFrame
     * @param name
     */
    public DBCalculateDescriptorsAction(Object userData, JFrame mainFrame,
            String name) {
        this(userData, mainFrame, name,UITools.createImageIcon("ambit2/ui/images/chart_bar.png"));
    }

    /**
     * @param userData
     * @param mainFrame
     * @param name
     * @param icon
     */
    public DBCalculateDescriptorsAction(Object userData, JFrame mainFrame,
            String name, Icon icon) {
        super(userData, mainFrame, name, icon);
        descriptors =  DescriptorFactory.createDescriptorsList(); 
        putValue(AbstractAction.SHORT_DESCRIPTION,"Calculated selected descriptors for compounds in the database.");
    }

    /* (non-Javadoc)
     * @see ambit2.ui.actions.BatchAction#getReader()
     */
    public IIteratingChemObjectReader getReader() {
    	if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = ((ISharedDbData) userData);
		    DescriptorsHashtableEditor editor = new DescriptorsHashtableEditor(descriptors,true);
		    try {
			    if (editor.view(mainFrame, true, "")) {
			    	descriptors = editor.getLookup();
			    	return new DbStructureMissingDescriptorsReader(dbaData.getDbConnection(),descriptors,	
			    			((AmbitDatabaseToolsData)userData).getSrcDataset(),
			    			0,10000000);
			    }	
			    
		    } catch (AmbitException x) {
		    	JOptionPane.showMessageDialog(mainFrame,x.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		    	return null;
		    } finally {
		    	editor = null;
		    }
		}  
    	return  null;  	
    }

    /* (non-Javadoc)
     * @see ambit2.ui.actions.BatchAction#getWriter()
     */
    public IChemObjectWriter getWriter() {
    	if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = ((ISharedDbData) userData);
		    try {
		    	return new CDKDescriptorWriter(dbaData.getDbConnection(),descriptors);
		    } catch (Exception x) {
		    	return null;
		    }
		}  return  null;  	
    }

    /* (non-Javadoc)
     * @see ambit2.ui.actions.BatchAction#getBatshStatistics()
     */
    public IBatchStatistics getBatchStatistics() {
    	IBatchStatistics bs = null;
    	if (userData instanceof AmbitDatabaseToolsData) 
			bs = ((AmbitDatabaseToolsData) userData).getBatchStatistics();
		else bs = new DefaultBatchStatistics();
    	bs.setResultCaption("Processed ");
    	return bs;
    }
    /* (non-Javadoc)
     * @see ambit2.ui.actions.BatchAction#getProcessor()
     */
    public IAmbitProcessor getProcessor() {
        ProcessorsChain processors = new ProcessorsChain();
    	if (userData instanceof ISharedDbData) {
		    ISharedDbData dbaData = ((ISharedDbData) userData);
		    try {
		        processors.add(new ReadStructureProcessor(dbaData.getDbConnection().getConn()));
		    } catch (Exception x) {

		    }
		} ;  
        if (userData instanceof AmbitDatabaseToolsData) 
            processors.add(new CalculateDescriptors(descriptors));
        return processors;
    }

}

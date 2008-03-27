/* CalculateMOPACAction.java
 * Author: Nina Jeliazkova
 * Date: 2006-5-16 
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

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.database.DbConnection;
import ambit2.external.DescriptorMopacShell;
import ambit2.external.ShellException;
import ambit2.data.descriptors.DescriptorDefinition;
import ambit2.data.descriptors.DescriptorFactory;
import ambit2.data.descriptors.DescriptorGroups;
import ambit2.data.literature.LiteratureEntry;
import ambit2.data.literature.ReferenceFactory;
import ambit2.database.data.AmbitDatabaseToolsData;
import ambit2.database.data.ISharedDbData;
import ambit2.database.processors.ReadStructureProcessor;
import ambit2.database.readers.DbStructureMissingDescriptorsReader;
import ambit2.database.writers.PropertyDescriptorWriter;
import ambit2.exceptions.AmbitException;
import ambit2.io.batch.DefaultBatchStatistics;
import ambit2.io.batch.IBatchStatistics;
import ambit2.processors.IAmbitProcessor;
import ambit2.processors.ProcessorsChain;
import ambit2.processors.descriptors.MopacProcessor;
import ambit2.ui.actions.BatchAction;

/**
 * Reads structures without {@link ambit2.processors.descriptors.MopacProcessor} descriptors from the database, calculates descriptors and writes back to the database.	
 * seems like a duplicate for {@link ambit2.ui.actions.process.DbMOPACAction} ...
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-5-16
 */
public class CalculateMOPACAction extends BatchAction {
    protected DescriptorMopacShell shell = null;
    protected DescriptorGroups g = null;
    protected LiteratureEntry ref = null;
    protected DescriptorDefinition descriptor = null;
    /**
     * @param userData
     * @param mainFrame
     */
    public CalculateMOPACAction(Object userData, JFrame mainFrame) {
        this(userData, mainFrame,"Calculate electronic descriptors by WinMOPAC");
    }

    /**
     * @param userData
     * @param mainFrame
     * @param name
     */
    public CalculateMOPACAction(Object userData, JFrame mainFrame, String name) {
        this(userData, mainFrame, name,null);
        putValue(AbstractAction.SHORT_DESCRIPTION,"Runs WinMOPAC in order to calculate electronic descriptors.");
    }

    /**
     * @param userData
     * @param mainFrame
     * @param name
     * @param icon
     */
    public CalculateMOPACAction(Object userData, JFrame mainFrame, String name,
            Icon icon) {
        super(userData, mainFrame, name, icon);
        try {
        shell = new DescriptorMopacShell();
        g = new DescriptorGroups();
	    g.addItem(DescriptorFactory.createDescriptorElectronicGroup());
	    ref = ReferenceFactory.createEmptyReference();
	    descriptor = new DescriptorDefinition(DescriptorMopacShell.EHOMO,ref );   
	    descriptor.setDescriptorGroups(g);
        } catch (ShellException x ) {
        	shell = null;
        }
    }

    /* (non-Javadoc)
     * @see ambit2.ui.actions.BatchAction#getReader()
     */
    public IIteratingChemObjectReader getReader() {
        if (userData instanceof ISharedDbData) {
            
            return new DbStructureMissingDescriptorsReader(((ISharedDbData) userData).getDbConnection(),
                    descriptor,1,1000);
        } else return null;
    }

    /* (non-Javadoc)
     * @see ambit2.ui.actions.BatchAction#getWriter()
     */
    public IChemObjectWriter getWriter() {
    	return null;
    	//TODO refactor
    	/*
        if (userData instanceof ISharedDbData) {
            DbConnection conn = ((ISharedDbData) userData).getDbConnection();
	        PropertyDescriptorWriter writer = new PropertyDescriptorWriter(conn,"eHOMO",descriptor);
	        
	        DescriptorDefinition d = MopacShell.createDescriptor_eLumo(g,ref);
		    writer.addDescriptorPair("eLUMO",d);
	        //d.setRemark("Energy of the highest occupied molecular orbital");
		    writer.addDescriptorPair("FINAL HEAT OF FORMATION",MopacShell.createDescriptor_FinalHeatOfFormation(g,ref));
		    writer.addDescriptorPair("TOTAL ENERGY",MopacShell.createDescriptor_TotalEnergy(g,ref));
		    writer.addDescriptorPair("ELECTRONIC ENERGY",MopacShell.createDescriptor_ElectronicEnergy(g,ref));
		    writer.addDescriptorPair("CORE-CORE REPULSION",MopacShell.createDescriptor_CoreCoreRepulsion(g,ref));
		    writer.addDescriptorPair("IONIZATION POTENTIAL",MopacShell.createDescriptor_IonizationPotential(g,ref));
		    writer.addDescriptorPair("MOLECULAR WEIGHT",MopacShell.createDescriptor_MolWeight(g,ref));
		    return writer;
        } else return null;
        */
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
    public IAmbitProcessor getProcessor() {
        ProcessorsChain processors = new ProcessorsChain();
        try {
        if (userData instanceof ISharedDbData)
            processors.add(new ReadStructureProcessor(((ISharedDbData) userData).getDbConnection().getConn()));
        } catch (AmbitException x) {
            logger.error(x);
            return null;
        }
		MopacProcessor mopac = new MopacProcessor(shell);
		mopac.setInteractive(false);
		mopac.setFrame(mainFrame);
        processors.add(mopac);
        return processors;
    }
    
}

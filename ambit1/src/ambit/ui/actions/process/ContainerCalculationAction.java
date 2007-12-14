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

package ambit.ui.actions.process;

import javax.swing.Icon;
import javax.swing.JFrame;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit.data.IDataContainers;
import ambit.data.molecule.CurrentMoleculeWriter;
import ambit.data.molecule.DataContainer;
import ambit.data.molecule.MoleculesIteratorReader;
import ambit.database.data.AmbitDatabaseToolsData;
import ambit.io.batch.DefaultBatchStatistics;
import ambit.io.batch.IBatchStatistics;
import ambit.ui.actions.BatchAction;

/**
 * 
 * TODO add description
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class ContainerCalculationAction extends BatchAction {
	protected boolean processQueryCompounds = false;
	public ContainerCalculationAction(Object userData, JFrame mainFrame, boolean processQueryCompounds) {
		this(userData, mainFrame,"Process",processQueryCompounds);
	}

	public ContainerCalculationAction(Object userData, JFrame mainFrame,
			String name, boolean processQueryCompounds) {
		this(userData, mainFrame, name,null,processQueryCompounds);
	}

	public ContainerCalculationAction(Object userData, JFrame mainFrame,
			String name, Icon icon, boolean processQueryCompounds) {
		super(userData, mainFrame, name, icon);
		setProcessQueryCompounds(processQueryCompounds);
	}

	public DataContainer getMolecules() {
		if (userData instanceof IDataContainers) {
			IDataContainers dbaData = (IDataContainers) userData;
			if (processQueryCompounds)
				return dbaData.getQueries();
			else
				return dbaData.getMolecules();
		} else return null;
	}
	public IIteratingChemObjectReader getReader() {
		DataContainer c = getMolecules();
		c.setEnabled(false);
		return new MoleculesIteratorReader(c.getContainers());
	}

	public IChemObjectWriter getWriter() {
		if (userData instanceof IDataContainers) {
			IDataContainers dbaData = (IDataContainers) userData;		
			return new CurrentMoleculeWriter(getMolecules());
		} else return null;	
	}
    public IBatchStatistics getBatchStatistics() {
    	IBatchStatistics bs = null;
    	if (userData instanceof AmbitDatabaseToolsData) 
			bs = ((AmbitDatabaseToolsData) userData).getBatchStatistics();
		else bs = new DefaultBatchStatistics();
    	bs.setResultCaption("Processed ");
    	return bs;
    }
    public void completed() {
    	getMolecules().setEnabled(true);
    	super.completed();
    }

	public boolean isProcessQueryCompounds() {
		return processQueryCompounds;
	}

	public void setProcessQueryCompounds(boolean processQueryCompounds) {
		this.processQueryCompounds = processQueryCompounds;
	}
}



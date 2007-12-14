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
import javax.swing.JOptionPane;

import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit.data.descriptors.DescriptorsHashtable;
import ambit.data.descriptors.ISharedDescriptors;
import ambit.exceptions.AmbitException;
import ambit.processors.IAmbitProcessor;
import ambit.processors.ProcessorsChain;
import ambit.processors.descriptors.CalculateDescriptors;
import ambit.ui.data.descriptors.DescriptorsHashtableEditor;

public class ContainerDescriptorCalculationAction extends
		ContainerCalculationAction {
	
	public ContainerDescriptorCalculationAction(Object userData,
			JFrame mainFrame, boolean processQueryCompounds) {
		this(userData, mainFrame,"Calculate descriptors",processQueryCompounds);
	}

	public ContainerDescriptorCalculationAction(Object userData,
			JFrame mainFrame, String name, boolean processQueryCompounds) {
		this(userData, mainFrame, name,null,processQueryCompounds);
	}

	public ContainerDescriptorCalculationAction(Object userData,
			JFrame mainFrame, String name, Icon icon, boolean processQueryCompounds) {
		super(userData, mainFrame, name, icon, processQueryCompounds);
        
        interactive = false;
	}
	public IIteratingChemObjectReader getReader() {
		return super.getReader();
	
	}
    public IAmbitProcessor getProcessor() {
        ProcessorsChain processors = new ProcessorsChain();

		if (userData instanceof ISharedDescriptors) {
			DescriptorsHashtable descriptors = ((ISharedDescriptors) userData).getDescriptors();
		    DescriptorsHashtableEditor editor = new DescriptorsHashtableEditor(descriptors,true);
		    try {
			    if (editor.view(mainFrame, true, "")) {
			    	descriptors = editor.getLookup();
			    	processors.add(new CalculateDescriptors(descriptors,null));
			    }	
			    
		    } catch (AmbitException x) {
		    	JOptionPane.showMessageDialog(mainFrame,x.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		    	return null;
		    } finally {
		    	editor = null;
		    }
	
        return processors;
		} else return null;
    }

}



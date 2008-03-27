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

package ambit2.ui.actions.process;

import javax.swing.Icon;
import javax.swing.JFrame;

import ambit2.data.descriptors.DescriptorFactory;
import ambit2.data.descriptors.DescriptorsHashtable;
import ambit2.processors.IAmbitProcessor;
import ambit2.processors.descriptors.CalculateDescriptors;
import ambit2.ui.UITools;

/**
 * 
 * Calculate descriptors of a molecule
 * 
 * TODO switch to DescriptorEngine
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class DescriptorCalculatorAction extends MoleculeCalculationAction {
	//protected DescriptorEngine engine;
	protected DescriptorsHashtable descriptors;
	
	public DescriptorCalculatorAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Descriptors calculator",null);
	}

	public DescriptorCalculatorAction(Object userData, JFrame mainFrame,
			String name) {
		this(userData, mainFrame, name,UITools.createImageIcon("ambit2/ui/images/chart_bar.png"));
	}

	public DescriptorCalculatorAction(Object userData, JFrame mainFrame,
			String name, Icon icon) {
		super(userData, mainFrame, name, icon);
		interactive = false;
		descriptors =  DescriptorFactory.createDescriptorsList(); 
	}
	public IAmbitProcessor getProcessor() {
		//return new DescriptorCalculatorProcessor();
        /*
        TemplateHandler3D th = null;
        if (userData instanceof AmbitDatabaseToolsData) 
            th = ((AmbitDatabaseToolsData) userData).getTemplateHandler();
            */        
		return new CalculateDescriptors(descriptors);
	}

}



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

import ambit2.processors.IAmbitProcessor;
import ambit2.processors.ISharedProcessors;
import ambit2.similarity.FingerprintSimilarity;

public class ContainerSimilarityCalculationAction extends
		ContainerCalculationAction {
	
	public ContainerSimilarityCalculationAction(Object userData,
			JFrame mainFrame, boolean processQueryCompounds) {
		super(userData, mainFrame, processQueryCompounds);

	}

	public ContainerSimilarityCalculationAction(Object userData,
			JFrame mainFrame, String name, boolean processQueryCompounds) {
		super(userData, mainFrame, name, processQueryCompounds);

	}

	public ContainerSimilarityCalculationAction(Object userData,
			JFrame mainFrame, String name, Icon icon,
			boolean processQueryCompounds) {
		super(userData, mainFrame, name, icon, processQueryCompounds);

	}
	public IAmbitProcessor getProcessor() {
		if (userData instanceof ISharedProcessors) {
			if (processQueryCompounds) 
				return ((ISharedProcessors) userData).getQueryProcessor();
			else
				return ((ISharedProcessors) userData).getDataProcessor();
		} else	
			return new FingerprintSimilarity();
	}
}



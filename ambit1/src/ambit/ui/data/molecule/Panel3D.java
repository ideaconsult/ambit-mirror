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

package ambit.ui.data.molecule;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JPanel;

import org.jmol.api.JmolAdapter;
import org.jmol.api.JmolViewer;
import org.jmol.viewer.Viewer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.ISetOfMolecules;

import ambit.ui.CdkJmolAdapter;

/**
 * Makes use of {@link org.jmol.api.JmolViewer} to display 3D molecule.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class Panel3D extends JPanel {
	   JmolViewer viewer;
	   JmolAdapter adapter;
	   ISetOfMolecules moleculeSet;
       ChemModel model;
       ChemSequence sequence;
       ChemFile chemFile;
	     
	   public Panel3D() {
		 super();
	     adapter = new CdkJmolAdapter(null);
	     viewer = Viewer.allocateViewer(this, adapter);
	     viewer.setAutoBond(true);
	     viewer.setShowHydrogens(true);
	     viewer.setDebugScript(false);
	     viewer.setCenterSelected();
	     
	     
	     setPreferredSize(new Dimension(200,200));
	     setMaximumSize(new Dimension(200,200));
	     
	     moleculeSet = new SetOfMolecules();
	     model = new ChemModel();
	     model.setSetOfMolecules(moleculeSet);
	     sequence = new ChemSequence();
	     sequence.addChemModel(model);
	     chemFile = new ChemFile();
	     chemFile.addChemSequence(sequence);
	    
	   }
	 
	   public JmolViewer getViewer() {
	     return viewer;
	   }
	 
	   final Dimension currentSize = new Dimension();
	 
	   public void paint(Graphics g) {
		   if (viewer != null) {
		     viewer.setScreenDimension(getSize(currentSize));
		     Rectangle rectClip = new Rectangle();
		     g.getClipBounds(rectClip);
		     g.clearRect(rectClip.x, rectClip.y, rectClip.width,  rectClip.height);
		     viewer.renderScreenImage(g, currentSize, rectClip);
		   }
	   }
	 
	   public void executeCmd(String command) {
	     viewer.evalString(command);
	   }
	 
	   public void setMol(IAtomContainer mol) {
		   if (viewer == null) return;
		 moleculeSet.removeAllAtomContainers();
		 if (mol != null) moleculeSet.addAtomContainer(mol);
		 /*  
	     SetOfMolecules moleculeSet = new SetOfMolecules();
	     moleculeSet.addAtomContainer(mol);
	     ChemModel model = new ChemModel();
	     model.setSetOfMolecules(moleculeSet);
	     ChemSequence sequence = new ChemSequence();
	     sequence.addChemModel(model);
	     ChemFile chemFile = new ChemFile();
	     chemFile.addChemSequence(sequence);
	 	*/
		 	try {
		 			chemFile.setProperty(CDKConstants.TITLE,"MOLECULE");
		 			viewer.openClientFile("","",chemFile);
		 	} catch (Exception x) {
		 			viewer = null;
		 			x.printStackTrace();
		 			
		 	}
	   }   

}



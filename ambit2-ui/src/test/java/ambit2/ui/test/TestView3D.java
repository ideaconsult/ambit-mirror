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

package ambit2.ui.test;

import javax.swing.JOptionPane;
import javax.vecmath.Point3d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.core.data.MoleculeTools;
import ambit2.ui.jmol.Panel3D;

public class TestView3D  {

	public static IMolecule getMolecule() {
		IMolecule methane = MoleculeTools.newMolecule(SilentChemObjectBuilder.getInstance());
		Atom atom = new Atom("C");
		atom.setPoint3d(new Point3d(0.26,-0.36,0.00));
		methane.addAtom(atom);
		atom = new Atom("H");
		atom.setPoint3d(new Point3d(0.26,0.73,0.00));
		methane.addAtom(atom);
		atom = new Atom("H");
		atom.setPoint3d(new Point3d(0.77,-0.73,0.89));
		methane.addAtom(atom);
		atom = new Atom("H");
		atom.setPoint3d(new Point3d(0.77,-0.73,-0.89));
		methane.addAtom(atom);
		atom = new Atom("H");
		atom.setPoint3d(new Point3d(-0.77,-0.73,0.00));
		methane.addAtom(atom);
		return methane;
	}
	public static void main(String[] args) {
		Panel3D jmolPanel = new Panel3D();

		
		// then send it to the Jmol Viewer
		//jmolPanel.getViewer().openClientFile("", "", methane);
		jmolPanel.setObject(getMolecule());
		
		JOptionPane.showMessageDialog(null,jmolPanel);
		System.out.println("Done");
	}

}



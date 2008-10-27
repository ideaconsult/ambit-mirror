/*
Copyright (C) 2005-2008  

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

import java.awt.Dimension;
import java.awt.Event;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.smiles.SmilesParser;

import ambit2.core.io.CompoundImageTools;
import ambit2.ui.editors.Panel2D;


public class Panel2DTest {
	protected IMolecule mol;
	IAtomContainer selected;
	@Before
	public void setup() throws Exception  {
		String smiles="CCc1ccccc1.C1CCCC1.C1CCC1.[Na+].CCCCCCCCCCCCCCCCC(CCCCCCCCC)CCCCCCCCCCCC";
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        mol = sp.parseSmiles(smiles);		

		for (int i=0; i < mol.getBondCount();i++)
			mol.getBond(i).setID(Integer.toString(i+1));
		selected = new Molecule();

		selected.addBond(mol.getBond(0));
		selected.addBond(mol.getBond(1));		
	}
	@Test
	public void testHighlight() throws Exception {
		Panel2D panel = new Panel2D();
		panel.setPreferredSize(new Dimension(400,400));

		panel.setAtomContainer(mol);
		panel.setSelected(selected);		
		JOptionPane.showMessageDialog(null,
				new JSplitPane(JSplitPane.VERTICAL_SPLIT,
						new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,panel,new JPanel()),
						new JButton("?") {
					@Override
					public boolean action(Event evt, Object what) {
						return true;
						
					}
				
				})
				);
	}
	@Test
	public void testImage() throws Exception {

		CompoundImageTools tools = new CompoundImageTools();
		tools.setImageSize(new Dimension(300,300));
		BufferedImage image = tools.getImage(mol,selected);
		
		File file = new File("image.png");
		FileOutputStream out = new FileOutputStream(file);
		System.out.println(file.getAbsolutePath());
		ImageIO.write(image, "png", out);
		out.close();

	}
}

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

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.cdk.renderer.selection.SingleSelection;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import ambit2.rendering.CompoundImageTools;
import ambit2.ui.Panel2D;

public class Panel2DTest {
	protected IMolecule mol;

	@Before
	public void setup() throws Exception {
		// String
		// smiles="CCc1ccccc1.C1CCCC1.C1CCC1.[Na+].CCCCCCCCCCCCCCCCC(CCCCCCCCC)CCCCCCCCCCCC";
		String smiles = "NCCCCCCCCC";
		SmilesParser sp = new SmilesParser(
				SilentChemObjectBuilder.getInstance());
		mol = sp.parseSmiles(smiles);

		for (int i = 0; i < mol.getBondCount(); i++)
			mol.getBond(i).setID(Integer.toString(i + 1));

	}

	protected IProcessor<IAtomContainer, IChemObjectSelection> getSelector(
			IAtomContainer mol) {
		return new IProcessor<IAtomContainer, IChemObjectSelection>() {
			public IChemObjectSelection process(IAtomContainer mol)
					throws AmbitException {
				Molecule selected = new Molecule();
				for (int i = 0; i < 2; i++) {
					selected.addAtom(mol.getBond(i).getAtom(0));
					selected.addAtom(mol.getBond(i).getAtom(1));
					selected.addBond(mol.getBond(i));
				}
				return new SingleSelection<Molecule>(selected);
			}

			public boolean isEnabled() {
				return true;
			}

			public void setEnabled(boolean value) {
			}

			public long getID() {
				return 0;
			}

			@Override
			public void open() throws Exception {
			}

			@Override
			public void close() throws Exception {
			}
		};

	}

	@Test
	public void testHighlight() throws Exception {
		Panel2D panel = new Panel2D();

		panel.setPreferredSize(new Dimension(400, 400));

		panel.setAtomContainer(mol);
		panel.setSelector(getSelector(mol));
		JOptionPane.showMessageDialog(null, new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, new JSplitPane(
						JSplitPane.HORIZONTAL_SPLIT, panel, new JPanel()),
				new JButton("?") {
					@Override
					public boolean action(Event evt, Object what) {
						return true;

					}

				}));
	}

	@Test
	public void testImage() throws Exception {

		CompoundImageTools tools = new CompoundImageTools();
		tools.setImageSize(new Dimension(300, 300));
		BufferedImage image = tools.getImage(mol, getSelector(mol), false,
				false);

		File file = new File("image.png");
		FileOutputStream out = new FileOutputStream(file);
		System.out.println(file.getAbsolutePath());
		ImageIO.write(image, "png", out);
		out.close();

	}
}

package ambit2.jchempaint.test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.jchempaint.renderer.selection.IChemObjectSelection;
import org.openscience.jchempaint.renderer.selection.SingleSelection;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.core.data.MoleculeTools;
import ambit2.jchempaint.CompoundImageTools;



public class RenderingTest {
	/**
	 * There is smth wrong with jcp rendering bonds over atom labels ... 
	 * and also I would need a way to control labels background 
	 * @throws Exception
	 */
	@Test
	public void testHilights() throws Exception {
		
		IMolecule mol = MoleculeFactory.makePyridine();
		CompoundImageTools tools = new CompoundImageTools();
		IProcessor<IAtomContainer,IChemObjectSelection> selector = new  IProcessor<IAtomContainer,IChemObjectSelection> () {
			@Override
			public IChemObjectSelection process(IAtomContainer target)
					throws AmbitException {
				IAtomContainer ac = MoleculeTools.newMolecule(SilentChemObjectBuilder.getInstance());
				for (IAtom a : target.atoms()) {
					
					if ("N".equals(a.getSymbol())) {
						a.setProperty(CompoundImageTools.SELECTED_ATOM_COLOR,
								new Color(Color.orange.getRed(),Color.orange.getGreen(),Color.orange.getBlue(),255 ));
						a.setProperty(CompoundImageTools.SELECTED_ATOM_SIZE, 2);
						ac.addAtom(a);
					}
									}
				return new SingleSelection<IAtomContainer>(ac);
			}

			@Override
			public long getID() {
				return 0;
			}

			@Override
			public boolean isEnabled() {
				return true;
			}

			@Override
			public void setEnabled(boolean value) {
			}
		};
	    BufferedImage image = tools.getImage(mol,selector, true,false);
	    File file = new File(String.format("%s/test.png",System.getProperty("java.io.tmpdir")));
	    if (file.exists()) file.delete();
	    //file.deleteOnExit();
		ImageIO.write(image, "png", file) ;
		Assert.assertTrue(file.exists());
	
	}
}


/* HydrogenAdderProcessorTest.java
 * Author: nina
 * Date: Feb 7, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
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

package ambit2.core.processors.test;


import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import ambit2.core.processors.structure.HydrogenAdderProcessor;

public class HydrogenAdderProcessorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	/**
	 * CDK bug and a fix https://sourceforge.net/tracker/index.php?func=detail&aid=2983334&group_id=20024&atid=120024#
	 * @throws Exception
	 */
	@Test
	public void testImplicitH_processorFormula() throws Exception {
		HydrogenAdderProcessor p = new HydrogenAdderProcessor();
		p.setAddEexplicitHydrogens(false);
		IAtomContainer mol = MoleculeFactory.makeBenzene();
		
		IMolecularFormula f = MolecularFormulaManipulator.getMolecularFormula(mol);
		Assert.assertEquals("C6",MolecularFormulaManipulator.getString(f));
		
		Assert.assertEquals(6,mol.getAtomCount());
		mol = p.process(mol);
		Assert.assertEquals(6,mol.getAtomCount());
		f = MolecularFormulaManipulator.getMolecularFormula(mol);
		Assert.assertEquals("C6H6",MolecularFormulaManipulator.getString(f));

	}
	//CDK bug submitted
	@Test
	public void testImplicitHFormula() throws Exception {
		
		CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());

		IAtomContainer mol = MoleculeFactory.makeBenzene();
		
		IMolecularFormula f = MolecularFormulaManipulator.getMolecularFormula(mol);
		Assert.assertEquals("C6",MolecularFormulaManipulator.getString(f));
		
		Assert.assertEquals(6,mol.getAtomCount());
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		adder.addImplicitHydrogens(mol);
		Assert.assertEquals(6,mol.getAtomCount());
		f = MolecularFormulaManipulator.getMolecularFormula(mol);
		Assert.assertEquals("C6H6",MolecularFormulaManipulator.getString(f));

	}	
	@Test
	public void testExplicitH() throws Exception {
		HydrogenAdderProcessor p = new HydrogenAdderProcessor();
		IAtomContainer mol = MoleculeFactory.makeBenzene();
		IMolecularFormula f = MolecularFormulaManipulator.getMolecularFormula(mol);
		Assert.assertEquals("C6",MolecularFormulaManipulator.getString(f));
	
		Assert.assertEquals(6,mol.getAtomCount());
		mol = p.process(mol);
		Assert.assertEquals(12,mol.getAtomCount());
		f = MolecularFormulaManipulator.getMolecularFormula(mol);
		Assert.assertEquals("C6H6",MolecularFormulaManipulator.getString(f));

	}	
	
}

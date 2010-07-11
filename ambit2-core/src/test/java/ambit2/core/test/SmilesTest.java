/* SmilesTest.java
 * Author: nina
 * Date: Apr 7, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  
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

package ambit2.core.test;


import java.io.StringReader;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.DeduceBondSystemTool;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.data.MoleculeTools;

public class SmilesTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
    //String[] smiles = {"NCCO","OCCN"};
	@Test
	public void testCanonicalSmiles() throws Exception  {

	    String[] smiles = {"c1cc(ccc1C(c2ccc(cc2)Cl)=C(Cl)Cl)Cl",
	            "Clc1ccc(cc1)C(=C(Cl)Cl)c2ccc(Cl)cc2",
	            "C(=C(Cl)Cl)(C1C=CC(=CC=1)Cl)C2=CC=C(C=C2)Cl" 
	            };
	    String[] newSmiles = {"","",""};
	    SmilesGenerator gen = new SmilesGenerator(true);
	    gen.setUseAromaticityFlag(true);
	    IMolecule m = getMolecule();
	    String m_smiles = gen.createSMILES(m);
	    Assert.assertFalse("".equals(m_smiles));
		SmilesParser parser = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
		
		int count_differences = 0;
	    for (int i=0; i < smiles.length;i++) {
				IMolecule mol = parser.parseSmiles(smiles[i]);
				Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(m,mol));
				newSmiles[i] = gen.createSMILES(mol);
				
				if (!newSmiles[i].equals(m_smiles)) {
					count_differences++;
					System.out.print(m_smiles);
					System.out.print('\t');
					System.out.println(newSmiles[i]);
				}
	    }
	    Assert.assertEquals(0,count_differences);
	}	
	public IMolecule getMolecule() {
	    
	    String sdf = 

		"\n\n\n"+ 

		 "18 19  0  0  0  0  0  0  0  0  1 V2000\n"+
		 "    4.6054   -1.9895    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    4.6054   -0.6632    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    3.4540   -0.0000    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    5.7567   -0.0000    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    5.7567   -2.6527    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    6.9080   -1.9895    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    8.0594   -2.6527    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    8.0594   -3.9882    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    6.9080   -4.6514    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    5.7567   -3.9882    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    9.2107   -4.6514    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    3.4540   -2.6527    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    3.4540   -3.9882    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    2.3027   -4.6514    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    1.1513   -3.9882    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    1.1513   -2.6527    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    2.3027   -1.9895    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "    0.0000   -4.6514    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\n"+
		 "  1  2  2  0  0  0  0\n"+
		 "  1  5  1  0  0  0  0\n"+
		 "  1 12  1  0  0  0  0\n"+
		 "  2  3  1  0  0  0  0\n"+
		 "  2  4  1  0  0  0  0\n"+
		 "  5  6  1  0  0  0  0\n"+
		 "  5 10  2  0  0  0  0\n"+
		 "  6  7  2  0  0  0  0\n"+
		 "  7  8  1  0  0  0  0\n"+
		 "  8  9  2  0  0  0  0\n"+
		 "  8 11  1  0  0  0  0\n"+
		 "  9 10  1  0  0  0  0\n"+
		 " 12 13  2  0  0  0  0\n"+
		 " 12 17  1  0  0  0  0\n"+
		 " 13 14  1  0  0  0  0\n"+
		 " 14 15  2  0  0  0  0\n"+
		 " 15 16  1  0  0  0  0\n"+
		 " 15 18  1  0  0  0  0\n"+
		 " 16 17  2  0  0  0  0\n"+
		"M  END\n" +
		"\n" +
		"$$$$";
	    
	    MDLReader r = new MDLReader(new StringReader(sdf));
	    IMolecule m = MoleculeTools.newMolecule(DefaultChemObjectBuilder.getInstance());
	    try {
	        m = (IMolecule)r.read(m);
	    } catch (CDKException x) {
	        x.printStackTrace();
	    }
	    return m;
	}	
	@Test
	public void testAromaticityRing7() throws Exception {
		SmilesParser parser = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
		IMolecule mol = parser.parseSmiles("c1cccccc1");
		for (IAtom atom : mol.atoms())
			Assert.assertTrue(atom.getFlag(CDKConstants.ISAROMATIC));
	}
	
	@Test
	public void testAromaticityRing7a() throws Exception {
		SmilesParser parser = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
		IMolecule mol = parser.parseSmiles("c1cccccc1");
		//AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		//for (IAtom atom : mol.atoms())	Assert.assertTrue(atom.getFlag(CDKConstants.HYBRIDIZATION_SP2));		
		DeduceBondSystemTool d = new DeduceBondSystemTool();
		System.out.println(d.isOK(mol));

		for (IBond bond: mol.bonds())
			System.out.println(bond.getOrder());
	}	
	
	@Test
	public void testAromaticityRing6() throws Exception {
		SmilesParser parser = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
		IMolecule mol = parser.parseSmiles("c1ccccc1");
		for (IAtom atom : mol.atoms())
			Assert.assertTrue(atom.getFlag(CDKConstants.ISAROMATIC));
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		DeduceBondSystemTool d = new DeduceBondSystemTool();
		d.fixAromaticBondOrders(mol);
		for (IBond bond: mol.bonds())
			System.out.println(bond.getOrder());		
	}	
}

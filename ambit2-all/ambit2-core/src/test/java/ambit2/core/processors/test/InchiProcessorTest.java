/* InchiProcessorTest.java
 * Author: nina
 * Date: Jan 11, 2009
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

import java.io.InputStream;

import junit.framework.Assert;
import net.sf.jniinchi.INCHI_RET;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.io.FileInputState;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.processors.structure.HydrogenAdderProcessor;
import ambit2.core.processors.structure.InchiProcessor;

public class InchiProcessorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProcessBenzene() throws Exception {
		generate(MoleculeFactory.makeBenzene(),"InChI=1S/C6H6/c1-2-4-6-5-3-1/h1-6H");
	}

	
	@Test
	public void testProcessCaffeineAromaticity() throws Exception {
		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IMolecule mol = p.parseSmiles("CN1C=NC2=C1C(=O)N(C(=O)N2C)C");
		generate(mol,"InChI=1S/C8H10N4O2/c1-10-4-9-6-5(10)7(13)12(3)8(14)11(6)2/h4H,1-3H3",true);
	}	
	
	@Test
	public void testProcess2() throws Exception {
		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IMolecule mol = p.parseSmiles("NC1=CC(N)=NC(O)=N1");
		generate(mol,"InChI=1S/C4H6N4O/c5-2-1-3(6)8-4(9)7-2/h1H,(H5,5,6,7,8,9)",false);
	}	
	
	
	@Test
	public void testProcessAromaticity() throws Exception {
		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IMolecule mol = p.parseSmiles("CN1C=NC2=C1C(=O)N(C(=O)N2C)C");
		//IMolecule mol = p.parseSmiles("c1ccccc1");

		CDKHydrogenAdder ha = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
		ha.addImplicitHydrogens(mol);
		//AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);  // this is the most important
		
		InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();  
        InChIGenerator gen = factory.getInChIGenerator(mol);
        INCHI_RET ret = gen.getReturnStatus();
        if (ret != INCHI_RET.OKAY) {
			throw new Exception(String.format("InChI failed: %s [%s]",
					ret.toString(),gen.getMessage()));
        }
        String inchi = gen.getInchi();
        Assert.assertEquals("InChI=1S/C8H10N4O2/c1-10-4-9-6-5(10)7(13)12(3)8(14)11(6)2/h4H,1-3H3", inchi);
	}
	
	@Test
	public void testProcess1() throws Exception {
		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IMolecule m = p.parseSmiles("Cc1ccc(cc1)C(C)C=O");
		
	}
	
	@Test
	public void testProcessAlkane() throws Exception {
		generate(MoleculeFactory.makeAlkane(10),"InChI=1S/C10H22/c1-3-5-7-9-10-8-6-4-2/h3-10H2,1-2H3");		

	}
	public void generate(IAtomContainer mol,String expected) throws Exception {
		generate(mol, expected,false);
	}
	public void generate(IAtomContainer mol,String expected,boolean aromatic) throws Exception {
		InchiProcessor p = new InchiProcessor();
		HydrogenAdderProcessor ha = new HydrogenAdderProcessor();
		mol = ha.process(mol);
		
		if (aromatic) {
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
			CDKHueckelAromaticityDetector.detectAromaticity(mol);
		}
		InChIGenerator gen = p.process(mol);
		String inchi = gen.getInchi();
		String auxinfo = gen.getAuxInfo();
		Assert.assertEquals(expected, inchi);
	}
	
	
	@Test
	public void parse() throws Exception {
		InChIGeneratorFactory f = InChIGeneratorFactory.getInstance();
		
		InChIToStructure c =f.getInChIToStructure("InChI=1S/C5H12O/c1-3-4-5-6-2/h3-5H2,1-2H3", SilentChemObjectBuilder.getInstance());
		
		System.out.println(c.getLog());
		System.out.println(c.getWarningFlags());
		
		AtomConfigurator cfg = new AtomConfigurator();
		HydrogenAdderProcessor ha = new HydrogenAdderProcessor();
		
		IAtomContainer a = c.getAtomContainer();
		a = ha.process(a);

		generate(a,"InChI=1S/C5H12O/c1-3-4-5-6-2/h3-5H2,1-2H3");
		// InChI=1/C5O/c1-3-4-5-6-2
	}

	
	public IAtomContainer isInChI(String inchi) throws Exception {
		if ((inchi!= null) && inchi.startsWith(AmbitCONSTANTS.INCHI)) {
			InChIGeneratorFactory f = InChIGeneratorFactory.getInstance();
			InChIToStructure c =f.getInChIToStructure(inchi, SilentChemObjectBuilder.getInstance());
			if ((c==null) || (c.getAtomContainer()==null) || (c.getAtomContainer().getAtomCount()==0)) 
				throw new Exception("Invalid InChI");
			return c.getAtomContainer();
		} else return null;
	}
	
	@Test
	public void testProcessStereo() throws Exception {
		InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();  
		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		
		InputStream in = getClass().getClassLoader().getResourceAsStream("ambit2/core/data/stereo/stereo.sdf");
		IIteratingChemObjectReader<IMolecule> reader =  FileInputState.getReader(in, "stereo.sdf");
		int count = 0;
		while (reader.hasNext()) {
			IMolecule o = reader.next();
			InChIGenerator gen = factory.getInChIGenerator(o);
		    INCHI_RET ret = gen.getReturnStatus();
		    if (ret != INCHI_RET.OKAY) {
		    	System.err.println(String.format("InChI failed: %s [%s]",ret.toString(),gen.getMessage()));
		    }
		    String inchi = gen.getInchi();
		    System.out.println(inchi);
		    Assert.assertEquals(o.getProperty("InChI"),inchi);
		    
			Object smiles = o.getProperty("SMILES");
			Assert.assertNotNull(smiles);
			 System.out.println(smiles);
			IMolecule smilesMol = p.parseSmiles(smiles.toString());
			gen = factory.getInChIGenerator(smilesMol);
			System.out.println(gen.getReturnStatus() + " " + gen.getMessage());
			System.out.println("from SMILES " + gen.getInchi());
			count++;
			if (!gen.getInchi().equals(inchi)) {
				System.out.println("Mismatch");
			}
		}
		reader.close();
		Assert.assertEquals(4,count);

	}
}

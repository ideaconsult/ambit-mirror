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

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;

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
	public void test() throws Exception {
		SmilesParser p = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule m = p.parseSmiles("CCCCOC");
		
		generate(m,"InChI=1S/C5H12O/c1-3-4-5-6-2/h3-5H2,1-2H3");
	}
	@Test
	public void testProcessBenzene() throws Exception {
		generate(MoleculeFactory.makeBenzene(),"InChI=1/C6H6/c1-2-4-6-5-3-1/h1-6H");
	}
	@Test
	public void testProcessAlkane() throws Exception {
		generate(MoleculeFactory.makeAlkane(10),"InChI=1/C10H22/c1-3-5-7-9-10-8-6-4-2/h3-10H2,1-2H3");		

	}	
	public void generate(IAtomContainer mol,String expected) throws Exception {
		InchiProcessor p = new InchiProcessor();
		HydrogenAdderProcessor ha = new HydrogenAdderProcessor();
		mol = ha.process(mol);
		InChIGenerator gen = p.process(mol);
		String inchi = gen.getInchi();
		String auxinfo = gen.getAuxInfo();
		Assert.assertEquals(expected, inchi);
	}
	@Test
	public void parse() throws Exception {
		InChIGeneratorFactory f = InChIGeneratorFactory.getInstance();
		
		InChIToStructure c =f.getInChIToStructure("InChI=1S/C5H12O/c1-3-4-5-6-2/h3-5H2,1-2H3", DefaultChemObjectBuilder.getInstance());
		
		System.out.println(c.getLog());
		System.out.println(c.getWarningFlags());
		
		AtomConfigurator cfg = new AtomConfigurator();
		HydrogenAdderProcessor ha = new HydrogenAdderProcessor();
		
		IAtomContainer a = c.getAtomContainer();
		a = ha.process(a);

		generate(a,"InChI=1/C5H12O/c1-3-4-5-6-2/h3-5H2,1-2H3");
		// InChI=1/C5O/c1-3-4-5-6-2
	}
}

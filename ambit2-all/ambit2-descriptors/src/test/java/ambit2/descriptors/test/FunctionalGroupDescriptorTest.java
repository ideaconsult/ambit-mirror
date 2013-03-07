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

package ambit2.descriptors.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.base.data.Property;
import ambit2.core.processors.structure.HydrogenAdderProcessor;
import ambit2.descriptors.FunctionalGroup;
import ambit2.descriptors.FunctionalGroupDescriptor;
import ambit2.descriptors.VerboseDescriptorResult;
import ambit2.smarts.query.SmartsPatternAmbit;

public class FunctionalGroupDescriptorTest {
	FunctionalGroupDescriptor d; 
	List<FunctionalGroup> groups;
	HydrogenAdderProcessor hadder;
	@Before
	public void setUp() throws Exception {
		d = new FunctionalGroupDescriptor();
		groups = new ArrayList<FunctionalGroup>();
		groups.add(new FunctionalGroup("alkane","CCCC","test"));
		hadder =  new HydrogenAdderProcessor();
	}
	


	@Test
	public void testFunctionalGroupDescriptor() throws Exception{
		FunctionalGroupDescriptor newd = new FunctionalGroupDescriptor();
		Assert.assertNotNull(newd);
	}

	@Test
	public void testVerbose() throws Exception {
		groups = new ArrayList<FunctionalGroup>();
		groups.add(new FunctionalGroup("C","C","testC"));
		calculate(groups,true,MoleculeFactory.makeAlkane(4),1);

	}
	@Test
	public void testSingle() throws Exception {
	
		groups = new ArrayList<FunctionalGroup>();
		groups.add(new FunctionalGroup("C4","CCCC","test4"));	
		calculate(groups,false,MoleculeFactory.makeAlkane(4),1);


	}	
	@Test
	public void testMultiple() throws Exception {
	
		groups = new ArrayList<FunctionalGroup>();
		groups.add(new FunctionalGroup("C4","CCCC","test4"));	
		groups.add(new FunctionalGroup("C5","CCCCC","test5"));	
		calculate(groups,false,MoleculeFactory.makeAlkane(4),2);

	}		
	
	@Test
	public void testDefaultGroups() throws Exception {
		FunctionalGroupDescriptor d = new FunctionalGroupDescriptor();
		SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IAtomContainer mol =  p.parseSmiles("C(=O)Cl");
		calculate((List<FunctionalGroup> )d.getParameters()[0], false,mol ,84);
		//Alkyl C [CX4]
		//Alanine side chain [CH3X4]
	}			
	
	@Test
	public void testDefaultGroups1() throws Exception {
		IAtomContainer m = MoleculeFactory.makeAlkane(10);
		m = hadder.process(m);
		String[] smarts = new String[] {"[CX4]","[$([CX2](=C)=C)]"};
		int[] results = new int[] {1,0};
		for (int i=0; i < results.length; i++) {
			String s = smarts[i];
			SmartsPatternAmbit p = new SmartsPatternAmbit(SilentChemObjectBuilder.getInstance());
			p.setSmarts(s);
			Assert.assertEquals(results[i],p.match(m));
		}
	}		
		
	protected void calculate(List<FunctionalGroup> groups,boolean verbose,IAtomContainer m, int hits) throws Exception {
		d.setParameters(new Object[]{groups,verbose});
		m = hadder.process(m);
		DescriptorValue value = d.calculate(m);
		IDescriptorResult v = value.getValue();
		
		Assert.assertEquals(hits,value.getNames().length);
		Assert.assertTrue(v instanceof VerboseDescriptorResult);
		VerboseDescriptorResult verboseResult = (VerboseDescriptorResult) v;
		IDescriptorResult r = verboseResult.getResult();
		Assert.assertTrue(r instanceof IntegerArrayResult);
		Assert.assertEquals(hits,((IntegerArrayResult)r).length());		
		
		if (verbose) {
			Assert.assertTrue(verboseResult.getExplanation() instanceof List);
			Assert.assertTrue(((List)verboseResult.getExplanation()).get(0) instanceof IAtomContainer);			
		}
			
	}
	@Test
	public void testGetParameterNames() throws Exception {
		String[] params = d.getParameterNames();
		assertEquals(2,params.length);
		assertEquals("funcgroups",params[0]);
		assertEquals("verbose",params[1]);

	}

	@Test
	public void testGetParameterType() throws Exception  {
		Assert.assertTrue(d.getParameterType("funcgroups") instanceof List);
		assertEquals(Boolean.class,d.getParameterType("verbose").getClass());

	}

	@Test
	public void testGetSpecification() {
		DescriptorSpecification spec = d.getSpecification();
		assertEquals(FunctionalGroupDescriptor.class.getName(),spec.getImplementationTitle());
		assertEquals(String.format(Property.AMBIT_DESCRIPTORS_ONTOLOGY,"OECDCategories")
				,spec.getSpecificationReference());
	}

	@Test
	public void testSetParameters() throws Exception  {
		
		d.setParameters(new Object[] {groups});
		Object[] p = d.getParameters();
		Assert.assertTrue(p[0] instanceof List);
		Assert.assertEquals(false,p[1]);
		
		Assert.assertTrue(((List)p[0]).get(0) instanceof FunctionalGroup);
		Assert.assertEquals("CCCC", ((FunctionalGroup) (((List)p[0]).get(0))).getSmarts() );
	}

	@Test
	public void testToString() throws Exception {
		d.setParameters(new Object[]{groups});
		Assert.assertEquals("Functional groups: (1)",d.toString());
	}

	@Test
	public void testGetDescriptorResultType() {
		Assert.assertTrue(d.getDescriptorResultType() instanceof VerboseDescriptorResult);
	}

}

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

package ambit2.descriptors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.templates.MoleculeFactory;

public class FunctionalGroupDescriptorTest {
	FunctionalGroupDescriptor d; 
	@Before
	public void setUp() throws Exception {
		d = new FunctionalGroupDescriptor();
	}
	
	
	public void testFactory() throws Exception {
		/*
		List<FunctionalGroupDescriptor> list = FuncGroupsDescriptorFactory.create();
		assertNotNull(list);
		assertEquals(114,list.size());
		for (int i =0; i < list.size();i++)  {
			Object[] o = list.get(i).getParameters();
			assertTrue(!"".equals(o[0]));
			assertTrue(!"".equals(o[1]));
			assertNotNull(o[2]);
		}
		*/	
	}	
	@Test
	public void testHashCode() {
		fail("Not yet implemented");
	}

	@Test
	public void testFunctionalGroupDescriptor() throws Exception{
		FunctionalGroupDescriptor newd = new FunctionalGroupDescriptor();
	}

	@Test
	public void testCalculate() throws Exception {
		d.setParameters(new String[] {"CCCC","alkane"});
		IMolecule m = MoleculeFactory.makeAlkane(4);
		DescriptorValue value = d.calculate(m);
		IDescriptorResult v = value.getValue();
		assertTrue(v instanceof VerboseDescriptorResult);
		IDescriptorResult r = ((VerboseDescriptorResult) v).getResult();
		assertEquals(1,((IntegerResult)r).intValue());
		d.setParameters(new String[] {"CCCCCC","alkane"});
		value = d.calculate(m);
		
		v = value.getValue();
		r = ((VerboseDescriptorResult) v).getResult();
		assertEquals(0,((IntegerResult)r).intValue());
		
		d.setParameters(new Object[] {"C","alkane","none",Boolean.TRUE});
		value = d.calculate(m);
		v = value.getValue();
		r = ((VerboseDescriptorResult) v).getResult();

		assertEquals(1,((IntegerResult)r).intValue());		
	}

	@Test
	public void testGetParameterNames() throws Exception {
		String[] params = d.getParameterNames();
		assertEquals(4,params.length);
		assertEquals("smarts",params[0]);
		assertEquals("name",params[1]);
		assertEquals("comment",params[2]);
		assertEquals("verbose",params[3]);

	}

	@Test
	public void testGetParameterType() throws Exception  {
		assertEquals(String.class,d.getParameterType("smarts").getClass());
		assertEquals(String.class,d.getParameterType("name").getClass());
		assertEquals(String.class,d.getParameterType("comment").getClass());

	}

	@Test
	public void testGetSpecification() {
		DescriptorSpecification spec = d.getSpecification();
		assertEquals(FunctionalGroupDescriptor.class.getName(),spec.getImplementationTitle());
		assertEquals("http://ambit.acad.bg/downloads/AmbitDb/html/funcgroups.xml",spec.getSpecificationReference());
	}

	@Test
	public void testSetParameters() throws Exception  {
		d.setParameters(new String[] {"CCCC","alkane"});
		Object[] p = d.getParameters();
		assertEquals("CCCC",p[0]);
		assertEquals("alkane",p[1]);
		assertEquals(null,p[2]);
	}

	@Test
	public void testToString() throws Exception {
		d.setParameters(new String[] {"CCCC","alkane"});
		assertEquals("Functional group: CCCC",d.toString());
	}

	@Test
	public void testGetDescriptorResultType() {
		fail("Not yet implemented");
	}

}

/*
Copyright (C) 2005-2007  

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

package ambit.test.smarts;

import java.util.List;

import junit.framework.TestCase;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit.data.descriptors.FuncGroupsDescriptorFactory;
import ambit.data.descriptors.FunctionalGroupDescriptor;


public class FunctionalGroupDescriptorTest extends TestCase  {
	public void testParams() throws Exception {
		FunctionalGroupDescriptor d = new FunctionalGroupDescriptor();
		String[] params = d.getParameterNames();
		assertEquals(3,params.length);
		assertEquals("smarts",params[0]);
		assertEquals("name",params[1]);
		assertEquals(String.class,d.getParameterType("smarts").getClass());
		d.setParameters(new String[] {"CCCC","alkane"});
		assertEquals("CCCC",d.getParameters()[0]);
	}
	public void testmatch() throws Exception {
		FunctionalGroupDescriptor d = new FunctionalGroupDescriptor();
		d.setParameters(new String[] {"CCCC","alkane"});
		IMolecule m = MoleculeFactory.makeAlkane(4);
		DescriptorValue value = d.calculate(m);
		IDescriptorResult r = value.getValue();
		assertTrue(r instanceof IntegerResult);
		assertEquals(2,((IntegerResult)r).intValue());
		
		d.setParameters(new String[] {"CCCCCC","alkane"});
		value = d.calculate(m);
		r = value.getValue();
		assertEquals(0,((IntegerResult)r).intValue());
	}	
	public void testFactory() throws Exception {
		List<FunctionalGroupDescriptor> list = FuncGroupsDescriptorFactory.create();
		assertNotNull(list);
		assertEquals(114,list.size());
		for (int i =0; i < list.size();i++)  {
			Object[] o = list.get(i).getParameters();
			assertTrue(!"".equals(o[0]));
			assertTrue(!"".equals(o[1]));
			assertNotNull(o[2]);
		}	
	}
	
}



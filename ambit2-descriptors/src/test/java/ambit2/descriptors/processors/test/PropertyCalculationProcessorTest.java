/* PropertyCalculationProcessorTest.java
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

package ambit2.descriptors.processors.test;


import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.base.data.Property;
import ambit2.core.data.StringArrayResult;
import ambit2.descriptors.processors.PropertyCalculationProcessor;

public class PropertyCalculationProcessorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void testProcess() throws Exception {
		PropertyCalculationProcessor p = new PropertyCalculationProcessor();
		Property prop = Property.getInstance("Count","Descriptors");
		prop.setLabel("Count");
		prop.setClazz(Class.forName("org.openscience.cdk.qsar.descriptors.molecular.AtomCountDescriptor"));
		p.setProperty(prop);
		DescriptorValue value = p.process(MoleculeFactory.makeAlkane(10));
		Assert.assertNotNull(value);
		Assert.assertEquals(10,((IntegerResult)value.getValue()).intValue());
	}
	
	@Test
	public void testInChI() throws Exception {
		PropertyCalculationProcessor p = new PropertyCalculationProcessor();
		
		Property prop = Property.getInstance("Count","Descriptors");
		prop.setLabel("Count");
		prop.setClazz(Class.forName("ambit2.descriptors.InChI"));
		p.setProperty(prop);
		DescriptorValue value = p.process(MoleculeFactory.makeAlkane(10));
		Assert.assertNotNull(value);
		Assert.assertTrue(value.getValue() instanceof StringArrayResult);
		StringArrayResult r = (StringArrayResult) value.getValue();
		Assert.assertEquals("InChI=1S/C10H22/c1-3-5-7-9-10-8-6-4-2/h3-10H2,1-2H3", r.get(0));
		Assert.assertEquals("AuxInfo=1/0/N:1,10,2,9,3,8,4,7,5,6/E:(1,2)(3,4)(5,6)(7,8)(9,10)/rA:10CCCCCCCCCC/rB:s1;s2;s3;s4;s5;s6;s7;s8;s9;/rC:;;;;;;;;;;", r.get(1));
		Assert.assertEquals("DIOQZVSQGTUSAI-UHFFFAOYSA-N", r.get(2));
	}	
}

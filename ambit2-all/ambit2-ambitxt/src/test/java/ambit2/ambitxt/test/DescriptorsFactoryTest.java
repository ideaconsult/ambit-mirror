/* DescriptorsFactoryTest.java
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

package ambit2.ambitxt.test;

import java.util.Iterator;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.templates.MoleculeFactory;

import toxTree.tree.cramer.CramerRules;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.descriptors.processors.DescriptorsFactory;

public class DescriptorsFactoryTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProcess() throws Exception {
		DescriptorsFactory factory = new DescriptorsFactory();
		Profile profile = factory.process(null);
		Assert.assertNotNull(profile);

		Iterator<Property> p = profile.getProperties(false);
		while (p.hasNext()) {
			Assert.assertNotNull(p.next().getClazz());
		}
		Assert.assertEquals(55,profile.size());		
	}
	@Test
	public void testCramerAsDescriptor() throws Exception {
		CramerRules rules = new CramerRules();
		DescriptorValue value = rules.calculate(MoleculeFactory.makeAlkane(10));
		Assert.assertTrue(value != null);
		Assert.assertTrue(rules instanceof IMolecularDescriptor);
	}	
	@Test
	public void testVerhaar() throws Exception {
		testToxtree("verhaar.VerhaarScheme");
	}
	@Test
	public void testCramer() throws Exception {
		testToxtree("toxTree.tree.cramer.CramerRules");
	}	
	@Test
	public void testCramer2() throws Exception {
		testToxtree("cramer2.CramerRulesWithExtensions");
	}	

	@Test
	public void testEye() throws Exception {
		testToxtree("eye.EyeIrritationRules");
	}	
	@Test
	public void testSicret() throws Exception {
		testToxtree("sicret.SicretRules");
	}		
	@Test
	public void testMic() throws Exception {
		testToxtree("mic.MICRules");
	}	
	@Test
	public void testMA() throws Exception {
		testToxtree("michaelacceptors.MichaelAcceptorRules");
	}	
	@Test
	public void testBB() throws Exception {
		testToxtree("mutant.BB_CarcMutRules");
	}		
	public void testToxtree(String className) throws Exception {
		Class clazz = DescriptorsFactory.class.getClassLoader().loadClass(className);
		Object r = clazz.newInstance();
		Assert.assertNotNull(r);
		Assert.assertTrue(r instanceof IMolecularDescriptor);
	}	
}

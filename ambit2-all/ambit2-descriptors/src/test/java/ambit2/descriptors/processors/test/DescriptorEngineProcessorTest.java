/* DescriptorEngineProcessorTest.java
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ambit2.descriptors.processors.DescriptorEngineProcessor;

public class DescriptorEngineProcessorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProcess() {
		//fail("Not yet implemented");
		//DescriptorEngine still has troubles loading ambit classes
	}
	@Test
	public void testEngine() throws Exception {
		DescriptorEngineProcessor p = new DescriptorEngineProcessor();
		/*
		for (String s : p.getDescriptorEngine().getAvailableDictionaryClasses())
			System.out.println(s);
			*/
		for (Object s : p.getDescriptorEngine().getDescriptorClassNames())
			System.out.println(s);		
		/*
		for (Object s : p.getDescriptorEngine().getDescriptorInstances())
			System.out.println(s);
			*/
		/*
		List list = p.getDescriptorEngine().getDescriptorClassNameByPackage("ambit2.descriptors",
				 new String[] {"E://Ideaconsult//AmbitXT-v2.00//ambit//ambit2-descriptors-2.0.0-SNAPSHOT.jar"});
				 *
		List list = p.getDescriptorEngine().getDescriptorClassNameByInterface("IMolecularDescriptor",
				 new String[] {"E://Ideaconsult//AmbitXT-v2.00//ambit//ambit2-descriptors-2.0.0-SNAPSHOT.jar"});
				
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() >0);
		for (Object s : list)
			System.out.println(s);			
			 */	
	}
	@Test
	public void testExternalClasses() throws Exception {
		
	}
}

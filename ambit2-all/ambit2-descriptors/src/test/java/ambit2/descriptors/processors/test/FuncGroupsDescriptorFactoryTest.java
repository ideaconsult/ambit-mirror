/* FuncGroupsDescriptorFactoryTest.java
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ambit2.descriptors.FuncGroupsDescriptorFactory;
import ambit2.descriptors.FunctionalGroup;

public class FuncGroupsDescriptorFactoryTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProcess() throws Exception {
		FuncGroupsDescriptorFactory factory = new FuncGroupsDescriptorFactory();
		Collection<FunctionalGroup> list = factory.process(null);
		Assert.assertNotNull(list);
		Assert.assertEquals(84,list.size());
		for (FunctionalGroup group : list)  {
			Assert.assertNotSame("",group.getSmarts());
			Assert.assertNotSame("",group.getName());
			Assert.assertNotSame("",group.getFamily());
		}
			
	}		

	@Test
	public void testSaveFragments() throws Exception {
		FuncGroupsDescriptorFactory factory = new FuncGroupsDescriptorFactory();
		Collection<FunctionalGroup> list = factory.process(null);
		File temp = File.createTempFile("test_save_funcgroups", ".xml");
		OutputStream writer = new FileOutputStream(temp);
		FuncGroupsDescriptorFactory.saveFragments(writer, list);
		writer.flush();
		writer.close();
		Collection<FunctionalGroup> newlist = factory.process(temp.getAbsolutePath());
		Assert.assertEquals(list.size(),newlist.size());
		for (FunctionalGroup group : newlist)  {
			Assert.assertNotSame("",group.getSmarts());
			Assert.assertNotSame("",group.getName());
			Assert.assertNotSame("",group.getFamily());
		}
		temp.delete();
		
	}

}

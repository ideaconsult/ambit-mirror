/* RawIteratingFolderReaderTest.java
 * Author: nina
 * Date: Mar 14, 2009
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

package ambit2.core.test.io;


import java.io.File;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ambit2.core.io.RawIteratingFolderReader;
import ambit2.core.io.ZipReader;

public class RawIteratingFolderReaderTest {
	protected static Logger logger = Logger.getLogger(RawIteratingFolderReaderTest.class.getName());
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void test() throws Exception {
		File[] files = new File[] {
			new File("src/test/resources/ambit2/core/data/mdl/12027-77-9.sdf"),
			new File("src/test/resources/ambit2/core/data/mdl/12040-13-0.sdf"),
			new File("src/test/resources/ambit2/core/data/mdl/12042-37-4.sdf"),
			new File("src/test/resources/ambit2/core/data/mdl/12401-47-7.mol"),			
			new File("src/test/resources/ambit2/core/data/mdl/12401-47-7.sdf"),
			new File("src/test/resources/ambit2/core/data/mdl/polymer.mol"),
						
		};
		int count = 0;
		RawIteratingFolderReader reader = new RawIteratingFolderReader(files);
		while (reader.hasNext()) {
			logger.info(reader.next().toString());
			count++;
		}
		reader.close();
		Assert.assertEquals(6,count);
	}
	
	@Test
	public void testZip() throws Exception {
		File zfile = new File("src/test/resources/ambit2/core/data/zip/test.zip");
		int count = 0;
		ZipReader reader = new ZipReader(zfile);
		while (reader.hasNext()) {
			logger.info(reader.next().toString());
			count++;
		}
		reader.close();
		Assert.assertEquals(2,count);
	}
	
}

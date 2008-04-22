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

/**
 * 
 */
package ambit2.test.repository;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import ambit2.repository.StructureRecord;
import ambit2.repository.processors.EntrezESearchParser;
import ambit2.repository.processors.PUGProcessor;

/**
 * @author nina
 *
 */
public class EntrezESearchParserTest extends TestCase {
	protected EntrezESearchParser parser;
	/**
	 * @param arg0
	 */
	public EntrezESearchParserTest(String arg0) {
		super(arg0);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		parser = new EntrezESearchParser();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		parser = null;
		super.tearDown();
	}
	public void test_single_id() throws Exception {
		List<String> ids = new ArrayList<String>();
		ids.add("7474");
		process("data/pug/100-00-5.xml", 1, 0,ids);
	}
	public void test_two_ids() throws Exception {
		List<String> ids = new ArrayList<String>();
		ids.add("70475");
		ids.add("7919");
		process("data/pug/1000-90-4.xml", 2, 0,ids);
	}
	public void test_notfound() throws Exception {
		List<String> ids = new ArrayList<String>();
		process("data/pug/100019-57-6.xml", 0, 1,ids);
	}		
	public void process(String filename, int numids, int error, List<String> ids) throws Exception {
		FileInputStream in = new FileInputStream(filename);
		List<StructureRecord> records = parser.process(in);
		int cids = 0;
		int err = 0;
		for (StructureRecord record : records) {
			if (PUGProcessor.PUBCHEM_CID.equals(record.getFormat())) {
				cids++;
				assertTrue(ids.indexOf(record.getContent())>-1);
			}
			else if ("PhraseNotFound".equals(record.getFormat())) err++;
		}	
		
		assertEquals(numids,cids);
		assertEquals(err,error);
	}
}



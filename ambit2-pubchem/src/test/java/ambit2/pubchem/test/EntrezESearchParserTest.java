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
package ambit2.pubchem.test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.pubchem.EntrezESearchParser;
import ambit2.pubchem.PUGProcessor;

/**
 * @author nina
 *
 */
public class EntrezESearchParserTest  {
	protected EntrezESearchParser parser;


	@Before
	public void setUp() throws Exception {
		parser = new EntrezESearchParser();
	}

	@After
	public void tearDown() throws Exception {
		parser = null;
	}
	
	@Test
	public void test_single_id() throws Exception {
		List<String> ids = new ArrayList<String>();
		ids.add("7474");
		process("/pug/100-00-5.xml", 1, 0,ids);
	}
	@Test	
	public void test_two_ids() throws Exception {
		List<String> ids = new ArrayList<String>();
		ids.add("70475");
		ids.add("7919");
		process("/pug/1000-90-4.xml", 2, 0,ids);
	}
	@Test	
	public void test_notfound() throws Exception {
		List<String> ids = new ArrayList<String>();
		process("/pug/100019-57-6.xml", 0, 1,ids);
	}	

	public void process(String filename, int numids, int error, List<String> ids) throws Exception {
		System.out.println("ambit2/pubchem"+filename);
		InputStream in =  this.getClass().getClassLoader().getResourceAsStream(
				//"ambit2/core/pubchem/pug/100019-57-6.xml");
				  "ambit2/pubchem"+filename);
		Assert.assertNotNull(in);
		List<IStructureRecord> records = parser.process(in);
		int cids = 0;
		int err = 0;
		for (IStructureRecord record : records) {
			if (PUGProcessor.PUBCHEM_CID.equals(record.getFormat())) {
				cids++;
				Assert.assertTrue(ids.indexOf(record.getContent())>-1);
			}
			else if ("PhraseNotFound".equals(record.getFormat())) err++;
		}	
		
		Assert.assertEquals(numids,cids);
		Assert.assertEquals(err,error);
	}
}



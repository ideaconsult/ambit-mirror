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

import java.io.File;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import ambit2.pubchem.BatchRetrievePubChem;

/**
 * @author nina
 *
 */
public class CAS2SDFbyPubchemTtest  {
	protected static BatchRetrievePubChem batch;

	@BeforeClass public static void setUp() throws Exception {
		batch = new BatchRetrievePubChem();
	}

	
	public void test_einecs_noformula() throws Exception {
		batch.setResultDir("D:/nina/IdeaConsult/AMBIT2/Data cleanup/EINECS/Pubchem/sdf/einecs_structures_V13Apr07_cas_Low");
		/*
		assertEquals(new Integer(7117),
				batch.process("D:/nina/IdeaConsult/AMBIT2/Data cleanup/EINECS/Pubchem/einecs_structures_V13Apr07_PubChem/einecs_structures_V13Apr07_PubChem/einecs_structures_V13Apr07_cas_Low")
				);
*/	
		int r = 1;
		while (r > 0) {
			batch.setResultDir("D:/nina/IdeaConsult/AMBIT2/Data cleanup/EINECS/Pubchem/sdf/einecs_structures_V13Apr07_cas_Low");
			r = batch.process("D:/nina/IdeaConsult/AMBIT2/Data cleanup/EINECS/Pubchem/einecs_structures_V13Apr07_PubChem/einecs_structures_V13Apr07_PubChem/einecs_structures_V13Apr07_cas_Low");
			
			batch.setResultDir("D:/nina/IdeaConsult/AMBIT2/Data cleanup/EINECS/Pubchem/sdf/einecs_structures_V13Apr07_cas_N_A_formula");
			r += batch.process("D:/nina/IdeaConsult/AMBIT2/Data cleanup/EINECS/Pubchem/einecs_structures_V13Apr07_PubChem/einecs_structures_V13Apr07_PubChem/einecs_structures_V13Apr07_cas_N_A_formula");			
				 
			System.out.println("Repeat");
		};
		//12391);
	}
	
	@Test public void test() throws Exception {
		
		new File("ambit2/pubchem/pug/100-00-5.sdf").delete();
		new File("ambit2/pubchem/1000-90-4.sdf").delete();
		new File("ambit2/pubchem/100019-57-6.sdf").delete();
		Assert.assertTrue(new File("ambit2/pubchem/100-00-5.xml").exists());
		Assert.assertTrue(new File("ambit2/pubchem/1000-90-4.xml").exists());
		Assert.assertTrue(new File("ambit2/pubchem/100019-57-6.xml").exists());
		
		batch.setResultDir("ambit2/pubchem");
		Assert.assertEquals(new Integer(3),batch.process("ambit2/pubchem"));
		Assert.assertTrue(new File("ambit2/pubchem/100-00-5.sdf").exists());
		Assert.assertTrue(new File("ambit2/pubchem/1000-90-4.sdf").exists());
		Assert.assertTrue(new File("ambit2/pubchem/100019-57-6.sdf").exists());
	}

}



/*
Copyright (C) 2005-2006  

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

package ambit2.test.database;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.config.AmbitCONSTANTS;
import ambit2.database.core.DbSQL;

public class DbSQLTest extends TestCase {

	@Test
	public void testGetExactSearchSQL() {
		Hashtable<String, String> properties = new Hashtable<String, String>();
		properties.put(CDKConstants.CASRN, "50-00-0");
		List<String> p = new ArrayList<String>();
        String sql = DbSQL.getExactSearchSQL(properties,0,10,0,p);
        assertTrue(!sql.equals(""));
        
        properties.clear();
        p.clear();
        properties.put(AmbitCONSTANTS.SMILES, "CCC");
        sql = DbSQL.getExactSearchSQL(properties,0,10,0,p);
        //System.out.println(sql);
        assertEquals(
        		"SELECT s.idsubstance,c.idstructure,casno,formula,smiles,name.name as chemname,r.name as dataset \n"+ 
        		"FROM  substance as s\n"+
        		"LEFT join structure as c using (idsubstance) \n"+ 
        		"LEFT join cas using (idstructure) \n"+
        		"LEFT JOIN name using (idstructure) \n"+
        		"LEFT JOIN struc_dataset using (idstructure) \n"+
        		"LEFT JOIN src_dataset as r using (id_srcdataset) \n"+
        		"WHERE smiles=? group by idsubstance order by type_structure limit 0,10",
        		sql);        
	}

	public void testGetSimilaritySearchSQL() {
		try {
		List<String> p = new ArrayList<String>();
		IMolecule m = MoleculeFactory.makeBenzene();
        String sql = DbSQL.getSimilaritySearchSQL(m, 0, 100, 0, -1, p);
        System.out.println(sql);
        assertEquals(
	        		"select cbits,bc,? as NA,round(cbits/(bc+?-cbits),2) as similarity,smiles,formula,molweight,L.idsubstance  from (select fp1024.idsubstance,(bit_count(0& fp1) + bit_count(2147483648& fp2) + bit_count(8589934592& fp3) + bit_count(2305843009215791104& fp4) + bit_count(0& fp5) + bit_count(0& fp6) + bit_count(0& fp7) + bit_count(0& fp8) + bit_count(4611686018427387904& fp9) + bit_count(0& fp10) + bit_count(0& fp11) + bit_count(274877906944& fp12) + bit_count(0& fp13) + bit_count(0& fp14) + bit_count(0& fp15) + bit_count(0& fp16))  as cbits,bc from fp1024 ) as L, substance where bc > 0 and cbits > 0 and (cbits/(bc+?-cbits)>?) and L.idsubstance=substance.idsubstance order by similarity desc limit ?,?",
        		sql);    
		} catch (Exception x) {
			x.printStackTrace();
			fail();
		}
	}	
}



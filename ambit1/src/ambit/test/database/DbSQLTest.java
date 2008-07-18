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

package ambit.test.database;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit.database.core.DbSQL;
import ambit.misc.AmbitCONSTANTS;

public class DbSQLTest extends TestCase {

	
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
	
	public void xtestGetSimilaritySearchSQL() throws Exception  {
		List<String> p = new ArrayList<String>();
		IMolecule m = MoleculeFactory.makeBenzene();
        String sql = DbSQL.getSimilaritySearchSQL(m, 0, 100, 0,null, p);
        System.out.println(sql);
        System.out.println(p);
        String sqlResult= "select cbits,bc,?\n as NA,round(cbits/(bc+?-cbits),2) as similarity,smiles,formula,molweight,L.idsubstance  from (\nselect fp1024.idsubstance,(\nbit_count(0& fp1) +\nbit_count(2147483648& fp2) +\nbit_count(8589934592& fp3) +\nbit_count(2305843009215791104& fp4) +\nbit_count(0& fp5) + \nbit_count(0& fp6) +\nbit_count(0& fp7) +\nbit_count(0& fp8) +\nbit_count(4611686018427387904& fp9) +\n bit_count(0& fp10) +\nbit_count(0& fp11) +\nbit_count(274877906944& fp12) +\nbit_count(0& fp13) +\nbit_count(0& fp14) +\n bit_count(0& fp15) +\nbit_count(0& fp16))\n as cbits,bc from fp1024 \n) as L, substance where bc > 0 and cbits > 0 and (cbits/(bc+?-cbits)>?) and L.idsubstance=substance.idsubstance order by similarity desc limit 0,100";
        Assert.assertEquals(sqlResult,sql);
	}
	public void testGetPrescreenSearchSQL() throws Exception  {
		List<String> p = new ArrayList<String>();
		IMolecule m = MoleculeFactory.makeBenzene();
        String sql = DbSQL.getPrescreenSearchSQL(m, 0, 100,null, p);
        System.out.println(sql);
        System.out.println(p);
        String sqlResult= "";
        Assert.assertEquals(sqlResult,sql);
	}	
}



/* QuerySimilarityStructureTest.java
 * Author: nina
 * Date: Apr 7, 2009
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

package ambit2.db.search.test;

import java.sql.ResultSet;
import java.util.List;

import junit.framework.Assert;
import net.idea.modbcum.i.query.QueryParam;

import org.junit.Test;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.db.search.NumberCondition;
import ambit2.db.search.structure.QuerySimilarityStructure;

public class QuerySimilarityStructureTest  extends QueryTest<QuerySimilarityStructure>{

	@Test
	public void testQ() throws Exception {
		/*
		query.setForceOrdering(true);
		Assert.assertEquals("select ? as idquery,s1.idchemical,s1.idstructure,if(s1.type_structure='NA',0,1) as selected,round(cbits/(bc+?-cbits),2) as metric,null as text\nFROM structure s1\n%sjoin (\nselect fp1024.idchemical,(bit_count(? & fp1) + bit_count(? & fp2) + bit_count(? & fp3) + bit_count(? & fp4) +\nbit_count(? & fp5) + bit_count(? & fp6) + bit_count(? & fp7) + bit_count(? & fp8) + bit_count(? & fp9) + bit_count(? & fp10) +\nbit_count(? & fp11) + bit_count(? & fp12) + bit_count(? & fp13) + bit_count(? & fp14) + bit_count(? & fp15) + bit_count(? & fp16)) \n as cbits,bc from fp1024) a on a.idchemical=s1.idchemicalwhere\n%s(cbits/(bc+?-cbits)>?) order by metric desc",
				query.getSQL());
		query.setForceOrdering(false);
		Assert.assertEquals("select ? as idquery,s1.idchemical,s1.idstructure,if(s1.type_structure='NA',0,1) as selected,round(cbits/(bc+?-cbits),2) as metric,null as text\nFROM structure s1\n%sjoin (\nselect fp1024.idchemical,(bit_count(? & fp1) + bit_count(? & fp2) + bit_count(? & fp3) + bit_count(? & fp4) +\nbit_count(? & fp5) + bit_count(? & fp6) + bit_count(? & fp7) + bit_count(? & fp8) + bit_count(? & fp9) + bit_count(? & fp10) +\nbit_count(? & fp11) + bit_count(? & fp12) + bit_count(? & fp13) + bit_count(? & fp14) + bit_count(? & fp15) + bit_count(? & fp16)) \n as cbits,bc from fp1024) a on a.idchemical=s1.idchemicalwhere\n%s(cbits/(bc+?-cbits)>?)\n",
					query.getSQL());
		//select cbits,bc,? as NA,round(cbits/(bc+?-cbits),2) as similarity,smiles,formula,L.idchemical from (select fp1024.idchemical,(bit_count(?& fp1) + bit_count(?& fp2) + bit_count(?& fp3) + bit_count(?& fp4) + bit_count(?& fp5) + bit_count(?& fp6) + bit_count(?& fp7) + bit_count(?& fp8) + bit_count(?& fp9) + bit_count(?& fp10) + bit_count(?& fp11) + bit_count(?& fp12) + bit_count(?& fp13) + bit_count(?& fp14) + bit_count(?& fp15) + bit_count(?& fp16))  as cbits,bc from fp1024 ) as L, chemicals where bc > 0 and cbits > 0 and (cbits/(bc+?-cbits)>?) and L.idchemical=chemicals.idchemical order by similarity desc
		 * */
		 
		List<QueryParam> params = query.getParameters();
		Assert.assertNotNull(params);
		Assert.assertEquals(20,params.size());
	
	}

	@Override
	protected QuerySimilarityStructure createQuery() throws Exception {
		QuerySimilarityStructure qf = new QuerySimilarityStructure();
		qf.setStructure(MoleculeFactory.makeAlkane(3));
		qf.setCondition(NumberCondition.getInstance(">"));
		qf.setThreshold(0.24);
		qf.setId(1);
		return qf;
	}

	@Override
	protected void verify(QuerySimilarityStructure query, ResultSet rs)
			throws Exception {
		while (rs.next()) {
			Assert.assertEquals(query.getId().intValue(),rs.getInt(1));
			Assert.assertEquals(10,rs.getInt(2));
			//Assert.assertEquals(100214,rs.getInt(3));
			Assert.assertEquals(1,rs.getInt(4));
			Assert.assertEquals(0.33,rs.getFloat(5),1E-4);	
			Assert.assertEquals(0.33,rs.getFloat("metric"),1E-4);	
		}
	}
	
	@Test
	public void testSetFieldname() throws Exception {
		QuerySimilarityStructure q = new QuerySimilarityStructure();
		q.setThreshold(0.81);
		q.setForceOrdering(false);
		q.setCondition(NumberCondition.getInstance("<"));
		q.setStructure(MoleculeFactory.makeAlkane(10));
		String similaritySQL = q.getSQL();		
		
		/*
		q.setFieldname(QuerySimilarityStructure.methods[1]);
		Assert.assertEquals(0.81, q.getThreshold());
		Assert.assertEquals(false, q.isForceOrdering());
		Assert.assertEquals(NumberCondition.getInstance("<"), q.getCondition());
		Assert.assertEquals(1,q.getValue().getAtomContainerCount());
		Assert.assertEquals(10,q.getValue().getAtomContainer(0).getAtomCount());
		System.out.println(similaritySQL);
		Assert.assertFalse(similaritySQL.equals(q.getSQL()));
		*/
		
		q.setFieldname(QuerySimilarityStructure.methods[1]);
		//Assert.assertEquals(0.81, q.getThreshold());
		//Assert.assertEquals(false, q.isForceOrdering());
		//Assert.assertEquals(NumberCondition.getInstance("<"), q.getCondition());
		Assert.assertEquals(1,q.getValue().getAtomContainerCount());
		Assert.assertEquals(10,q.getValue().getAtomContainer(0).getAtomCount());
		//Assert.assertFalse(similaritySQL.equals(q.getSQL()));
		//System.out.println(q.getSQL());		
	}
}

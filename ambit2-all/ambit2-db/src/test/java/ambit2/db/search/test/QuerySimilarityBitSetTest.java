package ambit2.db.search.test;

import java.sql.ResultSet;
import java.util.List;

import junit.framework.Assert;
import net.idea.modbcum.i.query.QueryParam;

import org.junit.Test;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.db.search.NumberCondition;
import ambit2.db.search.structure.QuerySimilarityBitset;

/**
 *
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class QuerySimilarityBitSetTest extends QueryTest<QuerySimilarityBitset>  {
	@Test
	public void testQ() throws Exception {
		/*
		query.setForceOrdering(true);
		Assert.assertEquals("select ? as idquery,L.idchemical,L.idstructure,1 as selected,round(cbits/(bc+?-cbits),2) as metric,null as text from\n(select fp1024.idchemical,structure.idstructure,(bit_count(? & fp1) + bit_count(? & fp2) + bit_count(? & fp3) + bit_count(? & fp4) + bit_count(? & fp5) + bit_count(? & fp6) + bit_count(? & fp7) + bit_count(? & fp8) + bit_count(? & fp9) + bit_count(? & fp10) + bit_count(? & fp11) + bit_count(? & fp12) + bit_count(? & fp13) + bit_count(? & fp14) + bit_count(? & fp15) + bit_count(? & fp16))  as cbits,bc from fp1024 join structure using(idchemical) ) as L, chemicals where bc > 0 and cbits > 0 and (cbits/(bc+?-cbits)>?) and L.idchemical=chemicals.idchemical order by metric desc",
				query.getSQL());
		query.setForceOrdering(false);
		Assert.assertEquals("select ? as idquery,L.idchemical,L.idstructure,1 as selected,round(cbits/(bc+?-cbits),2) as metric,null as text from\n(select fp1024.idchemical,structure.idstructure,(bit_count(? & fp1) + bit_count(? & fp2) + bit_count(? & fp3) + bit_count(? & fp4) + bit_count(? & fp5) + bit_count(? & fp6) + bit_count(? & fp7) + bit_count(? & fp8) + bit_count(? & fp9) + bit_count(? & fp10) + bit_count(? & fp11) + bit_count(? & fp12) + bit_count(? & fp13) + bit_count(? & fp14) + bit_count(? & fp15) + bit_count(? & fp16))  as cbits,bc from fp1024 join structure using(idchemical) ) as L, chemicals where bc > 0 and cbits > 0 and (cbits/(bc+?-cbits)>?) and L.idchemical=chemicals.idchemical ",
					query.getSQL());
		//select cbits,bc,? as NA,round(cbits/(bc+?-cbits),2) as similarity,smiles,formula,L.idchemical from (select fp1024.idchemical,(bit_count(?& fp1) + bit_count(?& fp2) + bit_count(?& fp3) + bit_count(?& fp4) + bit_count(?& fp5) + bit_count(?& fp6) + bit_count(?& fp7) + bit_count(?& fp8) + bit_count(?& fp9) + bit_count(?& fp10) + bit_count(?& fp11) + bit_count(?& fp12) + bit_count(?& fp13) + bit_count(?& fp14) + bit_count(?& fp15) + bit_count(?& fp16))  as cbits,bc from fp1024 ) as L, chemicals where bc > 0 and cbits > 0 and (cbits/(bc+?-cbits)>?) and L.idchemical=chemicals.idchemical order by similarity desc
		 * 		 */
		List<QueryParam> params = query.getParameters();
		Assert.assertNotNull(params);
		Assert.assertEquals(20,params.size());
	
	}

	@Override
	protected QuerySimilarityBitset createQuery() throws Exception {
		Fingerprinter fp = new Fingerprinter();
		QuerySimilarityBitset qf = new QuerySimilarityBitset();
		qf.setValue(fp.getFingerprint(MoleculeFactory.makeAlkane(3)));
		qf.setCondition(NumberCondition.getInstance(">"));
		qf.setThreshold(0.24);
		qf.setId(1);
		return qf;
	}

	@Override
	protected void verify(QuerySimilarityBitset query, ResultSet rs)
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
}

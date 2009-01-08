package ambit2.db.search.test;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.db.search.NumberCondition;
import ambit2.db.search.QueryParam;
import ambit2.db.search.QuerySimilarityBitset;

/**
 *
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class QuerySimilarityBitSetTest  {
	@Test
	public void testQ() throws Exception {
		Fingerprinter fp = new Fingerprinter();
		QuerySimilarityBitset qf = new QuerySimilarityBitset();
		qf.setValue(fp.getFingerprint(MoleculeFactory.makeAlkane(10)));
		qf.setCondition(NumberCondition.getInstance(">"));
		qf.setThreshold(0.5);
		qf.setId(1);
		System.out.println(qf.getSQL());
		Assert.assertEquals("select ? as idquery,L.idchemical,L.idstructure,1 as selected,round(cbits/(bc+?-cbits),2) as metric from\n(select fp1024.idchemical,structure.idstructure,(bit_count(? & fp1) + bit_count(? & fp2) + bit_count(? & fp3) + bit_count(? & fp4) + bit_count(? & fp5) + bit_count(? & fp6) + bit_count(? & fp7) + bit_count(? & fp8) + bit_count(? & fp9) + bit_count(? & fp10) + bit_count(? & fp11) + bit_count(? & fp12) + bit_count(? & fp13) + bit_count(? & fp14) + bit_count(? & fp15) + bit_count(? & fp16))  as cbits,bc from fp1024 join structure using(idchemical) ) as L, chemicals where bc > 0 and cbits > 0 and (cbits/(bc+?-cbits)>?) and L.idchemical=chemicals.idchemical order by metric desc",qf.getSQL());
		//select cbits,bc,? as NA,round(cbits/(bc+?-cbits),2) as similarity,smiles,formula,L.idchemical from (select fp1024.idchemical,(bit_count(?& fp1) + bit_count(?& fp2) + bit_count(?& fp3) + bit_count(?& fp4) + bit_count(?& fp5) + bit_count(?& fp6) + bit_count(?& fp7) + bit_count(?& fp8) + bit_count(?& fp9) + bit_count(?& fp10) + bit_count(?& fp11) + bit_count(?& fp12) + bit_count(?& fp13) + bit_count(?& fp14) + bit_count(?& fp15) + bit_count(?& fp16))  as cbits,bc from fp1024 ) as L, chemicals where bc > 0 and cbits > 0 and (cbits/(bc+?-cbits)>?) and L.idchemical=chemicals.idchemical order by similarity desc
		List<QueryParam> params = qf.getParameters();
		Assert.assertNotNull(params);
		Assert.assertEquals(20,params.size());
		/*O
		assertEquals(Integer.class,params.get(0).getType());
		assertEquals(String.class,params.get(1).getType());
		assertEquals(1,params.get(0).getValue());
		assertEquals("CC",params.get(1).getValue());
		*/
	
	}
}

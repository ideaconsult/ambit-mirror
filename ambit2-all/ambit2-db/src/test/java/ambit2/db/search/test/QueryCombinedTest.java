package ambit2.db.search.test;

import java.util.BitSet;
import java.util.List;

import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.QueryCombined;
import ambit2.db.search.QueryParam;
import ambit2.db.search.QuerySimilarityBitset;
import ambit2.db.search.QueryStored;
import ambit2.db.search.QueryStructureByID;
import ambit2.db.test.RepositoryTest;

public class QueryCombinedTest extends RepositoryTest {
	public void testStructure() throws Exception {
		QueryStored qs = new QueryStored();
		qs.setName("test");
		
		QueryCombined qc = new QueryCombined();
		qc.setId(55);
		QuerySimilarityBitset q = new QuerySimilarityBitset();
		FingerprintGenerator gen = new FingerprintGenerator();
		BitSet bitset = gen.process(MoleculeFactory.makeAlkane(10));
		q.setBitset(bitset);
		
		assertNotNull(q.getParameters().get(1).getValue());
		qc.add(q);
		System.out.println(qc.getSQL());
		System.out.println(q.getSQL());
		
	}
	public void test() throws Exception {
		/*
		QueryDataset qs = new QueryDataset();
		qs.setName("EINECS");
		*/
		QueryStored qs = new QueryStored();
		qs.setName("test");
		
		QueryCombined qc = new QueryCombined();
		qc.setId(55);
		QueryStructureByID q = new QueryStructureByID(100);
		q.setCondition(NumberCondition.getInstance("<="));
		
		assertNotNull(q.getParameters().get(1).getValue());
		qc.add(q);

		//between 150 and 200
		QueryStructureByID q1 = new QueryStructureByID(150,200);
		assertNotNull(q1.getParameters().get(1).getValue());
		assertNotNull(q1.getParameters().get(2).getValue());		
		qc.add(q1);
		

		qc.setCombine_as_and(false);
		
		System.out.println(qc.getSQL());
		assertEquals("select ? as idquery,idchemical,idstructure,1 as selected,1 as metric from structure where idstructure <= ?\nunion\nselect ? as idquery,idchemical,idstructure,1 as selected,1 as metric from structure where idstructure between ? and ?",
				qc.getSQL());
		
		assertNotNull(q.getParameters().get(1).getValue());

		
		List<QueryParam> params = qc.getParameters();
		assertNotNull(params);
		
		assertEquals(5,params.size());
		int[] values = {55,100,55,150,200};
		for (int i=0; i < params.size(); i++) {
			assertEquals(Integer.class,params.get(i).getType());
			assertEquals(values[i],params.get(i).getValue());
		}

		qc.setScope(qs);
		System.out.println();
		System.out.println(qc.getSQL());
		
		/*
		qc.setScope(null);
		qc.setCombine_as_and(true);
		assertEquals("select Q1.idquery,s.idchemical,idstructure,Q1.selected as selected,Q1.metric as metric from structure as s\njoin\n(select ? as idquery,idchemical,idstructure,1 as selected,1 as metric from structure where idstructure <= ?)\nas Q1\nusing (idstructure)\njoin\n(select ? as idquery,idchemical,idstructure,1 as selected,1 as metric from structure where idstructure between ? and ?)\nas Q2\nusing (idstructure)",qc.getSQL());
		System.out.println(qc.getSQL());
		
		QueryStored qs = new QueryStored();
		qs.setName("test");
		
		qc.setScope(qs);
		System.out.println(qc.getSQL());
		*/
	}
}
/**
 * select d1.idstructure, 
d1.value, d2.value,d3.value,d4.value,d5.value,d6.value ,distance
from dvalues as d1
join dvalues as d2 using(idstructure) 
join dvalues as d3 using(idstructure) 
join dvalues as d4 using(idstructure) 
join dvalues as d5 using(idstructure) 
join dvalues as d6 using(idstructure) 
join atom_structure as s using(idstructure)
join atom_distance using(iddistance)
where 
d1.iddescriptor=1 and (d1.value between 0 and 2) and
d2.iddescriptor=2 and (d2.value between -11 and -10) and
d3.iddescriptor=3 and (d3.value between 200 and 300) and
d4.iddescriptor=4 and (d4.value between 10 and 11) and
d5.iddescriptor=5 and (d5.value between -2 and -1) and
d6.iddescriptor=6  and (d6.value between -10000 and -9000) and
atom1="C" and atom2="N"  and distance between 2 and 3
order by idstructure
limit 30;
**/
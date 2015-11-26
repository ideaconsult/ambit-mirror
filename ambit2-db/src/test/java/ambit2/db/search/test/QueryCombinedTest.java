package ambit2.db.search.test;

import java.sql.ResultSet;
import java.util.BitSet;
import java.util.List;

import junit.framework.Assert;
import net.idea.modbcum.i.query.QueryParam;

import org.dbunit.database.IDatabaseConnection;
import org.junit.Test;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.templates.MoleculeFactory;

import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.core.processors.structure.key.ExactStructureSearchMode;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.QueryCombined;
import ambit2.db.search.StringCondition;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.search.structure.QueryDataset;
import ambit2.db.search.structure.QueryFieldNumeric;
import ambit2.db.search.structure.QuerySimilarityBitset;
import ambit2.db.search.structure.QueryStructure;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.db.update.storedquery.ReadStoredQuery;

public class QueryCombinedTest extends QueryTest<QueryCombined> {
	@Override
	public void setUp() throws Exception {
		super.setUp();
		setDbFile("src/test/resources/ambit2/db/processors/test/dataset-properties.xml");
	}
	@Test
	public void testStructure() throws Exception {
		
		QueryCombined qc = new QueryCombinedStructure();
		qc.setId(55);
		QuerySimilarityBitset q = new QuerySimilarityBitset();
		FingerprintGenerator gen = new FingerprintGenerator(new Fingerprinter());
		BitSet bitset = gen.process(MoleculeFactory.makeAlkane(10));
		q.setValue(bitset);
		
		Assert.assertNotNull(q.getParameters().get(1).getValue());
		qc.add(q);
		
	}
	
	@Test
	public void testDataset() throws Exception {
		
		QueryCombined qc = new QueryCombinedStructure();
		qc.setId(55);
		QueryStructure qs = new QueryStructure();
		qs.setFieldname(ExactStructureSearchMode.idchemical);
		qs.setValue("10");
		QueryDataset dataset = new QueryDataset();
		dataset.setValue(new SourceDataset("Dataset 1"));
		qc.setScope(dataset);
		qc.add(qs);
		//System.out.println(qc.getSQL());
		
	}	
	@Test
	public void test() throws Exception {
		ReadStoredQuery qs = new ReadStoredQuery();
		qs.setName("test");
		
		QueryCombined qc = new QueryCombinedStructure();
		qc.setId(55);
		QueryStructureByID q = new QueryStructureByID(100);
		q.setCondition(NumberCondition.getInstance("<="));
		
		Assert.assertNotNull(q.getParameters().get(1).getValue());
		qc.add(q);

		//between 150 and 200
		QueryStructureByID q1 = new QueryStructureByID(150,200);
		Assert.assertNotNull(q1.getParameters().get(1).getValue());
		Assert.assertNotNull(q1.getParameters().get(3).getValue());		
		qc.add(q1);
		

		qc.setCombine_as_and(false);
		/*
		Assert.assertEquals("select ? as idquery,idchemical,idstructure,if(type_structure='NA',0,1) as selected,? as metric,? as text from structure where idstructure <= ? order by type_structure desc\nunion\nselect ? as idquery,idchemical,idstructure,if(type_structure='NA',0,1) as selected,? as metric,? as text from structure where idstructure between  ? and ? order by type_structure desc",
				qc.getSQL());
		*/
		Assert.assertNotNull(q.getParameters().get(1).getValue());

		
		List<QueryParam> params = qc.getParameters();
		Assert.assertNotNull(params);
		
		Assert.assertEquals(9,params.size());
		Object[] values = {55,1,null,100,55,1,null,150,200};
		for (int i=0; i < params.size(); i++) {
			if (values[i] != null) {
			Assert.assertEquals(Integer.class,params.get(i).getType());
			Assert.assertEquals(values[i],params.get(i).getValue());
			}
		}

		qc.setScope(qs);
		
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
	@Override
	protected QueryCombined createQuery() throws Exception {
		return this.createQuery(ExactStructureSearchMode.inchi,"InChI=1/C20H20P.BrH/c1-2-21(18-12-6-3-7-13-18,19-14-8-4-9-15-19)20-16-10-5-11-17-20;/h3-17H,2H2,1H3;1H/q+1;/p-1");
	}
	protected QueryCombined createQuery(ExactStructureSearchMode mode, String value) throws Exception {		
		QueryCombined q = new QueryCombinedStructure();
		QueryFieldNumeric d = new QueryFieldNumeric();
		d.setCondition(NumberCondition.getInstance(NumberCondition.between));
		d.setValue(new Double(10));
		d.setMaxValue(new Double(15));
		d.setFieldname(Property.getInstance("Property 1","ref"));
		
		QueryStructure qf = new QueryStructure();
		qf.setFieldname(mode);
		qf.setValue(value);
		
		qf.setCondition(StringCondition.getInstance("="));
		
		q.setScope(null);
		q.setCombine_as_and(true);
		q.add(d);
		q.add(qf);
		return q;
	}
	@Override
	protected void verify(QueryCombined query, ResultSet rs) throws Exception {
		int records = 0;
		while (rs.next()) {
			records ++;
			Assert.assertEquals(query.getId().intValue(),rs.getInt(1));
			Assert.assertEquals(11,rs.getInt(2));
			Assert.assertEquals(100215,rs.getInt(3));
			Assert.assertEquals(12.0,rs.getDouble(5));			

		}
		Assert.assertEquals(1,records);
		
	}
	
	/**
	 * gen = SmilesGenerator.unique().aromatic()
	 * [Br-].[CH]:1:[CH]:[CH]:[C](:[CH]:[CH]1)[P+]([C]:2:[CH]:[CH]:[CH]:[CH]:[CH]2)([C]:3:[CH]:[CH]:[CH]:[CH]:[CH]3)CC
	 * @throws Exception
	 */
	@Test
	public void testSelectBySmiles() throws Exception {
		QueryCombined q = createQuery(ExactStructureSearchMode.smiles,	"[Br-].c1ccc(cc1)[P+](c2ccccc2)(c3ccccc3)CC");
		q.setId(-1);
		setUpDatabase(getDbFile());
		IDatabaseConnection c = getConnection();
		ResultSet rs = null;
		try {
			executor.setConnection(c.getConnection());
			executor.open();
			rs = executor.process(q); 
			Assert.assertNotNull(rs);
			verify(q,rs);
		} finally {
			if (rs != null) rs.close();
			c.close();
		}
	}
}

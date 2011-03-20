package ambit2.db.search.structure.pairwise.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.structure.pairwise.QueryStructurePairsDataset;
import ambit2.db.search.test.QueryTest;

public class PairwiseQueryTest extends QueryTest<QueryStructurePairsDataset> {

	@Override
	public void setUp() throws Exception {

		setDbFile("src/test/resources/ambit2/db/processors/test/src-datasets.xml");
		super.setUp();
	}
	@Override
	protected QueryStructurePairsDataset createQuery() throws Exception {
		QueryStructurePairsDataset query = new QueryStructurePairsDataset();
		SourceDataset dataset = new SourceDataset();
		dataset.setId(1);
		query.setFieldname(dataset);
		query.setValue(dataset);
		return query;
	}

	@Override
	protected void verify(QueryStructurePairsDataset query, ResultSet rs)
			throws Exception {
		int count = 0;
		while (rs.next()) {
			IStructureRecord[] records = query.getObject(rs);
			//System.out.println(String.format("%d,%d",record[0],record[1]));
			Assert.assertTrue(records[0].getIdstructure()>0);
			Assert.assertTrue(records[1].getIdstructure()>0);
			count++;
		}
		Assert.assertEquals(6,count);
		
	}

}

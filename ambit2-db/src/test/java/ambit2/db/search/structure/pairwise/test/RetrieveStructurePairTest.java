package ambit2.db.search.structure.pairwise.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.structure.pairwise.RetrieveStructurePair;
import ambit2.db.search.test.QueryTest;

public class RetrieveStructurePairTest extends QueryTest<RetrieveStructurePair> {

	@Override
	public void setUp() throws Exception {
		setDbFile("src/test/resources/ambit2/db/processors/test/src-datasets.xml");
		super.setUp();
	}
	
	@Override
	protected RetrieveStructurePair createQuery() throws Exception {
		RetrieveStructurePair query = new RetrieveStructurePair();
		IStructureRecord record1 = new StructureRecord();
		record1.setIdstructure(100214);
		query.setFieldname(record1);
		IStructureRecord record2 = new StructureRecord();
		record2.setIdstructure(100215);
		query.setValue(record2);
		return query;
	}


	@Override
	protected void verify(RetrieveStructurePair query, ResultSet rs)
			throws Exception {
		int count = 0;
		while (rs.next()) {
			IStructureRecord[] records = query.getObject(rs);
			//System.out.println(String.format("%d,%d",record[0],record[1]));
			Assert.assertTrue(records[0].getIdstructure()>0);
			Assert.assertNotNull(records[0].getContent());
			Assert.assertFalse("".equals(records[0].getContent()));
			Assert.assertTrue(records[1].getIdstructure()>0);
			Assert.assertNotNull(records[1].getContent());
			Assert.assertFalse("".equals(records[1].getContent()));
			count++;
		}
		Assert.assertEquals(1,count);
		
	}

}

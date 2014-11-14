package ambit2.db.search.dataset.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import net.idea.modbcum.i.IQueryRetrieval;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.structure.QueryDataset;
import ambit2.db.search.test.QueryTest;

public class QueryDatasetByNameTest  extends QueryTest<IQueryRetrieval<IStructureRecord>>{

	@Override
	public void setUp() throws Exception {
		super.setUp();
		setDbFile("src/test/resources/ambit2/db/processors/test/src-datasets.xml");
	}
	@Override
	protected IQueryRetrieval<IStructureRecord> createQuery() throws Exception {
		return new QueryDataset(new SourceDataset("Dataset 1"));
	}

	@Override
	protected void verify(IQueryRetrieval<IStructureRecord> query, ResultSet rs)
			throws Exception {
		int count = 0;
		while (rs.next()) {
			IStructureRecord record = query.getObject(rs);
			Assert.assertTrue((record.getIdstructure()==100211) || (record.getIdstructure()==100215));
			count++;
			//assertEquals(query.getValue(),rs.getInt(2));
		}
		Assert.assertEquals(2,count);
		
	}

}

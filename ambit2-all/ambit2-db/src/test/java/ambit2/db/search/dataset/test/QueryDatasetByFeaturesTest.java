package ambit2.db.search.dataset.test;

import java.sql.ResultSet;

import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.test.QueryTest;
import ambit2.db.update.dataset.QueryDatasetByFeatures;

public class QueryDatasetByFeaturesTest  extends QueryTest<IQueryRetrieval<SourceDataset>>{
	//TODO proper dataset content for testing
	@Override
	public void setUp() throws Exception {
		super.setUp();
		dbFile = "src/test/resources/ambit2/db/processors/test/src-datasets.xml";
	}
	@Override
	protected IQueryRetrieval<SourceDataset> createQuery() throws Exception {
		QueryDatasetByFeatures query = new QueryDatasetByFeatures();
		Property p = new Property("Property 1");
		p.setClazz(null);
		p.setLabel(null);
		query.setFieldname(p);
		return query;
	}

	@Override
	protected void verify(IQueryRetrieval<SourceDataset> query, ResultSet rs)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}

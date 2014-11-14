package ambit2.db.search.dataset.test;

import java.sql.ResultSet;

import net.idea.modbcum.i.IQueryRetrieval;
import ambit2.base.data.ISourceDataset;
import ambit2.base.data.Property;
import ambit2.db.search.test.QueryTest;
import ambit2.db.update.dataset.QueryDatasetByFeatures;

public class QueryDatasetByFeaturesTest  extends QueryTest<IQueryRetrieval<ISourceDataset>>{
	//TODO proper dataset content for testing
	@Override
	public void setUp() throws Exception {
		super.setUp();
		setDbFile("src/test/resources/ambit2/db/processors/test/src-datasets.xml");
	}
	@Override
	protected IQueryRetrieval<ISourceDataset> createQuery() throws Exception {
		QueryDatasetByFeatures query = new QueryDatasetByFeatures();
		Property p = new Property("Property 1");
		p.setClazz(null);
		p.setLabel(null);
		query.setFieldname(p);
		return query;
	}

	@Override
	protected void verify(IQueryRetrieval<ISourceDataset> query, ResultSet rs)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}

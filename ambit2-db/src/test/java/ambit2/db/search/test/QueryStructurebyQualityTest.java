package ambit2.db.search.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.junit.Test;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.QLabel;
import ambit2.base.data.QLabel.QUALITY;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.structure.QueryStructureByQuality;

public class QueryStructurebyQualityTest extends QueryTest<QueryStructureByQuality> {

	@Override
	protected QueryStructureByQuality createQuery() throws Exception {
		QueryStructureByQuality q = new QueryStructureByQuality();
		q.setValue(new QLabel(QUALITY.OK));
		ISourceDataset d = new StoredQuery(2);
		d.setID(2);
		q.setFieldname(d);
		return q;
	}

	@Override
	protected void verify(QueryStructureByQuality query, ResultSet rs)
			throws Exception {
		while (rs.next()) {
			IStructureRecord record = query.getObject(rs);
			Assert.assertEquals(10,record.getIdchemical());
			Assert.assertEquals(100214,record.getIdstructure());
			Assert.assertEquals(new QLabel(QUALITY.OK),query.retrieveValue(rs));
		}
		
	}
	
	protected QueryStructureByQuality createQueryNull() throws Exception {
		QueryStructureByQuality q = new QueryStructureByQuality();
		return q;
	}	
	
	protected void verifyNull(QueryStructureByQuality query, ResultSet rs)
			throws Exception {
		int count = 0;
		while (rs.next()) {
			Assert.assertNull(query.retrieveValue(rs));
			count++;
		}
		Assert.assertEquals(3,count);
	}
		
	@Test
	public void testSelectNull() throws Exception {
		setUpDatabase(getDbFile());
		IDatabaseConnection c = getConnection();
		executor.setConnection(c.getConnection());
		executor.open();
		QueryStructureByQuality q = createQueryNull();
		ResultSet rs = executor.process(q); 
		Assert.assertNotNull(rs);
		verifyNull(q,rs);
		rs.close();
		c.close();
	}

}

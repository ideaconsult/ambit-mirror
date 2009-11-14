package ambit2.db.search.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.base.data.Template;
import ambit2.db.SessionID;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.update.assessment.ReadAssessment;

public class ReadAssessmentTest extends QueryTest<IQueryRetrieval<SessionID>>{
	@Override
	public void setUp() throws Exception {
		super.setUp();
			
	}	
	@Override
	protected IQueryRetrieval<SessionID> createQuery() throws Exception {
		dbFile = "src/test/resources/ambit2/db/processors/test/query-datasets-string.xml";	
		setUpDatabase(dbFile);
		SessionID id = new SessionID(1);
		id.setName("test");
		return new ReadAssessment(null,id);
	}

	@Override
	protected void verify(IQueryRetrieval<SessionID> query, ResultSet rs)
			throws Exception {
		int records = 0;
		while (rs.next()) {
			records ++;
			Assert.assertEquals(1,rs.getInt(1));
			Assert.assertEquals("test",rs.getString(2));

		}
		Assert.assertEquals(1,records);
		
	}
}

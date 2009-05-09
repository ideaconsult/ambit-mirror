package ambit2.db.search.test;

import java.sql.ResultSet;

import org.dbunit.database.IDatabaseConnection;
import org.junit.Test;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.SessionID;
import ambit2.db.processors.ProcessorCreateQuery;
import ambit2.db.processors.ProcessorCreateSession;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.structure.QuerySMARTS;
import ambit2.descriptors.FunctionalGroup;

public class QuerySmartsTest extends QueryTest<QuerySMARTS> {

	@Override
	protected QuerySMARTS createQuery() throws Exception {
		QuerySMARTS q = new QuerySMARTS();
		q.setValue(new FunctionalGroup("Br","Br",""));
		q.setId(999);
		return q;
	}

	@Test
	public void testSelect() throws Exception {
		setUpDatabase(dbFile);
		IDatabaseConnection c = getConnection();
		ProcessorCreateSession p = new ProcessorCreateSession();
		p.setConnection(c.getConnection());
		SessionID id = p.process(null);
		c = getConnection();
		ProcessorCreateQuery q = new ProcessorCreateQuery();
		q.setConnection(c.getConnection());
		q.open();
		q.setSession(id);
		IStoredQuery sq = q.process(query);
		p.close();
		q.close();
		c.close();
	}
	
	@Override
	protected void verify(QuerySMARTS query, ResultSet rs) throws Exception {
		while (rs.next()) {
			/*
			Assert.assertEquals(query.getId().intValue(),rs.getInt(1));
			Assert.assertEquals(10,rs.getInt(2));
			Assert.assertEquals(100214,rs.getInt(3));
			Assert.assertEquals(1,rs.getInt(4));
			Assert.assertEquals(0.25,rs.getFloat(5),1E-4);	
			Assert.assertEquals(0.25,rs.getFloat("metric"),1E-4);
			*/
			IStructureRecord r = query.getObject(rs);
			System.out.println(r.getContent());
		}
		
	}


}

package ambit2.db.search.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.base.data.QLabel;
import ambit2.base.data.QLabel.QUALITY;
import ambit2.base.data.StructureRecord;
import ambit2.db.search.NumberCondition;
import ambit2.db.search.qlabel.QueryQLabel;

public class QueryQLabelTest extends QueryTest<QueryQLabel> {

	@Override
	protected QueryQLabel createQuery() throws Exception {
		QueryQLabel q = new QueryQLabel();
		q.setCondition(NumberCondition.getInstance("="));
		q.setValue(new StructureRecord(11,100214,null,null));
		return q;
	}

	@Override
	protected void verify(QueryQLabel query, ResultSet rs) throws Exception {
		while (rs.next()) {
			QLabel q = query.getObject(rs);
			Assert.assertEquals(QUALITY.OK,q.getLabel());
			Assert.assertEquals("guest",q.getUser().getName());
		}
		
	}

}

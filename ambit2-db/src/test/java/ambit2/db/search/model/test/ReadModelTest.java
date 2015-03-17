package ambit2.db.search.model.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.db.search.test.QueryTest;
import ambit2.db.update.model.ReadModel;

public class ReadModelTest extends QueryTest<ReadModel> {

    @Override
    protected ReadModel createQuery() throws Exception {
	ReadModel q = new ReadModel(100);
	return q;
    }

    @Override
    protected void verify(ReadModel query, ResultSet rs) throws Exception {
	int count = 0;
	while (rs.next()) {
	    ModelQueryResults record = query.getObject(rs);
	    Assert.assertEquals("Model", record.getName());
	    Assert.assertEquals("A123 B456", record.getParameters()[0]);
	    count++;
	}
	Assert.assertEquals(1, count);

    }


}

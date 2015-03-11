package ambit2.db.search.bundle.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.junit.Test;

import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.search.test.QueryTest;
import ambit2.db.update.bundle.ReadBundle;
import ambit2.db.update.bundle.ReadBundleVersion;

public class ReadBundleTest extends QueryTest<ReadBundle> {

    @Override
    protected ReadBundle createQuery() throws Exception {
	ReadBundle q = new ReadBundle();
	SubstanceEndpointsBundle b = new SubstanceEndpointsBundle();
	b.setID(1);
	q.setValue(b);
	return q;
    }

    @Override
    protected void verify(ReadBundle query, ResultSet rs) throws Exception {
	int count = 0;
	while (rs.next()) {
	    SubstanceEndpointsBundle record = query.getObject(rs);
	    Assert.assertEquals(1, record.getID());
	    Assert.assertEquals("Assessment", record.getName());
	    Assert.assertEquals("http://creativecommons.org/publicdomain/zero/1.0/", record.getLicenseURI());
	    Assert.assertEquals("guest", record.getUserName());
	    Assert.assertEquals(1, record.getVersion());
	    Assert.assertEquals("efdb21bb-e79f-3286-a988-b6f6944d3734", record.getBundle_number().toString());
	    count++;
	}
	Assert.assertEquals(1, count);

    }


    protected ReadBundle createQueryVersion() throws Exception {
	ReadBundleVersion q = new ReadBundleVersion();
	SubstanceEndpointsBundle b = new SubstanceEndpointsBundle();
	b.setID(1);
	q.setValue(b);
	return q;
    }
    @Test
    public void testSelectVersion() throws Exception {
	setUpDatabase(getDbFile());
	IDatabaseConnection c = getConnection();
	ResultSet rs = null;
	try {
	    executor.setConnection(c.getConnection());
	    executor.open();
	    ReadBundle q = createQueryVersion();
	    rs = executor.process(q);
	    Assert.assertNotNull(rs);
	    verify(q, rs);
	} finally {
	    if (rs != null)
		rs.close();
	    c.close();
	}
    }
}

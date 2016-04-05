package ambit2.db.search.bundle.test;

import java.sql.ResultSet;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.junit.Test;

import ambit2.db.search.test.QueryTest;
import ambit2.db.update.bundle.BundleOwnerByNumber;

/**
 * test for {@link BundleOwnerByNumber}
 * 
 * @author nina
 * 
 */
public class ReadBundleOwnership extends QueryTest<BundleOwnerByNumber> {
	private static final String test_number = "efdb21bb-e79f-3286-a988-b6f6944d3734";

	@Override
	protected BundleOwnerByNumber createQuery() throws Exception {
		BundleOwnerByNumber q = new BundleOwnerByNumber();
		q.setFieldname(test_number);
		q.setValue("guest");
		return q;
	}

	@Override
	protected void verify(BundleOwnerByNumber query, ResultSet rs)
			throws Exception {
		int count = 0;
		while (rs.next()) {
			Assert.assertEquals(new Integer(1), query.getObject(rs));
			count++;
		}
		Assert.assertEquals(1, count);
	}

	@Test
	public void testSelectNotOwner() throws Exception {
		setUpDatabaseFromResource(getDbFile());
		IDatabaseConnection c = getConnection();
		ResultSet rs = null;
		try {
			BundleOwnerByNumber q = new BundleOwnerByNumber();
			q.setFieldname(test_number);
			q.setValue("admin");
			query = q;
			executor.setConnection(c.getConnection());
			executor.open();
			rs = executor.process(query);
			Assert.assertNotNull(rs);
			Assert.assertTrue(query.getObject(rs)<0);
		} finally {
			if (rs != null)
				rs.close();
			c.close();
		}
	}
}

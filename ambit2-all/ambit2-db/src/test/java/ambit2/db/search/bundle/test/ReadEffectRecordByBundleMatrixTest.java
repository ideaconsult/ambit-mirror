package ambit2.db.search.bundle.test;

import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.ProtocolEffectRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.search.test.QueryTest;
import ambit2.db.update.bundle.matrix.ReadEffectRecordByBundleMatrix;
import ambit2.db.update.bundle.matrix.ReadEffectRecordByBundleMatrix._matrix;

public class ReadEffectRecordByBundleMatrixTest extends
		QueryTest<ReadEffectRecordByBundleMatrix> {

	@Override
	protected ReadEffectRecordByBundleMatrix createQuery() throws Exception {
		SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle();
		bundle.setID(1);
		ReadEffectRecordByBundleMatrix q = new ReadEffectRecordByBundleMatrix(bundle,
				_matrix.matrix_working);
		SubstanceRecord record = new SubstanceRecord();
		record.setSubstanceUUID("IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734");
		q.setFieldname(record);
		return q;
	}

	@Override
	public void setUpDatabaseFromResource(String resource) throws Exception {
		super.setUpDatabaseFromResource(resource);
		IDatabaseConnection c = getConnection();
		Statement t = c.getConnection().createStatement();
		try {
			t.executeUpdate("update bundle_substance_protocolapplication set interpretation_result='test',deleted=1,remarks='test' where idbundle=1");
		} finally {
			t.close();
		}
		org.dbunit.dataset.ITable table = c
				.createQueryTable(
						"EXPECTED",
						"SELECT deleted from bundle_substance_protocolapplication where idbundle=1 and deleted=1");
		Assert.assertEquals(1, table.getRowCount());
		c.close();

	}

	@Override
	protected void verify(ReadEffectRecordByBundleMatrix query, ResultSet rs)
			throws Exception {
		int count = 0;
		while (rs.next()) {
			ProtocolEffectRecord<String, String, String> record = query
					.getObject(rs);
			Assert.assertNotNull(record.getEndpoint());
			System.out.println(record.getIdresult());
			count++;
		}
		Assert.assertEquals(1, count);

	}

}

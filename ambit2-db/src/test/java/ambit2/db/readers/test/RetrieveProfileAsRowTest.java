package ambit2.db.readers.test;

import java.sql.ResultSet;

import junit.framework.Assert;
import net.idea.modbcum.i.IQueryRetrieval;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.RetrieveProfileValuesAsRow;

public class RetrieveProfileAsRowTest extends RetrieveTest<IStructureRecord> {

	@Override
	protected String getTestDatabase() {
		return "ambit2/db/processors/test/dataset-properties.xml";
	}

	@Override
	protected IQueryRetrieval<IStructureRecord> createQuery() {
		Profile<Property> profile = new Profile<Property>();

		for (int i = 1; i < 5; i++) {
			// for the test only id matters
			Property p = Property.getInstance(String.format("test%d", i),
					String.format("test%d", i));
			p.setId(i);
			p.setEnabled(true);
			profile.add(p);
		}

		RetrieveProfileValuesAsRow q = new RetrieveProfileValuesAsRow();
		q.setChemicalsOnly(false);
		q.setFieldname(profile);
		q.setValue(new int[] { 100211, 100214, 100215, 129345 });
		return q;
	}

	@Override
	protected void verifyRows(IQueryRetrieval<IStructureRecord> query,
			ResultSet rows) throws Exception {
		// IDatabaseConnection c = getConnection();
		Assert.assertNotNull(rows);
		// Assert.assertEquals(4,rows.size());
		while (rows.next()) {
			IStructureRecord r = query.getObject(rows);
			logger.fine(r.toString());
			for (Property p : r.getRecordProperties()) {
				logger.fine(p.toString());
				logger.fine(r.getRecordProperty(p).toString());
			}

		}
	}

}

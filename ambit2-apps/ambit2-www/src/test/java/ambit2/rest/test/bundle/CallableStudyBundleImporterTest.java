package ambit2.rest.test.bundle;

import java.io.File;

import junit.framework.Assert;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.db.CreateDatabaseProcessor;
import net.idea.restnet.db.test.DbUnitTest;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.rest.dataset.DatasetURIReporter;
import ambit2.rest.substance.CallableStudyBundleImporter;
import ambit2.rest.substance.CallableStudyBundleImporter._mode;
import ambit2.rest.substance.SubstanceURIReporter;
import ambit2.rest.task.TaskResult;
import ambit2.rest.test.CreateAmbitDatabaseProcessor;

public class CallableStudyBundleImporterTest extends DbUnitTest {

	protected String dbFile = "src/test/resources/descriptors-datasets.xml";

	@Override
	protected CreateDatabaseProcessor getDBCreateProcessor() {
		return new CreateAmbitDatabaseProcessor();
	}

	@Override
	public String getDBTables() {
		return "src/test/resources/tables.xml";
	}

	@Override
	protected String getConfig() {
		return "ambit2/rest/config/ambit2.pref";
	}

	@Test
	public void test() throws Exception {
		setUpDatabase(dbFile);
		IDatabaseConnection c = getConnection();
		IDatabaseConnection c1 = getConnection();

		Form form = new Form();
		form.add(
				"substance_uri",
				"http://localhost:8081/ambit2/substance/IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734");
		form.add("tag", "CM");
		form.add("remarks", "remark");

		try {
			String ref = "http://localhost:8081/ambit2";
			Reference rootref = new Reference(ref);
			SubstanceEndpointsBundle bundle = new SubstanceEndpointsBundle(1);
			SubstanceURIReporter<IQueryRetrieval<SubstanceRecord>> reporter = new SubstanceURIReporter<IQueryRetrieval<SubstanceRecord>>(
					rootref);
			DatasetURIReporter dreporter = new DatasetURIReporter(
					rootref);
			File file = new File(getClass().getClassLoader()
					.getResource("matrixvalue_delete.json").getFile());
			CallableStudyBundleImporter callable = new CallableStudyBundleImporter(
					_mode.matrixvaluedelete, file, rootref, null, reporter,
					dreporter, null, ref);
			TaskResult task = callable.call();
			ITable table = c1
					.createQueryTable(
							"EXPECTED",
							String.format("SELECT idbundle,idsubstance,tag,remarks from bundle_substance"));
			Assert.assertEquals(1, table.getRowCount());
			Assert.assertEquals("CM", table.getValue(0, "tag"));
			Assert.assertEquals("remark", table.getValue(0, "remarks"));

		} catch (Exception x) {
			throw x;
		} finally {
			try {
				c.close();
			} catch (Exception x) {
			}
			try {
				c1.close();
			} catch (Exception x) {
			}
		}
	}

}

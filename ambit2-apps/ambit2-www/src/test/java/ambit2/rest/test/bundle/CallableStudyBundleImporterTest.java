package ambit2.rest.test.bundle;

import net.idea.restnet.db.CreateDatabaseProcessor;
import net.idea.restnet.db.test.DbUnitTest;
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

}

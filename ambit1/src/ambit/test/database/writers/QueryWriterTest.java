package ambit.test.database.writers;

import junit.framework.TestCase;
import ambit.database.DbConnection;
import ambit.database.search.DbQueryReader;
import ambit.database.writers.QueryResults;
import ambit.database.writers.QueryWriter;
import ambit.test.ITestDB;

public class QueryWriterTest extends TestCase {
    public void test() throws Exception {
        	DbConnection dbconn = new DbConnection(ITestDB.host,"3306","repdose","guest","guest");
			dbconn.open();
			QueryResults query = new QueryResults();
			query.setId(33);
			query.setOverwrite(false);
		    QueryWriter writer = new QueryWriter(query,dbconn);
		    for (int i=10; i < 20; i++)
		    	try {
		    		writer.write(i,null);
		    	} catch (Exception x) {
		    		x.printStackTrace();
		    	}
		    writer.close();
		    
		    DbQueryReader reader = new DbQueryReader(dbconn.getConn(),query,0,1000);
		    int count = 0;
		    while (reader.hasNext()) {
		    	Object o = reader.getStructure();
		    	count++;
		    }
		    assertEquals(10,count);
		    reader.close();
		    dbconn.close();
    }
}

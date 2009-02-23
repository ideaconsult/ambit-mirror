package ambit2.plugin.dbtools.test;



import org.junit.Test;

import ambit2.plugin.dbtools.MysqlServerLauncher;
import ambit2.workflow.DBWorkflowContext;

public class MysqlServerLauncherTest  {


	/*
	public void testPing() throws Exception {
		IDatabaseConnection c = getConnection();
		if (c.getConnection() instanceof com.mysql.jdbc.Connection) {
			((com.mysql.jdbc.Connection)c.getConnection()).ping();
		}
		c.close();
		
	}
	*/
	@Test
	public void test() {
		
	}
	public static void main(String[] args) {
		MysqlServerLauncher w = new MysqlServerLauncher();
		DBWorkflowContext context = new DBWorkflowContext();
		w.executeWith(context);		
	}

}

package ambit2.plugin.dbtools.test;



import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import junit.framework.Assert;

import org.dbunit.database.IDatabaseConnection;
import org.junit.Test;

import ambit2.db.processors.MySQLCommand;
import ambit2.plugin.dbtools.MySQLServerStop;
import ambit2.plugin.dbtools.MysqlServerLauncher;
import ambit2.workflow.DBWorkflowContext;

public class MysqlServerLauncherTest extends DbUnitTest {


	
	public boolean testPing() throws Exception {
		IDatabaseConnection c = null;
		try {
			c = getConnection();
			if (c.getConnection() instanceof com.mysql.jdbc.Connection) 
			((com.mysql.jdbc.Connection)c.getConnection()).ping();
			return true;
		} catch (Exception x) {
			return false;
		} finally {
			try {
			c.close();
			} catch (Exception x) {}
		}
		
		
	}
	
	@Test
	public void test() {
		
	}

	
	public void testLauncher() throws Exception {
		MysqlServerLauncher start = new MysqlServerLauncher();
		MySQLServerStop stop = new MySQLServerStop();
		DBWorkflowContext context = new DBWorkflowContext();
		MySQLCommand cmd = new MySQLCommand();
		cmd.setMysqlPath("E:/Ideaconsult/AmbitXT-v2.00/mysql");
		context.put(MySQLCommand.MYSQLCOMMAND, cmd);
		context.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println(evt.getNewValue());
				
			}
		});
		start.executeWith(context);		
		if (testPing())
			stop.executeWith(context);		
		if (testPing())
			System.out.println("Error");
		start.executeWith(context);		
		if (cmd.getProcess() != null)
			stop.executeWith(context);
	}
	public static void main(String[] args) {
		MysqlServerLauncherTest test = new MysqlServerLauncherTest();
		try {
			test.testLauncher();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

}

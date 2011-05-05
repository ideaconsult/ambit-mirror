package ambit2.plugin.dbtools;

import java.sql.Connection;
import java.sql.Statement;

import ambit2.base.config.Preferences;
import ambit2.db.pool.DatasourceFactory;
import ambit2.db.processors.MySQLCommand;
import ambit2.db.processors.MySQLShell;
import ambit2.dbui.LoginInfoBean;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.ProcessorPerformer;

import com.microworkflow.process.Conditional;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.TestCondition;
import com.microworkflow.process.Workflow;

/**
 * Starts and stops local MySQL server. 
 * @author nina
 *
 */
public class MysqlServerLauncher extends Workflow {
	
	public MysqlServerLauncher() {
		Preferences.setProperty(Preferences.USER,"guest");
		Preferences.setProperty(Preferences.PASSWORD,"guest");
       
    	Primitive<MySQLCommand,MySQLCommand> start = new Primitive<MySQLCommand,MySQLCommand>( 
    			MySQLCommand.MYSQLCOMMAND,
    			MySQLCommand.MYSQLCOMMAND,
    			new ProcessorPerformer<MySQLShell, MySQLCommand,MySQLCommand>(
    					new MySQLShell()) {
    				@Override
    				public MySQLCommand execute() throws Exception {
    					MySQLCommand cmd = getTarget();
    					if (cmd == null)
    						cmd = new MySQLCommand();
   						cmd.setCommand(MySQLCommand.COMMAND.START);
   						context.put(MySQLCommand.MYSQLCOMMAND,cmd);
    					cmd =  super.execute();
    					if (cmd.getException() != null)
    						context.put(DBWorkflowContext.ERROR, cmd.getException());
    					else
    						context.put(DBWorkflowContext.BATCHSTATS, "MySQL started");

    					return cmd;
    				}
    			}
    			);
        start.setName("Start MySQL");    
        
        Conditional connected = new Conditional(
                new TestCondition() {
                    public boolean evaluate() {
                    	LoginInfoBean li = new LoginInfoBean();
	    				li.setPort("3306");
	    				li.setUser("guest");
	    				li.setPassword("guest");
                        return ping(li);
                    }
                }, 
                null,
                start);
        connected.setName("Is MySQL running already?");
        
        setDefinition(connected);
	}
	
    public static boolean ping(LoginInfoBean li) {
    	Connection connection = null;
    	Statement st = null;
    	try {
	        String dburi = DatasourceFactory.getConnectionURI(
	                    li.getScheme(), li.getHostname(), li.getPort(), 
	                    li.getDatabase(), li.getUser(), li.getPassword());    	    					
	
	        connection = DatasourceFactory.getConnection(dburi.toString());
	        st = connection.createStatement();
	        st.execute("/*ping*/SELECT 1");
	        return true;    
    	} catch (Exception x) {
    		return false;
    	} finally {
    		if (st != null) try {st.close();} catch (Exception x) {};
            if (connection != null) try {connection.close();} catch (Exception x) {};
    	}
    }	
	
}

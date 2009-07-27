package ambit2.plugin.dbtools;

import ambit2.base.config.Preferences;
import ambit2.db.DatasourceFactory;
import ambit2.db.LoginInfo;
import ambit2.db.processors.MySQLCommand;
import ambit2.db.processors.MySQLShell;
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
	    				LoginInfo li = new LoginInfo();
	    				li.setPort("3306");
	    				li.setUser("guest");
	    				li.setPassword("guest");
                        return DatasourceFactory.ping(li);
                    }
                }, 
                null,
                start);
        connected.setName("Is MySQL running already?");
        
        setDefinition(connected);
	}
	
}

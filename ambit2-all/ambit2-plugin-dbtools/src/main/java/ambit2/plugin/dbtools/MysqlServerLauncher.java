package ambit2.plugin.dbtools;

import ambit2.core.config.Preferences;
import ambit2.db.DatasourceFactory;
import ambit2.db.LoginInfo;
import ambit2.db.processors.MySQLCommand;
import ambit2.db.processors.MySQLShell;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.ProcessorPerformer;

import com.microworkflow.process.Conditional;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.TestCondition;
import com.microworkflow.process.Workflow;

/**
 * Starts and stops local MySQL server. 
 * @author nina
 *
 */
public class MysqlServerLauncher extends Workflow {
	public static String MYSQLCOMMAND = "ambit2.db.processors.MySQLCommand";
	public MysqlServerLauncher() {
		Preferences.setProperty(Preferences.USER,"guest");
		Preferences.setProperty(Preferences.PASSWORD,"guest");
        Sequence seq=new Sequence();
        seq.setName("MySQL server");        	

       
    	Primitive<MySQLCommand,MySQLCommand> start = new Primitive<MySQLCommand,MySQLCommand>( 
    			MYSQLCOMMAND,
    			MYSQLCOMMAND,
    			new ProcessorPerformer<MySQLShell, MySQLCommand,MySQLCommand>(
    					new MySQLShell()) {
    				@Override
    				public MySQLCommand execute() throws Exception {
   						MySQLCommand cmd = new MySQLCommand();
   						cmd.setCommand(MySQLCommand.COMMAND.START);
   						context.put(MYSQLCOMMAND,cmd);
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
        
    	Primitive<MySQLCommand,MySQLCommand> stop = new Primitive<MySQLCommand,MySQLCommand>( 
    			MYSQLCOMMAND,
    			MYSQLCOMMAND,
    			new ProcessorPerformer<MySQLShell, MySQLCommand,MySQLCommand>(
    					new MySQLShell()) {
    				@Override
    				public MySQLCommand execute() throws Exception {
   						MySQLCommand cmd = new MySQLCommand();
   						cmd.setCommand(MySQLCommand.COMMAND.START);
   						context.put(MYSQLCOMMAND,cmd);
      					cmd =  super.execute();
    					if (cmd.getException() != null)
    						context.put(DBWorkflowContext.ERROR, cmd.getException());
    					else
    						context.put(DBWorkflowContext.BATCHSTATS, "MySQL stopped");    						
    					return cmd;
    				}
    			}
    			);
        stop.setName("Stop MySQL");        
        
        Conditional canStop = new Conditional(
                new TestCondition() {
                    public boolean evaluate() {
                    	
    					Object target = context.get(MYSQLCOMMAND);
    					if (target != null) {
    						MySQLCommand mc = (MySQLCommand) target;
    						if (mc.getProcess()!=null) {
    							mc.setCommand(MySQLCommand.COMMAND.STOP);	
    							return true;
    						} 
    					}
    					context.put(DBWorkflowContext.ERROR, "Can't stop MySQL");
    					return false;
                    }
                }, 
                stop,
                null);
        canStop.setName("MySQL was started internally?");
        
        Conditional connected = new Conditional(
                new TestCondition() {
                    public boolean evaluate() {
	    				LoginInfo li = new LoginInfo();
	    				li.setPort("33060");
	    				li.setUser("guest");
	    				li.setPassword("guest");
                        return DatasourceFactory.ping(li);
                    }
                }, 
                canStop,
                start);
        connected.setName("MySQL is running on port 33060?");
        
        

        
        seq.addStep(connected);

        setDefinition(seq);
	}
	
}

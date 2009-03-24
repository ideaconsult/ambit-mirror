package ambit2.plugin.dbtools;

import ambit2.base.config.Preferences;
import ambit2.db.processors.MySQLCommand;
import ambit2.db.processors.MySQLShell;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.ProcessorPerformer;

import com.microworkflow.process.Conditional;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.TestCondition;
import com.microworkflow.process.Workflow;

public class MySQLServerStop extends Workflow {
	public MySQLServerStop() {
		Preferences.setProperty(Preferences.USER,"guest");
		Preferences.setProperty(Preferences.PASSWORD,"guest");
        Sequence seq=new Sequence();
        seq.setName("MySQL server");     
        
    	Primitive<MySQLCommand,MySQLCommand> stop = new Primitive<MySQLCommand,MySQLCommand>( 
    			MySQLCommand.MYSQLCOMMAND,
    			MySQLCommand.MYSQLCOMMAND,
    			new ProcessorPerformer<MySQLShell, MySQLCommand,MySQLCommand>(
    					new MySQLShell()) {
    				@Override
    				public MySQLCommand execute() throws Exception {
    					MySQLCommand cmd = getTarget();
    					if (cmd == null)
    						cmd = new MySQLCommand();
   						cmd.setCommand(MySQLCommand.COMMAND.STOP);
   						context.put(MySQLCommand.MYSQLCOMMAND,cmd);
      					cmd =  super.execute();
    					if (cmd.getException() != null)
    						context.put(DBWorkflowContext.ERROR, cmd.getException());
    					else
    						context.put(DBWorkflowContext.BATCHSTATS, "MySQL server stopped");    						
    					return cmd;
    				}
    			}
    			);
        stop.setName("Stop MySQL");       
        
        Conditional canStop = new Conditional(
                new TestCondition() {
                    public boolean evaluate() {
                    	
    					Object target = context.get(MySQLCommand.MYSQLCOMMAND);
    					if (target != null) {
    						MySQLCommand mc = (MySQLCommand) target;
    						if (mc.getProcess()!=null) {
    							mc.setCommand(MySQLCommand.COMMAND.STOP);	
    							return true;
    						} 
    					}
    					context.put(DBWorkflowContext.ERROR, "MySQL has been started outside of AMBIT. Unable to stop MySQL server.");
    					return false;
                    }
                }, 
                stop,
                null
                );
        canStop.setName("Was MySQL started by AMBIT XT?");
        setDefinition(canStop);        
	}
}

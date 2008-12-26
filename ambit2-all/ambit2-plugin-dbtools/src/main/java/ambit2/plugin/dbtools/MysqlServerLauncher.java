package ambit2.plugin.dbtools;

import ambit2.db.processors.MySQLCommand;
import ambit2.db.processors.MySQLShell;
import ambit2.workflow.ProcessorPerformer;

import com.microworkflow.execution.Performer;
import com.microworkflow.process.Primitive;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.Workflow;

/**
 * Starts and stops local MySQL server. 
 * @author nina
 *
 */
public class MysqlServerLauncher extends Workflow {
	public static String MYSQLCOMMAND = "ambit2.db.processors.MySQLCommand";
	public MysqlServerLauncher() {
        Sequence seq=new Sequence();
        seq.setName("MySQL server");        	

    	Primitive p = new Primitive( 
    			MYSQLCOMMAND,
    			MYSQLCOMMAND,
    			new Performer<MySQLCommand,MySQLCommand>() {
    				@Override
    				public MySQLCommand execute() throws Exception {
    					MySQLCommand target = getTarget();
    					if (target == null) 
    						target = new MySQLCommand();
    					else 
    						if (target.getProcess()!=null) 
    							target.setCommand(MySQLCommand.COMMAND.STOP);	
    						else
    							target.setCommand(MySQLCommand.COMMAND.START);
    					return target;
    				}
    			}
    			);
        p.setName("Start/Stop MySQL"); 
        
    	Primitive p1 = new Primitive( 
    			MYSQLCOMMAND,
    			MYSQLCOMMAND,
    			new ProcessorPerformer<MySQLShell, MySQLCommand,MySQLCommand>(
    					new MySQLShell())
    			);
        p1.setName("Start/Stop MySQL");    
        seq.addStep(p);
        seq.addStep(p1);
        setDefinition(seq);
	}
	
}

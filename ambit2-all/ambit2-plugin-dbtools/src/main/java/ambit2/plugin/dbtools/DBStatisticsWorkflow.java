package ambit2.plugin.dbtools;

import ambit2.db.processors.ConnectionStatisticsProcessor;
import ambit2.workflow.ActivityPrimitive;
import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.library.LoginSequence;

import com.microworkflow.process.Workflow;
/**
 * Database stats
 * @author nina
 *
 */
public class DBStatisticsWorkflow extends Workflow {
	public DBStatisticsWorkflow() {
    	ActivityPrimitive<String,StringBuffer> p1 = 
    		new ActivityPrimitive<String,StringBuffer>( 
    			null,
    			DBWorkflowContext.BATCHSTATS,
    			new ConnectionStatisticsProcessor(),false) {
    		@Override
    		public String toString() {
    			return "Statistics";
    		}
    		
    	};
    	p1.setName("Table statistics");
    	 setDefinition(new LoginSequence(p1));
    	 
	}
	@Override
	public String toString() {
		return "Database statistics";
	}
}

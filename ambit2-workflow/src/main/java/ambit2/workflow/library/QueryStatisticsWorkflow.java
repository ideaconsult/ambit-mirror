package ambit2.workflow.library;

import ambit2.db.processors.QueryStatisticsProcessor;
import ambit2.db.search.IStoredQuery;
import ambit2.workflow.ActivityPrimitive;
import ambit2.workflow.DBWorkflowContext;

import com.microworkflow.process.Workflow;
/**
 * Database stats
 * @author nina
 *
 */
public class QueryStatisticsWorkflow extends Workflow {
	public QueryStatisticsWorkflow() {
    	ActivityPrimitive<IStoredQuery,StringBuffer> p1 = 
    		new ActivityPrimitive<IStoredQuery,StringBuffer>( 
    			DBWorkflowContext.STOREDQUERY,
    			DBWorkflowContext.BATCHSTATS,
    			new QueryStatisticsProcessor(),false) {
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

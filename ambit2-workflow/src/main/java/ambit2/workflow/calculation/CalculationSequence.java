package ambit2.workflow.calculation;

import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IBatchStatistics.RECORDS_STATS;
import ambit2.workflow.DBWorkflowContext;

import com.microworkflow.process.Activity;
import com.microworkflow.process.Sequence;
import com.microworkflow.process.TestCondition;
import com.microworkflow.process.While;

/**
 * Parent class for calculation workflows
 * @author nina
 *
 */
public abstract class CalculationSequence extends Sequence {
	protected long batchSize = 10000;
	public CalculationSequence() {
		this(10000);
	}
	public CalculationSequence(long batchSize) {
		super();
		this.batchSize = batchSize;
	}	
	protected Activity getBatchLoop(Activity batch) {
		While loop = new While();
        loop.setTestCondition(new LoopTestCondition());
        loop.setBody(batch);	
        return loop;
	}
}

class LoopTestCondition extends TestCondition {
	protected long recordRead = 0;
	@Override
	public boolean evaluate() {
		Object o = getContext().get(DBWorkflowContext.BATCHSTATS);
		if (o==null) return false;
		boolean result = false;
		if (o instanceof IBatchStatistics) {
			result = ((IBatchStatistics)o).getRecords(RECORDS_STATS.RECORDS_READ)>recordRead;
			recordRead =((IBatchStatistics)o).getRecords(RECORDS_STATS.RECORDS_READ);
		} else result = false;

		return result;
	}
}
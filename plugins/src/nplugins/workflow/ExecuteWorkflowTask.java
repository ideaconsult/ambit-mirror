package nplugins.workflow;

import nplugins.core.NPluginsException;
import nplugins.shell.application.Task;
import nplugins.shell.application.TaskMonitor;

import com.microworkflow.process.Workflow;
import com.microworkflow.process.WorkflowContext;

public class ExecuteWorkflowTask implements Task<WorkflowContext,Void> {
	Workflow workflow;
	WorkflowContext context;
	public ExecuteWorkflowTask(Workflow workflow, WorkflowContext context) {
		this.workflow = workflow;
		this.context = context;
		
	}
	public WorkflowContext execute(TaskMonitor monitor) throws NPluginsException {
		workflow.executeWith(context);
		return context;
	}

	public void done() {
		// TODO Auto-generated method stub

	}

	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return false;
	}

	public void cancel() {
		// TODO Auto-generated method stub

	}

}

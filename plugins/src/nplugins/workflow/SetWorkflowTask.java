package nplugins.workflow;

import nplugins.core.NPluginsException;
import nplugins.shell.application.Task;
import nplugins.shell.application.TaskMonitor;

import com.microworkflow.process.Workflow;

public class SetWorkflowTask implements Task<Workflow,Void> {
	protected MWorkflowPlugin plugin;
	protected Workflow workflow;
	public SetWorkflowTask(MWorkflowPlugin plugin,Workflow workflow) {
		this.plugin = plugin;
		this.workflow = workflow;
	}
	public Workflow execute(TaskMonitor monitor) throws NPluginsException {
		this.plugin.setWorkflow(this.workflow);
		return this.workflow;
	}

	public void done() {

	}

	public boolean isCancelled() {
		return false;
	}

	public void cancel() {

	}

}

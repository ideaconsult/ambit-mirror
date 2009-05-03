package ambit2.plugin.analogs;

import com.microworkflow.process.Activity;
import com.microworkflow.process.NullActivity;
import com.microworkflow.process.Workflow;

public class EndpointValidationWorkflow extends Workflow {
	public EndpointValidationWorkflow() {
		Activity a = new NullActivity();
		a.setName("Not implemented yet!");
		setDefinition(a);
	}
}

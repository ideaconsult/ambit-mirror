package ambit2.plugin.usermgr;

import com.microworkflow.process.Activity;
import com.microworkflow.process.NullActivity;
import com.microworkflow.process.Workflow;

public class PropertiesWorkflow extends Workflow {
	public PropertiesWorkflow() {
		Activity a = new NullActivity();
		a.setName("Not implemented yet!");
		setDefinition(a);
	}
}

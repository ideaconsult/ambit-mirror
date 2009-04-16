package ambit2.plugin.search;

import ambit2.workflow.library.DefineProfile;
import ambit2.workflow.library.LoginSequence;

import com.microworkflow.process.Workflow;

public class ProfileWorkflow extends Workflow {
	public ProfileWorkflow() {
		setDefinition(new LoginSequence(new DefineProfile()));
	}
}

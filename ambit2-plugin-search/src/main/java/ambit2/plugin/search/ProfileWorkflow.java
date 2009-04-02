package ambit2.plugin.search;

import ambit2.workflow.library.DefineProfile;

import com.microworkflow.process.Workflow;

public class ProfileWorkflow extends Workflow {
	public ProfileWorkflow() {
		setDefinition(new DefineProfile());
	}
}

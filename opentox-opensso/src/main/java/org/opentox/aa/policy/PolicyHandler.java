package org.opentox.aa.policy;

public class PolicyHandler implements IPolicyHandler {
	protected int processed = 0;
	@Override
	public int getProcessed() {
		return processed;
	}

	@Override
	public void handlePolicy(String policyID) throws Exception {
		processed++;
	}

	@Override
	public void handlePolicy(String policyID, String content) throws Exception {
		processed++;

	}

	@Override
	public void handleOwner(String owner) throws Exception {

	}

}

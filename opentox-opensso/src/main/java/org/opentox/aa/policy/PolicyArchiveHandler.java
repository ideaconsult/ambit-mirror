package org.opentox.aa.policy;

import org.opentox.aa.opensso.OpenSSOPolicy;

public class PolicyArchiveHandler extends PolicyHandler {
	protected OpenSSOPolicy policy;
	
	public PolicyArchiveHandler(OpenSSOPolicy policy) {
		super();
		this.policy = policy;
	}
		@Override
		public void handleOwner(String owner) throws Exception {
			super.handleOwner(owner);

				
		}				
		@Override
		public void handlePolicy(String policyID) throws Exception {
			super.handlePolicy(policyID);

		}
		@Override
		public void handlePolicy(String policyID, String content)
				throws Exception {
			super.handlePolicy(policyID,content);

			
		}
				
}

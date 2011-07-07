package org.opentox.aa.policy;

public interface IPolicyHandler {
	void handlePolicy(String policyID) throws Exception ;
	void handlePolicy(String policyID,String content) throws Exception ;
}

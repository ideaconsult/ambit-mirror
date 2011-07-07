package org.opentox.aa.policy;

public interface IPolicyHandler {
	int getProcessed();
	void handlePolicy(String policyID) throws Exception ;
	void handlePolicy(String policyID,String content) throws Exception ;
	void handleOwner(String owner) throws Exception;
}

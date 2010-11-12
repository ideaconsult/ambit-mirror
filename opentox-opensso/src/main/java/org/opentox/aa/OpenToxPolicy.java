package org.opentox.aa;

import java.util.Hashtable;

import org.opentox.aa.opensso.OpenSSOToken;


public abstract class OpenToxPolicy<Token extends OpenToxToken,PolicyContent> {
	protected static final String MSG_EMPTY_POLICYID = "Empty policy id";
	protected static final String MSG_EMPTY_URI = "Empty URI";
	protected String policyService;

	public OpenToxPolicy(String policyService) {
		this.policyService = policyService;
	}
	public abstract int createGroupPolicy(String group,OpenSSOToken token, String uri, String[] methods) throws Exception;
	/**
	 * Create a policy
	 * @param token
	 * @return 200 (OK) 400 (XML contains errors) 500 (Other Errors)
	 */
	public abstract int createGroupPolicy(String group, Token token,String uri, String[] methods, String policyID) throws Exception;
	
	public abstract int createUserPolicy(String user,OpenSSOToken token, String uri, String[] methods) throws Exception;
	/**
	 * Create a policy
	 * @param token
	 * @return 200 (OK) 400 (XML contains errors) 500 (Other Errors)
	 */
	public abstract int createUserPolicy(String user,Token token,String uri, String[] methods, String policyID) throws Exception;
	/**
	 * List my policies
	 * @param token
	 * @return 200 (OK) 500 (Other Errors)
	 */
	public abstract int listPolicies(Token token,Hashtable<String, PolicyContent> policies) throws Exception;
	/**
	 * List my policy id
	 * @param token
	 * @param policyid
	 * @return
	 * 200 (OK) 401 (Unauthorized) 500 (Other Errors)
	 */
	public abstract int listPolicy(Token token,String policyid,Hashtable<String, PolicyContent> policies) throws Exception;
	/**
	 * GET owner of URI uri
	 * @param token
	 * @param uri
	 * @return 200 (OK) 401 (Unauthorized) 500 (Other Errors)
	 */
	public abstract int getURIOwner(Token token,String uri,IOpenToxUser user) throws Exception;
	/**
	 * GET owner of URI uri and all affected policy names
	 * @param token
	 * @param uri
	 * @param policyNames
	 * @return 200 (OK) 401 (Unauthorized) 500 (Other Errors)
	 */
	public abstract int getURIOwner(Token token,String uri,IOpenToxUser user,Hashtable<String, PolicyContent> policies) throws Exception;
	/**
	 * Delete policy id
	 * @param token
	 * @param policyId
	 * @return 	200 (OK) 400 (Policy non-existent) 401 (Unauthorized) 500 (General Error)
	 */
	public abstract int deletePolicy(Token token,String policyId) throws Exception;
}

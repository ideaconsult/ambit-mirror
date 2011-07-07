package org.opentox.aa.opensso.test;

import java.util.Hashtable;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.opentox.aa.IOpenToxUser;
import org.opentox.aa.OpenToxUser;
import org.opentox.aa.opensso.AAServicesConfig;
import org.opentox.aa.opensso.OpenSSOPolicy;
import org.opentox.aa.opensso.OpenSSOToken;
import org.opentox.aa.policy.IPolicyHandler;
import org.restlet.data.Status;

public class OpenToxPolicyTest {
	protected AAServicesConfig config;
	@Before
	public void setUp() {
		config = AAServicesConfig.getSingleton();	
	}
	@Test
	public void testConfig() throws Exception {
		Assert.assertNotNull(config.getPolicyService());
	}
	
	@Test
	public void testCreateGroupPolicy() throws Exception {
		OpenSSOToken token=null;
		try {
			token = new OpenSSOToken(config.getOpenSSOService());
			IOpenToxUser user = new OpenToxUser();
			user.setUserName(config.getTestUser());
			user.setPassword(config.getTestUserPass());
			Assert.assertTrue(token.login(user));
			Assert.assertTrue(token.isTokenValid());
		
			OpenSSOPolicy policy = new OpenSSOPolicy(config.getPolicyService());
			String uri= "http://blabla.uni-plovdiv.bg:8080/ambit2/dataset/1001";
			String policyID =  "group_"+UUID.randomUUID().toString(); //uri.replace(":","_").replace("/","_");
			try {
				policy.deletePolicy(token, policyID);
			} catch (Exception x) {}
			
			Assert.assertEquals(Status.SUCCESS_OK.getCode(),
					policy.createGroupPolicy("member",token, 
						uri, 
						new String[] {"GET","POST"} ,
						policyID)
			);
			
			Hashtable<String,String> policies = new Hashtable<String, String>();
			
			//check policies for this URI
			if (Status.SUCCESS_OK.getCode() == policy.getURIOwner(token, uri, user, policies)) {
				Assert.assertNotNull(policies.get(policyID));
			}			
			/*
			try {
				policy.listPolicy(token, policyID, policies);
				System.out.println(policies.get(policyID));
			} catch (Exception x) {}
		*/
			
			try {
				Assert.assertTrue(token.authorize(uri, "GET"));
				Assert.assertTrue(token.authorize(uri, "POST"));
			} catch (Exception x) {}
			//clean up
			policy.deletePolicy(token, policyID);
		} catch (Exception x) {
			throw x;
		} finally {
			try { if (token!=null) token.logout(); } catch (Exception x) {}
		}

	}	
	@Test
	public void testCreateUserPolicy() throws Exception {
		OpenSSOToken token=null;
		try {
			token = new OpenSSOToken(config.getOpenSSOService());
			IOpenToxUser user = new OpenToxUser();
			user.setUserName(config.getTestUser());
			user.setPassword(config.getTestUserPass());
			Assert.assertTrue(token.login(user));
			Assert.assertTrue(token.isTokenValid());
		
			System.out.println(config.getTestUser());
			OpenSSOPolicy policy = new OpenSSOPolicy(config.getPolicyService());
			String uri="http://blabla.uni-plovdiv.bg:8080/ambit2/dataset/999";
			String policyID =  user.getUsername()+"__"+UUID.randomUUID().toString();
			try {
				policy.deletePolicy(token, policyID);
			} catch (Exception x) {}
			
			Assert.assertEquals(Status.SUCCESS_OK.getCode(),
					policy.createUserPolicy(config.getTestUser(),token, 
						uri, 
						new String[] {"GET","POST"} ,
						policyID)
			);

			Hashtable<String,String> policies = new Hashtable<String, String>();
			
			//check policies for this URI
			if (Status.SUCCESS_OK.getCode() == policy.getURIOwner(token, uri, user, policies)) {
				Assert.assertNotNull(policies.get(policyID));
			}
			/*
			 * this is slow
			try {
				policy.listPolicy(token, policyID, new IPolicyHandler() {
					@Override
					public void handlePolicy(String policyID) throws Exception {
						System.out.println(policyID);
						
					}
					@Override
					public void handlePolicy(String policyID, String content)
							throws Exception {
						System.out.println(policyID);
						System.out.println(content);
						
					}
				});
				
			} catch (Exception x) {
				x.printStackTrace();
			}
			*/
			
			try {
				Assert.assertTrue(token.isTokenValid());
				Hashtable<String, String> results = new Hashtable<String, String>();
				token.getAttributes(new String[] {"uid"}, results);
				System.out.println(results);
				Assert.assertTrue(token.authorize(uri, "GET"));
				Assert.assertTrue(token.authorize(uri, "POST"));
			} catch (Exception x) {
				throw x;
			}
			//clean up
			policy.deletePolicy(token, policyID);
			
		} catch (Exception x) {
			throw x;
		} finally {
			try { if (token!=null) token.logout(); } catch (Exception x) {}
		}

	}
	@Test
	public void testListPolicies() throws Exception {
		OpenSSOToken token=null;
		try {
			token = new OpenSSOToken(config.getOpenSSOService());
			Assert.assertTrue(token.login(config.getTestUser(),config.getTestUserPass()));
			
			Assert.assertTrue(token.isTokenValid());
		
			OpenSSOPolicy policy = new OpenSSOPolicy(config.getPolicyService());
			Hashtable<String,String> policies = new Hashtable<String, String>();
			if (Status.SUCCESS_OK.getCode() == policy.listPolicies(token,policies)) {
				System.out.println(policies);
			}
			
		} catch (Exception x) {
			throw x;
		} finally {
			try { if (token!=null) token.logout(); } catch (Exception x) {}
		}

	}

	
	@Test
	public void testDeletePolicy() throws Exception {
		//TODO create policy and then delete
		OpenSSOToken token=null;
		try {
			token = new OpenSSOToken(config.getOpenSSOService());
			Assert.assertTrue(token.login(config.getTestUser(),config.getTestUserPass()));
			Assert.assertTrue(token.isTokenValid());
		
			OpenSSOPolicy policy = new OpenSSOPolicy(config.getPolicyService());
			Hashtable<String,String> policies = new Hashtable<String, String>();
			if (Status.SUCCESS_OK.getCode() == policy.listPolicies(token,policies)) {
				System.out.println(policies);
			}
			String deletePolicy = "ninas_evil_policy_1";
			/*
			Enumeration<String> pol = policies.keys();
			while (pol.hasMoreElements()) {
				deletePolicy = pol.nextElement();
				policy.deletePolicy(token, deletePolicy);
				break;
			}
			*/
			Hashtable<String,String> policies1 = new Hashtable<String, String>();
			if (Status.SUCCESS_OK.getCode() == policy.listPolicies(token,policies1)) {
				System.out.println(policies1);
				Assert.assertFalse(policies1.containsKey(deletePolicy));
			}
		} catch (Exception x) {
			throw x;
		} finally {
			try { if (token!=null) token.logout(); } catch (Exception x) {}
		}

	}

	@Test
	public void testListPolicy() throws Exception {
		//TODO create policy and then list
		OpenSSOToken token=null;
		try {
			token = new OpenSSOToken(config.getOpenSSOService());
			Assert.assertTrue(token.login(config.getTestUser(),config.getTestUserPass()));
			Assert.assertTrue(token.isTokenValid());
		
			OpenSSOPolicy policy = new OpenSSOPolicy(config.getPolicyService());
			Hashtable<String,String> policies = new Hashtable<String, String>();
			//String policyId = "httpsambit.uni-plovdiv.bg8443ambit2model28768GETPUTPOSTDELETE"; //"ninas_evil_policy_1";
			String policyId = "guest__d282c424-f4b2-44c7-a914-35e3930a2d54";// "http___blabla.uni-plovdiv.bg_8080_ambit2";
			if (Status.SUCCESS_OK.getCode() == policy.listPolicy(token,policyId,policies)) {
				System.out.println(policies.get(policyId));
				Assert.assertNotNull(policies.get(policyId));
				
			}
		
		} catch (Exception x) {
			throw x;
		} finally {
			try { if (token!=null) token.logout(); } catch (Exception x) {}
		}

	}
	@Test
	public void testGetURIOwner() throws Exception {

		OpenSSOToken token=null;
		try {
			token = new OpenSSOToken(config.getOpenSSOService());
			Assert.assertTrue(token.login(config.getTestUser(),config.getTestUserPass()));
			Assert.assertTrue(token.isTokenValid());
		
			OpenSSOPolicy policy = new OpenSSOPolicy(config.getPolicyService());
			
			String uri = "http://blabla.uni-plovdiv.bg:8080/ambit2";
			//"https://ambit.uni-plovdiv.bg:8443/ambit2/dataset/63634";
			
			IOpenToxUser user = new OpenToxUser();
			if (Status.SUCCESS_OK.getCode() == policy.getURIOwner(token, uri, user)) {
				Assert.assertEquals(config.getTestUser(),user.getUsername());

			}
		
		} catch (Exception x) {
			throw x;
		} finally {
			try { if (token!=null) token.logout(); } catch (Exception x) {}
		}

	}
	
	@Test
	public void testGetURIOwnerAndPolicy() throws Exception {

		OpenSSOToken token=null;
		try {
			token = new OpenSSOToken(config.getOpenSSOService());
			Assert.assertTrue(token.login(config.getTestUser(),config.getTestUserPass()));
			Assert.assertTrue(token.isTokenValid());
		
			OpenSSOPolicy policy = new OpenSSOPolicy(config.getPolicyService());
			
			String uri = "http://blabla.uni-plovdiv.bg:8080/ambit2/dataset/999";
			//https://ambit.uni-plovdiv.bg:8443/ambit2/dataset/63634";
			
			IOpenToxUser user = new OpenToxUser();
			Hashtable<String,String> policies = new Hashtable<String, String>();
				if (Status.SUCCESS_OK.getCode() == policy.getURIOwner(token, uri, user, policies)) {
					if (policies.size()>0)
						Assert.assertEquals(config.getTestUser(),user.getUsername());

				}
		
		} catch (Exception x) {
			throw x;
		} finally {
			try { if (token!=null) token.logout(); } catch (Exception x) {}
		}

	}
	
	@Test
	public void testAuthorize() throws Exception {
		OpenSSOToken token=null;
		try {
			token = new OpenSSOToken(config.getOpenSSOService());
			Assert.assertTrue(token.login(config.getTestUser(),config.getTestUserPass()));

			Assert.assertTrue(token.isTokenValid());
			String uri="http://blabla.uni-plovdiv.bg:8080/ambit2/dataset/999";
			Assert.assertTrue(token.authorize(uri, "GET"));
			Assert.assertTrue(token.authorize(uri, "POST"));
			
		} catch (Exception x) {
			throw x;
		} finally {
			try { if (token!=null) token.logout(); } catch (Exception x) {}
		}

	}
}

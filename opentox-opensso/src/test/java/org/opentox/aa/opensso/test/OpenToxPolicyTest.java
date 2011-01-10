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
			Assert.assertTrue(token.login(config.getTestUser(),config.getTestUserPass()));
			Assert.assertTrue(token.isTokenValid());
		
			OpenSSOPolicy policy = new OpenSSOPolicy(config.getPolicyService());
			String uri= "http://blabla.uni-plovdiv.bg:8080/ambit2";
			String policyID =  UUID.randomUUID().toString(); //uri.replace(":","_").replace("/","_");
			try {
				policy.deletePolicy(token, policyID);
			} catch (Exception x) {}
			
			Assert.assertEquals(Status.SUCCESS_OK.getCode(),
					policy.createGroupPolicy("partner",token, 
						uri, 
						new String[] {"GET","POST"} ,
						policyID)
			);
			
			Hashtable<String,String> policies = new Hashtable<String, String>();
			if (Status.SUCCESS_OK.getCode() == policy.listPolicies(token,policies)) {
				Assert.assertNotNull(policies.get(policyID));
			}
			try {
				policy.listPolicy(token, policyID, policies);
				System.out.println(policies.get(policyID));
			} catch (Exception x) {}
			
			try {
				policy.listPolicy(token, policyID, policies);
				System.out.println(policies.get(policyID));
			} catch (Exception x) {}
			
			try {
				Assert.assertTrue(token.authorize(uri, "GET"));
				Assert.assertTrue(token.authorize(uri, "POST"));
			} catch (Exception x) {}
			
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
			Assert.assertTrue(token.login(config.getTestUser(),config.getTestUserPass()));
			Assert.assertTrue(token.isTokenValid());
		
			OpenSSOPolicy policy = new OpenSSOPolicy(config.getPolicyService());
			String uri="http://blabla.uni-plovdiv.bg:8080/ambit2";
			String policyID = uri.replace(":","_").replace("/","_");
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
			if (Status.SUCCESS_OK.getCode() == policy.listPolicies(token,policies)) {
				Assert.assertNotNull(policies.get(policyID));
			}
			try {
				policy.listPolicy(token, policyID, policies);
				System.out.println(policies.get(policyID));
			} catch (Exception x) {}
			
			try {
				Assert.assertTrue(token.isTokenValid());
				Assert.assertTrue(token.authorize(uri, "GET"));
				Assert.assertTrue(token.authorize(uri, "POST"));
			} catch (Exception x) {}

			
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
	//http://nina-vpn.acad.bg:8080/sso_protected/1000
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
			String policyId = "nina_test_service_1000"; //"ninas_evil_policy_1";
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
			
			String uri = "http://nina-vpn.acad.bg:8080/sso_protected/1000";
			
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
			
			String uri = "http://nina-vpn.acad.bg:8080/sso_protected/1000";
			
			IOpenToxUser user = new OpenToxUser();
			Hashtable<String,String> policies = new Hashtable<String, String>();
			if (Status.SUCCESS_OK.getCode() == policy.getURIOwner(token, uri, user, policies)) {
				Assert.assertEquals(config.getTestUser(),user.getUsername());
				System.out.println(policies);

			}
		
		} catch (Exception x) {
			throw x;
		} finally {
			try { if (token!=null) token.logout(); } catch (Exception x) {}
		}

	}
	
}

package org.opentox.aa.opensso.test;

import java.util.Hashtable;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.opentox.aa.opensso.AAServicesConfig;
import org.opentox.aa.opensso.OpenSSOToken;

public class OpenSSOTokenTest {
	protected AAServicesConfig config;
	@Before
	public void setUp() {
		config = AAServicesConfig.getSingleton();	
	}
	@Test
	public void testConfig() throws Exception {
		
		Assert.assertNotNull(config.getOpenSSOService());
		Assert.assertNotNull(config.getTestUser());
		Assert.assertNotNull(config.getTestUserPass());
	}
	
	@Test
	public void testLoginLogoutValidUser() throws Exception {
		
		OpenSSOToken token = new OpenSSOToken(config.getOpenSSOService());
		Assert.assertTrue(token.login(config.getTestUser(),config.getTestUserPass()));
		Assert.assertTrue(token.isTokenValid());
		Assert.assertTrue(token.logout());

	}
	
	@Test
	public void testLoginLogoutInvalidUser() throws Exception {
		
		OpenSSOToken token = new OpenSSOToken(config.getOpenSSOService());
		Assert.assertFalse(token.login("blabla",""));

	}
	
	@Test
	public void testInvalidToken() throws Exception {
		
		OpenSSOToken token = new OpenSSOToken(config.getOpenSSOService());
		token.setToken("blabla");
		Assert.assertFalse(token.isTokenValid());

	}
	
	@Test
	public void testAuthorizeTest() throws Exception {
		
		OpenSSOToken token = new OpenSSOToken(config.getOpenSSOService());
		Assert.assertTrue(token.login(config.getTestUser(),config.getTestUserPass()));
		Assert.assertFalse(token.authorize("blabla", "GET"));
		Assert.assertTrue(token.logout());

	}
	/*
	@Test
	public void testAuthorizeValidUser() throws Exception {
		
		OpenSSOToken token = new OpenSSOToken(config.getOpenSSOService());
		Assert.assertTrue(token.login(config.getTestUser(),config.getTestUserPass()));
		Assert.assertFalse(token.authorize("blabla", "GET"));
		Assert.assertTrue(token.logout());

	}
	*/
	@Test
	public void testGetAttributes() throws Exception {
		
		OpenSSOToken token = new OpenSSOToken(config.getOpenSSOService());
		Assert.assertTrue(token.login(config.getTestUser(),config.getTestUserPass()));
		Hashtable<String, String> results = new Hashtable<String, String>();
		Assert.assertTrue(token.getAttributes(new String[] {"uid"},results));
		Assert.assertEquals(config.getTestUser(),results.get("uid"));
		Assert.assertTrue(token.logout());
	}
}

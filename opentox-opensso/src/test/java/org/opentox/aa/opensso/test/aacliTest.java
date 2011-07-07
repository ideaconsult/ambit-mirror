package org.opentox.aa.opensso.test;

import org.junit.Test;
import org.opentox.aa.opensso.aacli;

public class aacliTest {
	@Test
	public void testPoliciesPerURI() throws Exception {
		aacli cli = new aacli();
		cli.main(new String[] {
				"-u","guest",
				"-p","guest",
				"-r","http://blabla.uni-plovdiv.bg:8080/ambit2/dataset/1003",
				"-c","list"
		});

	}
	
	@Test
	public void testAllPoliciesPerUser() throws Exception {
		aacli cli = new aacli();
		cli.main(new String[] {
				"-u","guest",
				"-p","guest",
				"-c","list"
		});

	}	
	
	@Test
	public void testRetrievePolicySpecifyService() throws Exception {
		aacli cli = new aacli();
		cli.main(new String[] {
				"-n","http://opensso.in-silico.ch/opensso/identity",
				"-z","http://opensso.in-silico.ch/Pol/opensso-pol",
				"-u","guest",
				"-p","guest",
				"-c","list",
			//	"-r","https://ambit.uni-plovdiv.bg:8443/ambit2/dataset/1",
				"-i","member_rohttpsambit.uni-plovdiv.bg8443ambit2dataset1"
		});

	}		
	
	@Test
	public void testRetrievePolicy() throws Exception {
		aacli cli = new aacli();
		cli.main(new String[] {
				"-u","nina",
				"-p","sinanica666",
				"-c","list",
			//	"-r","https://ambit.uni-plovdiv.bg:8443/ambit2/dataset/1",
				"-i","ea3b97dd-d439-4d20-84ad-299fe0e01d92"
		});

	}	
	
	@Test
	public void testDeletePolicy() throws Exception {
		aacli cli = new aacli();
		cli.main(new String[] {
				"-u","guest",
				"-p","guest",
				"-i","guest_c35ceda9-e548-47d6-a377-ac2cae708100",
				"-c","delete"
		});
	}
	
	@Test
	public void testDeletePolicyURI() throws Exception {
		aacli cli = new aacli();
		cli.main(new String[] {
				
				"-u","guest",
				"-p","guest",
				"-r","http://blabla.uni-plovdiv.bg:8080/ambit2/dataset/999",
				"-c","delete"
		});
	}	
	
	@Test
	public void testAuthorize() throws Exception {
		aacli cli = new aacli();
		cli.main(new String[] {
				"-n","http://opensso.in-silico.ch/opensso/identity",
				"-z","http://opensso.in-silico.ch/Pol/opensso-pol",				
				"-u","guest",
				"-p","guest",
				"-r","https://ambit.uni-plovdiv.bg:8443/ambit2/dataset/1",
				"-c","authorize"
		});
	}		
//
}

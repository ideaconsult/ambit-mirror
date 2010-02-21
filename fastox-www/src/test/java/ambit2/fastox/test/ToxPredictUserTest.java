package ambit2.fastox.test;

import junit.framework.Assert;

import org.junit.Test;

import ambit2.fastox.users.MemoryUsersStorage;
import ambit2.fastox.users.ToxPredictUser;

public class ToxPredictUserTest {
	@Test
	public void test() throws Exception {
		ToxPredictUser user = new ToxPredictUser();
		MemoryUsersStorage.addSession(user);
		Assert.assertTrue(MemoryUsersStorage.getSession(user)!=null);
		Assert.assertTrue(MemoryUsersStorage.getSession(user.getId())!=null);
	}
}

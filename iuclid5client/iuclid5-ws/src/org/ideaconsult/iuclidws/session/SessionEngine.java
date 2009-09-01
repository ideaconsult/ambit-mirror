package org.ideaconsult.iuclidws.session;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis2.AxisFault;
import org.ideaconsult.iuclidws.ClientServiceException;
import org.ideaconsult.iuclidws.PropertiesUtil;
import org.ideaconsult.iuclidws.StubUtil;
import org.ideaconsult.iuclidws.types.Types.Credentials;
import org.ideaconsult.iuclidws.types.Types.Login;
import org.ideaconsult.iuclidws.types.Types.LoginResponse;
import org.ideaconsult.iuclidws.types.Types.Logout;
import org.ideaconsult.iuclidws.types.Types.Session;


/**
 * {@link SessionEngine} class contains helper methods to manage sessions
 * returned from SessionEngine web service 
 *
 */
public class SessionEngine {

	private static Map<String, SessionEngineStub> sessionEngineStubsMap = new HashMap<String, SessionEngineStub>();
	private static String lastTarget;

	private static final char DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
			'D', 'E', 'F' };

	private static final String ENDPOINT_NAME = "SessionEngine";

	/**
	 * Login with default username, password and web services target url
	 * parameters from configuration file
	 * 
	 * @return the {@link Session} object
	 * 
	 * @see PropertiesUtil
	 * 
	 * @throws RemoteException
	 * @throws AxisFault
	 * @throws SessionEngineNotAvailableFault
	 * @throws SessionEngineFault
	 */
	public static Session login() throws RemoteException, AxisFault, SessionEngineNotAvailableFault,
			SessionEngineFault {

		return login(PropertiesUtil.getUsername(), PropertiesUtil.getPassword(), PropertiesUtil.getTarget());
	}

	/**
	 * login
	 * 
	 * @param username
	 * @param password
	 * @param target web services url. Example <b>http://example.com/axis2/services</b>
	 * 
	 * @return the {@link Session} object
	 * 
	 * @throws RemoteException
	 * @throws AxisFault
	 * @throws SessionEngineNotAvailableFault
	 * @throws SessionEngineFault
	 */
	public static Session login(String username, String password, String target) throws RemoteException,
			AxisFault, SessionEngineNotAvailableFault, SessionEngineFault {

		final SessionEngineStub sessionEngine = getSessionEngineStub(target);
		Login login = new Login();
		login.setCredentials(getCredentials(username, password));
		final LoginResponse loginResponse = sessionEngine.login(login);

		return loginResponse.getSession();
	}

	/**
	 * Logout for given {@link Session} object and target.
	 * 
	 * @param session
	 * @param target web services url. Example <b>http://example.com/axis2/services</b>
	 * 
	 * @throws ClientServiceException
	 * @throws SessionEngineFault
	 * @throws RemoteException
	 * @throws SessionEngineNotAvailableFault
	 */
	public static void logout(Session session, String target) throws ClientServiceException,
			SessionEngineFault, RemoteException, SessionEngineNotAvailableFault {
		if (sessionEngineStubsMap.isEmpty()) {
			throw new ClientServiceException("Not loged in");
		}
		SessionEngineStub sessionEngineStub = sessionEngineStubsMap.get(target);
		if (sessionEngineStub == null) {
			throw new ClientServiceException("Invalid target parameter");
		}
		Logout logout = new Logout();
		logout.setSession(session);
		sessionEngineStub.logout(logout);
	}

	/**
	 * Logout for given {@link Session} object.
	 * 
	 * @param session
	 * 
	 * @throws SessionEngineFault
	 * @throws RemoteException
	 * @throws SessionEngineNotAvailableFault
	 */
	public static void logout(Session session) throws ClientServiceException, SessionEngineFault,
			RemoteException, SessionEngineNotAvailableFault {
		try {
			logout(session, PropertiesUtil.getTarget());
		} catch (ClientServiceException ipe) {
			logout(session, lastTarget);
		}
	}

	protected synchronized static SessionEngineStub getSessionEngineStub(String target) throws AxisFault {
		SessionEngineStub sessionEngineStub = sessionEngineStubsMap.get(target);
		if (sessionEngineStub == null) {
			sessionEngineStub = new SessionEngineStub(StubUtil.concatEndpointAddress(target, ENDPOINT_NAME));
			sessionEngineStubsMap.put(target, sessionEngineStub);
			StubUtil.initializeAxisService(sessionEngineStub._getServiceClient());
		}
		return sessionEngineStub;
	}

	protected static Credentials getCredentials(String username, String password) {
		Credentials credentials = new Credentials();
		credentials.setUsername(username);
		credentials.setPassword(encryptPassword(password));
		return credentials;
	}

	protected static String encryptPassword(String decryptedPassword) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte pwdByte[] = decryptedPassword.getBytes("UTF-8");
			byte md5Byte[] = md5.digest(pwdByte);
			return encodeHex(md5Byte);
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	protected static String encodeHex(byte hash[]) {
		int currenLength = hash.length;
		char out[] = new char[currenLength * 2];
		int i = 0;
		int j = 0;
		for (; i < currenLength; i++) {
			int high = hash[i] >>> 4 & 0xf;
			int low = hash[i] & 0xf;
			out[j++] = DIGITS[high];
			out[j++] = DIGITS[low];
		}

		return new String(out);
	}

}

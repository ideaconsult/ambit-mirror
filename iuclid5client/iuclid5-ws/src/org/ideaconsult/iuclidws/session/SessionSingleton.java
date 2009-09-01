package org.ideaconsult.iuclidws.session;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.ideaconsult.iuclidws.ClientServiceException;
import org.ideaconsult.iuclidws.types.Types.Session;

/**
 * SessionSingleton class contains {@link Session} object.
 * 
 * @see Session
 * @see SessionEngine
 */
public class SessionSingleton {

	private static Session session;
	private static SessionSingleton instance;

	private static Logger logger = Logger.getLogger(SessionSingleton.class);

	private SessionSingleton() throws SessionEngineNotAvailableFault, SessionEngineFault,
			ClientServiceException {
		logger.debug("Create SessionSingleton object");
		init();
	}

	/**
	 * initialize {@link Session} object
	 * 
	 * @throws SessionEngineNotAvailableFault
	 * @throws SessionEngineFault
	 * @throws ClientServiceException
	 */
	private void init() throws SessionEngineNotAvailableFault, SessionEngineFault, ClientServiceException {
		try {
			session = SessionEngine.login();
		} catch (AxisFault e) {
			throw new ClientServiceException("AxisFault");
		} catch (RemoteException e) {
			throw new ClientServiceException("RemoteException");
		}
	}

	/**
	 * 
	 * @return the {@link SessionSingleton} object
	 * 
	 * @throws SessionEngineNotAvailableFault
	 * @throws SessionEngineFault
	 * @throws ClientServiceException
	 */
	public static synchronized SessionSingleton getInstance() throws SessionEngineNotAvailableFault,
			SessionEngineFault, ClientServiceException {
		if (instance == null) {
			instance = new SessionSingleton();
		}

		return instance;
	}

	/**
	 * 
	 * @return the {@link Session} object
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * This method make logout for current {@link Session} object and then
	 * login to initialize new {@link Session}.
	 * 
	 * @throws SessionEngineNotAvailableFault
	 * @throws SessionEngineFault
	 * @throws ClientServiceException
	 * 
	 * @see SessionEngine
	 */
	public void relogin() throws SessionEngineNotAvailableFault, SessionEngineFault, ClientServiceException {
		try {
			logger.debug("Trying logout session with ID=" + session.getId());
			try {
				SessionEngine.logout(session);
			} catch (RemoteException e) {
				throw new ClientServiceException("RemoteException");
			}
		} catch (SessionEngineNotAvailableFault e) {
			logger.debug("Can not logout: SessionEngineNotAvailableFault");
		}
		logger.debug("Login......");
		init();
	}

}

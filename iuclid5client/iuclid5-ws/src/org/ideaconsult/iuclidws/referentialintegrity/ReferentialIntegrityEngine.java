package org.ideaconsult.iuclidws.referentialintegrity;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.ideaconsult.iuclidws.ClientServiceException;
import org.ideaconsult.iuclidws.PropertiesUtil;
import org.ideaconsult.iuclidws.StubUtil;
import org.ideaconsult.iuclidws.session.SessionEngineFault;
import org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault;
import org.ideaconsult.iuclidws.session.SessionSingleton;
import org.ideaconsult.iuclidws.types.Types.Check;
import org.ideaconsult.iuclidws.types.Types.CheckItem;
import org.ideaconsult.iuclidws.types.Types.Session;

/**
 * The referential integrity engine is typically needed in an import scenario. The referential
 * integrity engine is then used as a last step after all documents have been created in the
 * IUCLID 5 database and ensures that a set of documents is linked correctly. Additionally it
 * updates the dossier search index (for finding dossiers through queries), if dossiers are created/handled.
 * 
 * @author Nikolay Taskov
 */
public class ReferentialIntegrityEngine {
	
	// fields
	/** Referential integrity engine endpoint context */
	private String END_POINT_REFERENTIAL_INTEGRITY_ENGINE = "ReferentialIntegrityEngine";
	
	/** Current session */
	private Session session;
	
	/** Instance of ReferentialIntegrityEngineStub class */
	private ReferentialIntegrityEngineStub referentialIntegrityEngineStub;
	
	private String target;
	
	/** The logger for this class */
	private final static Logger logger = Logger.getLogger(ReferentialIntegrityEngine.class);
	
	// constructors
	/**
	 * Init ReferentialIntegrityEngine class and logged in automatically.
	 * 
	 * @throws ClientServiceException 
	 * @throws SessionEngineFault 
	 * @throws SessionEngineNotAvailableFault 
	 */
	public ReferentialIntegrityEngine() throws SessionEngineNotAvailableFault, SessionEngineFault, ClientServiceException, AxisFault {
		this(SessionSingleton.getInstance().getSession());
	}
	
	/**
	 * Init ReferentialIntegrityEngine class with custom Session
	 * 
	 * @param session
	 */
	public ReferentialIntegrityEngine(Session session) throws AxisFault {
		this(session, PropertiesUtil.getTarget());
	}
	
	/**
	 * Init ReferentialIntegrityEngine class with custom Session and target
	 * 
	 * @param session
	 */
	public ReferentialIntegrityEngine(Session session, String target) throws AxisFault {
		this.session = session;
		this.target = target;
		
		referentialIntegrityEngineStub = new ReferentialIntegrityEngineStub(StubUtil.concatEndpointAddress(
				this.target	, END_POINT_REFERENTIAL_INTEGRITY_ENGINE));
		StubUtil.initializeAxisService(referentialIntegrityEngineStub._getServiceClient());
	}
	
	// methods
	/**
	 * Check the referential integrity of the given documents.
	 * 
	 * @param checkItemsArray - a array of items to include in the referential integrity check
	 * @throws ReferentialIntegrityEngineNotAvailableFault Is thrown when e.g. the back-end 
	 * service is not available to fulfill the request
	 * @throws ReferentialIntegrityEngineFault Is thrown if Referential Integrity Engine is not 
	 * able to fulfill your request. This may happen if the Engine caught a back-end Exception.
	 */
	public void check(CheckItem[] checkItemsArray) 
			throws ReferentialIntegrityEngineNotAvailableFault, ReferentialIntegrityEngineFault {
		
		try {
			Check check = new Check();
			check.setSession(session);
			check.setItems(checkItemsArray);
			referentialIntegrityEngineStub.check(check);
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
	}	
}

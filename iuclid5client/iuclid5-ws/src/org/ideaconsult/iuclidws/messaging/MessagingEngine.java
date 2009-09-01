package org.ideaconsult.iuclidws.messaging;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.ideaconsult.iuclidws.ClientServiceException;
import org.ideaconsult.iuclidws.PropertiesUtil;
import org.ideaconsult.iuclidws.StubUtil;
import org.ideaconsult.iuclidws.session.SessionEngineFault;
import org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault;
import org.ideaconsult.iuclidws.session.SessionSingleton;
import org.ideaconsult.iuclidws.types.Types.BroadcastShowDocument;
import org.ideaconsult.iuclidws.types.Types.DocumentReferencePK;
import org.ideaconsult.iuclidws.types.Types.Receiver;
import org.ideaconsult.iuclidws.types.Types.Session;

/**
 * The Messaging Engine can be used to show a document on all IUCLID 5 
 * clients where a given user is logged in.
 * 
 * @author Nikolay Taskov
 */
public class MessagingEngine {
	
	// fields
	/** Messaging engine endpoint context */
	private String END_POINT_MESSAGING_ENGINE = "MessagingEngine";
	
	/** Current session */
	private Session session;
	
	/** Instance of MessagingEngineStub class */
	private MessagingEngineStub messagingEngineStub;
	
	private String target;
	
	/** The logger for this class */
	private final static Logger logger = Logger.getLogger(MessagingEngine.class);
	
	// constructors
	/**
	 * Init MessagingEngine class and logged in automatically.
	 * 
	 * @throws ClientServiceException 
	 * @throws SessionEngineFault 
	 * @throws SessionEngineNotAvailableFault 
	 */
	public MessagingEngine() throws SessionEngineNotAvailableFault, SessionEngineFault, ClientServiceException, AxisFault {
		this(SessionSingleton.getInstance().getSession());
	}
	
	/**
	 * Init MessagingEngine class with custom Session
	 * 
	 * @param session
	 */
	public MessagingEngine(Session session) throws AxisFault {
		this(session, PropertiesUtil.getTarget());
	}
	
	/**
	 * Init MessagingEngine class with custom Session and target
	 * 
	 * @param session
	 */
	public MessagingEngine(Session session, String target) throws AxisFault {
		this.session = session;
		this.target = target;
		
		messagingEngineStub = new MessagingEngineStub(StubUtil.concatEndpointAddress(
				this.target	, END_POINT_MESSAGING_ENGINE));
		StubUtil.initializeAxisService(messagingEngineStub._getServiceClient());		
	}
	
	// methods
	/**
	 * This operation sends a message to all logged in IUCLID 5 clients and requests
     * that clients that are operated by the given user (specified as receiver) open the
     * specified document.
	 * 
	 * @param receiver
	 * @param documentReferencePK
	 * @throws MessagingEngineNotAvailableFault thrown when e.g. the service is not 
	 * available to fulfill the request
	 * @throws MessagingEngineFault thrown if the Service is not able to fulfill your request. This may happen if the
     * Engine caught a back-end Exception.
	 */
	public void broadcastShowDocument(Receiver receiver, DocumentReferencePK documentReferencePK)
			throws MessagingEngineNotAvailableFault, MessagingEngineFault {
		
		try {
			BroadcastShowDocument broadcastShowDocument = new BroadcastShowDocument();
			broadcastShowDocument.setSession(session);
			broadcastShowDocument.setReceiver(receiver);
			broadcastShowDocument.setDocumentReferencePK(documentReferencePK);
			
			messagingEngineStub.broadcastShowDocument(broadcastShowDocument);
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
	}
}

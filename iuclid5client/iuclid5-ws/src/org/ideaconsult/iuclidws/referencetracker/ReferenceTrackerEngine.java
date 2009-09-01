package org.ideaconsult.iuclidws.referencetracker;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.ideaconsult.iuclidws.ClientServiceException;
import org.ideaconsult.iuclidws.PropertiesUtil;
import org.ideaconsult.iuclidws.StubUtil;
import org.ideaconsult.iuclidws.session.SessionEngineFault;
import org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault;
import org.ideaconsult.iuclidws.session.SessionSingleton;
import org.ideaconsult.iuclidws.types.Types.DocReferenceList;
import org.ideaconsult.iuclidws.types.Types.DocumentReferencePK;
import org.ideaconsult.iuclidws.types.Types.QueryInbound;
import org.ideaconsult.iuclidws.types.Types.QueryInboundResponse;
import org.ideaconsult.iuclidws.types.Types.QueryOutbound;
import org.ideaconsult.iuclidws.types.Types.QueryOutboundResponse;
import org.ideaconsult.iuclidws.types.Types.Session;

/**
 * The reference tracker engine tracks the linkage between IUCLID 5 documents. The methods
 * of the Reference Tracker Engine can be used to find out which documents reference a
 * document and which documents are referenced by a document. The Reference Tracker
 * Engine offers a very convenient way to get information on the relations between documents
 * without the need to scan the XML of the documents for references.
 * 
 * @author Nikolay Taskov
 */
public class ReferenceTrackerEngine {

	// fields
	/** Reference tracker engine endpoint context */
	private String END_POINT_REFERENCE_TRACKER_ENGINE = "ReferenceTrackerEngine";
	
	/** Current session */
	private Session session;
	
	/** Instance of ReferenceTrackerEngineStub class */
	private ReferenceTrackerEngineStub referenceTrackerEngineStub;
	
	private String target;
	
	/** The logger for this class */
	private final static Logger logger = Logger.getLogger(ReferenceTrackerEngine.class);
	
	// constructors
	/**
	 * Init ReferenceTrackerEngine class and logged in automatically.
	 * 
	 * @throws ClientServiceException 
	 * @throws SessionEngineFault 
	 * @throws SessionEngineNotAvailableFault 
	 */
	public ReferenceTrackerEngine() throws SessionEngineNotAvailableFault, SessionEngineFault, 
			ClientServiceException, AxisFault {
		this(SessionSingleton.getInstance().getSession());
	}
	
	/**
	 * Init ReferenceTrackerEngine class with custom Session
	 * 
	 * @param session
	 */
	public ReferenceTrackerEngine(Session session) throws AxisFault {
		this(session, PropertiesUtil.getTarget());
	}
	
	/**
	 * Init ReferenceTrackerEngine class with custom Session and target
	 * 
	 * @param session
	 */
	public ReferenceTrackerEngine(Session session, String target) throws AxisFault {
		this.session = session;
		this.target = target;
		
		referenceTrackerEngineStub = new ReferenceTrackerEngineStub(StubUtil.concatEndpointAddress(
				this.target	, END_POINT_REFERENCE_TRACKER_ENGINE));
		StubUtil.initializeAxisService(referenceTrackerEngineStub._getServiceClient());		
	}
	
	// methods
	/**
	 * This operation answers all references from other documents 
	 * to the given document reference.
	 * 
	 * @param documentReferencePK - the unique keys of document for which to 
	 * query the inbound references
	 * @return docReferenceList - a list of all documents that reference the document given in the IN
	 * parameter as list
	 * @throws ReferenceTrackerEngineNotAvailableFault thrown when e.g. the service is not available 
	 * to fulfill the request
	 * @throws ReferenceTrackerEngineFault thrown if the Service is not able to fulfill your request.
	 * This may happen if the Engine caught a back-end Exception.
	 */
	public DocReferenceList queryInbound(DocumentReferencePK documentReferencePK)
			throws ReferenceTrackerEngineNotAvailableFault, ReferenceTrackerEngineFault {
		
		try {
			QueryInbound queryInbound = new QueryInbound();
			queryInbound.setSession(session);
			queryInbound.setRefPK(documentReferencePK);
			QueryInboundResponse queryInboundResponse = referenceTrackerEngineStub.queryInbound(queryInbound);
			
			return queryInboundResponse.getDocReferenceList();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * This operation answers all references from the given document reference
	 * to other document.
	 * 
	 * @param documentReferencePK - The unique keys of document for which to 
	 * query the outbound references
	 * @return docReferenceList - a list of all documents that reference the document given in the IN
	 * parameter as list
	 * @throws ReferenceTrackerEngineNotAvailableFault thrown when e.g. the service is not available 
	 * to fulfill the request
	 * @throws ReferenceTrackerEngineFault thrown if the Service is not able to fulfill your request.
	 * This may happen if the Engine caught a back-end Exception.
	 */
	public DocReferenceList queryOutbound(DocumentReferencePK documentReferencePK)
			throws ReferenceTrackerEngineNotAvailableFault, ReferenceTrackerEngineFault {
		
		try {
			QueryOutbound queryOutbound = new QueryOutbound();
			queryOutbound.setSession(session);
			queryOutbound.setRefPK(documentReferencePK);
			QueryOutboundResponse queryOutboundResponse = referenceTrackerEngineStub.queryOutbound(queryOutbound);
			
			return queryOutboundResponse.getDocReferenceList();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
}

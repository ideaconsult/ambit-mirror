package org.ideaconsult.iuclidws.endpointaccess;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.ideaconsult.iuclidws.ClientServiceException;
import org.ideaconsult.iuclidws.PropertiesUtil;
import org.ideaconsult.iuclidws.StubUtil;
import org.ideaconsult.iuclidws.session.SessionEngineFault;
import org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault;
import org.ideaconsult.iuclidws.session.SessionSingleton;
import org.ideaconsult.iuclidws.types.Types.CopyEndpoint;
import org.ideaconsult.iuclidws.types.Types.CopyEndpointResponse;
import org.ideaconsult.iuclidws.types.Types.CopyEndpoints;
import org.ideaconsult.iuclidws.types.Types.CopyEndpointsResponse;
import org.ideaconsult.iuclidws.types.Types.CreateNewEndpoint;
import org.ideaconsult.iuclidws.types.Types.CreateNewEndpointResponse;
import org.ideaconsult.iuclidws.types.Types.CreateNewEndpointSummary;
import org.ideaconsult.iuclidws.types.Types.CreateNewEndpointSummaryResponse;
import org.ideaconsult.iuclidws.types.Types.DeleteEndpoint;
import org.ideaconsult.iuclidws.types.Types.DeleteEndpointResponse;
import org.ideaconsult.iuclidws.types.Types.DeleteEndpoints;
import org.ideaconsult.iuclidws.types.Types.DeleteEndpointsResponse;
import org.ideaconsult.iuclidws.types.Types.DetachEndpoint;
import org.ideaconsult.iuclidws.types.Types.DetachEndpointResponse;
import org.ideaconsult.iuclidws.types.Types.Document;
import org.ideaconsult.iuclidws.types.Types.DocumentReference;
import org.ideaconsult.iuclidws.types.Types.DocumentReferencePK;
import org.ideaconsult.iuclidws.types.Types.EndpointReferenceList;
import org.ideaconsult.iuclidws.types.Types.ReferenceEndpoint;
import org.ideaconsult.iuclidws.types.Types.ReferenceEndpointResponse;
import org.ideaconsult.iuclidws.types.Types.ReferenceEndpoints;
import org.ideaconsult.iuclidws.types.Types.ReferenceEndpointsResponse;
import org.ideaconsult.iuclidws.types.Types.Session;
import org.ideaconsult.iuclidws.types.Types.UpdateEndpoint;
import org.ideaconsult.iuclidws.types.Types.UpdateEndpointResponse;

/**
 * The Endpoint Access Engine offers convenient operations for endpoint (endpoint record,
 * endpoint study record and endpoint summary) handling. Normally, endpoint operations
 * modify the endpoint document and the enclosing container (template, substance, mixture).
 * The operations of the Endpoint Access Engine take care of all necessary steps when
 * handling endpoints.
 * 
 * @author Nikolay Taskov
 */
public class EndpointAccessEngine {
	
	// fields
	/** Endpoint access context */
	private String END_POINT_ACCESS_ENGINE = "EndpointAccessEngine";
	
	/** Current session */
	private Session session;
	
	/** Instance of EndpointAccessEngineStub class */
	private EndpointAccessEngineStub endpointAccessEngineStub;
	
	private String target;
	
	/** The logger for this class */
	private final static Logger logger = Logger.getLogger(EndpointAccessEngine.class);
	
	// constructors
	/**
	 * Init EndpointAccessEngine class and logged in automatically.
	 * 
	 * @throws ClientServiceException 
	 * @throws SessionEngineFault 
	 * @throws SessionEngineNotAvailableFault 
	 */
	public EndpointAccessEngine() throws SessionEngineNotAvailableFault, SessionEngineFault, ClientServiceException, AxisFault {
		this(SessionSingleton.getInstance().getSession());
	}
	
	/**
	 * Init EndpointAccessEngine class with custom Session
	 * 
	 * @param session
	 */
	public EndpointAccessEngine(Session session) throws AxisFault {
		this(session, PropertiesUtil.getTarget());
	}
	
	/**
	 * Init EndpointAccessEngine class with custom Session and target
	 * 
	 * @param session
	 */
	public EndpointAccessEngine(Session session, String target) throws AxisFault {
		this.session = session;
		this.target = target;
		
		endpointAccessEngineStub = new EndpointAccessEngineStub(StubUtil.concatEndpointAddress(
				this.target	, END_POINT_ACCESS_ENGINE));
		StubUtil.initializeAxisService(endpointAccessEngineStub._getServiceClient());		
	}
	
	
	// methods
	/**
	 * Create a new endpoint of the given type and add the created endpoint to an
     * endpoint container document (substance, mixture or template).
	 * 
	 * @param document - Endpoint container document (substance, mixture or template)
	 * @param kind - the valid endpoint kind (e.g. EC_ABOVE_GROUND_TOX) as string
	 * according to the IUCLID 5 format.
	 * @param name - the name of the endpoint document as string.
	 * @return document - The updated endpoint container document (substance, mixture or template)
	 * as Document. The caller must make sure to use this in subsequent calls.
	 * To finalize the creation of the endpoint, this document has to be committed.
	 * @throws EndpointAccessEngineNotAvailableFault Is thrown when e.g. 
	 * the back-end service is not available to fulfill the request
	 * @throws EndpointAccessEngineFault Is thrown if Endpoint Access Engine is not able to fulfill your request. This
	 * may happen if the Engine caught a back-end Exception
	 */
	public Document createNewEndpoint(Document document, String kind, String name) 
			throws EndpointAccessEngineNotAvailableFault, EndpointAccessEngineFault {
		
		try {
			CreateNewEndpoint createNewEndpoint = new CreateNewEndpoint();
			createNewEndpoint.setSession(session);
			createNewEndpoint.setContainerDoc(document);
			createNewEndpoint.setKind(kind);
			createNewEndpoint.setName(name);
			CreateNewEndpointResponse createEndpointResponse = 
				endpointAccessEngineStub.createNewEndpoint(createNewEndpoint);
			
			return createEndpointResponse.getCreateNewEndpointResponseType().getContainerDoc();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (DocumentFault e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Create a new endpoint of the given type and add the created endpoint to an
     * endpoint container document (substance, mixture or template).
	 * 
	 * @param document - Endpoint container document (substance, mixture or template)
	 * @param kind - the valid endpoint kind (e.g. EC_ABOVE_GROUND_TOX) as string
	 * according to the IUCLID 5 format.
	 * @param name - the name of the endpoint document as string.
	 * @return documentReference - The reference of the created endpoint document as DocumentReference
	 * as Document. The caller must make sure to use this in subsequent calls.
	 * To finalize the creation of the endpoint, this document has to be committed.
	 * @throws EndpointAccessEngineNotAvailableFault Is thrown when e.g. 
	 * the back-end service is not available to fulfill the request
	 * @throws EndpointAccessEngineFault Is thrown if Endpoint Access Engine is not able to fulfill your request. This
	 * may happen if the Engine caught a back-end Exception
	 */
	public DocumentReference createNewEndpointOnRef(Document document, String kind, String name) 
			throws EndpointAccessEngineNotAvailableFault, EndpointAccessEngineFault {
		
		try {
			CreateNewEndpoint createNewEndpoint = new CreateNewEndpoint();
			createNewEndpoint.setSession(session);
			createNewEndpoint.setContainerDoc(document);
			createNewEndpoint.setKind(kind);
			createNewEndpoint.setName(name);
			CreateNewEndpointResponse createEndpointResponse = 
				endpointAccessEngineStub.createNewEndpoint(createNewEndpoint);
			
			return createEndpointResponse.getCreateNewEndpointResponseType().getEndpointReferenceRef();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (DocumentFault e) {
			logger.error(e.getMessage(), e);
		}

		return null;
	}
	
	/**
	 * Create a new endpoint summary of the given type and add the created
     * endpoint summary to an endpoint container document (substance, mixture or template)
	 * 
	 * @param document - Endpoint container document (substance, mixture or template)
	 * @param kind - the valid endpoint kind (e.g. EC_ABOVE_GROUND_TOX) as string
	 * according to the IUCLID 5 format.
	 * @param name - the name of the endpoint document as string.
	 * @return document - The updated endpoint container document (substance, mixture or template)
	 * as Document. The caller must make sure to use this in subsequent calls.
	 * To finalize the creation of the endpoint, this document has to be committed.
	 * @throws EndpointAccessEngineNotAvailableFault Is thrown when e.g. 
	 * the back-end service is not available to fulfill the request
	 * @throws EndpointAccessEngineFault Is thrown if Endpoint Access Engine is not able to fulfill your request. This
	 * may happen if the Engine caught a back-end Exception
	 */
	public Document createNewEndpointSummary(Document document, String kind, String name) 
			throws EndpointAccessEngineNotAvailableFault, EndpointAccessEngineFault {
		
		try {
			CreateNewEndpointSummary createNewEndpointSummary = new CreateNewEndpointSummary();
			createNewEndpointSummary.setSession(session);
			createNewEndpointSummary.setContainerDoc(document);
			createNewEndpointSummary.setKind(kind);
			createNewEndpointSummary.setName(name);
			CreateNewEndpointSummaryResponse createNewEndpointSummaryResponse = 
				endpointAccessEngineStub.createNewEndpointSummary(createNewEndpointSummary);
			
			return createNewEndpointSummaryResponse.getCreateNewEndpointSummaryResponseType().getContainerDoc();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (DocumentFault e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Create a new endpoint summary of the given type and add the created
     * endpoint summary to an endpoint container document (substance, mixture or template)
	 * 
	 * @param document - Endpoint container document (substance, mixture or template)
	 * @param kind - the valid endpoint kind (e.g. EC_ABOVE_GROUND_TOX) as string
	 * according to the IUCLID 5 format.
	 * @param name - the name of the endpoint document as string.
	 * @return documentReference - The reference of the created endpoint document as DocumentReference
	 * as Document. The caller must make sure to use this in subsequent calls.
	 * To finalize the creation of the endpoint, this document has to be committed.
	 * @throws EndpointAccessEngineNotAvailableFault Is thrown when e.g. 
	 * the back-end service is not available to fulfill the request
	 * @throws EndpointAccessEngineFault Is thrown if Endpoint Access Engine is not able to fulfill your request. This
	 * may happen if the Engine caught a back-end Exception
	 */
	public DocumentReference createNewEndpointSummaryOnRef(Document document, String kind, String name) 
			throws EndpointAccessEngineNotAvailableFault, EndpointAccessEngineFault {
		
		try {
			CreateNewEndpointSummary createNewEndpointSummary = new CreateNewEndpointSummary();
			createNewEndpointSummary.setSession(session);
			createNewEndpointSummary.setContainerDoc(document);
			createNewEndpointSummary.setKind(kind);
			createNewEndpointSummary.setName(name);
			CreateNewEndpointSummaryResponse createNewEndpointSummaryResponse = 
				endpointAccessEngineStub.createNewEndpointSummary(createNewEndpointSummary);
			
			return createNewEndpointSummaryResponse.getCreateNewEndpointSummaryResponseType().getEndpointReferenceRef();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (DocumentFault e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Copies an endpoint from one endpoint container document (substance, mixture or template) 
	 * to another endpoint container document.
	 * 
	 * @param document - Endpoint container document (substance, mixture or template)
	 * @param documentReferencePK - The unique key of the source endpoint as DocumentReferencePK
	 * @return document - The updated endpoint container document (substance, mixture or template)
	 * as Document. The caller must make sure to use this in subsequent calls.
	 * To finalize the creation of the endpoint, this document has to be committed.
	 * @throws EndpointAccessEngineNotAvailableFault Is thrown when e.g. 
	 * the back-end service is not available to fulfill the request
	 * @throws EndpointAccessEngineFault Is thrown if Endpoint Access Engine is not able to fulfill your request. This
	 * may happen if the Engine caught a back-end Exception
	 */
	public Document copyEndpoint(Document document, DocumentReferencePK documentReferencePK) 
			throws EndpointAccessEngineNotAvailableFault, EndpointAccessEngineFault {
		
		try {
			CopyEndpoint copyEndpoint = new CopyEndpoint();
			copyEndpoint.setSession(session);
			copyEndpoint.setContainerDoc(document);
			copyEndpoint.setEndpointRef(documentReferencePK);
			CopyEndpointResponse copyEndpointResponse = 
				endpointAccessEngineStub.copyEndpoint(copyEndpoint);
			
			return copyEndpointResponse.getCopyEndpointResponseType().getContainerDoc();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (DocumentFault e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Copies an endpoint from one endpoint container document (substance, mixture or template) 
	 * to another endpoint container document.
	 * 
	 * @param document - Endpoint container document (substance, mixture or template)
	 * @param documentReferencePK - The unique key of the source endpoint as DocumentReferencePK
	 * @return documentReference - The reference of the created endpoint document as DocumentReference
	 * as Document. The caller must make sure to use this in subsequent calls.
	 * To finalize the creation of the endpoint, this document has to be committed.
	 * @throws EndpointAccessEngineNotAvailableFault Is thrown when e.g. 
	 * the back-end service is not available to fulfill the request
	 * @throws EndpointAccessEngineFault Is thrown if Endpoint Access Engine is not able to fulfill your request. This
	 * may happen if the Engine caught a back-end Exception
	 */
	public DocumentReference copyEndpointOnDocRef(Document document, DocumentReferencePK documentReferencePK) 
			throws EndpointAccessEngineNotAvailableFault, EndpointAccessEngineFault {
		
		try {
			CopyEndpoint copyEndpoint = new CopyEndpoint();
			copyEndpoint.setSession(session);
			copyEndpoint.setContainerDoc(document);
			copyEndpoint.setEndpointRef(documentReferencePK);
			CopyEndpointResponse copyEndpointResponse = 
				endpointAccessEngineStub.copyEndpoint(copyEndpoint);
			
			return copyEndpointResponse.getCopyEndpointResponseType().getEndpointCopyRef();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (DocumentFault e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Copies multiple endpoints from one endpoint container document (substance, mixture or template) 
	 * to another endpoint container document. 
     * 
	 * @param document - Endpoint container document (substance, mixture or template)
	 * @param documentReferencePKsArray - The unique keys of the source endpoints as DocumentReferencePK
	 * @return document - The updated endpoint container document (substance, mixture or template)
	 * as Document. The caller must make sure to use this in subsequent calls.
	 * To finalize the creation of the endpoint, this document has to be committed.
	 * @throws EndpointAccessEngineNotAvailableFault Is thrown when e.g. 
	 * the back-end service is not available to fulfill the request
	 * @throws EndpointAccessEngineFault Is thrown if Endpoint Access Engine is not able to fulfill your request. This
	 * may happen if the Engine caught a back-end Exception
	 */
	public Document copyEndpoints(Document document, DocumentReferencePK[] documentReferencePKsArray) 
			throws EndpointAccessEngineNotAvailableFault, EndpointAccessEngineFault {
		
		try {
			CopyEndpoints copyEndpoints = new CopyEndpoints();
			copyEndpoints.setSession(session);
			copyEndpoints.setContainerDoc(document);
			copyEndpoints.setEndpointRefs(documentReferencePKsArray);
			CopyEndpointsResponse copyEndpointsResponse = endpointAccessEngineStub.copyEndpoints(copyEndpoints);
			
			return copyEndpointsResponse.getCopyEndpointsResponseType().getContainerDoc();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (DocumentFault e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Copies multiple endpoints from one endpoint container document (substance, mixture or template) 
	 * to another endpoint container document. 
     * 
	 * @param document - Endpoint container document (substance, mixture or template)
	 * @param documentReferencePKsArray - The unique keys of the source endpoints as DocumentReferencePK
	 * @return endpointReferenceList - the references of the copied endpoint documents as EndpointReferenceList.
	 * To finalize the creation of the endpoint, this document has to be committed.
	 * @throws EndpointAccessEngineNotAvailableFault Is thrown when e.g. 
	 * the back-end service is not available to fulfill the request
	 * @throws EndpointAccessEngineFault Is thrown if Endpoint Access Engine is not able to fulfill your request. This
	 * may happen if the Engine caught a back-end Exception
	 */
	public EndpointReferenceList copyEndpointsOnRefList(Document document, DocumentReferencePK[] documentReferencePKsArray) 
			throws EndpointAccessEngineNotAvailableFault, EndpointAccessEngineFault {
		
		try {
			CopyEndpoints copyEndpoints = new CopyEndpoints();
			copyEndpoints.setSession(session);
			copyEndpoints.setContainerDoc(document);
			copyEndpoints.setEndpointRefs(documentReferencePKsArray);
			CopyEndpointsResponse copyEndpointsResponse = endpointAccessEngineStub.copyEndpoints(copyEndpoints);
			
			return copyEndpointsResponse.getCopyEndpointsResponseType().getEndpointCopyRefs();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (DocumentFault e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Delete a single endpoint from persistence. The reference to the endpoint is also
     * automatically removed from its parent document. (substance, mixture or template)
	 * 
	 * @param document - Endpoint container document (substance, mixture or template)
	 * @param documentReferencePK - The unique key of the endpoint to be deleted as DocumentReferencePK
	 * @return The updated endpoint container document (substance, mixture or template) as Document. 
	 * The caller must make sure to use this in subsequent calls.
	 * @throws EndpointAccessEngineNotAvailableFault Is thrown when e.g. 
	 * the back-end service is not available to fulfill the request
	 * @throws EndpointAccessEngineFault Is thrown if Endpoint Access Engine is not able to fulfill your request. This
	 * may happen if the Engine caught a back-end Exception
	 */
	public Document deleteEndpoint(Document document, DocumentReferencePK documentReferencePK) 
			throws EndpointAccessEngineNotAvailableFault, EndpointAccessEngineFault {
		
		try {
			DeleteEndpoint deleteEndpoint = new DeleteEndpoint();
			deleteEndpoint.setSession(session);
			deleteEndpoint.setContainerDoc(document);
			deleteEndpoint.setEndpointRef(documentReferencePK);
			DeleteEndpointResponse deleteEndpointResponse = endpointAccessEngineStub.deleteEndpoint(deleteEndpoint);
			
			return deleteEndpointResponse.getDeleteEndpointResponseType().getContainerDoc();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (DocumentFault e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Delete a multiple endpoint from persistence. The reference to the endpoint is
     * also automatically removed from its parent document (substance, mixture or template).
	 * 
	 * @param document - Endpoint container document (substance, mixture or template)
	 * @param documentReferencePKsArray - The unique keys of the DocumentReferencePK
	 * @return The updated endpoint container document (substance, mixture or template) as Document. 
	 * The caller must make sure to use this in subsequent calls.
	 * @throws EndpointAccessEngineNotAvailableFault Is thrown when e.g. 
	 * the back-end service is not available to fulfill the request
	 * @throws EndpointAccessEngineFault Is thrown if Endpoint Access Engine is not able to fulfill your request. This
	 * may happen if the Engine caught a back-end Exception
	 */
	public Document deleteEndpoints(Document document, DocumentReferencePK[] documentReferencePKsArray) 
			throws EndpointAccessEngineNotAvailableFault, EndpointAccessEngineFault {
		
		try {
			DeleteEndpoints deleteEndpoints = new DeleteEndpoints();
			deleteEndpoints.setSession(session);
			deleteEndpoints.setContainerDoc(document);
			deleteEndpoints.setEndpointRefs(documentReferencePKsArray);
			DeleteEndpointsResponse deleteEndpointsResponse = endpointAccessEngineStub.deleteEndpoints(deleteEndpoints);
			
			return deleteEndpointsResponse.getDeleteEndpointResponseType().getContainerDoc();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (DocumentFault e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Reference the specified endpoint to the specified new owner (substance, mixture or template) 
	 * document by copying it and establishing a special local
     * reference to the original (filling in the .origin attribute).
	 * 
	 * @param document - Endpoint container document (substance, mixture or template)
	 * @param documentReferencePK - The unique key of the endpoint to be referenced (the source of the
	 * reference) as DocumentReferencePK
	 * @param notes - A note to be added to the origin reference
	 * @return document - The updated endpoint container document (substance, mixture or template) as Document. 
	 * The caller must make sure to use this in subsequent calls.
	 * @throws EndpointAccessEngineNotAvailableFault Is thrown when e.g. 
	 * the back-end service is not available to fulfill the request
	 * @throws EndpointAccessEngineFault Is thrown if Endpoint Access Engine is not able to fulfill your request. This
	 * may happen if the Engine caught a back-end Exception
	 */
	public Document referenceEndpoint(Document document, DocumentReferencePK documentReferencePK, String notes) 
			throws EndpointAccessEngineNotAvailableFault, EndpointAccessEngineFault {
		
		try {
			ReferenceEndpoint referenceEndpoint = new ReferenceEndpoint();
			referenceEndpoint.setSession(session);
			referenceEndpoint.setContainerDoc(document);
			referenceEndpoint.setEndpointRef(documentReferencePK);
			referenceEndpoint.setNotes(notes);
			ReferenceEndpointResponse referenceEndpointResponse = endpointAccessEngineStub.referenceEndpoint(referenceEndpoint);
			
			return referenceEndpointResponse.getReferenceEndpointResponseType().getContainerDoc();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (DocumentFault e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Reference the specified endpoint to the specified new owner (substance, mixture or template) 
	 * document by copying it and establishing a special local
     * reference to the original (filling in the .origin attribute).
	 * 
	 * @param document - Endpoint container document (substance, mixture or template)
	 * @param documentReferencePK - The unique key of the endpoint to be referenced (the source of the
	 * reference) as DocumentReferencePK
	 * @param notes - A note to be added to the origin reference
	 * @return documentReference - The unique keys of the new endpoint reference as DocumentReference
	 * @throws EndpointAccessEngineNotAvailableFault Is thrown when e.g. 
	 * the back-end service is not available to fulfill the request
	 * @throws EndpointAccessEngineFault Is thrown if Endpoint Access Engine is not able to fulfill your request. This
	 * may happen if the Engine caught a back-end Exception
	 */
	public DocumentReference referenceEndpointOnDocRef(Document document, DocumentReferencePK documentReferencePK, String notes) 
			throws EndpointAccessEngineNotAvailableFault, EndpointAccessEngineFault {
		
		try {
			ReferenceEndpoint referenceEndpoint = new ReferenceEndpoint();
			referenceEndpoint.setSession(session);
			referenceEndpoint.setContainerDoc(document);
			referenceEndpoint.setEndpointRef(documentReferencePK);
			referenceEndpoint.setNotes(notes);
			ReferenceEndpointResponse referenceEndpointResponse = endpointAccessEngineStub.referenceEndpoint(referenceEndpoint);
			
			return referenceEndpointResponse.getReferenceEndpointResponseType().getEndpointReferenceRef();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (DocumentFault e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Reference the specified endpoints to the specified new owner (substance, mixture or template)
	 * document by copying it and establishing a special local
	 * reference to the original (filling in the origin attribute).
	 * 
	 * @param document - Endpoint container document (substance, mixture or template)
	 * @param documentReferencePKsArray - the unique keys of the endpoinst to be referenced 
	 * (the source of the reference) as DocumentReferencePK
	 * @param notes - a note to be added to the origin references
	 * @return document - The updated endpoint container document (substance, mixture or template) as Document. 
	 * The caller must make sure to use this in subsequent calls.
	 * @throws EndpointAccessEngineNotAvailableFault Is thrown when e.g. 
	 * the back-end service is not available to fulfill the request
	 * @throws EndpointAccessEngineFault Is thrown if Endpoint Access Engine is not able to fulfill your request. This
	 * may happen if the Engine caught a back-end Exception
	 */
	public Document referenceEndpoints(Document document, DocumentReferencePK[] documentReferencePKsArray, String notes)
			throws EndpointAccessEngineNotAvailableFault, EndpointAccessEngineFault {
		
		try {
			ReferenceEndpoints referenceEndpoints = new ReferenceEndpoints();
			referenceEndpoints.setSession(session);
			referenceEndpoints.setContainerDoc(document);
			referenceEndpoints.setEndpointRefs(documentReferencePKsArray);
			referenceEndpoints.setNotes(notes);
			ReferenceEndpointsResponse referenceEndpointsResponse = endpointAccessEngineStub.referenceEndpoints(referenceEndpoints);
			
			return referenceEndpointsResponse.getReferenceEndpointsResponseType().getContainerDoc();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (DocumentFault e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Reference the specified endpoints to the specified new owner (substance, mixture or template)
	 * document by copying it and establishing a special local
	 * reference to the original (filling in the origin attribute).
	 * 
	 * @param document - Endpoint container document (substance, mixture or template)
	 * @param documentReferencePKsArray - the unique keys of the endpoinst to be referenced 
	 * (the source of the reference) as DocumentReferencePK
	 * @param notes - a note to be added to the origin references
	 * @return referenceList - The unique keys of the new endpoint references as EndpointReferenceList
	 * @throws EndpointAccessEngineNotAvailableFault Is thrown when e.g. 
	 * the back-end service is not available to fulfill the request
	 * @throws EndpointAccessEngineFault Is thrown if Endpoint Access Engine is not able to fulfill your request. This
	 * may happen if the Engine caught a back-end Exception
	 */
	public EndpointReferenceList referenceEndpointsOnRefList(Document document, DocumentReferencePK[] documentReferencePKsArray, 
			String notes)
			throws EndpointAccessEngineNotAvailableFault, EndpointAccessEngineFault {
		
		try {
			ReferenceEndpoints referenceEndpoints = new ReferenceEndpoints();
			referenceEndpoints.setSession(session);
			referenceEndpoints.setContainerDoc(document);
			referenceEndpoints.setEndpointRefs(documentReferencePKsArray);
			referenceEndpoints.setNotes(notes);
			ReferenceEndpointsResponse referenceEndpointsResponse = endpointAccessEngineStub.referenceEndpoints(referenceEndpoints);
			
			return referenceEndpointsResponse.getReferenceEndpointsResponseType().getEndpointReferenceRefs();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (DocumentFault e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Update the specified referencing endpoint document accordingly to 
	 * the content of its original endpoint.
	 * 
	 * @param containerDocument - the endpoint container document (substance, mixture or template) as Document
	 * @param endpointDocument - the endpoint reference document to be updated as Document
	 * @return containerDocument - The updated endpoint container document (substance, mixture or template) as Document. 
	 * The caller must make sure to use this in subsequent calls.
	 * @throws EndpointAccessEngineNotAvailableFault Is thrown when e.g. 
	 * the back-end service is not available to fulfill the request
	 * @throws EndpointAccessEngineFault Is thrown if Endpoint Access Engine is not able to fulfill your request. This
	 * may happen if the Engine caught a back-end Exception
	 */
	public Document updateEndpoint(Document containerDocument, Document endpointDocument)
			throws EndpointAccessEngineNotAvailableFault, EndpointAccessEngineFault {
		
		try {
			UpdateEndpoint updateEndpoint = new UpdateEndpoint();
			updateEndpoint.setSession(session);
			updateEndpoint.setContainerDoc(containerDocument);
			updateEndpoint.setEndpoint(endpointDocument);
			UpdateEndpointResponse updateEndpointResponse = endpointAccessEngineStub.updateEndpoint(updateEndpoint);
			
			return updateEndpointResponse.getUpdateEndpointResponseType().getContainerDoc();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (DocumentFault e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Update the specified referencing endpoint document accordingly to 
	 * the content of its original endpoint.
	 * 
	 * @param containerDocument - the endpoint container document (substance, mixture or template) as Document
	 * @param endpointDocument - the modified endpoint reference document as Document
	 * @return endpointDocument - The updated endpoint container document (substance, mixture or template) as Document. 
	 * The caller must make sure to use this in subsequent calls.
	 * @throws EndpointAccessEngineNotAvailableFault Is thrown when e.g. 
	 * the back-end service is not available to fulfill the request
	 * @throws EndpointAccessEngineFault Is thrown if Endpoint Access Engine is not able to fulfill your request. This
	 * may happen if the Engine caught a back-end Exception
	 */
	public Document updateEndpointAsEndpointDocument(Document containerDocument, Document endpointDocument)
			throws EndpointAccessEngineNotAvailableFault, EndpointAccessEngineFault {
		
		try {
			UpdateEndpoint updateEndpoint = new UpdateEndpoint();
			updateEndpoint.setSession(session);
			updateEndpoint.setContainerDoc(containerDocument);
			updateEndpoint.setEndpoint(endpointDocument);
			UpdateEndpointResponse updateEndpointResponse = endpointAccessEngineStub.updateEndpoint(updateEndpoint);
			
			return updateEndpointResponse.getUpdateEndpointResponseType().getEndpoint();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (DocumentFault e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Detach the referencing endpoint from its original endpoint document. The
     * reference to the endpoint is also automatically updated and the endpoint
     * container document (substance, mixture or template) is modified. After the
     * operation, two separated, independent endpoints exist.
	 * 
	 * @param containerDocument - the endpoint container document (substance, mixture or template) as Document
	 * @param endpointDocument - the modified endpoint reference document as Document
	 * @return containerDocument - The updated endpoint container document (substance, mixture or template) as Document. 
	 * The caller must make sure to use this in subsequent calls.
	 * @throws EndpointAccessEngineNotAvailableFault Is thrown when e.g. 
	 * the back-end service is not available to fulfill the request
	 * @throws EndpointAccessEngineFault Is thrown if Endpoint Access Engine is not able to fulfill your request. This
	 * may happen if the Engine caught a back-end Exception
	 */
	public Document detachEndpoint(Document containerDocument, Document endpointDocument)
			throws EndpointAccessEngineNotAvailableFault, EndpointAccessEngineFault {
		
		try {
			DetachEndpoint detachEndpoint = new DetachEndpoint();
			detachEndpoint.setSession(session);
			detachEndpoint.setContainerDoc(containerDocument);
			detachEndpoint.setEndpoint(endpointDocument);
			DetachEndpointResponse detachEndpointResponse = endpointAccessEngineStub.detachEndpoint(detachEndpoint);
			
			return detachEndpointResponse.getDetachEndpointResponseType().getContainerDoc();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (DocumentFault e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Detach the referencing endpoint from its original endpoint document. The
     * reference to the endpoint is also automatically updated and the endpoint
     * container document (substance, mixture or template) is modified. After the
     * operation, two separated, independent endpoints exist.
	 * 
	 * @param containerDocument - the endpoint container document (substance, mixture or template) as Document
	 * @param endpointDocument - the modified endpoint reference document as Document
	 * @return endpointDocument - The updated endpoint container document (substance, mixture or template) as Document. 
	 * The caller must make sure to use this in subsequent calls.
	 * @throws EndpointAccessEngineNotAvailableFault Is thrown when e.g. 
	 * the back-end service is not available to fulfill the request
	 * @throws EndpointAccessEngineFault Is thrown if Endpoint Access Engine is not able to fulfill your request. This
	 * may happen if the Engine caught a back-end Exception
	 */
	public Document detachEndpointAsEndpointDocument(Document containerDocument, Document endpointDocument)
			throws EndpointAccessEngineNotAvailableFault, EndpointAccessEngineFault {
		
		try {
			DetachEndpoint detachEndpoint = new DetachEndpoint();
			detachEndpoint.setSession(session);
			detachEndpoint.setContainerDoc(containerDocument);
			detachEndpoint.setEndpoint(endpointDocument);
			DetachEndpointResponse detachEndpointResponse = endpointAccessEngineStub.detachEndpoint(detachEndpoint);
			
			return detachEndpointResponse.getDetachEndpointResponseType().getEndpoint();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (DocumentFault e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
}

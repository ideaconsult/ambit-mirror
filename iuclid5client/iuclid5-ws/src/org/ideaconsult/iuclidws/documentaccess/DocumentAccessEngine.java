package org.ideaconsult.iuclidws.documentaccess;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.ideaconsult.iuclidws.ClientServiceException;
import org.ideaconsult.iuclidws.PropertiesUtil;
import org.ideaconsult.iuclidws.StubUtil;
import org.ideaconsult.iuclidws.session.SessionEngineFault;
import org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault;
import org.ideaconsult.iuclidws.session.SessionSingleton;
import org.ideaconsult.iuclidws.types.Types.CloseDocument;
import org.ideaconsult.iuclidws.types.Types.CloseDocumentResponse;
import org.ideaconsult.iuclidws.types.Types.CommitDocument;
import org.ideaconsult.iuclidws.types.Types.CommitDocumentResponse;
import org.ideaconsult.iuclidws.types.Types.CompareDocuments;
import org.ideaconsult.iuclidws.types.Types.CompareDocumentsResponse;
import org.ideaconsult.iuclidws.types.Types.CreateDocument;
import org.ideaconsult.iuclidws.types.Types.CreateDocumentResponse;
import org.ideaconsult.iuclidws.types.Types.DeleteDocument;
import org.ideaconsult.iuclidws.types.Types.DeleteDocumentResponse;
import org.ideaconsult.iuclidws.types.Types.DetermineDocumentType;
import org.ideaconsult.iuclidws.types.Types.DetermineDocumentTypeResponse;
import org.ideaconsult.iuclidws.types.Types.Document;
import org.ideaconsult.iuclidws.types.Types.DocumentComparisonStatus;
import org.ideaconsult.iuclidws.types.Types.DocumentReferencePK;
import org.ideaconsult.iuclidws.types.Types.DocumentReferencePKType;
import org.ideaconsult.iuclidws.types.Types.DocumentType;
import org.ideaconsult.iuclidws.types.Types.ExistsDocument;
import org.ideaconsult.iuclidws.types.Types.ExistsDocumentResponse;
import org.ideaconsult.iuclidws.types.Types.FetchDocument;
import org.ideaconsult.iuclidws.types.Types.FetchDocumentResponse;
import org.ideaconsult.iuclidws.types.Types.OpenDocumentForReading;
import org.ideaconsult.iuclidws.types.Types.OpenDocumentForReadingResponse;
import org.ideaconsult.iuclidws.types.Types.OpenDocumentForWriting;
import org.ideaconsult.iuclidws.types.Types.OpenDocumentForWritingResponse;
import org.ideaconsult.iuclidws.types.Types.Session;

/**
 * 
 * @author Nikolay Taskov
 */
public class DocumentAccessEngine {
	
	// fields
	/** Document access endpoint context */
	private String END_POINT_DOCUMENT_ACCESS_ENGINE = "DocumentAccessEngine";
	
	/** Current session */
	private Session session;
	
	/** Instance of DocumentAccessEngineStub class */
	private DocumentAccessEngineStub documentAccessEngineStub;
	
	private String target;
	
	/** The logger for this class */
	private final static Logger logger = Logger.getLogger(DocumentAccessEngine.class);
	
	// constructors
	/**
	 * Init DocumentAccessEngine class and logged in automatically.
	 * 
	 * @throws ClientServiceException 
	 * @throws SessionEngineFault 
	 * @throws SessionEngineNotAvailableFault 
	 */
	public DocumentAccessEngine() throws SessionEngineNotAvailableFault, SessionEngineFault, ClientServiceException, AxisFault {
		this(SessionSingleton.getInstance().getSession());
	}
	
	/**
	 * Init DocumentAccessEngine class with custom Session
	 * 
	 * @param session
	 */
	public DocumentAccessEngine(Session session) throws AxisFault {
		this(session, PropertiesUtil.getTarget());
	}
	
	/**
	 * Init DocumentAccessEngine class with custom Session and target
	 * 
	 * @param session
	 */
	public DocumentAccessEngine(Session session, String target) throws AxisFault {
		this.session = session;
		this.target = target;
		
		documentAccessEngineStub = new DocumentAccessEngineStub(StubUtil.concatEndpointAddress(
				this.target	, END_POINT_DOCUMENT_ACCESS_ENGINE));
		StubUtil.initializeAxisService(documentAccessEngineStub._getServiceClient());		
	}
	
	// TODO: DocumentReferencePKType and others class parameters are 
	// very strange for initialization
	// TODO: Boolean - return null descriptions
	
	/**
	 * Verify if the Unique Key points to an existing document
	 * 
	 * @param documentReferencePKType
	 * @return a Boolean flag - if existing true, otherwise false
	 * @throws DocumentAccessEngineNotAvailableFault Is thrown when e.g. the back-end service 
	 * is not available to fulfill the request
	 * @throws DocumentAccessEngineFault Is thrown if Document Access Engine is not able to fulfill your request.
	 * This may happen if the Engine caught a back-end Exception
	 * @throws DocumentNotFoundFault Is a tagged exception used to inform the caller that the requested document
	 * cannot be found
	 * @throws DocumentAccessFault Is a tagged exception used to tell the caller that the desired access to a
	 * document is prohibited due to sharing restriction
	 */
	public Boolean existsDocument(DocumentReferencePK documentReferencePK) throws DocumentAccessEngineNotAvailableFault, 
			DocumentAccessEngineFault, DocumentNotFoundFault, DocumentAccessFault {
		
		try {
			ExistsDocument existsDocument = new ExistsDocument();
			existsDocument.setSession(session);
			existsDocument.setDocumentReferencePK(documentReferencePK);
			ExistsDocumentResponse existsDocumentResponse = documentAccessEngineStub.existsDocument(existsDocument);
			
			return existsDocumentResponse.getIsExisting();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Return the Type of the associated document by its UniqueKey
	 * 
	 * @param documentReferencePK
	 * @return documentType
	 * @throws DocumentAccessEngineNotAvailableFault Is thrown when e.g. the back-end service 
	 * is not available to fulfill the request
	 * @throws DocumentAccessEngineFault Is thrown if Document Access Engine is not able to fulfill your request. 
	 * This may happen if the Engine caught a back-end Exception
	 * @throws DocumentNotFoundFault Is a tagged exception used to inform the caller that the requested document
	 * cannot be found
	 */
	public DocumentType determineDocumentType(DocumentReferencePK documentReferencePK) throws DocumentAccessEngineNotAvailableFault, 
			DocumentAccessEngineFault, DocumentNotFoundFault {
		
		try {
			DetermineDocumentType determineDocumentType = new DetermineDocumentType();
			determineDocumentType.setSession(session);
			determineDocumentType.setDocumentReferencePK(documentReferencePK);
			DetermineDocumentTypeResponse determineDocumentTypeResponse = 
				documentAccessEngineStub.determineDocumentType(determineDocumentType);
			
			return determineDocumentTypeResponse.getDocumentType();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Release the reader or writer lock of the current session on document the
     * specified by the given UniqueKey.
     * <p><b>Note:</b> <i>Uncommitted changes will be lost from the server-side point of view.</i>
	 * 
	 * @param documentReferencePK
	 * @return True to explicitly notify that everything was OK otherwise will throw
	 * a detailed exception, otherwise return false.
	 * @throws DocumentAccessEngineNotAvailableFault Is thrown if Document Access Engine is not able to fulfill your request. 
	 * This may happen if the Engine caught a back-end Exception
	 * @throws DocumentNotFoundFault Is a tagged exception used to inform the caller that the requested document
	 * cannot be found
	 * @throws DocumentAccessFault Is a tagged exception used to tell the caller that the desired access to a
	 * document is prohibited due to sharing restriction
	 */
	public Boolean closeDocument(DocumentReferencePK documentReferencePK) throws DocumentAccessEngineNotAvailableFault, 
			DocumentNotFoundFault, DocumentAccessFault {
		
		try {
			CloseDocument closeDocument = new CloseDocument();
			closeDocument.setSession(session);
			closeDocument.setDocumentReferencePK(documentReferencePK);
			CloseDocumentResponse closeDocumentResponse = documentAccessEngineStub.closeDocument(closeDocument);
			return closeDocumentResponse.getSuccess();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Fetch a document without setting any access locks. This means that no lock
	 * would be created on the server side.
     * Obviously this might return the same result as openDocumentForReading() method
     * but fetchDocument will close the document immediately and the web service
     * customer does not have to do this. If you are using openDocumentForReading
     * you have to call closeDocument after you have finished your process.
     * <p>The structure of the XML document fits to the official public export format of the
     * IUCLID 5 System.
	 * 
	 * @param documentReferencePK
	 * @return document
	 * @throws DocumentAccessEngineNotAvailableFault Is thrown when e.g. the back-end service 
	 * is not available to fulfill the request
	 * @throws DocumentAccessEngineFault Is thrown if Document Access Engine is not able to fulfill your request.
	 * This may happen if the Engine caught a back-end Exception
	 * @throws DocumentNotFoundFault Is a tagged exception used to inform the caller that the requested document
	 * cannot be found
	 * @throws DocumentAccessFault Is a tagged exception used to tell the caller that the desired access to a
	 * document is prohibited due to sharing restriction
	 */
	public Document fetchDocument(DocumentReferencePK documentReferencePK) throws DocumentAccessEngineNotAvailableFault, 
			DocumentAccessEngineFault, DocumentNotFoundFault, DocumentAccessFault {
		
		try {
			FetchDocument fetchDocument = new FetchDocument();
			fetchDocument.setSession(session);
			fetchDocument.setDocumentReferencePK(documentReferencePK);
			FetchDocumentResponse fetchDocumentResponse = documentAccessEngineStub.fetchDocument(fetchDocument);
			
			return fetchDocumentResponse.getDocument();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Open a document for read-only access and acquiring the read lock at the server.
	 * 
	 * @param documentReferencePK
	 * @return document
	 * @throws DocumentAccessEngineNotAvailableFault Is thrown when e.g. the back-end service 
	 * is not available to fulfill the request
	 * @throws DocumentAccessEngineFault Is thrown if Document Access Engine is not able to fulfill your request.
	 * This may happen if the Engine caught a back-end Exception
	 * @throws DocumentNotFoundFault Is a tagged exception used to inform the caller that the requested document
	 * cannot be found
	 * @throws DocumentAccessFault Is a tagged exception used to tell the caller that the desired access to a
	 * document is prohibited due to sharing restriction
	 */
	public Document openDocumentForReading(DocumentReferencePK documentReferencePK) throws DocumentAccessEngineNotAvailableFault, 
			DocumentAccessEngineFault, DocumentNotFoundFault, DocumentAccessFault {
		
		try {
			OpenDocumentForReading openDocumentForReading = new OpenDocumentForReading();
			openDocumentForReading.setSession(session);
			openDocumentForReading.setDocumentReferencePK(documentReferencePK);
			OpenDocumentForReadingResponse openDocumentForReadingResponse = 
				documentAccessEngineStub.openDocumentForReading(openDocumentForReading);
			
			return openDocumentForReadingResponse.getDocument();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Compare the specified remote Document to the local server content.
	 * 
	 * @param document
	 * @return documentComparisonStatusAs - result you will get a simple notification which declares the state:
	 * <li> <b>INDETERMINATE:</b> The comparison status has not (yet) been determined
	 * <li> <b>NOTFOUND:</b> The remote document does not have a local counterpart with the same identifier
	 * <li> <b>IDENTICAL:</b> The documents are absolutely identical
	 * <li> <b>OLDER:</b> The documents are unequal, the remote document is older than the local document
	 * <li> <b>NEWER:</b> The documents are unequal, the remote document is newer than the local document
	 * <li> <b>CONFLICT:</b> The documents are unequal, although the last-modification are identical (filtered documents)
	 * <li> <b>LOCKED:</b> The documents could not be compared because the local document was currently locked
	 * <li> <b>ACCESSDENIED:</b> The documents could not be compared because the local document is access protected
	 * <li> <b>VALIDATION_VIOLATION:</b> The documents could not be compared because validation detected a violation
	 * <li> <b>ERROR:</b> The documents could not be compared because of an unexpected error
	 * @throws DocumentAccessEngineNotAvailableFault Is thrown if Document Access Engine is not able to fulfill your request. 
	 * This may happen if the Engine caught a back-end Exception
	 * @throws DocumentNotFoundFault Is a tagged exception used to inform the caller that the requested document
	 * cannot be found
	 * @throws DocumentAccessFault Is a tagged exception used to tell the caller that the desired access to a
	 * document is prohibited due to sharing restriction
	 */
	public DocumentComparisonStatus compareDocuments(Document document) throws DocumentAccessEngineNotAvailableFault, 
			DocumentAccessEngineFault, DocumentAccessFault {
		
		try {
			CompareDocuments compareDocuments = new CompareDocuments();
			compareDocuments.setSession(session);
			compareDocuments.setRemoteDocument(document);
			CompareDocumentsResponse compareDocumentsResponse = documentAccessEngineStub.compareDocuments(compareDocuments);
			
			return compareDocumentsResponse.getDocumentComparisonStatus();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Open a document for read-write access and acquiring the write lock at the server.
	 * 
	 * @param documentReferencePK
	 * @return document
	 * @throws DocumentAccessEngineNotAvailableFault Is thrown when e.g. the back-end service 
	 * is not available to fulfill the request
	 * @throws DocumentAccessEngineFault Is thrown if Document Access Engine is not able to fulfill your request.
	 * This may happen if the Engine caught a back-end Exception
	 * @throws DocumentNotFoundFault Is a tagged exception used to inform the caller that the requested document
	 * cannot be found
	 * @throws DocumentAccessFault Is a tagged exception used to tell the caller that the desired access to a
	 * document is prohibited due to sharing restriction
	 */
	public Document openDocumentForWriting(DocumentReferencePK documentReferencePK) throws DocumentAccessEngineNotAvailableFault, 
			DocumentAccessEngineFault, DocumentNotFoundFault, DocumentAccessFault {
		
		try {
			OpenDocumentForWriting openDocumentForWriting = new OpenDocumentForWriting();
			openDocumentForWriting.setSession(session);
			openDocumentForWriting.setDocumentReferencePK(documentReferencePK);
			OpenDocumentForWritingResponse openDocumentForWritingResponse = 
				documentAccessEngineStub.openDocumentForWriting(openDocumentForWriting);
			
			return openDocumentForWritingResponse.getDocument();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Commit the specified document to the IUCLID 5 Server. The requestor must
     * have the exclusive write lock on the Document which could be obtained via
     * open the document for writing
     * <p><b>Note:</b> <i>the lock state will not be modified by this call. The document remains open after
	 * the operation! If you like to release the write lock, you have to call closeDocument.</i>
	 * 
	 * @param document
	 * @return True to explicitly notify that everything was OK, 
	 * otherwise will throw a detailed exception
	 * @throws DocumentAccessEngineNotAvailableFault Is thrown when e.g. the back-end service 
	 * is not available to fulfill the request
	 * @throws DocumentAccessEngineFault Is thrown if Document Access Engine is not able to fulfill your request.
	 * This may happen if the Engine caught a back-end Exception
	 * @throws DocumentNotFoundFault Is a tagged exception used to inform the caller that the requested document
	 * cannot be found
	 * @throws DocumentAccessFault Is a tagged exception used to tell the caller that the desired access to a
	 * document is prohibited due to sharing restriction
	 */
	public Boolean commitDocument(Document document) throws DocumentAccessEngineNotAvailableFault, 
			DocumentAccessEngineFault, DocumentNotFoundFault, DocumentAccessFault {
		
		try {
			CommitDocument commitDocument = new CommitDocument();
			commitDocument.setSession(session);
			commitDocument.setRemoteDocument(document);
			CommitDocumentResponse commitDocumentResponse = documentAccessEngineStub.commitDocument(commitDocument);
			
			return commitDocumentResponse.getSuccess();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Delete the document referenced by the specified document reference.
	 * <p><b>Note:</b> <i>the server call can only be successful if no lock remains on the requested
	 * document. Therefore, no such lock can be present in the local handle table if
	 * the server call succeeds and this manager works correctly.</i>
	 * 
	 * @param documentReferencePK
	 * @return True to explicitly notify that everything was OK otherwise will throw
	 * a detailed exception
	 * @throws DocumentAccessEngineNotAvailableFault Is thrown when e.g. the back-end service 
	 * is not available to fulfill the request
	 * @throws DocumentNotFoundFault Is a tagged exception used to inform the caller that the requested document
	 * cannot be found
	 * @throws DocumentAccessFault Is a tagged exception used to tell the caller that the desired access to a
	 * document is prohibited due to sharing restriction
	 * @throws DocumentAccessEngineFault
	 */
	public Boolean deleteDocument(DocumentReferencePK documentReferencePK) throws DocumentAccessEngineNotAvailableFault, 
			DocumentNotFoundFault, DocumentAccessFault, DocumentAccessEngineFault {
		
		try {
			DeleteDocument deleteDocument = new DeleteDocument();
			deleteDocument.setSession(session);
			deleteDocument.setDocumentReferencePK(documentReferencePK);
			DeleteDocumentResponse deleteDocumentResponse = documentAccessEngineStub.deleteDocument(deleteDocument);
			
			return deleteDocumentResponse.getSuccess();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Creates a new Document object for the given document type.
	 * <p><b>Note:</b> <i>The document has open write access to the document after successful call to
	 * this method, so to finish working with the document a call to
	 * closeDocument() is required.</i>
	 * 
	 * @param documentType
	 * @return document
	 * @throws DocumentAccessEngineNotAvailableFault
	 * @throws DocumentCreationFault
	 * @throws DocumentAccessFault
	 * @throws DocumentAccessEngineFault
	 */
	public Document createDocument(DocumentType documentType) throws DocumentAccessEngineNotAvailableFault, 
			DocumentCreationFault, DocumentAccessFault, DocumentAccessEngineFault {
		
		try {
			CreateDocument createDocument = new CreateDocument();
			createDocument.setSession(session);
			createDocument.setDocumentType(documentType);
			CreateDocumentResponse createDocumentResponse = 
				documentAccessEngineStub.createDocument(createDocument);
			
			return createDocumentResponse.getDocument();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
}

package org.ideaconsult.iuclidws.container;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.ideaconsult.iuclidws.ClientServiceException;
import org.ideaconsult.iuclidws.PropertiesUtil;
import org.ideaconsult.iuclidws.StubUtil;
import org.ideaconsult.iuclidws.session.SessionEngineFault;
import org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault;
import org.ideaconsult.iuclidws.session.SessionSingleton;
import org.ideaconsult.iuclidws.types.Types.I5ZContainer;
import org.ideaconsult.iuclidws.types.Types.ImportContainerInformation;
import org.ideaconsult.iuclidws.types.Types.ImportExternalContainer;
import org.ideaconsult.iuclidws.types.Types.ImportExternalContainerResponse;
import org.ideaconsult.iuclidws.types.Types.ResultReport;
import org.ideaconsult.iuclidws.types.Types.Session;
import org.ideaconsult.iuclidws.types.Types.UploadContainer;
import org.ideaconsult.iuclidws.types.Types.UploadContainerResponse;

/**
 * The Document Access Engine is responsible to save the documents at the IUCLID 5 server.
 * Therefore it receives the content as XML, validate it against its official format and then
 * commit it against the server. If you want to implement a dossier import, you have to extract
 * the container document and call the commit message for each included document
 * separately.
 * 
 * @author Nikolay Taskov
 */
public class ContainerEngine {

	// fields
	/** Container engine endpoint context */
	private String END_POINT_CONTAINER_ENGINE = "ContainerEngine";
	
	/** Current session */
	private Session session;
	
	/** Instance of ContainerEngineStub class */
	private ContainerEngineStub containerEngineStub;
	
	private String target;
	
	/** The logger for this class */
	private final static Logger logger = Logger.getLogger(ContainerEngine.class);
	
	// constructors
	/**
	 * Init ContainerEngine class and logged in automatically.
	 * 
	 * @throws ClientServiceException 
	 * @throws SessionEngineFault 
	 * @throws SessionEngineNotAvailableFault 
	 */
	public ContainerEngine() throws SessionEngineNotAvailableFault, SessionEngineFault, ClientServiceException, AxisFault {
		this(SessionSingleton.getInstance().getSession());
	}
	
	/**
	 * Init ContainerEngine class with custom Session
	 * 
	 * @param session
	 */
	public ContainerEngine(Session session) throws AxisFault {
		this(session, PropertiesUtil.getTarget());
	}
	
	/**
	 * Init ContainerEngine class with custom Session and target
	 * 
	 * @param session
	 */
	public ContainerEngine(Session session, String target) throws AxisFault {
		this.session = session;
		this.target = target;
		
		containerEngineStub = new ContainerEngineStub(StubUtil.concatEndpointAddress(
				this.target	, END_POINT_CONTAINER_ENGINE));
		StubUtil.initializeAxisService(containerEngineStub._getServiceClient());		
	}
	
	// methods
	/**
	 * Send a binary IUCLID 5 export container to the web service 
	 * and starts the transactional import.
	 * 
	 * @param i5ZContainer
	 * @return
	 * @throws ContainerEngineNotAvailableFault Is thrown when e.g. 
	 * the back-end service is not available to fulfill the request
	 * @throws ContainerEngineFault Is thrown if Container Engine is not able to fulfill your request. 
	 * This may happen if the Engine caught a back-end Exception.
	 * @throws IllegalContentTypeFault This exception is thrown if the received container 
	 * content type (application/zip) is not allowed.
	 * @throws ContainerContentFault This exception is thrown if the processing of an import container fails.
	 */
	public ResultReport uploadContainer(I5ZContainer i5ZContainer)
			throws ContainerEngineNotAvailableFault, ContainerEngineFault, 
			IllegalContentTypeFault, ContainerContentFault {
		
		try {
			UploadContainer uploadContainer = new UploadContainer();
			uploadContainer.setSession(session);
			uploadContainer.setI5ZContainer(i5ZContainer);
			UploadContainerResponse uploadContainerResponse = containerEngineStub.uploadContainer(uploadContainer);
			
			return uploadContainerResponse.getResultReport();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Call the web service to import an external uploaded i5z-container. The client
     * has to upload the external container file by himself, e.g. via ftp. After the web
     * service is called to importExternalContainer it scans the external upload
     * directory for the specified file name
	 * 
	 * @param importContainerInformation
	 * @return
	 * @throws ContainerEngineNotAvailableFault Is thrown when e.g. 
	 * the back-end service is not available to fulfill the request
	 * @throws ContainerEngineFault Is thrown if Container Engine is not able to fulfill your request. 
	 * This may happen if the Engine caught a back-end Exception.
	 * @throws IllegalContentTypeFault This exception is thrown if the received container 
	 * content type (application/zip) is not allowed.
	 * @throws ContainerContentFault This exception is thrown if the processing of an import container fails.
	 */
	public ResultReport importExternalContainer(ImportContainerInformation importContainerInformation)
		throws ContainerEngineNotAvailableFault, ContainerEngineFault, 
		IllegalContentTypeFault, ContainerContentFault {
		
		try {
			ImportExternalContainer importExternalContainer = new ImportExternalContainer();
			importExternalContainer.setSession(session);
			importExternalContainer.setImportContainerInformation(importContainerInformation);
			ImportExternalContainerResponse importExternalContainerResponse = 
				containerEngineStub.importExternalContainer(importExternalContainer);
			
			return importExternalContainerResponse.getResultReport();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	// NOTE: Missing some importants parameters to implement this method!
	// downloadContainer()
}

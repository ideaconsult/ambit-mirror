package org.ideaconsult.iuclidws.resource;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.ideaconsult.iuclidws.ClientServiceException;
import org.ideaconsult.iuclidws.PropertiesUtil;
import org.ideaconsult.iuclidws.StubUtil;
import org.ideaconsult.iuclidws.session.SessionEngineFault;
import org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault;
import org.ideaconsult.iuclidws.session.SessionSingleton;
import org.ideaconsult.iuclidws.types.Types.GetAvailableLanguages;
import org.ideaconsult.iuclidws.types.Types.GetAvailableLanguagesResponse;
import org.ideaconsult.iuclidws.types.Types.GetResource;
import org.ideaconsult.iuclidws.types.Types.GetResourceResponse;
import org.ideaconsult.iuclidws.types.Types.I5Resource;
import org.ideaconsult.iuclidws.types.Types.LanguageList;
import org.ideaconsult.iuclidws.types.Types.ResourceDefinition;
import org.ideaconsult.iuclidws.types.Types.Session;

/**
 * IUCLID 5 defines several server based resource files (e.g. phrases.xml), which depends on
 * the installed server version of IUCLID 5.
 * This class allows Web Service clients to access the phrases.xml via the Web Service and
 * in that case they always getting the correct phrases.xml file of the current IUCLID 5 server.
 * Otherwise they have to manually download the file from the IUCLID web side to getting the
 * phrase content.
 * 
 * @author Nikolay Taskov
 */
public class ResourceEngine {
	
	// fields
	/** Resource engine engine endpoint context */
	private String END_POINT_RESOURCE_ENGINE = "ResourceEngine";
	
	/** Current session */
	private Session session;
	
	/** Instance of ResourceEngineStub class */
	private ResourceEngineStub resourceEngineStub;
	
	private String target;
	
	/** The logger for this class */
	private final static Logger logger = Logger.getLogger(ResourceEngine.class);
	
	// constructors
	/**
	 * Init ResourceEngine class and logged in automatically.
	 * 
	 * @throws ClientServiceException 
	 * @throws SessionEngineFault 
	 * @throws SessionEngineNotAvailableFault 
	 */
	public ResourceEngine() throws SessionEngineNotAvailableFault, SessionEngineFault, 
			ClientServiceException, AxisFault {
		this(SessionSingleton.getInstance().getSession());
	}
	
	/**
	 * Init ResourceEngine class with custom Session
	 * 
	 * @param session
	 */
	public ResourceEngine(Session session) throws AxisFault {
		this(session, PropertiesUtil.getTarget());
	}
	
	/**
	 * Init ResourceEngine class with custom Session and target
	 * 
	 * @param session
	 */
	public ResourceEngine(Session session, String target) throws AxisFault {
		this.session = session;
		this.target = target;
		
		resourceEngineStub = new ResourceEngineStub(StubUtil.concatEndpointAddress(
				this.target	, END_POINT_RESOURCE_ENGINE));
		StubUtil.initializeAxisService(resourceEngineStub._getServiceClient());		
	}
	
	// methods
	/**
	 * This operation returns a list of available languages. 
	 * The languages are installed by using the resource plug-ins at the IUCLID 5 server.
	 * 
	 * @return languageList - a list of all available languages
	 * @throws ResourceEngineNotAvailableFault thrown when e.g. 
	 * the service is not available to fulfill the request
	 * @throws ResourceEngineFault thrown if the Service is not able to fulfill your request. 
	 * This may happen if the Engine caught a back-end Exception.
	 */
	public LanguageList getAvailableLanguages() throws ResourceEngineNotAvailableFault, ResourceEngineFault {
		
		try {
			GetAvailableLanguages getAvailableLanguages = new GetAvailableLanguages();
			getAvailableLanguages.setSession(session);
			GetAvailableLanguagesResponse getAvailableLanguagesResponse = 
				resourceEngineStub.getAvailableLanguages(getAvailableLanguages);
			
			return getAvailableLanguagesResponse.getLanguageList();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * This operation returns the requested IUCLID 5 resource as SOAP content.
	 * 
	 * @param resourceDefinition - the requested resource 
	 * definition as ResourceDefinition which contains:
	 * <li> resource id
	 * <li> Optional language, if null or empty the web service using the
	 * default language associated which the specified user session.
	 * @return i5Resource - the requested resource as I5Resource
	 * @throws ResourceEngineNotAvailableFault thrown when e.g. 
	 * the service is not available to fulfill the request
	 * @throws ResourceEngineFault thrown if the Service is not able to fulfill your request. 
	 * This may happen if the Engine caught a back-end Exception.
	 */
	public I5Resource getResource(ResourceDefinition resourceDefinition)
			throws ResourceEngineNotAvailableFault, ResourceEngineFault {
		
		try {
			GetResource getResource = new GetResource();
			getResource.setSession(session);
			getResource.setResourceDefinition(resourceDefinition);
			GetResourceResponse getResourceResponse = 
				resourceEngineStub.getResource(getResource);
			
			return getResourceResponse.getI5Resource();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
}

package org.ideaconsult.iuclidws.query;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.ideaconsult.iuclidws.ClientServiceException;
import org.ideaconsult.iuclidws.PropertiesUtil;
import org.ideaconsult.iuclidws.StubUtil;
import org.ideaconsult.iuclidws.session.SessionEngineFault;
import org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault;
import org.ideaconsult.iuclidws.session.SessionSingleton;
import org.ideaconsult.iuclidws.types.Types;
import org.ideaconsult.iuclidws.types.Types.DocumentType;
import org.ideaconsult.iuclidws.types.Types.DocumentTypeType;
import org.ideaconsult.iuclidws.types.Types.ExecuteQuery;
import org.ideaconsult.iuclidws.types.Types.ExecuteQueryResponse;
import org.ideaconsult.iuclidws.types.Types.GetAllQueryDefinitions;
import org.ideaconsult.iuclidws.types.Types.GetAllQueryDefinitionsResponse;
import org.ideaconsult.iuclidws.types.Types.GetAllQueryNames;
import org.ideaconsult.iuclidws.types.Types.GetAllQueryResultTypes;
import org.ideaconsult.iuclidws.types.Types.GetAllQueryResultTypesResponse;
import org.ideaconsult.iuclidws.types.Types.GetQueryDefinitionByName;
import org.ideaconsult.iuclidws.types.Types.GetQueryDefinitionByNameResponse;
import org.ideaconsult.iuclidws.types.Types.GetQueryDefinitionsByName;
import org.ideaconsult.iuclidws.types.Types.GetQueryDefinitionsByNameResponse;
import org.ideaconsult.iuclidws.types.Types.GetQueryDefinitionsByResultDocumentType;
import org.ideaconsult.iuclidws.types.Types.GetQueryDefinitionsByResultDocumentTypeResponse;
import org.ideaconsult.iuclidws.types.Types.QueryDefinition;
import org.ideaconsult.iuclidws.types.Types.QueryDefinitionList;
import org.ideaconsult.iuclidws.types.Types.QueryExecutionDefinition;
import org.ideaconsult.iuclidws.types.Types.QueryName;
import org.ideaconsult.iuclidws.types.Types.QueryNameList;
import org.ideaconsult.iuclidws.types.Types.QueryResultList;
import org.ideaconsult.iuclidws.types.Types.QueryResultTypeList;
import org.ideaconsult.iuclidws.types.Types.Session;

/**
 * This class enables to execute the standard IUCLID 5 queries which are
 * accessible via the user interface.
 * 
 * @author Nikolay Taskov
 */
public class QueryEngine {
	
	// query names IDs
	// Substance
	public static String FIND_ALL_SUBSTANCES_ID = "find_all_substances";
	public static String FIND_COMPLEX_SUBSTANCE_ID = "find_complex_substance";
	public static String FIND_COMPLEX_SUBSTANCE_BY_IMPURITY_ID = "find_complex_substance_by_impurity";
	public static String FIND_COMPLEX_SUBSTANCE_BY_LEGALENTITY_ID = "find_complex_substance_by_legalentity";
	public static String FIND_COMPLEX_SUBSTANCE_BY_JOINTSUBMISSION_ID = "find_complex_substance_by_jointsubmission";
	public static String FIND_COMPLEX_SUBSTANCE_BY_ADDITIVE_ID = "find_complex_substance_by_additive";
	public static String FIND_COMPLEX_SUBSTANCE_BY_REFERENCESUBSTANCE_ID = "find_complex_substance_by_referencesubstance";
	public static String FIND_COMPLEX_SUBSTANCE_BY_SITE_ID = "find_complex_substance_by_site";
	public static String FIND_COMPLEX_SUBSTANCE_BY_SUBSTANCECOMPOSITION_CONSTITUENT_ID = "find_complex_substance_by_substancecomposition_constituent";
	public static String FIND_COMPLEX_SUBSTANCE_BY_CLASSIFICATION_AND_LABELLING_ID = "find_complex_substance_by_classification_and_labelling";
	
	// Mixture
	public static String FIND_ALL_MIXTURES_ID = "find_all_mixtures";
	public static String FIND_COMPLEX_MIXTURE_ID = "find_complex_mixture";
	public static String FIND_COMPLEX_MIXTURES_BY_IMPURITY_ID = "find_complex_mixtures_by_impurity";
	public static String FIND_COMPLEX_MIXTURES_BY_CONSTITUENT_ID = "find_complex_mixtures_by_constituent";
	public static String FIND_COMPLEX_MIXTURES_BY_ADDITIVE_ID = "find_complex_mixtures_by_additive";
	public static String FIND_COMPLEX_MIXTURES_BY_RELATED_SUBSTANCE_ID = "find_complex_mixtures_by_related_substance";
	
	// Site
	public static String FIND_ALL_SITES_ID = "find_all_sites";
	public static String FIND_ALL_COMPLEX_SITE_ID = "find_complex_site";
	
	// Category
	public static String FIND_ALL_CATEGORIES_ID = "find_all_categories";
	public static String FIND_COMPLEX_CATEGORIES_ID = "find_complex_categories";
	public static String FIND_COMPLEX_CATEGORIES_BY_LEGALENTITY_ID = "find_complex_categories_by_legalentity";
	
	// Template
	public static String FIND_ALL_TEMPLATES_ID = "find_all_templates";
	public static String FIND_COMPLEX_TEMPLATE_ID = "find_complex_template";
	
	// Reference Substance
	public static String FIND_ALL_REFERENCESUBSTANCES_ID = "find_all_referencesubstances";
	public static String FIND_COMPLEX_REFERENCESUBSTANCE_ID = "find_complex_referencesubstance";
	
	// Legal Entity
	public static String FIND_ALL_LEGALENTITIES_ID = "find_all_legalentities";
	public static String FIND_COMPLEX_LEGALENTITY_ID = "find_complex_legalentity";
	
	// Unknown
	public static String FIND_COMPLEX_DOCUMENT_BY_UUID_ID = "find_complex_document_by_uuid";
	
	// Dossier
	public static String FIND_ALL_DOSSIERS_ID = "find_all_dossiers";
	public static String FIND_COMPLEX_DOSSIER_ID = "find_complex_dossier";
	public static String FIND_DOSSIER_BY_SUBSTANCE_ID = "find_dossier_by_substance";
	public static String FIND_DOSSIER_BY_IMPURITY_ID = "find_dossier_by_impurity";
	public static String FIND_DOSSIER_BY_ADDITIVE_ID = "find_dossier_by_additive";
	public static String FIND_DOSSIER_BY_SUBMISSION_ID = "find_dossier_by_joint_submission";
	public static String FIND_DOSSIER_BY_LEGAL_ENTITY_ID = "find_dossier_by_legal_entity";
	public static String FIND_DOSSIER_BY_REFERENCE_SUBSTANCE_ID = "find_dossier_by_reference_substance";
	public static String FIND_DOSSIER_BY_CONSTITUENTS_ID = "find_dossier_by_constituents";
	public static String FIND_DOSSIER_BY_SITE_ID = "find_dossier_by_site";
	public static String FIND_DOSSIER_BY_CLASSIFICATION_AND_LABELLING_ID = "find_dossier_by_classification_and_labelling";
	public static String FIND_DOSSIER_BY_MIXTURE_ID = "find_dossier_by_mixture";
	
	// query result types
	public static String SUBSTANCE_TYPE = "Substance";
	public static String SITE_TYPE = "Site";
	public static String TEMPLATE_TYPE = "Template";
	public static String DOSSIER_TYPE = "Dossier";
	public static String LEGAL_ENTITY_TYPE = "LegalEntity";
	public static String MIXTURE_TYPE = "Mixture";
	public static String CATEGORY_TYPE = "Category";
	public static String REFERENCE_SUBSTANCE_TYPE = "ReferenceSubstance";
	public static String UNKNOWN_TYPE = "Unknown";
	
	// fields
	/** Query engine endpoint context */
	private String END_POINT_QUERY_ENGINE = "QueryEngine";
	
	/** Current session */
	private Session session;
	
	/** Instance of QueryEngineStub class */
	private QueryEngineStub queryEngineStub;
	
	private String target;
	
	/** The logger for this class */
	private final static Logger logger = Logger.getLogger(QueryEngine.class);
	
	// constructors
	/**
	 * Init QueryEngine class and logged in automatically.
	 * 
	 * @throws ClientServiceException 
	 * @throws SessionEngineFault 
	 * @throws SessionEngineNotAvailableFault 
	 */
	public QueryEngine() throws SessionEngineNotAvailableFault, SessionEngineFault, ClientServiceException, AxisFault {
		this(SessionSingleton.getInstance().getSession());
	}
	
	/**
	 * Init QueryEngine class with custom Session
	 * 
	 * @param session
	 */
	public QueryEngine(Session session) throws AxisFault {
		this(session, PropertiesUtil.getTarget());
	}
	
	/**
	 * Init QueryEngine class with custom Session and target
	 * 
	 * @param session
	 */
	public QueryEngine(Session session, String target) throws AxisFault {
		this.session = session;
		this.target = target;
		
		queryEngineStub = new QueryEngineStub(StubUtil.concatEndpointAddress(
				this.target	, END_POINT_QUERY_ENGINE));
			StubUtil.initializeAxisService(queryEngineStub._getServiceClient());		
	}	
	
	// methods
	/**
	 * Each query could be identified by its query name (e.g.
	 * find_complex_substance). Before you can execute a query you need the
	 * Query Definition which defines the meta data of a query. To get this
	 * Query Definition you could use its query name.
	 * 
	 * @return a list of query names
	 * @throws QueryEngineNotAvailableFault Is thrown when the back-end service 
	 * is not available to fulfill the request
	 * @throws QueryEngineFault Is thrown for all occurred exception during the web service call
	 */
	public QueryNameList getAllQueryNames() throws QueryEngineNotAvailableFault, QueryEngineFault {
		
		try {
			// execute the call for to receive all query definitions
			Types.GetAllQueryNames queryNames = new GetAllQueryNames();
			queryNames.setSession(session);

			QueryNameList queryNameList = queryEngineStub.getAllQueryNames(queryNames).getQueryNameList();
			
			return queryNameList;
		} catch (AxisFault e) {
			logger.error(e.getMessage(), e);
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * IUCLID 5 defines several document types e.g. Substance, Category, Mixture
	 * (for more info please visit: http://iuclid.eu/index.php?fuseaction=home.format&type=public). 
	 * Each IUCLID 5 Query defines one Document Result Type.
	 * 
	 * @return a list of avalable document types
	 * @throws QueryEngineNotAvailableFault Is thrown when the back-end service 
	 * is not available to fulfill the request
	 * @throws QueryEngineFault Is thrown for all occurred exception during the web service call
	 */
	public QueryResultTypeList getAllQueryResultTypes() throws QueryEngineNotAvailableFault, QueryEngineFault {
		
		try {
			GetAllQueryResultTypes getAllQueryResultTypes = new GetAllQueryResultTypes();
			getAllQueryResultTypes.setSession(session);

			// execute the call for to receive all query definitions
			GetAllQueryResultTypesResponse allResultTypes = queryEngineStub
					.getAllQueryResultTypes(getAllQueryResultTypes);

			QueryResultTypeList queryResultTypeList = allResultTypes.getQueryResultTypeList();
			
			return queryResultTypeList;
		} catch (AxisFault e) {
			logger.error(e.getMessage(), e);
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * A Query Definition contains the meta data of an IUCLID 5 query like name,
	 * Document Result Type and the list of Query Parameter Definitions.
	 * A Query Parameter Definition contains the name of the query parameter, the
	 * data type, the display type, the IUCLID 5 phrase group id and the
	 * corresponding list of phrases.
	 * 
	 * @return a list of all available query definitions.
	 * @throws QueryEngineNotAvailableFault Is thrown when the back-end service 
	 * is not available to fulfill the request
	 * @throws QueryEngineFault Is thrown for all occurred exception during the web service call
	 */
	public QueryDefinitionList getAllQueryDefinitions() throws QueryEngineNotAvailableFault, QueryEngineFault {
		
		try {
			GetAllQueryDefinitions getAllQueryDefinitions = new GetAllQueryDefinitions();
			getAllQueryDefinitions.setSession(session);
			
			// execute the call for to receive all query definitions
			GetAllQueryDefinitionsResponse getAllQueryDefinitionsResponse = queryEngineStub
					.getAllQueryDefinitions(getAllQueryDefinitions);
			QueryDefinitionList queryDefinitionList = getAllQueryDefinitionsResponse.getQueryDefinitionList();
			
			return queryDefinitionList;
		} catch (AxisFault e) {
			logger.error(e.getMessage(), e);
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	// TODO: DocumentType
	/**
	 * Returns all Query Definitions which fit to the given Document Result Type
	 * 
	 * @param documentTypeType - instance of DocumentTypeType class,
	 * <p> NOTE: <i> For initialization of this class please using QueryEngine consts *_TYPE </i>
	 * @return
	 * @throws QueryEngineNotAvailableFault Is thrown when the back-end service 
	 * is not available to fulfill the request
	 * @throws QueryEngineFault Is thrown for all occurred exception during the web service call
	 */
	public QueryDefinitionList getQueryDefinitionsByResultDocumentType(DocumentTypeType documentTypeType)
			throws QueryEngineNotAvailableFault, QueryEngineFault {
		
		try {
			Types.DocumentType documentType = new DocumentType();
			documentType.setType(documentTypeType);

			GetQueryDefinitionsByResultDocumentType getQueryDefinitionsByResultDocumentType = new GetQueryDefinitionsByResultDocumentType();
			getQueryDefinitionsByResultDocumentType.setSession(session);
			getQueryDefinitionsByResultDocumentType.setDocumentType(documentType);

			// execute the call for to receive all query definitions fitting to the substance
			GetQueryDefinitionsByResultDocumentTypeResponse getByResultDocumentTypeResponse = queryEngineStub
					.getQueryDefinitionsByResultDocumentType(getQueryDefinitionsByResultDocumentType);

			QueryDefinitionList queryDefinitionList = getByResultDocumentTypeResponse.getQueryDefinitionList();
			
			return queryDefinitionList;
		} catch (AxisFault e) {
			logger.error(e.getMessage(), e);
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Return the specified Query Definitions by its name
	 * 
	 * @param queryNameId
	 * @return queryDefinition
	 * @throws QueryEngineNotAvailableFault Is thrown when the back-end service 
	 * is not available to fulfill the request
	 * @throws QueryEngineFault Is thrown for all occurred exception during the web service call
	 */
	public QueryDefinition getQueryDefinitionByName(String queryNameId)
			throws QueryEngineNotAvailableFault, QueryEngineFault {
		
		try {
			QueryName queryName = new QueryName();
			queryName.setId(queryNameId);
			
			GetQueryDefinitionByName getQueryDefinitionByName = new GetQueryDefinitionByName();
			getQueryDefinitionByName.setSession(session);
			getQueryDefinitionByName.setQueryName(queryName);
			
			GetQueryDefinitionByNameResponse getQueryDefinitionByNameResponse = 
				queryEngineStub.getQueryDefinitionByName(getQueryDefinitionByName);
			
			return getQueryDefinitionByNameResponse.getQueryDefinition();
		} catch (AxisFault e) {
			logger.error(e.getMessage(), e);
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * 
	 * @return the {@link Session} object 
	 */
	public Session getSession() {
		return session;
	}


	/**
	 * Return the requested list of Query Definitions by the requested list of query.
     * If you need to load more than one query definition which has different
     * Document Result Type you could use this method. It takes a list of Query
     * Names and return the corresponding List of Query Definitions.
	 * 
	 * @param queryNameList
	 * @return queryDefinitionList - a list  of Query Definitions
	 * @throws QueryEngineNotAvailableFault Is thrown when the back-end service 
	 * is not available to fulfill the request
	 * @throws QueryEngineFault Is thrown for all occurred exception during the web service call
	 */
	public QueryDefinitionList getQueryDefinitionsByName(QueryNameList queryNameList) 
			throws QueryEngineNotAvailableFault, QueryEngineFault {
		
		try {			
			GetQueryDefinitionsByName getQueryDefinitionsByName = new GetQueryDefinitionsByName();
			getQueryDefinitionsByName.setSession(session);
			getQueryDefinitionsByName.setQueryNameList(queryNameList);
			GetQueryDefinitionsByNameResponse getQueryDefinitionsByNameResponse = 
				queryEngineStub.getQueryDefinitionsByName(getQueryDefinitionsByName);
			
			return getQueryDefinitionsByNameResponse.getQueryDefinitionList();
			
		} catch (AxisFault e) {
			logger.error(e.getMessage(), e);
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Execute the given query execution definition
	 * 
	 * @param queryExecutionDefinition
	 * @return queryResultList - a list of Document References containing:
	 * <li> Document Type
	 * <li> Last Modification Date
	 * <li> Unique Key
	 * <li> Description
	 * <li> Notes
	 * @throws QueryEngineNotAvailableFault Is thrown when the back-end service 
	 * is not available to fulfill the request
	 * @throws QueryEngineFault Is thrown if an internal error occurred
	 * @throws QueryDeliveredTooManyHitsFault This indicates a recoverable error while executing a query. E.g. this could
	 * be a too huge number of hits or errors resulting from other parameters the
	 * user was entered
	 * @throws QueryTimeoutFault The query reached its timeout limit before it completed.
	 * Query timeouts are generally enforced to prevent long running query to
	 * completely stall the system.
	 * This cannot fix the real culprit - the slow query, but is kind of a 'parachute' to
	 * keep the system operational.
	 * @throws QueryParameterFault This exception is thrown if the list of parameters does not fit the
	 * recommended list from the QueryParameterDefinition. You do not have to
	 * fill in all defined parameters but you should never put an undefined
	 * parameter into the list
	 */
	public QueryResultList executeQuery(QueryExecutionDefinition queryExecutionDefinition)
			throws QueryEngineNotAvailableFault, QueryEngineFault, 
			QueryDeliveredTooManyHitsFault, QueryTimeoutFault, QueryParameterFault {
		
		try {
			ExecuteQuery executeQuery = new ExecuteQuery();
			executeQuery.setSession(session);
			executeQuery.setQueryExecutionDefinition(queryExecutionDefinition);
			ExecuteQueryResponse executeQueryResponse = queryEngineStub.executeQuery(executeQuery);
			
			return executeQueryResponse.getQueryResultList();
		} catch (AxisFault e) {
			logger.error(e.getMessage(), e);
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
}


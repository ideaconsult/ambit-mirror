package org.ideaconsult.iuclidws.inventory;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.ideaconsult.iuclidws.ClientServiceException;
import org.ideaconsult.iuclidws.PropertiesUtil;
import org.ideaconsult.iuclidws.StubUtil;
import org.ideaconsult.iuclidws.session.SessionEngineFault;
import org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault;
import org.ideaconsult.iuclidws.session.SessionSingleton;
import org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntries;
import org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntriesResponse;
import org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntry;
import org.ideaconsult.iuclidws.types.Types.DeleteLiteratureInventoryEntryResponse;
import org.ideaconsult.iuclidws.types.Types.ExecuteLiteratureInventoryQuery;
import org.ideaconsult.iuclidws.types.Types.ExecuteLiteratureInventoryQueryResponse;
import org.ideaconsult.iuclidws.types.Types.GetLiteratureInventoryQuery;
import org.ideaconsult.iuclidws.types.Types.GetLiteratureInventoryQueryResponse;
import org.ideaconsult.iuclidws.types.Types.LiteratureInventoryEntry;
import org.ideaconsult.iuclidws.types.Types.LiteratureInventoryList;
import org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntries;
import org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntriesResponse;
import org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntry;
import org.ideaconsult.iuclidws.types.Types.PutLiteratureInventoryEntryResponse;
import org.ideaconsult.iuclidws.types.Types.QueryDefinition;
import org.ideaconsult.iuclidws.types.Types.QueryExecutionDefinition;
import org.ideaconsult.iuclidws.types.Types.Session;

/**
 * The inventory engine enables you to read, write, update and create 
 * Literature Inventory Entries.
 * 
 * @author Nikolay Taskov
 */
public class InventoryEngine {
	
	// fields
	/** InventoryeEngine endpoint context */
	private String END_POINT_INVENTORY_ENGINE = "InventoryEngine";
	
	/** Current session */
	private Session session;
	
	/** Instance of InventoryEngineStub class */
	private InventoryEngineStub inventoryEngineStub;
	
	private String target;
	
	/** The logger for this class */
	private final static Logger logger = Logger.getLogger(InventoryEngine.class);
	
	// constructors
	/**
	 * Init InventoryEngine class and logged in automatically.
	 * 
	 * @throws ClientServiceException 
	 * @throws SessionEngineFault 
	 * @throws SessionEngineNotAvailableFault 
	 */
	public InventoryEngine() throws SessionEngineNotAvailableFault, SessionEngineFault, ClientServiceException, AxisFault {
		this(SessionSingleton.getInstance().getSession());
	}
	
	/**
	 * Init InventoryEngine class with custom Session
	 * 
	 * @param session
	 */
	public InventoryEngine(Session session) throws AxisFault {
		this(session, PropertiesUtil.getTarget());
	}
	
	/**
	 * Init InventoryEngine class with custom Session and target
	 * 
	 * @param session
	 */
	public InventoryEngine(Session session, String target) throws AxisFault {
		this.session = session;
		this.target = target;
		
		inventoryEngineStub = new InventoryEngineStub(StubUtil.concatEndpointAddress(
				this.target	, END_POINT_INVENTORY_ENGINE));
		StubUtil.initializeAxisService(inventoryEngineStub._getServiceClient());		
	}
	
	// methods
	/**
	 * Return the QueryDefinition for the Literature Inventory.
	 * 
	 * @return queryDefinition - the Query Definition of the Literature Inventory
	 * @throws InventoryEngineNotAvailableFault Is thrown when e.g. the service is 
	 * not available to fulfill the request
	 * @throws InventoryEngineFault Is thrown if Inventory Engine is not able to fulfill your request. 
	 * This may happen if the Engine caught a back-end Exception.
	 */
	public QueryDefinition getLiteratureInventoryQuery() 
			throws InventoryEngineNotAvailableFault, InventoryEngineFault {
		
		try {
			GetLiteratureInventoryQuery getLiteratureInventoryQuery = new GetLiteratureInventoryQuery();
			getLiteratureInventoryQuery.setSession(session);
			GetLiteratureInventoryQueryResponse getInventoryQueryResponse = 
				inventoryEngineStub.getLiteratureInventoryQuery(getLiteratureInventoryQuery);
			
			return getInventoryQueryResponse.getQueryDefinition();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Execute the given literature inventory query
	 * 
	 * @param queryExecutionDefinition - the Query Execution Definition which defines:
	 * <li>Query Name
	 * <li>Query Parameter List - Query parameter name, Query parameter value
	 * @return literatureInventoryList - a list LiteratureInventoryList
	 * of Document References containing:
	 * <li> Document Type
	 * <li> Last Modification Date
	 * <li> Unique Key
	 * <li> Description
	 * <li> Notes
	 * @throws InventoryEngineNotAvailableFault Is thrown when e.g. the service is 
	 * not available to fulfill the request
	 * @throws InventoryEngineFault Is thrown if Inventory Engine is not able to fulfill your request. 
	 * This may happen if the Engine caught a back-end Exception.
	 */
	public LiteratureInventoryList executeLiteratureInventoryQuery(QueryExecutionDefinition queryExecutionDefinition)
			throws InventoryEngineNotAvailableFault, InventoryEngineFault {
		
		try {
			ExecuteLiteratureInventoryQuery executeLiteratureInventoryQuery = new ExecuteLiteratureInventoryQuery();
			executeLiteratureInventoryQuery.setSession(session);
			executeLiteratureInventoryQuery.setQueryExecutionDefinition(queryExecutionDefinition);
			ExecuteLiteratureInventoryQueryResponse executeLiteratureInventoryQueryResponse = 
				inventoryEngineStub.executeLiteratureInventoryQuery(executeLiteratureInventoryQuery);
			
			executeLiteratureInventoryQueryResponse.getLiteratureInventoryResultList();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Stores the given literature inventory entry.
	 * 
	 * @param literatureInventoryEntry
	 * @return true to explicitly notify that everything was OK.
	 * @throws InventoryEngineNotAvailableFault Is thrown when e.g. the service is 
	 * not available to fulfill the request
	 * @throws InventoryEngineFault Is thrown if Inventory Engine is not able to fulfill your request. 
	 * This may happen if the Engine caught a back-end Exception.
	 */
	public Boolean putLiteratureInventoryEntry(LiteratureInventoryEntry literatureInventoryEntry)
			throws InventoryEngineNotAvailableFault, InventoryEngineFault {
		
		PutLiteratureInventoryEntryResponse putLiteratureInventoryEntryResponse;
		try {
			PutLiteratureInventoryEntry putLiteratureInventoryEntry = new PutLiteratureInventoryEntry();
			putLiteratureInventoryEntry.setSession(session);
			putLiteratureInventoryEntry.setLiteratureInventoryEntry(literatureInventoryEntry);
			putLiteratureInventoryEntryResponse = inventoryEngineStub.putLiteratureInventoryEntry(putLiteratureInventoryEntry);
			
			return putLiteratureInventoryEntryResponse.getSuccess();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Stores the given list of inventory entries. This could be used as bulk upload.
	 * 
	 * @param literatureInventoryList
	 * @return true to explicitly notify that everything was OK.
	 * @throws InventoryEngineNotAvailableFault Is thrown when e.g. the service is 
	 * not available to fulfill the request
	 * @throws InventoryEngineFault Is thrown if Inventory Engine is not able to fulfill your request. 
	 * This may happen if the Engine caught a back-end Exception.
	 */
	public Boolean putLiteratureInventoryEntries(LiteratureInventoryList literatureInventoryList)
			throws InventoryEngineNotAvailableFault, InventoryEngineFault {
		
		try {
			PutLiteratureInventoryEntries putLiteratureInventoryEntries = new PutLiteratureInventoryEntries();
			putLiteratureInventoryEntries.setSession(session);
			putLiteratureInventoryEntries.setLiteratureInventoryList(literatureInventoryList);
			PutLiteratureInventoryEntriesResponse putLiteratureInventoryEntriesResponse = 
				inventoryEngineStub.putLiteratureInventoryEntries(putLiteratureInventoryEntries);
			
			return putLiteratureInventoryEntriesResponse.getSuccess();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Deletes the given literature inventory entry
	 * 
	 * @param literatureInventoryEntry
	 * @return true to explicitly notify that everything was OK.
	 * @throws InventoryEngineNotAvailableFault Is thrown when e.g. the service is 
	 * not available to fulfill the request
	 * @throws InventoryEngineFault Is thrown if Inventory Engine is not able to fulfill your request. 
	 * This may happen if the Engine caught a back-end Exception.
	 */
	public Boolean deleteLiteratureInventoryEntry(LiteratureInventoryEntry literatureInventoryEntry)
			throws InventoryEngineNotAvailableFault, InventoryEngineFault {
		
		try {
			DeleteLiteratureInventoryEntry deleteLiteratureInventoryEntry = new DeleteLiteratureInventoryEntry();
			deleteLiteratureInventoryEntry.setSession(session);
			deleteLiteratureInventoryEntry.setLiteratureInventoryEntry(literatureInventoryEntry);
			DeleteLiteratureInventoryEntryResponse deleteLiteratureInventoryEntryResponse = 
				inventoryEngineStub.deleteLiteratureInventoryEntry(deleteLiteratureInventoryEntry);

			return deleteLiteratureInventoryEntryResponse.getSuccess();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Deletes the given list of literature inventory entries
	 * 
	 * @param literatureInventoryList
	 * @return true to explicitly notify that everything was OK.
	 * @throws InventoryEngineNotAvailableFault Is thrown when e.g. the service is 
	 * not available to fulfill the request
	 * @throws InventoryEngineFault Is thrown if Inventory Engine is not able to fulfill your request. 
	 * This may happen if the Engine caught a back-end Exception.
	 */
	public Boolean deleteLiteratureInventoryEntries(LiteratureInventoryList literatureInventoryList)
			throws InventoryEngineNotAvailableFault, InventoryEngineFault {
		
		try {
			DeleteLiteratureInventoryEntries deleteLiteratureInventoryEntries = new DeleteLiteratureInventoryEntries();
			deleteLiteratureInventoryEntries.setSession(session);
			deleteLiteratureInventoryEntries.setLiteratureInventoryList(literatureInventoryList);
			DeleteLiteratureInventoryEntriesResponse deleteLiteratureInventoryEntriesResponse = 
				inventoryEngineStub.deleteLiteratureInventoryEntries(deleteLiteratureInventoryEntries);
			
			return deleteLiteratureInventoryEntriesResponse.getSuccess();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
}

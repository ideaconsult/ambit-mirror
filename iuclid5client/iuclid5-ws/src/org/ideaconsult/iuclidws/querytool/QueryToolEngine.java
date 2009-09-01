package org.ideaconsult.iuclidws.querytool;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.ideaconsult.iuclidws.ClientServiceException;
import org.ideaconsult.iuclidws.PropertiesUtil;
import org.ideaconsult.iuclidws.StubUtil;
import org.ideaconsult.iuclidws.session.SessionEngineFault;
import org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault;
import org.ideaconsult.iuclidws.session.SessionSingleton;
import org.ideaconsult.iuclidws.types.Types.CountQueryExpression;
import org.ideaconsult.iuclidws.types.Types.CountQueryExpressionResponse;
import org.ideaconsult.iuclidws.types.Types.ExecuteQueryExpression;
import org.ideaconsult.iuclidws.types.Types.ExecuteQueryExpressionResponse;
import org.ideaconsult.iuclidws.types.Types.QueryExpression;
import org.ideaconsult.iuclidws.types.Types.QueryResult;
import org.ideaconsult.iuclidws.types.Types.Session;

/**
 * <b>Query Tool Engine will only work when the
 * IUCLID 5 Query Tool server-side plug-in is installed on the IUCLID 5 distributed version
 * server</b>. Otherwise, the interface methods of the Query Tool Engine will abort with an exception.
 * 
 * @author Nikolay Taskov
 */
public class QueryToolEngine {
	
	// fields
	/** Query tool engine endpoint context */
	private String END_POINT_QUERY_TOOL_ENGINE = "QueryToolEngine";
	
	/** Current session */
	private Session session;
	
	/** Instance of QueryToolEngineStub class */
	private QueryToolEngineStub queryToolEngineStub;
	
	private String target;
	
	/** The logger for this class */
	private final static Logger logger = Logger.getLogger(QueryToolEngine.class);
	
	// constructors
	/**
	 * Init QueryToolEngine class and logged in automatically.
	 * 
	 * @throws ClientServiceException 
	 * @throws SessionEngineFault 
	 * @throws SessionEngineNotAvailableFault 
	 */
	public QueryToolEngine() throws SessionEngineNotAvailableFault, SessionEngineFault, ClientServiceException, AxisFault {
		this(SessionSingleton.getInstance().getSession());
	}
	
	/**
	 * Init QueryToolEngine class with custom Session
	 * 
	 * @param session
	 */
	public QueryToolEngine(Session session) throws AxisFault {
		this(session, PropertiesUtil.getTarget());
	}
	
	/**
	 * Init QueryToolEngine class with custom Session and target
	 * 
	 * @param session
	 */
	public QueryToolEngine(Session session, String target) throws AxisFault {
		this.session = session;
		this.target = target;
		
		queryToolEngineStub = new QueryToolEngineStub(StubUtil.concatEndpointAddress(
				this.target	, END_POINT_QUERY_TOOL_ENGINE));
		StubUtil.initializeAxisService(queryToolEngineStub._getServiceClient());		
	}
	
	// methods
	/**
	 * Count the number of result items that are produced when the specified query
	 * expression is executed. Note that counting does not support the block
	 * combination logic, so only simple query blocks can be contained in the query
	 * expression to be counted.
	 * 
	 * @param queryExpression - the query expression to count as QueryExpression. Note that a
	 * query expression specified can only contain simple query blocks. Logical
	 * operations that are combining query blocks are not supported by countQuery
	 * @return countQuery - the expected number of result items that will be found when the query OUT
     * expression specified will be executed
	 * @throws QueryToolEngineNotAvailableFault Is thrown when the back-end service 
	 * is not available to fulfill the request
	 * @throws QueryToolEngineFault Is thrown if the Query Tool Engine is not able to fulfill your request. This may
	 * happen if the Engine caught a back-end Exception
	 */
	public Long countQuery(QueryExpression queryExpression) throws QueryToolEngineNotAvailableFault, QueryToolEngineFault {
		
		try {
			CountQueryExpression countQueryExpression = new CountQueryExpression();
			countQueryExpression.setSession(session);
			countQueryExpression.setExpression(queryExpression);
			CountQueryExpressionResponse countQueryExpressionResponse = queryToolEngineStub.countQuery(countQueryExpression);
			
			return countQueryExpressionResponse.getCount();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Execute the given query expression, which may contain simple blocks as well
     * as block conjunction logic, and return the found query result.
	 * 
	 * @param queryExpression - the query expression to execute as QueryExpression
	 * @return queryResult - the query result which contains a list of query result items (containing the OUT
     * DocumentReferencePK of indexed items, parent documents, dossiers,
     * etc.) as well as original values as stored in the query index and the query fields
     * in the query expression that produced the result item
	 * @throws QueryToolEngineNotAvailableFault Is thrown when the back-end service 
	 * is not available to fulfill the request
	 * @throws QueryToolEngineFault Is thrown if the Query Tool Engine is not able to fulfill your request. This may
	 * happen if the Engine caught a back-end Exception
	 */
	public QueryResult executeQuery(QueryExpression queryExpression) throws QueryToolEngineNotAvailableFault, QueryToolEngineFault {
		
		try {
			ExecuteQueryExpression executeQueryExpression = new ExecuteQueryExpression();
			executeQueryExpression.setSession(session);
			executeQueryExpression.setExpression(queryExpression);
			ExecuteQueryExpressionResponse executeQueryExpressionResponse = queryToolEngineStub.executeQuery(executeQueryExpression);
			
			return executeQueryExpressionResponse.getQueryResult();
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		}
		
		return null;
	}
}

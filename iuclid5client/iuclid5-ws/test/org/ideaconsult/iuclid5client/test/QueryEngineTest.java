package org.ideaconsult.iuclid5client.test;

import junit.framework.TestCase;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.ideaconsult.iuclidws.ClientServiceException;
import org.ideaconsult.iuclidws.query.QueryEngine;
import org.ideaconsult.iuclidws.session.SessionEngineFault;
import org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault;
import org.ideaconsult.iuclidws.types.Types;
import org.ideaconsult.iuclidws.types.Types.DocumentType;
import org.ideaconsult.iuclidws.types.Types.DocumentTypeType;
import org.ideaconsult.iuclidws.types.Types.QueryDefinition;
import org.ideaconsult.iuclidws.types.Types.QueryDefinitionList;
import org.ideaconsult.iuclidws.types.Types.QueryNameList;
import org.ideaconsult.iuclidws.types.Types.QueryParameterDefinition;
import org.ideaconsult.iuclidws.types.Types.QueryParameterDefinitionList;
import org.ideaconsult.iuclidws.types.Types.QueryResultTypeList;

/**
 * The <code>QueryEngineTest</code> is used to demonstrate and test the
 * QueryEngine Web Service
 * 
 */
public class QueryEngineTest extends TestCase {

	private final static Logger LOGGER = Logger.getLogger(QueryEngineTest.class);

	public static final String SUBSTANCE_TYPE = "Substance";

	private static QueryEngine queryEngine;

	/**
	 * 
	 * @throws ClientServiceException
	 * @throws SessionEngineFault
	 * @throws SessionEngineNotAvailableFault
	 * @throws AxisFault
	 */
	public QueryEngineTest() throws AxisFault, SessionEngineNotAvailableFault, SessionEngineFault,
			ClientServiceException {
		super(QueryEngineTest.class.getSimpleName());
		queryEngine = new QueryEngine();
	}

	/**
	 * Test case to demonstrate how you could receive the available query name
	 * IDs
	 * 
	 * @throws Exception
	 *             for all occurred problems
	 */
	public void testGetAllQueryNames() throws Exception {
		LOGGER.info("test looking for all query names - start");
		try {
			final QueryNameList allNames = queryEngine.getAllQueryNames();
			LOGGER.info("Received " + allNames.getQueryName().length + " query name IDs (see list). \n");

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < allNames.getQueryName().length; i++) {
				sb.append("    ").append(allNames.getQueryName()[i].getId()).append("\n");
			}
			LOGGER.info(sb);
		} finally {
			LOGGER.info("test looking for all query names - end\n");
		}
	}

	/**
	 * Test case to demonstrate how you could receive the available query result
	 * types ({@link DocumentTypeWs})
	 * 
	 * @throws Exception
	 *             for all occurred problems
	 */
	public void testGetAllResultTypes() throws Exception {
		LOGGER.info("test looking for all query result types - start");

		try {
			QueryResultTypeList queryResultTypeList = queryEngine.getAllQueryResultTypes();
			DocumentType[] documentTypesArray = queryResultTypeList.getDocumentType();

			LOGGER.info("Received " + documentTypesArray.length + " query result types (see list).\n");
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < documentTypesArray.length; i++) {
				sb.append("    ").append(documentTypesArray[i].getType()).append("\n");
			}
			LOGGER.info(sb);

		} finally {
			LOGGER.info("test looking for all query result types - end\n");
		}
	}

	/**
	 * Test case to demonstrate how you could receive the available
	 * {@link QueryDefinition}
	 * 
	 * @throws Exception
	 *             for all occurred problems
	 */
	public void testGetAllQueryDefinitions() throws Exception {
		LOGGER.info("test looking for all query definition - start");
		try {
			QueryDefinitionList queryDefinitionList = queryEngine.getAllQueryDefinitions();
			printQueryDefinitionList(queryDefinitionList);
		} finally {
			LOGGER.info("test looking for all query definition - end\n");
		}
	}

	/**
	 * Test case to demonstrate how you could receive the available
	 * {@link QueryDefinition} for one specific {@link DocumentTypeType}.<br>
	 * <br>
	 * For this test we want to get all queries for substance.
	 * 
	 * @throws Exception
	 *             for all occurred problems
	 */
	public void testGetAllQueriesForSubstance() throws Exception {
		LOGGER.info("test looking for all query definition which returns a substance - start");
		try {
			// create the substance type
			final Types.DocumentTypeType documentTypeType = new DocumentTypeType(SUBSTANCE_TYPE, true);
			final QueryDefinitionList allQueryDefinitionList = queryEngine
					.getQueryDefinitionsByResultDocumentType(documentTypeType);

			assertFalse("The received list is empty", allQueryDefinitionList.getQueryDefinition().length == 0);

			printQueryDefinitionList(allQueryDefinitionList);
		} finally {
			LOGGER.info("test looking for all query definition which returns a substance - end\n");
		}
	}

	private void printQueryDefinitionList(QueryDefinitionList qdl) {
		QueryDefinition[] queryDefinitionsArray = qdl.getQueryDefinition();
		LOGGER.info((new StringBuilder()).append("Received ").append(queryDefinitionsArray.length).append(
				" query definition (see list). ").toString());

		for (int i = 0; i < queryDefinitionsArray.length; i++) {
			printQueryDefinition(queryDefinitionsArray[i]);
		}
	}

	private void printQueryDefinition(QueryDefinition definition) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("Query id: ");
		sb.append(definition.getQueryName().getId());
		sb.append("\n");
		sb.append("   Result document type: ");
		sb.append(definition.getResultDocumentType().getType());
		sb.append("\n");
		sb.append("   Query parameters:");
		sb.append("\n");
		sb.append(getQueryParameterDefinitionList(definition));
		LOGGER.info((new StringBuilder()).append("     -) ").append(sb.toString()).toString());
	}

	private String getQueryParameterDefinitionList(QueryDefinition definition) {
		QueryParameterDefinitionList queParameterDefinitionList = definition
				.getQueryParameterDefinitionList();
		QueryParameterDefinition[] queParameterDefinitionsArray = queParameterDefinitionList
				.getQueryParameterDefinition();

		StringBuilder sb = new StringBuilder();
		if (queParameterDefinitionsArray != null) {
			for (int i = 0; i < queParameterDefinitionsArray.length; i++) {
				sb.append("       parameter name: ");
				sb.append(queParameterDefinitionsArray[i].getQueryParameterName());
				sb.append("\n ");
				sb.append("             parameter data type: ");
				sb.append(queParameterDefinitionsArray[i].getQueryParameterDataType().getValue());
				sb.append("\n ");
				sb.append("             phrase group id: ");
				sb.append(queParameterDefinitionsArray[i].getPhraseGroupID());
				sb.append("\n ");
			}
		}

		return sb.toString();
	}

}

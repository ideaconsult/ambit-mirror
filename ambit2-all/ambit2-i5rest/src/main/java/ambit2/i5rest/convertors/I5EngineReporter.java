package ambit2.i5rest.convertors;

import java.io.IOException;
import java.io.Writer;
import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.ideaconsult.iuclidws.query.QueryDeliveredTooManyHitsFault;
import org.ideaconsult.iuclidws.query.QueryEngine;
import org.ideaconsult.iuclidws.query.QueryEngineFault;
import org.ideaconsult.iuclidws.query.QueryEngineNotAvailableFault;
import org.ideaconsult.iuclidws.query.QueryParameterFault;
import org.ideaconsult.iuclidws.query.QueryTimeoutFault;
import org.ideaconsult.iuclidws.session.SessionEngineFault;
import org.ideaconsult.iuclidws.session.SessionEngineNotAvailableFault;
import org.ideaconsult.iuclidws.types.Types.DocumentReference;
import org.ideaconsult.iuclidws.types.Types.QueryDefinition;
import org.ideaconsult.iuclidws.types.Types.QueryExecutionDefinition;
import org.ideaconsult.iuclidws.types.Types.QueryParameterList;
import org.ideaconsult.iuclidws.types.Types.QueryResultList;
import org.ideaconsult.iuclidws.types.Types.ResultLimitationOption;
import org.ideaconsult.iuclidws.types.Types.Session;
import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.i5rest.resource.SubstanceResource;
import ambit2.rest.AbstractResource;

/**
 *  Retrieves info from QueryEngine, given query parameters
 * @author nina
 *
 */
public class I5EngineReporter extends I5Reporter<QueryParameterList> {

	public I5EngineReporter( Request request,Session session) {
		super(request,session);
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 5144043712331173973L;
		
	public Writer process(QueryParameterList param)
			throws AmbitException {
		try {
			header();
			login();
			QueryEngine queryEngine = new QueryEngine(session,getTarget());
			QueryDefinition queryDefinition = queryEngine.getQueryDefinitionByName(QueryEngine.FIND_COMPLEX_REFERENCESUBSTANCE_ID);

			QueryExecutionDefinition x = new QueryExecutionDefinition();
			x.setQueryName(queryDefinition.getQueryName());
			x.setQueryParameterList(param);
			x.setOption(ResultLimitationOption.INFINITE);
			
			QueryResultList resultList = queryEngine.executeQuery(x);
			
			DocumentReference refs[] = resultList.getDocumentReference();
			if (refs == null) throw new NotFoundException();
	
				for (DocumentReference ref:refs) 
					processItem(ref);
		
			} catch (QueryEngineNotAvailableFault x) {
				throw new AmbitException(x);
			} catch (QueryDeliveredTooManyHitsFault x) {
				throw new AmbitException(x);
			} catch (QueryTimeoutFault x) {
				throw new AmbitException(x);
			} catch (QueryEngineFault x) {
				throw new AmbitException(x);
			} catch (SessionEngineFault x) {
				throw new AmbitException(x);
			} catch (SessionEngineNotAvailableFault x) {
				throw new AmbitException(x);
			} catch (AxisFault x) {
				throw new AmbitException(x);
			} catch (QueryParameterFault x) {
				throw new AmbitException(x);
			} catch (RemoteException x) {	
				throw new AmbitException(x);
			} catch (IOException x) {
				throw new AmbitException(x);
			} finally {
				LOGGER.info("test looking for all query definition - end\n");
				try { 
					logout(); 
					try { footer();}catch (Exception x) {}
				} catch (Exception x) {
					LOGGER.info(x);
				}
				
			}
			
			return output;
	}
	public void processItem(DocumentReference ref) throws AmbitException {
		try {

			output.write("<tr>");
			output.write("<th align='right'>");
			output.append(ref.getDocType().toString());
			output.write("</th>");
			output.write("<td align='right'>");

			output.append(ref.getDescription().toString());
			output.write("</td>");
			output.write("<td align='right'>");
			output.append(String.format("&nbsp;<a href='%s/document?key=%s&user=%s&pass=%s&target=%s'>%s</a>",
					request.getRootRef(),
					Reference.encode(ref.getUniqueKey().toString()),
					Reference.encode(getUser()),
					Reference.encode(getPass()),
					Reference.encode(getTarget()),
					"Download XML document (.i5)"));
			output.write("</td>");
			output.write("</tr>");
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	
	@Override
	public void header() throws IOException {
		super.header();
		String key = SubstanceResource.getKey(request);
		if (key==null) key="";		
		output.write("<form action='' name='form' method='get'>");
		output.write("<table>");

		output.write("<tr>");
		output.write(String.format("<th><label for='%s'>CAS RN&nbsp;</label></th><td><input type='text' name='%s' size='60' value='%s'></td>",
				AbstractResource.search_param,
				AbstractResource.search_param,
				key));
		output.write("</tr>");
		output.write("<tr>");
		output.write(String.format("<th><label for='target'>IUCLID5 server&nbsp;</label></th><td><input type='text' name='target' size='60' value='%s'></td>",getTarget()));
		output.write("</tr>");
		output.write("<tr>");
		output.write(String.format("<th><label for='user'>User name&nbsp;</label></th><td><input type='text' name='user' size='60' value='%s'></td>",getUser()));
		output.write("</tr>");
		output.write("<tr>");
		output.write(String.format("<th><label for='pass'>Password&nbsp;</label></th><td><input type='password' name='pass' size='60' value='%s'></td>",getPass()));
		output.write("</tr>");
		output.write("<tr>");
		output.write("<td></td><td><input type='submit' value='Search'></td>");
		output.write("</tr>");
		output.write("</table>");
		output.write("</form>");	
		

		output.write("<table width='90%' border='0'>");
	}
	@Override
	public void footer() throws IOException {
		output.write("</table>");
		String key = SubstanceResource.getKey(request);
		if (key==null) key="";

		output.write(String.format("<hr><a href='%s/query?search=%s&user=%s&pass=%s&target=%s'>Retrieve ecotox data</a>",
				request.getRootRef(),
				Reference.encode(key),
				Reference.encode(getUser()),
				Reference.encode(getPass()),
				Reference.encode(getTarget())
				));
		super.footer();
	}
	public void setEnabled(boolean value) {
		
	}


}

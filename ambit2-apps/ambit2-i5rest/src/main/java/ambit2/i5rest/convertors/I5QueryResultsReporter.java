package ambit2.i5rest.convertors;

import java.io.IOException;
import java.io.Writer;

import org.ideaconsult.iuclidws.querytool.QueryToolEngine;
import org.ideaconsult.iuclidws.types.Types.ItemsSequence;
import org.ideaconsult.iuclidws.types.Types.Items_type0;
import org.ideaconsult.iuclidws.types.Types.QueryExpression;
import org.ideaconsult.iuclidws.types.Types.QueryResult;
import org.ideaconsult.iuclidws.types.Types.QueryResultItem;
import org.ideaconsult.iuclidws.types.Types.Session;
import org.ideaconsult.iuclidws.types.Types.ValuesSequence;
import org.ideaconsult.iuclidws.types.Types.Values_type0;
import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.NotFoundException;
import ambit2.i5rest.resource.SubstanceResource;
import ambit2.rest.AbstractResource;

/**
 * Results from I5 QueryToolEngine
 * @author nina
 *
 */
public class I5QueryResultsReporter extends I5Reporter<QueryExpression> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4178808515151866598L;
	public I5QueryResultsReporter(Request request,Session session) {
		super(request,session);
	}
	public Writer process(QueryExpression target) throws AmbitException {
			
			try {
				header();
				login();
				QueryToolEngine qtEngine = new QueryToolEngine(getSession(),getTarget());
				QueryResult result = qtEngine.executeQuery(target);
				//return output;
				return process(result);
			} catch (Exception x) {
				throw new AmbitException(x);
			} finally {
				try { logout();}catch (Exception x) {}
				try { footer();}catch (Exception x) {}
			}
	}
	
	public Writer process(QueryResult target) throws AmbitException {
		
        Items_type0 items = target.getItems();
        ItemsSequence[] seq = items.getItemsSequence();
        if (seq==null) throw new NotFoundException();
        
        for (ItemsSequence s : seq) {
        	
        	QueryResultItem r = s.getQueryResultItem();
        	
        	try {
    			output.append(String.format("&nbsp;<a href='%s/document?key=%s&user=%s&pass=%s&target=%s'>%s .i5 XML</a>",
    					request.getRootRef(),
    					Reference.encode(r.getParentPk().toString()),
    					Reference.encode(getUser()),
    					Reference.encode(getPass()),
    					Reference.encode(getTarget()),    					
    					r.getParentType().toString()  
    					));
    			
    			output.append(String.format("&nbsp;<a href='%s/document?key=%s&user=%s&pass=%s&target=%s'>%s .i5 XML</a>",
    					request.getRootRef(),
    					Reference.encode(r.getIndexPk().toString()),
    					Reference.encode(getUser()),
    					Reference.encode(getPass()),
    					Reference.encode(getTarget()),    					
    					r.getEndpointKind().toString()
    					
    					));
    			
		    	Values_type0 values = r.getValues();
		    	ValuesSequence[] vs = values.getValuesSequence();
		    	for (ValuesSequence vvv: vs) {
		    		output.write("&nbsp;");
		    		output.write(vvv.getQueryResultValue().getQueryResultValueChoice_type0().getRangeQueryField().getQueryField().getFieldName());
		    		output.write("&nbsp;");
		    		output.write(vvv.getQueryResultValue().getOriginal());
		    		output.write("&nbsp;");
		    	}
		    	output.write("<br>");
        	} catch (Exception x) {
        		
        	}
        }
    	return output;
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
		output.write("<h3>Short term toxicity to fish</h3>");
	}
	@Override
	public void footer() throws IOException {
		String key = SubstanceResource.getKey(request);
		if (key==null) key="";

		output.write(String.format("<hr><a href='%s/compound?search=%s&user=%s&pass=%s&target=%s'>Retrieve substance data</a>",
				request.getRootRef(),
				Reference.encode(key),
				Reference.encode(getUser()),
				Reference.encode(getPass()),
				Reference.encode(getTarget())
				));
		super.footer();
	}
}

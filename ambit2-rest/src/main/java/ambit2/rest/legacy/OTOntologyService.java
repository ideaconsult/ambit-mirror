package ambit2.rest.legacy;

import net.idea.restnet.c.task.ClientResourceWrapper;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

/**
 * Implementation of OpenTox API {@link http://opentox.org/dev/apis/api-1.1/Ontology%20service}
 * 
 * @author nina
 *
 * @param <Z>
 */
@Deprecated
public class OTOntologyService<Z> extends OTObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8906409505260761688L;
	public OTOntologyService(Reference ref) {
		super(ref);
	}
	public OTOntologyService(String ref) {
			super(ref);
	}
	
	public Z processSolution(QuerySolution row) throws Exception {
		throw new Exception("Not implemented");
	}
	public Z createObject() throws Exception {
		throw new Exception("Not implemented");
	}
	public void header(ResultSet results) throws Exception {}
	public void footer(ResultSet results) throws Exception {}
	
	public String report(String query) throws Exception  {


		ClientResourceWrapper resource = null;
		Representation  r = null;
		try {
			resource = new ClientResourceWrapper(getUri());
			Form form = new Form();
			form.add("query",query);
			r = resource.post(form,MediaType.TEXT_HTML);
			String s = r.getText();
			return s;
			//ex = QueryExecutionFactory.sparqlService(uri.toString(), query);
			//results = ex.execSelect();
			//return ResultSetFormatter.asText(results);
			
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,toString() + " " + x.getMessage(),x);
		} finally {
			try { r.release();} catch (Exception x) {} ;
			try { resource.release();} catch (Exception x) {} ;
			
		}

	}	
	public Z query(String query) throws Exception  {

			QueryExecution ex = null;
			try {
				ex = QueryExecutionFactory.sparqlService(getUri().toString(), query);
				ResultSet results = ex.execSelect();
				header(results);
				while (results.hasNext()) {
					QuerySolution solution = results.next();
					processSolution(solution);
				}
				footer(results);
				return createObject();
			} catch (Exception x) {
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,toString(),x);
			} finally {
				try { ex.close();} catch (Exception x) {} ;
			}

		}	


}

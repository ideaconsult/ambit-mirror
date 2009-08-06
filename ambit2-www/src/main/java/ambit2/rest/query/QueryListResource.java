package ambit2.rest.query;

import java.util.ArrayList;
import java.util.Iterator;

import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;

import ambit2.rest.StatusException;
import ambit2.rest.algorithm.AlgorithmResource;

/**
 * Query types
 * @author nina
 *
 */
public class QueryListResource extends AlgorithmResource {
	public enum querytypes  {
		feature,smarts,smiles,substructure,inchi,similarity,dataset,endpoints
	};
	public QueryListResource(Context context, Request request, Response response) {
		super(context,request,response);
		setCategory("query");
	
	}
	@Override
	protected Iterator<String> createQuery(Context context, Request request,
			Response response) throws StatusException {
		ArrayList<String> q = new ArrayList<String>();
		for (querytypes d : querytypes.values())
			q.add(String.format("%s/%s",getCategory(),d.toString()));	
		return q.iterator();
	}
	

}

package ambit2.rest.template;

import java.io.Writer;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Dictionary;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.db.search.property.QueryOntology;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.StatusException;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.QueryResource;

/**
 * 
 * A resource wrapper fot {@link QueryOntology}
 * @author nina
 *
 */
public class OntologyResource extends QueryResource<QueryOntology, Object> {
	public static String resource = "/template";
	public static String resourceKey = "topnode";
	public static String resourceID = String.format("%s/{%s}",resource,resourceKey);

	@Override
	public IProcessor<QueryOntology, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
				return new StringConvertor(	new OntologyURIReporter(getRequest()) {
					@Override
					public void processItem(Object item, Writer output) {
						super.processItem(item, output);
						try {
						output.write('\n');
						} catch (Exception x) {}
					}
				},MediaType.TEXT_URI_LIST);
				
		} else 
			return new OutputStreamConvertor(
					new OntologyHTMLReporter(getRequest(),true)
					,MediaType.TEXT_HTML);
	}

	@Override
	protected QueryOntology createQuery(Context context, Request request,
			Response response) throws StatusException {
		Object key = request.getAttributes().get(resourceKey);
		QueryOntology q = new QueryOntology();
		q.setValue(key==null?null:new Dictionary(Reference.decode(key.toString()),null));
		return q;
	}

}

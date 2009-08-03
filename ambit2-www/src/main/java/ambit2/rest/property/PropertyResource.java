package ambit2.rest.property;

import java.io.Writer;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Variant;

import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.StringCondition;
import ambit2.db.search.property.RetrieveFieldNamesByAlias;
import ambit2.db.update.property.ReadProperty;
import ambit2.rest.DocumentConvertor;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.QueryResource;

/**
 * Feature definition resource
 * @author nina
 *
 */
public class PropertyResource extends QueryResource<IQueryRetrieval<Property>, Property> {
	public final static String featuredef = "/feature_definition";
	public final static String idfeaturedef = "id_feature_definition";
	public final static String featuredefID = String.format("%s/{%s}",featuredef,idfeaturedef);
	protected boolean collapsed ;
	
	public PropertyResource(Context context, Request request, Response response) {
		super(context,request,response);
		this.getVariants().add(new Variant(MediaType.TEXT_HTML));
		this.getVariants().add(new Variant(MediaType.TEXT_XML));
		this.getVariants().add(new Variant(MediaType.TEXT_URI_LIST));	
	}
	
	
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException {
		if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
			return new DocumentConvertor(new PropertyDOMReporter(getRequest().getRootRef()));
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
				return new StringConvertor(	new PropertyURIReporter(getRequest().getRootRef()) {
					@Override
					public void processItem(Property dataset, Writer output) {
						super.processItem(dataset, output);
						try {
						output.write('\n');
						} catch (Exception x) {}
					}
				},MediaType.TEXT_URI_LIST);
				
		} else 
			return new OutputStreamConvertor(
					new PropertyHTMLReporter(getRequest().getRootRef(),collapsed)
					,MediaType.TEXT_HTML);
	}

	@Override
	protected IQueryRetrieval<Property> createQuery(Context context,
			Request request, Response response) throws AmbitException {
		
		Object o = request.getAttributes().get(idfeaturedef);
		try {
			collapsed = o==null;
			if (o==null) {
				Form form = request.getResourceRef().getQueryAsForm();
				Object key = form.getFirstValue("search");
				if (key != null) {
					RetrieveFieldNamesByAlias q = new RetrieveFieldNamesByAlias(Reference.decode(key.toString()));
					q.setCondition(StringCondition.getInstance(StringCondition.C_REGEXP));
					return q;
				} else return new ReadProperty();
			}
			else return new ReadProperty(new Integer(o.toString()));
		} catch (Exception x) {
			collapsed = true;
			return new ReadProperty();
		} finally {
		}
	}

}

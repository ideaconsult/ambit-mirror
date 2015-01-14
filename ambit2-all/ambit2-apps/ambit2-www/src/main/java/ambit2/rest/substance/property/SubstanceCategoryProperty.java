package ambit2.rest.substance.property;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.idea.i5.io.I5_ROOT_OBJECTS;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.c.resource.CatalogResource;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;

public class SubstanceCategoryProperty extends CatalogResource<EffectRecord<String,IParams, String>> {
	protected List<EffectRecord<String, IParams, String>> effects;
	
	public SubstanceCategoryProperty() {
		super();
		effects = null;
		setHtmlbyTemplate(true);
	}
	@Override
	public String getTemplateName() {
		return "jsonplaceholder.ftl";
	}
	@Override
	protected Iterator<EffectRecord<String, IParams, String>> createQuery(
			Context context, Request request, Response response)
			throws ResourceException {
	    try {
		Object topcategory = Reference.decode(request.getAttributes().get(SubstancePropertyResource.topcategory).toString());
		Object endpointcategory = Reference.decode(request.getAttributes().get(SubstancePropertyResource.endpointcategory).toString());
		if (topcategory==null || endpointcategory==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		I5_ROOT_OBJECTS category;
		try {
			category = I5_ROOT_OBJECTS.valueOf(endpointcategory.toString().replace("_SECTION",""));
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage());
		}
		if (!category.getTopCategory().equals(topcategory.toString())) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					String.format("Expected /property/%s/%s",category.getTopCategory(),category.name()));
		}
		effects = new ArrayList<EffectRecord<String, IParams, String>>();
		
		if (category.getEndpoints()!=null && category.getEndpoints().length>0)
			for (String endpoint : category.getEndpoints()) {
				EffectRecord record = category.createEffectRecord();
				record.setEndpoint(endpoint);
				effects.add(record);
			}
		else 
			effects.add(category.createEffectRecord());
		return effects.iterator();
	    }catch (Exception x) {
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage());
	    }
	}
	protected Form getParams() {
		return getResourceRef(getRequest()).getQueryAsForm();
	}
	@Override
	public IProcessor<Iterator<EffectRecord<String, IParams, String>>, Representation> createJSONConvertor(
			Variant variant, String filenamePrefix) throws AmbitException,
			ResourceException {
		if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
			String jsonpcallback = getParams().getFirstValue("jsonp");
			if (jsonpcallback==null) jsonpcallback = getParams().getFirstValue("callback");
			EffectRecordJSONReporter cmpreporter = new EffectRecordJSONReporter(getRequest(),jsonpcallback);
			return new StringConvertor(cmpreporter,MediaType.APPLICATION_JAVASCRIPT,filenamePrefix);
		} else { //json by default
			EffectRecordJSONReporter cmpreporter = new EffectRecordJSONReporter(getRequest(),null);
			return new StringConvertor(cmpreporter,MediaType.APPLICATION_JSON,filenamePrefix);
		}		
	}
}

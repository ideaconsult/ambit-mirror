package ambit2.rest.substance.study;

import java.util.Map;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

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
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.db.substance.study.ReadSubstanceStudy;
import ambit2.rest.OpenTox;
import ambit2.rest.query.QueryResource;
import ambit2.rest.substance.SubstanceResource;

public class SubstanceStudyResource<Q extends IQueryRetrieval<ProtocolApplication>> extends QueryResource<Q,ProtocolApplication> { 

	public final static String study = OpenTox.URI.study.getURI();
	public final static String idstudy = OpenTox.URI.study.getKey();
	public final static String studyID = OpenTox.URI.study.getResourceID();
	protected String substanceUUID;
	
	public SubstanceStudyResource() {
		super();
		setHtmlbyTemplate(true);
	}

	@Override
	public String getTemplateName() {
		return "substancestudy.ftl";
	}
	@Override
	public IProcessor<Q, Representation> createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		/* workaround for clients not being able to set accept headers */
		Form acceptform = getResourceRef(getRequest()).getQueryAsForm();
		String media = acceptform.getFirstValue("accept-header");
		if (media != null) variant.setMediaType(new MediaType(media));

		String filenamePrefix = getRequest().getResourceRef().getPath();
		/*
		if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			QueryURIReporter r = (QueryURIReporter)getURIReporter();
			r.setDelimiter("\n");
			return new StringConvertor(
					r,MediaType.TEXT_URI_LIST,filenamePrefix);
		} else 
		*/
		if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
			String jsonpcallback = getParams().getFirstValue("jsonp");
			if (jsonpcallback==null) jsonpcallback = getParams().getFirstValue("callback");
			SubstanceStudyJSONReporter cmpreporter = new SubstanceStudyJSONReporter(getRequest(),jsonpcallback);
			return new OutputWriterConvertor<ProtocolApplication, Q>(
					cmpreporter,
					MediaType.APPLICATION_JAVASCRIPT,filenamePrefix);
		} else { //json by default
			SubstanceStudyJSONReporter cmpreporter = new SubstanceStudyJSONReporter(getRequest(),null);
			return new OutputWriterConvertor<ProtocolApplication, Q>(
					cmpreporter,
					MediaType.APPLICATION_JSON,filenamePrefix);
		}
	}	
	@Override
	protected Q createQuery(Context context, Request request, Response response)
			throws ResourceException {
		Object key = request.getAttributes().get(SubstanceResource.idsubstance);
		if (key==null) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		} else {
			substanceUUID = key.toString();
			try {
				Form form = getRequest().getResourceRef().getQueryAsForm();
				String topCategory = form.getFirstValue("top");
				String category = form.getFirstValue("category");
				String property = form.getFirstValue("property");
				String property_uri = form.getFirstValue("property_uri");
				ReadSubstanceStudy q = new ReadSubstanceStudy();
				q.setFieldname(substanceUUID);
				if (topCategory!=null || category!=null || property != null || property_uri!=null) {
					Protocol p = new ambit2.base.data.study.Protocol("");
					p.setTopCategory(topCategory);
					p.setCategory(category);
					ProtocolApplication papp = new ProtocolApplication(p);
					if (property_uri!=null) try {
						//not nice REST style, but easiest to parse the URI
						//not nice REST style, but easiest to parse the URI
						Reference puri = new Reference(property_uri.endsWith("/")?property_uri.substring(0, property_uri.length()-2):property_uri);
						//the very last segment denotes protocol, then study type, then one is the endpoint hash
						if (puri.getSegments().get(puri.getSegments().size()-1).indexOf("-") > 0) //this is the protocol
						    property=puri.getSegments().get(puri.getSegments().size()-3);
						else    
						    property=puri.getSegments().get(puri.getSegments().size()-2);
						if (property.length()!=40) property = null;
					} catch (Exception x) {}
					if (property!=null) {
						EffectRecord effect = new EffectRecord();
						effect.setSampleID(property);
						papp.addEffect(effect);
					}
					q.setValue(papp);
					
				}
				//q.setValue(new SubstanceRecord(Integer.parseInt(key.toString())));
				//q.setFieldname(relation);
				return (Q)q;
			} catch (Exception x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			}
		}

	}
	
	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		map.put("substanceUUID", substanceUUID);
	}
}

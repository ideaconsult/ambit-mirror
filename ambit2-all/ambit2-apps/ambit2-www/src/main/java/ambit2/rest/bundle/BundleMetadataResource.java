package ambit2.rest.bundle;

import java.util.Map;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.update.bundle.ReadBundle;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.dataset.DatasetURIReporter;
import ambit2.rest.dataset.MetadataRDFReporter;
import ambit2.rest.dataset.MetadatasetJSONReporter;
import ambit2.rest.dataset.MetadatasetURIReporter;
import ambit2.user.rest.resource.AmbitDBQueryResource;

public class BundleMetadataResource extends  AmbitDBQueryResource<IQueryRetrieval<SubstanceEndpointsBundle>,SubstanceEndpointsBundle> {
	protected SubstanceEndpointsBundle dataset;
	@Override
	public String getTemplateName() {
		return "bundles.ftl";
	}
	
	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		if (dataset!=null)
			map.put("datasetid",dataset.getID());
	}
	
	/*
	protected SubstanceEndpointsBundle createObjectFromWWWForm(Representation entity)
			throws ResourceException {
		Form form = new Form(entity);
		//only name and license updated
		SubstanceEndpointsBundle dataset = new SubstanceEndpointsBundle();
		dataset.setName(form.getFirstValue("title"));
		String licenseOptions = form.getFirstValue("licenseOptions");
		String license = form.getFirstValue("license");
		if ((licenseOptions==null) || "Other".equals(licenseOptions))
			dataset.setLicenseURI(license);
		else 
			dataset.setLicenseURI(licenseOptions);
		
		dataset.setrightsHolder(form.getFirstValue("rightsHolder"));
		return dataset;
	}	

	
	
	@Override
	protected AbstractUpdate createUpdateObject(SubstanceEndpointsBundle entry)
			throws ResourceException {
		return new UpdateBundle(entry);
	}
	*/	
	@Override
	protected IQueryRetrieval<SubstanceEndpointsBundle> createQuery(
			Context context, Request request, Response response)
			throws ResourceException {
		ReadBundle query = null;
		Object id = request.getAttributes().get("idbundle");
		if (id != null)  try {
				Integer idnum = new Integer(Reference.decode(id.toString()));
				dataset = new SubstanceEndpointsBundle();
				dataset.setID(idnum);
				query = new ReadBundle();
				query.setValue(dataset);
				return query;
			} catch (NumberFormatException x) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			}
		else 
			return new ReadBundle();	
	}
	@Override
	public IProcessor<IQueryRetrieval<SubstanceEndpointsBundle>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		String filenamePrefix = getRequest().getResourceRef().getPath();
		if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
				return new OutputWriterConvertor(new MetadatasetJSONReporter<IQueryRetrieval<SubstanceEndpointsBundle>,SubstanceEndpointsBundle>(getRequest()),MediaType.APPLICATION_JSON);			
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor(	new DatasetURIReporter<IQueryRetrieval<SubstanceEndpointsBundle>,SubstanceEndpointsBundle>(getRequest()),MediaType.TEXT_URI_LIST,filenamePrefix);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
				variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES) ||
				variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIG) ||
				variant.getMediaType().equals(MediaType.APPLICATION_RDF_TRIX) 
				) {
			return new RDFJenaConvertor<SubstanceEndpointsBundle, IQueryRetrieval<SubstanceEndpointsBundle>>(
					new MetadataRDFReporter<SubstanceEndpointsBundle,IQueryRetrieval<SubstanceEndpointsBundle>>(getRequest(),
							getDocumentation(),variant.getMediaType()),variant.getMediaType(),filenamePrefix);			

			
		} else //default json 	
			return new OutputWriterConvertor(new MetadatasetJSONReporter<IQueryRetrieval<SubstanceEndpointsBundle>,SubstanceEndpointsBundle>(getRequest()),MediaType.APPLICATION_JSON);
	}
	
	@Override
	protected QueryURIReporter<SubstanceEndpointsBundle, IQueryRetrieval<SubstanceEndpointsBundle>> getURIReporter(
			Request baseReference) throws ResourceException {
		return new MetadatasetURIReporter<IQueryRetrieval<SubstanceEndpointsBundle>,SubstanceEndpointsBundle>(baseReference,getDocumentation());
	}
	
	
}

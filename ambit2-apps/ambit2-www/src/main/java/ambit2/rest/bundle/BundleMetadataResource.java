package ambit2.rest.bundle;

import java.util.Map;

import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.update.AbstractUpdate;
import ambit2.db.update.bundle.ReadBundle;
import ambit2.db.update.bundle.UpdateBundle;
import ambit2.rest.rdf.RDFMetaDatasetIterator;
import ambit2.rest.rdf.RDFObjectIterator;

public class BundleMetadataResource extends AbstractMetadataResource<SubstanceEndpointsBundle> {
	@Override
	public String getTemplateName() {
		return "bundles.ftl";
	}
	@Override
	protected RDFObjectIterator<SubstanceEndpointsBundle> createObjectIterator(
			Representation entity) throws ResourceException {

		RDFMetaDatasetIterator<SubstanceEndpointsBundle> iterator = new RDFMetaDatasetIterator<SubstanceEndpointsBundle>(entity,entity.getMediaType()) {
			@Override
			protected SubstanceEndpointsBundle createRecord() {
				return new SubstanceEndpointsBundle();
			}
	
		};
		iterator.setForceReadRDFLocalObjects(true);
		iterator.setBaseReference(getRequest().getRootRef());
		return iterator;
	}
	
	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
		super.configureTemplateMap(map, request, app);
		if (dataset!=null)
			map.put("datasetid",dataset.getID());
	}
	
	//udpate support
	@Override
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
	
	//query
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
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	}
	
}

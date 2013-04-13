package ambit2.rest.dataEntry;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.SourceDataset;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.update.tuple.QueryDataEntry;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.DatasetStructuresResource;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.query.StructureQueryResource;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.ConformerResource;

/**
 * 
 * @author nina
 *
 */
public class DataEntryResource extends StructureQueryResource<IQueryRetrieval<IStructureRecord>> {
	public final static String resourceTag = "/dataEntry";
	public final static String resourceKey = "idDataEntry";
	
	//TODO properties per tuple!
	@Override
	protected String getDefaultTemplateURI(Context context, Request request,Response response) {
		Object id = request.getAttributes().get(DatasetStructuresResource.datasetKey);
		if (id != null)
			//return String.format("riap://application/dataset/%s%s",id,PropertyResource.featuredef);
		return String.format("%s%s/%s%s",
				getRequest().getRootRef(),OpenTox.URI.dataset.getURI(),id,PropertyResource.featuredef);		
		else 
			return super.getDefaultTemplateURI(context,request,response);
			
	}
	@Override
	protected IQueryRetrieval<IStructureRecord> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		
		setGroupProperties(context, request, response);
		setTemplate(createTemplate(context, request, response));
		QueryDataEntry query = new QueryDataEntry();
		query.setValue(getDataset());
		query.setFieldname(getRecord());
		
		Form form = getResourceRef(getRequest()).getQueryAsForm();
		try { includeMol = "true".equals(form.getFirstValue("mol")); } catch (Exception x) { includeMol=false;}
		setPaging(form, query);
		return query;
	}
	
	protected IStructureRecord getRecord() throws ResourceException {
		IStructureRecord record = new StructureRecord();
		//idcompound
		try {
			Object id = getRequest().getAttributes().get(CompoundResource.idcompound);
			if (id != null) 
				record.setIdchemical(new Integer(Reference.decode(id.toString())));
		} catch (Exception x) {	record.setIdchemical(-1);} 
		//idstructure
		try {
			Object id = getRequest().getAttributes().get(ConformerResource.idconformer);
			if (id != null) 
				record.setIdstructure(new Integer(Reference.decode(id.toString())));
		} catch (Exception x) {	record.setIdstructure(-1);} 	
		//idtuple
		try {
			Object id = getRequest().getAttributes().get(resourceKey);
			if (id != null) 
				record.setDataEntryID(new Integer(Reference.decode(id.toString())));
		} catch (Exception x) {	record.setDataEntryID(-1);} 	
		
		return record;
	}
	
	protected SourceDataset getDataset() throws ResourceException {
		Object id = getRequest().getAttributes().get(DatasetStructuresResource.datasetKey);
		if (id != null)  try {
			id = Reference.decode(id.toString());
			int iddataset = new Integer(id.toString());
			if (iddataset>0) {
				SourceDataset ds = new SourceDataset();
				ds.setId(iddataset);
				return ds;
			}
		} catch (NumberFormatException x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
		} 
		return null;
	}
}

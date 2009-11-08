package ambit2.rest.query;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.search.structure.QueryDatasetByID;
import ambit2.db.search.structure.QuerySMARTS;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.descriptors.FunctionalGroup;
import ambit2.rest.dataset.DatasetResource;
import ambit2.rest.structure.CompoundResource;

/**
 * SMARTS search in database
 * @author nina
 *
 */
public class SmartsQueryResource  extends StructureQueryResource<IQueryRetrieval<IStructureRecord>> {
	public final static String smartsKey =  "smarts";
	public final static String resourceID =  String.format("/{%s}",smartsKey);
	public final static String resource =  String.format("/%s",smartsKey);
	protected String dataset_id;
	
	protected String getDefaultTemplateURI(Context context, Request request,Response response) {
		return (dataset_id == null)?null:
			String.format("riap://dataset/%s/feature_definition",dataset_id);
	}
	@Override
	protected IQueryRetrieval<IStructureRecord> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		try {
			
			Form form = request.getResourceRef().getQueryAsForm();
			Object key = form.getFirstValue("search");
			if (key ==null) {
				key = request.getAttributes().get(smartsKey);
				if (key==null) throw new AmbitException("Empty smarts");
			}
			String smarts = getSMILES(form,true);
			QuerySMARTS query = new QuerySMARTS();
			query.setChemicalsOnly(true);
			query.setValue(new FunctionalGroup(smarts,smarts,smarts));
			
			
			try {
				Object cmpid = request.getAttributes().get(CompoundResource.idcompound);
				if (cmpid != null) {
					IStructureRecord record = new StructureRecord();
					record.setIdchemical(Integer.parseInt(Reference.decode(cmpid.toString())));
					QueryStructureByID scope = new QueryStructureByID();
					scope.setMaxRecords(1);
					scope.setChemicalsOnly(true);
					scope.setValue(record);
					
					QueryCombinedStructure combined = new QueryCombinedStructure();
					combined.setChemicalsOnly(true);
					combined.setCombine_as_and(true);
					combined.add(query);
					combined.setScope(scope);
					setTemplate(createTemplate(context, request, response));
					return combined;
				}
				
				Object datasetid = request.getAttributes().get(DatasetResource.datasetKey);
				if (datasetid != null) {
					QueryDatasetByID scope = new QueryDatasetByID();
					this.dataset_id=Reference.decode(datasetid.toString());
					setTemplate(createTemplate(context, request, response));
					scope.setValue(new Integer(dataset_id));
					
					QueryCombinedStructure combined = new QueryCombinedStructure() {
						@Override
						protected String getMainSQL() {
							return "select Q1.idquery,s.idchemical,-1,Q1.selected as selected,Q1.metric as metric from chemicals as s\n";
						}
						
						@Override
						protected String groupBy() {
							return " group by idchemical";
						}
						@Override
						protected String joinOn() {
							return "idchemical";
						}
					};
					combined.setCombine_as_and(true);
					combined.add(query);
					combined.setScope(scope);
					return combined;
				} 
				
			} catch (NumberFormatException x) {
				throw new ResourceException(
						Status.CLIENT_ERROR_BAD_REQUEST,String.format("Invalid resource id %s",getRequest().getOriginalRef()),x
						);				
			} catch (Exception x) {
				throw new ResourceException(
						Status.SERVER_ERROR_INTERNAL,x.getMessage(),x
						);
			}
			setTemplate(createTemplate(context, request, response));
			return query;

		} catch (Exception x) {
			throw new ResourceException(
					Status.SERVER_ERROR_INTERNAL,x.getMessage(),x
					);
		}
	}		


}

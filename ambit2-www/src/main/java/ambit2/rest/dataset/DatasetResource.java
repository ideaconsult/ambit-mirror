package ambit2.rest.dataset;

import java.util.HashMap;
import java.util.Map;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.util.Template;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.search.structure.QueryComplement;
import ambit2.db.update.structure.ChemicalByDataset;


/**
 * Dataset resource - A set of chemical compounds and assigned features
 * 
 * http://opentox.org/development/wiki/dataset
 * 
 * Supported operations:
 * <ul>
 * <li>GET /dataset  ; returns text/uri-list or text/xml or text/html
 * <li>POST /dataset ; accepts chemical/x-mdl-sdfile or multipart/form-data (SDF,mol, txt, csv, xls,all formats supported in Ambit)
 * <li>GET 	 /dataset/{id}  ; returns text/uri-list or text/xml
 * <li>PUT and DELETE not yet supported
 * </ul>
 * 
 * @author nina
 *
 */
public class DatasetResource<Q extends IQueryRetrieval<IStructureRecord>> extends DatasetStructuresResource<Q> {
	
	

	@Override
	protected String getDefaultTemplateURI(Context context, Request request,Response response) {
		Object id = request.getAttributes().get(datasetKey);
		if (id != null)
			return String.format("riap://application/dataset/%s/feature_definition",id);
		else 
			return super.getDefaultTemplateURI(context,request,response);
			
	}
	/**
	 * Finds compounds which are in the dataset {dataset_id} , but not in the datasets , 
	 * given in "complement" query 
	 * Two sets can also be "subtracted". The relative complement of A in B 
	 * (also called the set theoretic difference of B and A), denoted by \A, or (B-A)
	 * is the set of all elements which are members of B, but not members of A
	 * @param context
	 * @param request
	 * @param response
	 * @return
	 * @throws ResourceException
	 */
	protected Q createQueryComplement(Context context, Request request,
			Response response) throws ResourceException {
		
		
		Form form = request.getResourceRef().getQueryAsForm();
		String[] datasetsURI =  form.getValuesArray("complement");
		if ((datasetsURI != null) && (datasetsURI.length>0)) {
			QueryComplement qc = new QueryComplement();
			qc.setChemicalsOnly(true);
			try {
				ChemicalByDataset  cd = new ChemicalByDataset(new Integer(getRequest().getAttributes().get(datasetKey).toString()));
				qc.setScope(cd);
			} catch (Exception x) {}
			Template t = new Template(String.format("%s%s/{%s}",getRequest().getRootRef(),DatasetStructuresResource.dataset,DatasetStructuresResource.datasetKey));
			for (String datasetURI: datasetsURI ) {
				Map<String, Object> vars = new HashMap<String, Object>();
				t.parse(datasetURI, vars);
				try {
					qc.add(new ChemicalByDataset(new Integer(vars.get(DatasetStructuresResource.datasetKey).toString())));
				} catch (Exception x) {
					
				}
			}
			return (Q)qc;
		} 
		return null;
		
	}	
	protected Q createQueryIntersection(Context context, Request request,
			Response response) throws ResourceException {
		
		
		Form form = request.getResourceRef().getQueryAsForm();
		String[] datasetsURI =  form.getValuesArray("intersection");
		if ((datasetsURI != null) && (datasetsURI.length>0)) {
			QueryCombinedStructure qc = new QueryCombinedStructure() {
				@Override
				protected String getMainSQL() {
					return "select idchemical from chemicals\n";
				}
				@Override
				protected String groupBy() {
					return "";
				}
			};
			qc.setChemicalsOnly(true);
			qc.setCombine_as_and(true);
			try {
			ChemicalByDataset  cd = new ChemicalByDataset(new Integer(getRequest().getAttributes().get(datasetKey).toString()));
			qc.add(cd);
			} catch (Exception x) {}
			Template t = new Template(String.format("%s%s/{%s}",getRequest().getRootRef(),DatasetStructuresResource.dataset,DatasetStructuresResource.datasetKey));
			for (String datasetURI: datasetsURI ) {
				Map<String, Object> vars = new HashMap<String, Object>();
				t.parse(datasetURI, vars);
				try {
					qc.add(new ChemicalByDataset(new Integer(vars.get(DatasetStructuresResource.datasetKey).toString())));
				} catch (Exception x) {
					
				}
			}
			return (Q)qc;
		} 
		return null;
		
	}
	@Override
	protected Q createQuery(Context context, Request request,
			Response response) throws ResourceException {
		
		try {
			setTemplate(createTemplate(context, request, response));
			Q q = createQueryIntersection(context, request, response);
			if (q != null)  
				return q;
			else {
				q = createQueryComplement(context, request, response);
				if (q != null) return q;
				else
					return  super.createQuery(context, request, response);
			}

		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}
		
	}
}

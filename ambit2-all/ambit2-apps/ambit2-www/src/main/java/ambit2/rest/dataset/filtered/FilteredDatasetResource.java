package ambit2.rest.dataset.filtered;

import net.idea.modbcum.i.IQueryRetrieval;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.SetCondition;
import ambit2.db.search.structure.QueryDatasetByID;
import ambit2.db.search.structure.QueryStoredResults;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.db.search.structure.byproperty.QueryStructureByProperty;
import ambit2.db.search.structure.byproperty.QueryStructureByPropertyInCompounds;
import ambit2.db.search.structure.byproperty.QueryStructureByPropertyInDataset;
import ambit2.db.search.structure.byproperty.QueryStructuresByPropertyInResults;
import ambit2.rest.OpenTox;
import ambit2.rest.query.StructureQueryResource;
import ambit2.rest.task.CallableQueryProcessor;

public class FilteredDatasetResource<Q extends IQueryRetrieval<IStructureRecord>> extends StructureQueryResource<Q> {
	public static final String resource = "/filter";
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
	}
	@Override
	protected Q createQuery(Context context, Request request, Response response)
			throws ResourceException {
		Template filter= null;
		Form form = getResourceRef(getRequest()).getQueryAsForm();
		try { includeMol = "true".equals(form.getFirstValue("mol")); } catch (Exception x) { includeMol=false;}
		Object dataset = form.getFirstValue(OpenTox.params.dataset_uri.toString());
		String[] filteruris =  OpenTox.params.filter.getValuesArray(form);
		Object condition = form.getFirstValue(OpenTox.params.condition.toString());
		SetCondition.conditions sc = condition==null?SetCondition.conditions.in:
				"no".equals(condition)?SetCondition.conditions.not_in:SetCondition.conditions.in;
		//if (dataset==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Empty dataset");
		if (filteruris==null) 
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"No features to filter");
		setTemplate(createTemplate(context, request, response));
		try {
			filter = createTemplate(context, request, response,filteruris);
			if ((filter==null) || (filter.size()==0)) 
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"No features to filter");
			if (dataset==null) {
				QueryStructureByProperty q = new QueryStructureByProperty();
				q.setFieldname(filter);
				q.setCondition(new SetCondition(sc));
				setPaging(form, q);
				return (Q)q;
			} 
			Object q = CallableQueryProcessor.getQueryObject(new Reference(dataset.toString()), getRequest().getRootRef(),getApplication().getContext());
			if (q==null) {
				throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED,"Processing foreign datasets not implemented!");
			} else if (q instanceof QueryDatasetByID) {
				QueryStructureByPropertyInDataset qz = new QueryStructureByPropertyInDataset();
				qz.setValue((QueryDatasetByID)q);
				qz.setFieldname(filter);
				qz.setCondition(new SetCondition(sc));
				setPaging(form, qz);
				return (Q)qz;
			} else if (q instanceof QueryStoredResults) {
				QueryStructuresByPropertyInResults qz = new QueryStructuresByPropertyInResults();
				qz.setValue((QueryStoredResults)q);
				qz.setFieldname(filter);
				qz.setCondition(new SetCondition(sc));
				setPaging(form, qz);
				return (Q)qz;
			} else if (q instanceof QueryStructureByID) {
				QueryStructureByPropertyInCompounds qz = new QueryStructureByPropertyInCompounds();
				qz.setValue((QueryStructureByID)q);
				qz.setFieldname(filter);
				qz.setCondition(new SetCondition(sc));
				setPaging(form, qz);
				return (Q)qz;		
			} else 
				throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED,dataset.toString() + " " + q.getClass().getName() + " not implemented!");
			
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		}
		
	}

}

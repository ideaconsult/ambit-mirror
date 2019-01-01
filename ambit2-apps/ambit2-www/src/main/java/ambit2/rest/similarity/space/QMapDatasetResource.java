package ambit2.rest.similarity.space;

import net.idea.modbcum.i.IQueryRetrieval;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.reporters.CSVReporter;
import ambit2.db.simiparity.space.QueryQMapStructures;
import ambit2.rest.OpenTox;
import ambit2.rest.error.InvalidResourceIDException;
import ambit2.rest.query.StructureQueryResource;

public class QMapDatasetResource<Q extends IQueryRetrieval<IStructureRecord>> extends StructureQueryResource<IQueryRetrieval<IStructureRecord>> {


	
	@Override
	protected Q createQuery(Context context, Request request,
			Response response) throws ResourceException {
		try {
			setGroupProperties(context, request, response);
			setTemplate(createTemplate(context, request, response));
			
			Form form = getResourceRef(getRequest()).getQueryAsForm();
			try { includeMol = "true".equals(form.getFirstValue("mol")); } catch (Exception x) { includeMol=false;}

			
			Object id = request.getAttributes().get(QMapResource.qmapKey);
			if (id != null)  try {
				
				id = Reference.decode(id.toString());
				return getQueryById(new Integer(id.toString()));

			} catch (NumberFormatException x) {
			//	return getQueryById(id.toString());
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
			} catch (ResourceException x) {
				throw x;
			} catch (Exception x) {
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
			}  else throw new InvalidResourceIDException("");
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(
					Status.SERVER_ERROR_INTERNAL,x.getMessage(),x
					);
		}		
	}
	@Override
	protected Template createTemplate(Form form) throws ResourceException {
		String[] featuresURI =  OpenTox.params.feature_uris.getValuesArray(form);
		return createTemplate(getContext(),getRequest(),getResponse(), featuresURI);
	}
	
	@Override
	protected CSVReporter createCSVReporter() {
		CSVReporter csvReporter = super.createCSVReporter();
		csvReporter.setSimilarityColumn(Property.getInstance("metric",queryObject==null?"":queryObject.toString(),"http://ambit.sourceforge.net"));
		return csvReporter;
	}
	
	protected Q getQueryById(Integer key) throws ResourceException {
		Q query = null;
		QueryQMapStructures q = new QueryQMapStructures();
		((QueryQMapStructures)q).setValue(key);
		Form form = getResourceRef(getRequest()).getQueryAsForm();
		setPaging(form, q);
		return (Q)q;
	}
	

	
}

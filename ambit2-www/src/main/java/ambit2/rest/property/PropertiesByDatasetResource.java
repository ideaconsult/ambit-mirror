package ambit2.rest.property;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;

import ambit2.base.data.Property;
import ambit2.db.SourceDataset;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.StringCondition;
import ambit2.db.search.property.PropertiesByDataset;
import ambit2.db.search.property.PropertiesByDataset.QField;
import ambit2.rest.StatusException;
import ambit2.rest.dataset.DatasetsResource;
import ambit2.rest.error.InvalidResourceIDException;

/**
 * Retrieves feature definitions by dataset 
<pre>
	/dataset/{id}/feature_definition
</pre>
 * @author nina
 *
 */
public class PropertiesByDatasetResource extends PropertyResource {
	//public final static String DatasetFeaturedefID = String.format("%s%s/{%s}",DatasetsResource.datasetID,featuredef,idfeaturedef);
	public final static String DatasetFeaturedef = String.format("%s%s",DatasetsResource.datasetID,featuredef);

	public PropertiesByDatasetResource(Context context, Request request,
			Response response) {
		super(context, request, response);
	}
	@Override
	protected IQueryRetrieval<Property> createQuery(Context context,
			Request request, Response response) throws StatusException {
		Object id = request.getAttributes().get(DatasetsResource.datasetKey);
		collapsed = true;
		PropertiesByDataset q = new PropertiesByDataset();
		if (id != null) try {
			SourceDataset dataset = new SourceDataset();
			dataset.setId(new Integer(Reference.decode(id.toString())));
			q.setFieldname(QField.id.toString());
			q.setValue(dataset);
			collapsed = false;
		} catch (NumberFormatException x) {
			error = new InvalidResourceIDException(id);
			q=null;
		} catch (Exception x) {
			q.setFieldname(null);
		}
		else {
			Form form = request.getResourceRef().getQueryAsForm();
			Object key = form.getFirstValue("search");
			if (key != null) {
				q.setValue(new SourceDataset(Reference.decode(key.toString())));
				q.setFieldname(QField.name.toString());
				q.setCondition(StringCondition.getInstance(StringCondition.C_REGEXP));
				return q;
			} 
		}
		return q;
	}
}

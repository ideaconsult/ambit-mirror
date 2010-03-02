package ambit2.rest.structure.quality;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.QLabel;
import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.qlabel.QueryQLabel;
import ambit2.rest.OpenTox;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.query.QueryResource;

public class QualityLabelResource extends QueryResource<QueryQLabel, QLabel> {
	public final static String resource = "/comparison";
	@Override
	public IProcessor<QueryQLabel, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		return new OutputWriterConvertor(new QLabelReporter(),MediaType.TEXT_PLAIN);
	}

	@Override
	protected QueryQLabel createQuery(Context context, Request request,
			Response response) throws ResourceException {
		int idcompound = -1;
		int idstructure = -1;
		try {
			idcompound = OpenTox.URI.compound.getIntValue(getRequest());
			try {
				idstructure = OpenTox.URI.conformer.getIntValue(getRequest());
			} catch (Exception x ) {idstructure = -1;}
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,OpenTox.URI.compound.getKey());
		}
		IStructureRecord record = new StructureRecord(idcompound,idstructure,null,null);
		
		QueryQLabel q = new QueryQLabel();
		q.setValue(record);
		return q;
	}
}

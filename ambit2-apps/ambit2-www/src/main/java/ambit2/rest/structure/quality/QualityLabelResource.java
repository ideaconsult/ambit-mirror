package ambit2.rest.structure.quality;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.AmbitUser;
import ambit2.base.data.QLabel;
import ambit2.base.data.QLabel.QUALITY;
import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.qlabel.QueryQLabel;
import ambit2.db.update.qlabel.CreateStructureQLabel;
import ambit2.rest.OpenTox;
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
		
		IStructureRecord record = getRecord();
		
		QueryQLabel q = new QueryQLabel();
		q.setValue(record);
		return q;
	}
	
	protected IStructureRecord getRecord() {
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
		return new StructureRecord(idcompound,idstructure,null,null);
	}
	@Override
	protected AbstractUpdate createUpdateObject(QLabel entry)
			throws ResourceException {
		return new CreateStructureQLabel(getRecord(),entry);
	}
	@Override
	protected QLabel createObjectFromWWWForm(Representation entity)
			throws ResourceException {
		Form form = new Form(entity);
		QLabel label = new QLabel();
		try {
			label.setLabel(QUALITY.valueOf(form.getFirstValue("label")));
			label.setText(form.getFirstValue("text"));
			//to do
			label.setUser(new AmbitUser("guest"));
			return label;
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x);
		}
	}
	@Override
	protected Representation post(Representation entity)
			throws ResourceException {
		createNewObject(entity);
		return new StringRepresentation("");
	}
	@Override
	protected QueryURIReporter<QLabel, QueryQLabel> getURUReporter(
			Request baseReference) throws ResourceException {

		return null;
	}
}

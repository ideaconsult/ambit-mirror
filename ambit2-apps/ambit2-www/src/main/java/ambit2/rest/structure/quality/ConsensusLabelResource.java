package ambit2.rest.structure.quality;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.db.convertors.OutputWriterConvertor;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.StructureRecord;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.qlabel.QueryConsensus;
import ambit2.rest.OpenTox;
import ambit2.rest.query.QueryResource;

public class ConsensusLabelResource extends QueryResource<QueryConsensus,String> {
	public final static String resource = "/consensus";
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_PLAIN,
				/*
				MediaType.TEXT_HTML,
				MediaType.TEXT_URI_LIST,
				MediaType.TEXT_PLAIN,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE,
				MediaType.TEXT_RDF_N3,
				MediaType.TEXT_RDF_NTRIPLES,
				*/
				MediaType.APPLICATION_JAVA_OBJECT
				
		});		
	}
	
	@Override
	public IProcessor<QueryConsensus, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {

		return new OutputWriterConvertor(new ConsensusLabelReporter(),MediaType.TEXT_PLAIN);
	}

	@Override
	protected QueryConsensus createQuery(Context context, Request request, Response response)
			throws ResourceException {
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
		
		QueryConsensus q = new QueryConsensus();
		q.setFieldname(record);
		return q;
	}


}

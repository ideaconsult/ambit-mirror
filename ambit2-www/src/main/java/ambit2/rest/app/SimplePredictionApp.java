package ambit2.rest.app;

import java.io.Writer;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.structure.QueryStructureByID;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.structure.CompoundHTMLReporter;
import ambit2.rest.structure.CompoundResource;

public class SimplePredictionApp extends CompoundResource {

	public SimplePredictionApp(Context context, Request request,
			Response response) {
		super(context, request, response);
	}
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		return new OutputStreamConvertor<IStructureRecord, QueryStructureByID>(
				new CompoundHTMLReporter(
						(getRequest()==null)?null:getRequest().getRootRef(),
						collapsed,
						getURIReporter()) {
					@Override
					public void header(Writer output,
							IQueryRetrieval query) {
						super.header(output, query);
						/*
						try {
							output.write("</div><div id=\"div-1\"><table><tr><th><form><input name=\"Daily Intake,mg/kg\"><input type=\"submit\"/></form></th></tr></table>");
						} catch (Exception x) {
							
						}
						*/
					}
				},
				MediaType.TEXT_HTML);
	}
}

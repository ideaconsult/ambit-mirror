package ambit2.rest.substance.templates;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rest.OutputStreamConvertor;
import ambit2.user.rest.resource.AmbitDBQueryResource;
import net.enanomapper.maker.TR;
import net.enanomapper.maker.TemplateMaker;
import net.enanomapper.maker.TemplateMakerSettings;
import net.enanomapper.maker.TemplateMakerSettings._TEMPLATES_CMD;
import net.enanomapper.maker.TemplateMakerSettings._TEMPLATES_TYPE;
import net.idea.ambit.templates.db.ReadExperimentTemplate;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.c.StringConvertor;

public class AssayTemplateResource<Q extends IQueryRetrieval<TR>> extends AmbitDBQueryResource<Q, TR> {

	public AssayTemplateResource() {
		super();
		setHtmlbyTemplate(true);
	}

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_MSOFFICE_XLSX });
	}

	@Override
	public String getTemplateName() {
		return "jsonplaceholder.ftl";
	}

	@Override
	protected Q createQuery(Context context, Request request, Response response) throws ResourceException {
		Object idtemplate = request.getAttributes().get(AssayTemplatesFacetResource.idassaytemplate);
		if (idtemplate == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		TemplateMakerSettings settings = new TemplateMakerSettings();
		settings.setQueryTemplateid(idtemplate.toString());

		ReadExperimentTemplate q = new ReadExperimentTemplate();
		q.setFieldname(settings);
		/*
		 * String templatesdbname = getContext().getParameters().getFirstValue(
		 * AMBITConfig.templates_dbname.name());
		 * q.setDatabaseName(templatesdbname);
		 */
		return (Q) q;
	}

	@Override
	public IProcessor<Q, Representation> createConvertor(Variant variant) throws AmbitException, ResourceException {

		/* workaround for clients not being able to set accept headers */
		Form acceptform = getResourceRef(getRequest()).getQueryAsForm();
		String media = acceptform.getFirstValue("accept-header");
		if (media != null)
			variant.setMediaType(new MediaType(media));

		if (variant.getMediaType().equals(MediaType.APPLICATION_MSOFFICE_XLSX)) {
			AssayTemplateEntrySpreadsheetReporter reporter = new AssayTemplateEntrySpreadsheetReporter();
			
			return new OutputStreamConvertor(reporter, MediaType.APPLICATION_MSOFFICE_XLSX, "datatemplate");

		} else { // json by default
			return new StringConvertor(new AssayTemplateEntryJSONReporter(getRequest()), MediaType.APPLICATION_JSON,
					null);
		}

	}

	@Override
	public String getConfigFile() {
		return "ambit2/rest/config/ambit2.assay.properties";
	}
}



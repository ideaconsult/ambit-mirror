package ambit2.rest.substance.templates;

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
import net.enanomapper.maker.TemplateMakerSettings;
import net.idea.ambit.templates.db.ExperimentTemplateQuery._QUERY_TYPE_TEMPLATE;
import net.idea.ambit.templates.db.ReadExperimentTemplate;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.c.StringConvertor;

public class AssayTemplateResource<Q extends IQueryRetrieval<TR>> extends AmbitDBQueryResource<Q, TR> {
	protected TemplateMakerSettings settings = new TemplateMakerSettings();

	public AssayTemplateResource() {
		super();
		setHtmlbyTemplate(true);
	}

	enum _template_filter {
		id, endpoint
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
		_template_filter tf = _template_filter.id;
		try {
			tf = _template_filter
					.valueOf(request.getAttributes().get(AssayTemplatesFacetResource.templatefilter).toString());
		} catch (Exception x) {
			tf = _template_filter.id;
		}
		settings.getQuery().clear();
		switch (tf) {
		case id: {
			Object idtemplate = request.getAttributes().get(AssayTemplatesFacetResource.idassaytemplate);
			if (idtemplate != null) {
				settings.setQueryTemplateid(idtemplate.toString());
				ReadExperimentTemplate q = new ReadExperimentTemplate();
				q.setFieldname(settings);
				return (Q) q;
			} else {
				settings.setQueryTemplateid(null);
				ReadExperimentTemplate q = new ReadExperimentTemplate();
				q.setFieldname(settings);
				return (Q) q;
			}
		}
		case endpoint: {
			Object endpoint = request.getAttributes().get(AssayTemplatesFacetResource.idassaytemplate);
			if (endpoint != null) {
				settings.setQueryEndpoint(endpoint.toString());
				ReadExperimentTemplate q = new ReadExperimentTemplate();
				q.setFieldname(settings);
				return (Q) q;
			}	

		}

		default:
			
		}
		
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

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

			return new OutputStreamConvertor(reporter, MediaType.APPLICATION_MSOFFICE_XLSX,
					settings.getOutputFileName()) {
				protected void setDisposition(Representation rep) {
					super.setDisposition(rep);
					rep.setDownloadName(settings.getOutputFileName());
				};
			};

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
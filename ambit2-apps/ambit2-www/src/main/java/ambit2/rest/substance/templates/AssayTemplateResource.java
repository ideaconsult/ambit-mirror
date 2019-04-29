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

import ambit2.base.config.AMBITConfig;
import ambit2.user.rest.resource.AmbitDBQueryResource;
import net.enanomapper.maker.TR;
import net.enanomapper.maker.TemplateMakerSettings;
import net.idea.ambit.templates.db.ReadExperimentTemplate;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.c.StringConvertor;

public class AssayTemplateResource<Q extends IQueryRetrieval<TR>> extends AmbitDBQueryResource<Q, TR> {

	public AssayTemplateResource() {
		super();
		setHtmlbyTemplate(true);
	}
	
	@Override
	public String getTemplateName() {
		return "jsonplaceholder.ftl";
	}
	@Override
	public IProcessor<Q, Representation> createConvertor(Variant variant) throws AmbitException,
			ResourceException {
			return new StringConvertor(new AssayTemplateEntryJSONReporter(getRequest()),
					MediaType.APPLICATION_JSON, null);
	}	

	@Override
	protected Q createQuery(Context context, Request request, Response response) throws ResourceException {
		Object idtemplate = request.getAttributes().get(AssayTemplatesFacetResource.idassaytemplate);
		if (idtemplate==null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		TemplateMakerSettings settings = new TemplateMakerSettings();
		settings.setQueryTemplateid(idtemplate.toString());
		
		String templatesdbname = getContext().getParameters().getFirstValue(
				AMBITConfig.templates_dbname.name());
		ReadExperimentTemplate q = new ReadExperimentTemplate();
		q.setFieldname(settings);
		q.setDatabaseName(templatesdbname);
		return (Q) q;
	}

	@Override
	public String getConfigFile() {
		return "ambit2/rest/config/ambit2.assay.properties";
	}	
}

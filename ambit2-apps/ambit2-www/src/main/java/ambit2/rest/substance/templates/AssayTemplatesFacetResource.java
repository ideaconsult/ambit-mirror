package ambit2.rest.substance.templates;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.ResourceException;

import ambit2.base.config.AMBITConfigProperties;
import ambit2.rest.facet.AmbitFacetResource;
import net.idea.ambit.templates.db.AssayTemplateFacet;
import net.idea.ambit.templates.db.AssayTemplateFacetQuery;
import net.idea.modbcum.i.IQueryRetrieval;

public class AssayTemplatesFacetResource<Q extends IQueryRetrieval<AssayTemplateFacet>>
		extends AmbitFacetResource<AssayTemplateFacet, Q> {
	public static final String idassaytemplate = "idassaytemplate";
	public static final String templatefilter = "templatefilter";
	public static final String assaytemplate_facet = "/datatemplate";
	public static final String assaytemplate_filter = String.format("%s/{%s}", assaytemplate_facet,templatefilter);
	public static final String assaytemplate_key = String.format("%s/{%s}/{%s}", assaytemplate_facet,templatefilter, idassaytemplate);

	public AssayTemplatesFacetResource() {
		super();
		setHtmlbyTemplate(true);
	}
	@Override
	public String getTemplateName() {
		return "redirect.ftl";
	}
	

	@Override
	protected Q createQuery(Context context, Request request, Response response) throws ResourceException {
		AssayTemplateFacetQuery q = new AssayTemplateFacetQuery(
				String.format("%s", AssayTemplatesFacetResource.assaytemplate_facet));

		return (Q) q;
	}

	@Override
	public String getConfigFile() {
		return AMBITConfigProperties.templateProperties;
	}	
}

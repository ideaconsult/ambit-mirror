package ambit2.rest.substance.composition;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.relation.STRUCTURE_RELATION;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.OpenTox;
import ambit2.rest.query.QueryResource;

public class SubstanceCompositionResource<Q extends IQueryRetrieval<CompositionRelation>> extends QueryResource<Q,CompositionRelation> { 
	protected STRUCTURE_RELATION relation = STRUCTURE_RELATION.HAS_CONSTITUENT;
	public final static String composition = OpenTox.URI.composition.getURI();
	public final static String idcomposition = OpenTox.URI.composition.getKey();
	public final static String compositionID = OpenTox.URI.composition.getResourceID();
	
	public SubstanceCompositionResource() {
		super();
		setHtmlbyTemplate(true);
	}

	@Override
	public String getTemplateName() {
		return "composition.ftl";
	}
	@Override
	public IProcessor<Q, Representation> createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Q createQuery(Context context, Request request, Response response)
			throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}
	
}

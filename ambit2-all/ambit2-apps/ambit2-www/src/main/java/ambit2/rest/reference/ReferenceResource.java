package ambit2.rest.reference;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.c.RepresentationConvertor;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.db.update.reference.CreateReference;
import ambit2.db.update.reference.ReadReference;
import ambit2.db.update.reference.UpdateReference;
import ambit2.rest.DisplayMode;
import ambit2.rest.OpenTox;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.propertyvalue.PropertyValueReporter;
import ambit2.rest.query.QueryResource;
import ambit2.rest.rdf.RDFObjectIterator;
import ambit2.rest.rdf.RDFReferenceIterator;

/**
 * Reference resource, inOpenTox API coressponds to feature.hasSource
 * <br>
 * Supported operations:
 * <ul>
 * <li>GET 	 /reference/{id} 	 returns returns text/uri-list or text/xml or text/html
 * <li>POST 	 /reference?source_uri=URI-to-denote-the-reference 
 * <li>PUT not yet supported
 * </ul>
 * @author nina
 *
 * @param <Q>
 */
public class ReferenceResource	extends QueryResource<ReadReference,ILiteratureEntry> {

	public final static String reference = OpenTox.URI.reference.getURI();
	public final static String idreference = OpenTox.URI.reference.getKey();
	

	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		if (variant.getMediaType().equals(MediaType.TEXT_PLAIN)) {
			
			return new StringConvertor(new PropertyValueReporter(),MediaType.TEXT_PLAIN);
			} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
				return new StringConvertor(	new ReferenceURIReporter<IQueryRetrieval<ILiteratureEntry>>(getRequest()),MediaType.TEXT_URI_LIST);
			} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
					variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
					variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
					variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES) ||
					variant.getMediaType().equals(MediaType.APPLICATION_JSON)
					) {
				return new RDFJenaConvertor<ILiteratureEntry, IQueryRetrieval<ILiteratureEntry>>(
						new ReferenceRDFReporter<IQueryRetrieval<ILiteratureEntry>>(
								getRequest(),variant.getMediaType(),getDocumentation())
						,variant.getMediaType());					
			} else 
				return new OutputWriterConvertor(
						new ReferenceHTMLReporter(getRequest(),queryObject.getValue()==null?DisplayMode.table:DisplayMode.singleitem),
						MediaType.TEXT_HTML);
	}

	@Override
	protected ReadReference createQuery(Context context, Request request, Response response)
			throws ResourceException {
		Object idref = request.getAttributes().get(idreference);
		try {
			if (idref==null) {
				/*
				Form form = request.getResourceRef().getQueryAsForm();
				Object key = form.getFirstValue(QueryResource.search_param);
				if (key != null) {
					RetrieveFieldNamesByAlias q = new RetrieveFieldNamesByAlias(Reference.decode(key.toString()));
					q.setCondition(StringCondition.getInstance(StringCondition.C_SOUNDSLIKE));
					return q;
				} else 
				*/
					return new ReadReference();
			}			
			else return new ReadReference(new Integer(Reference.decode(idref.toString())));
		} catch (Exception x) {
			throw new ResourceException(
					Status.CLIENT_ERROR_BAD_REQUEST,
					String.format("Invalid resource id %d",idref),
					x
					);
		}
	} 
	@Override
	protected Representation post(Representation entity)
			throws ResourceException {
		if (getRequest().getAttributes().get(idreference)==null)
			createNewObject(entity);
		else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		return getResponse().getEntity();
	}
	@Override
	protected QueryURIReporter<ILiteratureEntry, ReadReference> getURUReporter(
			Request baseReference) throws ResourceException {
		return new ReferenceURIReporter<ReadReference>(baseReference);
	}
	@Override
	protected RDFObjectIterator<ILiteratureEntry> createObjectIterator(
			Reference reference, MediaType mediaType) throws ResourceException {
		try {
		return new RDFReferenceIterator(reference,mediaType);
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(x);
		}		
	}
	@Override
	protected RDFObjectIterator<ILiteratureEntry> createObjectIterator(
			Representation entity) throws ResourceException {
		return new RDFReferenceIterator(entity,entity.getMediaType());
	}
	@Override
	protected LiteratureEntry onError(String sourceURI) {
		return LiteratureEntry.getInstance(sourceURI,sourceURI);	
	}

	@Override
	protected AbstractUpdate createUpdateObject(
			ILiteratureEntry entry) throws ResourceException {
		if(entry.getId()>0) return new UpdateReference(entry);
		else return new CreateReference(entry);
	}
}

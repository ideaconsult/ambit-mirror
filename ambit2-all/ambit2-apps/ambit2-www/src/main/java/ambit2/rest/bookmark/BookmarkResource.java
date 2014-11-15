package ambit2.rest.bookmark;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.c.RepresentationConvertor;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Bookmark;
import ambit2.db.update.bookmark.CreateBookmark;
import ambit2.db.update.bookmark.DeleteBookmark;
import ambit2.db.update.bookmark.ReadBookmark;
import ambit2.db.update.bookmark.UpdateBookmark;
import ambit2.rest.DisplayMode;
import ambit2.rest.OpenTox;
import ambit2.rest.RDFJenaConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.QueryResource;
import ambit2.rest.rdf.Annotea;
import ambit2.rest.rdf.RDFBookmarkIterator;
import ambit2.rest.rdf.RDFObjectIterator;

import com.hp.hpl.jena.vocabulary.DC;

public class BookmarkResource extends QueryResource<ReadBookmark,Bookmark> {

	public final static String resource = OpenTox.URI.bookmark.getURI();
	public final static String idbookmark = OpenTox.URI.bookmark.getKey();
	public final static String creator = "creator";
	

	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
		String filenamePrefix = getRequest().getResourceRef().getPath();
		if (variant.getMediaType().equals(MediaType.TEXT_CSV)) {
			return new StringConvertor(new BookmarkCSVReporter<IQueryRetrieval<Bookmark>>(getRequest(),getDocumentation())
					,MediaType.TEXT_CSV,filenamePrefix);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
				return new StringConvertor(	new BookmarkURIReporter<IQueryRetrieval<Bookmark>>(getRequest()),MediaType.TEXT_URI_LIST,filenamePrefix);
				
			} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
					variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
					variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
					variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES) ||
					variant.getMediaType().equals(MediaType.APPLICATION_JSON)
					) {
		
				return new RDFJenaConvertor<Bookmark, IQueryRetrieval<Bookmark>>(
						new BookmarkRDFReporter<IQueryRetrieval<Bookmark>>(getRequest(),getDocumentation(),variant.getMediaType())
						,variant.getMediaType(),filenamePrefix);					
								
			} else 
				return new OutputWriterConvertor(
						new BookmarkHTMLReporter(getRequest(),
									queryObject.getValue()==null?DisplayMode.table:DisplayMode.singleitem,
									getDocumentation()),
						MediaType.TEXT_HTML);
	}

	@Override
	protected ReadBookmark createQuery(Context context, Request request, Response response)
			throws ResourceException {
		Object idref = request.getAttributes().get(idbookmark);
		Object user = request.getAttributes().get(creator);
		Bookmark bookmark = user==null?null:new Bookmark(user.toString());
		
		Form form = getResourceRef(request).getQueryAsForm();
		Object key = form.getFirstValue(QueryResource.search_param);
		if (key != null) {
			if (bookmark==null) bookmark = new Bookmark();
			bookmark.setTitle(Reference.decode(key.toString()));
		} 
		key = form.getFirstValue(Annotea.BookmarkProperty.hasTopic.toString());
		if (key != null) {
			if (bookmark==null) bookmark = new Bookmark();
			bookmark.setHasTopic(Reference.decode(key.toString()));
		} 		
			
		key = form.getFirstValue(Annotea.BookmarkProperty.recalls.toString());
		if (key != null) {
			if (bookmark==null) bookmark = new Bookmark();
			bookmark.setRecalls(Reference.decode(key.toString()));
		} 
		
		try {
			if (idref==null) {
				return new ReadBookmark(null,bookmark); //all
			}			
			else return new ReadBookmark(
					new Integer(Reference.decode(idref.toString())),
					bookmark);
		} catch (Exception x) {
			throw new ResourceException(
					Status.CLIENT_ERROR_BAD_REQUEST,
					String.format("Invalid resource id"),
					x
					);
		}
	} 
	@Override
	protected Representation post(Representation entity)
			throws ResourceException {
		if (getRequest().getAttributes().get(idbookmark)==null)
			createNewObject(entity);
		else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		return getResponse().getEntity();
	}
	@Override
	protected Bookmark createObjectFromWWWForm(Representation entity)
			throws ResourceException {
		
		Form queryForm = new Form(entity);
		String uri = queryForm.getFirstValue(OpenTox.params.source_uri.toString());
		if (uri!= null) {
			Bookmark bookmark = null;
			try {
				RDFBookmarkIterator it = new RDFBookmarkIterator(new Reference(uri));
				while (it.hasNext()) {
					bookmark = it.next();
					break;
				}
			} catch (ResourceException x) {
				throw x;
			} catch (Exception x) {
				throw new ResourceException(x);
			}
			if (bookmark == null) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			return bookmark;
		} else {
			Bookmark bookmark = new Bookmark();
			bookmark.setHasTopic(queryForm.getFirstValue(Annotea.BookmarkProperty.hasTopic.toString()));
			bookmark.setRecalls(queryForm.getFirstValue(Annotea.BookmarkProperty.recalls.toString()));
			
			if ((getRequest().getClientInfo()==null) || getRequest().getClientInfo().getUser()==null)
				bookmark.setCreator(queryForm.getFirstValue(DC.creator.toString()));
			else try {
				bookmark.setCreator(getRequest().getClientInfo().getUser().getIdentifier());
			} catch (Exception x) {}
			bookmark.setTitle(queryForm.getFirstValue(DC.title.toString()));
			bookmark.setDescription(queryForm.getFirstValue(DC.description.toString()));
			return bookmark;
		}
	}
	@Override
	protected QueryURIReporter<Bookmark, ReadBookmark> getURUReporter(
			Request baseReference) throws ResourceException {
		return new BookmarkURIReporter<ReadBookmark>(baseReference);
	}
	@Override
	protected RDFObjectIterator<Bookmark> createObjectIterator(
			Reference reference, MediaType mediaType) throws ResourceException {
		try {
			return new RDFBookmarkIterator(reference,mediaType);
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(x);
		}
	}
	@Override
	protected RDFObjectIterator<Bookmark> createObjectIterator(
			Representation entity) throws ResourceException {
		return new RDFBookmarkIterator(entity,entity.getMediaType());
	}
	@Override
	protected Bookmark onError(String sourceURI) {
		return null;
	}

	@Override
	protected AbstractUpdate createUpdateObject(
			Bookmark entry) throws ResourceException {
		if(entry.getId()>0) return new UpdateBookmark(entry);
		else return new CreateBookmark(entry);
	}
	

	
	protected Bookmark createObjectFromURI(Form queryForm,
			Representation entity) throws ResourceException {
		try {
			Object idref = getRequest().getAttributes().get(idbookmark);
			Bookmark bkm = new Bookmark();
			bkm.setId(Integer.parseInt(idref.toString()));
			return bkm;
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage());
		}
	}
	
	protected Representation delete(Variant variant) throws ResourceException {
		Representation entity = getRequestEntity();
		Form queryForm = null;
		if (MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType()))
			queryForm = new Form(entity);
		Bookmark entry = createObjectFromURI(queryForm, entity);
		executeUpdate(entity, 
				entry,
				createDeleteObject(entry));
		getResponse().setStatus(Status.SUCCESS_OK);
		return new EmptyRepresentation();
	};
	@Override
	protected AbstractUpdate createDeleteObject(Bookmark entry)
			throws ResourceException {
		DeleteBookmark x = new DeleteBookmark();
		x.setObject(entry);
		return x;
	}
}
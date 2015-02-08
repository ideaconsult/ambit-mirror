package ambit2.rest.bookmark;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;
import org.restlet.data.MediaType;

import ambit2.base.data.Bookmark;
import ambit2.rest.QueryRDFReporter;
import ambit2.rest.rdf.Annotea;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.vocabulary.DC;

public class BookmarkRDFReporter <Q extends IQueryRetrieval<Bookmark>> extends QueryRDFReporter<Bookmark, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8857789530109166243L;

	public BookmarkRDFReporter(Request request,MediaType mediaType) {
		super(request,mediaType);
	}
	@Override
	protected QueryURIReporter<Bookmark, IQueryRetrieval<Bookmark>> createURIReporter(
			Request req, ResourceDoc doc) {
		return new BookmarkURIReporter(req);
	}
	@Override
	public Object processItem(Bookmark item) throws AmbitException {
		return addToModel(getJenaModel(), item,uriReporter);
	}
	public static Individual addToModel(OntModel jenaModel,Bookmark item, 
				QueryURIReporter<Bookmark, IQueryRetrieval<Bookmark>> uriReporter) {
		Individual entry = null;
		if ((uriReporter==null) || (uriReporter.getBaseReference()==null)) {
			entry = jenaModel.createIndividual(Annotea.Bookmark.Bookmark.getOntClass(jenaModel));
		} else {
			entry = jenaModel.createIndividual(uriReporter.getURI(item),Annotea.Bookmark.Bookmark.getOntClass(jenaModel));
	
		}
		entry.addProperty(DC.title, item.getTitle());
		entry.addProperty(Annotea.BookmarkProperty.recalls.createProperty(jenaModel),item.getRecalls());
		entry.addProperty(Annotea.BookmarkProperty.hasTopic.createProperty(jenaModel),item.getHasTopic());
		entry.addProperty(DC.creator,item.getCreator());
		entry.addProperty(DC.description,item.getDescription());
		//entry.addProperty(DC.date,item.getDate());
		
		return entry;
		
	}
	public void open() throws DbAmbitException {
		
	}

}

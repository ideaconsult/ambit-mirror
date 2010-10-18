package ambit2.rest.bookmark;

import org.restlet.Request;

import ambit2.base.data.Bookmark;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryURIReporter;

public class BookmarkURIReporter <Q extends IQueryRetrieval<Bookmark>> extends QueryURIReporter<Bookmark, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8868430033131766579L;
	public BookmarkURIReporter(Request baseRef) {
		super(baseRef);
	}
	public BookmarkURIReporter() {
		this(null);
	}	

	@Override
	public String getURI(String ref, Bookmark item) {
		return String.format("%s%s/%s/%d",ref,BookmarkResource.resource,item.getCreator(),item.getId());
	}

}
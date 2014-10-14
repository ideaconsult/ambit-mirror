package ambit2.rest.bookmark;

import org.restlet.Request;

import ambit2.base.data.Bookmark;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;

public class BookmarkURIReporter <Q extends IQueryRetrieval<Bookmark>> extends QueryURIReporter<Bookmark, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8868430033131766579L;
	public BookmarkURIReporter(Request baseRef,ResourceDoc doc) {
		super(baseRef,doc);
	}
	public BookmarkURIReporter(ResourceDoc doc) {
		this(null,doc);
	}	

	@Override
	public String getURI(String ref, Bookmark item) {
		return String.format("%s%s/%s/entries/%d",ref,BookmarkResource.resource,item.getCreator(),item.getId());
	}

}
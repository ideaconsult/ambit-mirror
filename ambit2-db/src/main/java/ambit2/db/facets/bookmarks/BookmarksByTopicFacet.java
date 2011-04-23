package ambit2.db.facets.bookmarks;

import ambit2.base.facet.AbstractFacet;

public class BookmarksByTopicFacet extends AbstractFacet<String> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5838810808473432009L;
	protected String creator;
	
	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public BookmarksByTopicFacet() {
	
	}

}

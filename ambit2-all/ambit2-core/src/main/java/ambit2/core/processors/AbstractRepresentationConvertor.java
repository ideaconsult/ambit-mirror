package ambit2.core.processors;

import ambit2.base.processors.DefaultAmbitProcessor;

public abstract class AbstractRepresentationConvertor<Item,Content,Output,R,Media,ItemReporter extends Reporter<Content,Output>> 
														extends DefaultAmbitProcessor<Content,R> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3205584753771251514L;	
	protected ItemReporter reporter;
	
	protected Media mediaType;
	
	public Media getMediaType() {
		return mediaType;
	}
	public void setMediaType(Media mediaType) {
		this.mediaType = mediaType;
	}
	

	public ItemReporter getReporter() {
		return reporter;
	}
	public void setReporter(ItemReporter reporter) {
		this.reporter = reporter;
	}
	public AbstractRepresentationConvertor(ItemReporter reporter) {
		this.reporter = reporter;
	}
	public AbstractRepresentationConvertor(ItemReporter reporter,Media media) {
		this(reporter);
		setMediaType(media);
	}

	public abstract R process(Content query) throws ambit2.base.exceptions.AmbitException;
}

package ambit2.base.processors;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.reporter.Reporter;

/**
 * An abstract {@link IProcessor} , converting between arbitrary Content and arbitrary Representation.
 * @author nina
 *
 * @param <Item>
 * @param <Content>
 * @param <Output>
 * @param <R>
 * @param <Media>
 * @param <ItemReporter>
 */
public abstract class AbstractRepresentationConvertor<Item,Content,Output,R,Media,ItemReporter extends Reporter<Content,Output>> 
														extends DefaultAmbitProcessor<Content,R> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3205584753771251514L;	
	protected ItemReporter reporter;
	protected final String fileNamePrefix;
	protected Media mediaType;
	protected long startTime;
	
	public Media getMediaType() {
		return mediaType;
	}
	public void setMediaType(Media mediaType) {
		this.mediaType = mediaType;
	}
	

	public ItemReporter getReporter() {
		return reporter;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public void setReporter(ItemReporter reporter) {
		this.reporter = reporter;
	}
	public AbstractRepresentationConvertor(ItemReporter reporter) {
		this(reporter,null,null);
	}
	public AbstractRepresentationConvertor(ItemReporter reporter,Media media) {
		this(reporter,media,null);
	}
	public AbstractRepresentationConvertor(ItemReporter reporter,Media media,String fileNamePrefix) {
		setReporter(reporter);
		setMediaType(media);
		this.fileNamePrefix = fileNamePrefix;
	}
	public abstract R process(Content query) throws AmbitException;
	
	public void close() {}
}

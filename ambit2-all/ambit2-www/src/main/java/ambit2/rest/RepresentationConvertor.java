package ambit2.rest;

import org.apache.poi.hssf.record.formula.functions.T;
import org.restlet.data.MediaType;
import org.restlet.resource.Representation;

import ambit2.base.processors.Reporter;
import ambit2.core.processors.AbstractRepresentationConvertor;

/**
 * 
 * @author nina
 *
 * @param <Item>
 * @param <ItemList>
 * @param <Output>
 */
public abstract class RepresentationConvertor<Item,Content,Output,R extends Reporter<Content,Output>> 
	extends AbstractRepresentationConvertor<T,Content,Output,Representation,MediaType,R> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 50107090954200157L;

	public RepresentationConvertor(R reporter) {
		super(reporter);
	}
	public RepresentationConvertor(R reporter, MediaType media) {
		super(reporter);
	}
	
}

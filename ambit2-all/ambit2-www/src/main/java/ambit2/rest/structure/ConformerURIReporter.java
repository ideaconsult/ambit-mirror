package ambit2.rest.structure;

import org.restlet.Request;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.ResourceDoc;

/**
 * Generates conformer URI as /compound/{id}/conformer/{id}
 * @author nina
 *
 * @param <Q>
 */
public class ConformerURIReporter<Q extends IQueryRetrieval<IStructureRecord>> extends CompoundURIReporter<Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3240656616658257198L;

	public ConformerURIReporter(ResourceDoc doc) {
		this(null,false,doc);
	}	
	public ConformerURIReporter(Request baseRef,ResourceDoc doc) {
		super(baseRef,false,doc);
	}	
	public ConformerURIReporter(Request baseRef,boolean readStructure,ResourceDoc doc) {
		super(baseRef,readStructure,doc);
	}

	public String getURI(String ref, IStructureRecord item) {
		return String.format("%s%s/%d%s/%d%s",
				ref,
				CompoundResource.compound,item.getIdchemical(),ConformerResource.conformerKey,item.getIdstructure(),getDelimiter());
	}	
}

package ambit2.rest.structure;

import org.restlet.Request;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;

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

	public ConformerURIReporter() {
		this(null,false);
	}	
	public ConformerURIReporter(Request baseRef) {
		super(baseRef,false);
	}	
	public ConformerURIReporter(Request baseRef,boolean readStructure) {
		super(baseRef,readStructure);
	}

	public String getURI(String ref, IStructureRecord item) {
		return String.format("%s%s/%d%s/%d%s",
				ref,
				CompoundResource.compound,item.getIdchemical(),ConformerResource.conformerKey,item.getIdstructure(),getDelimiter());
	}	
}

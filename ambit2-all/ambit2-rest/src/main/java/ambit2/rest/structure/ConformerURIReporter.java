package ambit2.rest.structure;

import net.idea.modbcum.i.IQueryRetrieval;
import net.sf.saxon.style.DataElement;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.rest.DataResources;

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
	public ConformerURIReporter(String prefix) {
		this(prefix,null,false);
	}	
	public ConformerURIReporter(String prefix,Request baseRef) {
		super(prefix,baseRef,false);
	}	
	public ConformerURIReporter(Request baseRef) {
		super("",baseRef,false);
	}	
	public ConformerURIReporter(String prefix,Request baseRef,boolean readStructure) {
		super(prefix,baseRef,readStructure);
	}
	public ConformerURIReporter(Request baseRef,boolean readStructure) {
		this("",baseRef,readStructure);
	}	
	public ConformerURIReporter(String prefix,Reference baseRef) {
		super(prefix,baseRef);
	}
	public ConformerURIReporter(Reference baseRef) {
		this("",baseRef);
	}	

	public String getURI(String ref, IStructureRecord item) {
		if (item.getIdstructure()>0)
		return String.format("%s%s%s/%d%s/%d",
				ref,
				prefix,
				DataResources.compound_resource,item.getIdchemical(),DataResources.conformerKey_resource,item.getIdstructure());
		else return super.getURI(ref, item);
	}	
}

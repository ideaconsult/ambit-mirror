package ambit2.rest.structure;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.c.ResourceDoc;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.interfaces.IStructureRecord;

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
		this("",null,false,doc);
	}		
	public ConformerURIReporter(String prefix,ResourceDoc doc) {
		this(prefix,null,false,doc);
	}	
	public ConformerURIReporter(String prefix,Request baseRef,ResourceDoc doc) {
		super(prefix,baseRef,false,doc);
	}	
	public ConformerURIReporter(Request baseRef,ResourceDoc doc) {
		super("",baseRef,false,doc);
	}	
	public ConformerURIReporter(String prefix,Request baseRef,boolean readStructure,ResourceDoc doc) {
		super(prefix,baseRef,readStructure,doc);
	}
	public ConformerURIReporter(Request baseRef,boolean readStructure,ResourceDoc doc) {
		this("",baseRef,readStructure,doc);
	}	
	public ConformerURIReporter(String prefix,Reference baseRef,ResourceDoc doc) {
		super(prefix,baseRef,doc);
	}
	public ConformerURIReporter(Reference baseRef,ResourceDoc doc) {
		this("",baseRef,doc);
	}	

	public String getURI(String ref, IStructureRecord item) {
		if (item.getIdstructure()>0)
		return String.format("%s%s%s/%d%s/%d",
				ref,
				prefix,
				CompoundResource.compound,item.getIdchemical(),ConformerResource.conformerKey,item.getIdstructure());
		else return super.getURI(ref, item);
	}	
}

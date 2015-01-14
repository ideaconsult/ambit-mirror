package ambit2.rest.dataEntry;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.c.ResourceDoc;

import org.restlet.Request;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.rest.structure.CompoundResource;
import ambit2.rest.structure.CompoundURIReporter;
import ambit2.rest.structure.ConformerResource;

public class DataEntryURIReporter<Q extends IQueryRetrieval<IStructureRecord>> extends CompoundURIReporter<Q> {
	private final static String resourceTag = "/dataEntry";
	/**
	 * 
	 */
	private static final long serialVersionUID = 4993828013796643444L;

	public DataEntryURIReporter(Request request,ResourceDoc doc) {
		this(request,false,doc);
	}
	public DataEntryURIReporter(Request request,boolean readStructure,ResourceDoc doc) {
		super(request,doc);
		this.readStructure = readStructure;
		
	}
	public DataEntryURIReporter(ResourceDoc doc) {
		this(null,false,doc);
	}	
	
	@Override
	public String getURI(String ref, IStructureRecord item) {
		if ((item.getIdstructure()==-1) || (item.getType().equals(STRUC_TYPE.NA)))
			return String.format("%s%s/%d%s/%s",
					ref,
					CompoundResource.compound,
					item.getIdchemical(),
					resourceTag,
					item.getDataEntryID()>0?Integer.toString(item.getDataEntryID()):""
					);
		else
			return String.format("%s%s/%d%s/%d%s/%s",
						ref,
						CompoundResource.compound,
						item.getIdchemical(),
						ConformerResource.conformerKey,
						item.getIdstructure(),
						resourceTag,
						item.getDataEntryID()>0?Integer.toString(item.getDataEntryID()):""
						);				
		
	}
}

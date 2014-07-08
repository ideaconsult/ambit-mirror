package ambit2.db.reporters;

import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.data.Profile;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;

public class StructureListReporter extends AbstractStructureRecordReporter<List<IStructureRecord>> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4591388340477136425L;
	public StructureListReporter(Profile groupedProperties,Template template) {
		this(null,groupedProperties,template);
	}
	
	public StructureListReporter(List<IStructureRecord> record,Profile groupedProperties,Template template) {
		super(record,groupedProperties,template);
	}
	@Override
	public Object processItem(IStructureRecord item) throws AmbitException {
		try {if (item.isSelected()) getOutput().add((IStructureRecord)((IStructureRecord) item).clone());}
		catch (Exception x) {}
		return item;

	}
	
}


package ambit2.namestructure;

import uk.ac.cam.ch.wwmm.opsin.NameToInchi;
import uk.ac.cam.ch.wwmm.opsin.NameToStructure;
import uk.ac.cam.ch.wwmm.opsin.OpsinResult;
import uk.ac.cam.ch.wwmm.opsin.OpsinResult.OPSIN_RESULT_STATUS;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.search.AbstractFinder;

public class Name2StructureFinder extends AbstractFinder<Name2StructureProcessor, IStructureRecord>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3159036024126147810L;
	protected IStructureRecord record = new StructureRecord();
	
	public Name2StructureFinder(Template profile,MODE mode) {
		super(profile, new Name2StructureProcessor(), mode);
	}

	@Override
	protected IStructureRecord query(String value) throws AmbitException {
		OpsinResult result = request.name2structure(value);
		if (result.getStatus().equals(OPSIN_RESULT_STATUS.SUCCESS)) {
			record.clear();
			record.setContent(NameToInchi.convertResultToInChI(result));
			record.setFormat("INC");
			return record;
		} else return null;
	}

}

package ambit2.namestructure;

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
		String smiles = request.name2smiles(value);
		record.clear();
		if (smiles==null) 
			return null;
		record.setContent(smiles);
		record.setFormat("SMILES");
		return record;
	}

}

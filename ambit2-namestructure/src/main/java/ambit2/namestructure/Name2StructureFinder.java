package ambit2.namestructure;

import net.idea.modbcum.i.exceptions.AmbitException;
import nu.xom.Element;
import uk.ac.cam.ch.wwmm.opsin.OpsinResult;
import uk.ac.cam.ch.wwmm.opsin.OpsinResult.OPSIN_RESULT_STATUS;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.Template;
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
			/*
			String str = NameToInchi.convertResultToInChI(result);
			if (str!=null) {
				record.setContent(str);
				record.setFormat(IStructureRecord.MOL_TYPE.INC.toString());
				return record;
			} 
			*/
			/*
			String str = result.getSmiles();
			if (str!=null) {
				record.setContent(null);
				record.setSmiles(str);
				return record;
			} 
			*/
			Element cml = result.getCml();
			if (cml!=null) {
				record.setContent(cml.toXML());
				record.setFormat(IStructureRecord.MOL_TYPE.CML.toString());
				return record;
			}
			return null; //success reported,but no meaningful result
		} else return null;
	}

}

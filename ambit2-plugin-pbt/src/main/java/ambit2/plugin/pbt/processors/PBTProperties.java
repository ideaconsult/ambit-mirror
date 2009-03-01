package ambit2.plugin.pbt.processors;

import java.util.TreeMap;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.core.data.IStructureRecord;
import ambit2.core.data.StructureRecord;
import ambit2.core.exceptions.AmbitException;
import ambit2.core.processors.structure.MoleculeWriter;
import ambit2.core.processors.structure.MoleculeReader.MOL_TYPE;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.plugin.pbt.PBTWorkBook;
import ambit2.plugin.pbt.PBTWorksheet;
import ambit2.plugin.pbt.PBTWorkBook.WORKSHEET_INDEX;

public class PBTProperties extends AbstractDBProcessor<PBTWorkBook, IStructureRecord>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1014889142122479236L;
	protected MoleculeWriter molwriter = new MoleculeWriter();
	public IStructureRecord process(PBTWorkBook target) throws AmbitException {
		IStructureRecord record = target.getRecord();
		if (record ==null) record = new StructureRecord();
		IAtomContainer a = target.getStructure();
		if (a == null) a = DefaultChemObjectBuilder.getInstance().newAtomContainer();
		a.setProperties(new TreeMap());
		
		a.getProperties().clear();
		for (WORKSHEET_INDEX w : WORKSHEET_INDEX.values() ) 
			if (w != WORKSHEET_INDEX.WELCOME)
				addKeys(a,w.toString(),target.getWorksheet(w));
		
		record.setContent(molwriter.process(a));
		record.setFormat(MOL_TYPE.SDF.toString());
		record.setProperties(a.getProperties());
		return record;
	}
	protected void addKeys(IAtomContainer record,String prefix,PBTWorksheet ws) {
		for (int r = 0; r < ws.getMaxRow();r++)
			for (int c = 0; c < ws.getMaxCol();c++) {
				Object value = ws.get(r,c);
				if ((value!=null) && (!value.equals(""))) 
				record.getProperties().put(prefix+"_"+ws.getCellName(r,c),value.toString().replace('\r', ' ').replace('\n',' '));
			}
		
	}
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}

}

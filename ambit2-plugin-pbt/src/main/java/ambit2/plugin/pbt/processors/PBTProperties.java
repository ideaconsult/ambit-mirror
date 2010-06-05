package ambit2.plugin.pbt.processors;

import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;

import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.core.processors.structure.MoleculeWriter;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.plugin.pbt.Cell;
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
		IAtomContainer a = getAtomContainer(target);
		
		String s = molwriter.process(a);

		record.setContent(s);
		record.setFormat(MOL_TYPE.SDF.toString());
		record.clearProperties();
		record.addProperties(a.getProperties());
		return record;
	}
	
	public static IAtomContainer getAtomContainer(PBTWorkBook target) throws AmbitException {
		IAtomContainer a = target.getStructure();
		if (a == null) a = NoNotificationChemObjectBuilder.getInstance().newAtomContainer();
		a.setProperties(new TreeMap());
		
		a.getProperties().clear();
		for (WORKSHEET_INDEX w : WORKSHEET_INDEX.values() ) 
			addKeys(a,w.toString(),target.getWorksheet(w));
		
		return a;
	}	
	protected static void addKeys(IAtomContainer record,String prefix,PBTWorksheet ws) {
		for (int r = 0; r < ws.getMaxRow();r++)
			for (int c = 0; c < ws.getMaxCol();c++) {

				Cell extCell = ws.getExtendedCell(r,c);
				if (extCell == null) continue;
				switch (extCell.getMode()) {
				case NODE_INPUT: {
					Object value = getProperty(ws,r,c);
					if (value!= null) {
						record.getProperties().put(prefix+"_"+ws.getCellName(r,c),value);
					}
					break;
				}
				case NODE_LIST: {
					Object value = getProperty(ws,r,c);
					if (value!= null) {
						record.getProperties().put(prefix+"_"+ws.getCellName(r,c),value);
					}
					break;
				}
				}
			}
	}
	protected static Object getProperty(PBTWorksheet ws,int r, int c) {
		HSSFCell cell = ws.getCell(r,c);
		if (cell == null) return null;
		switch (cell.getCellType()) {
		case HSSFCell.CELL_TYPE_FORMULA: return null;
		case HSSFCell.CELL_TYPE_NUMERIC: {
			Object value = ws.get(r,c);
			if (value!=null) 
				return value;
		} 
		default: {
			Object value = ws.get(r,c);
			if (value!=null)
				return value.toString().replace('\r', ' ').replace('\n',' ');
						
		}
		}
		return null;
	}
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}

}

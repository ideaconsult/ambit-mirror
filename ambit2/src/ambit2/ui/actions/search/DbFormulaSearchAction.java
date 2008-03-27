package ambit2.ui.actions.search;

import java.sql.Connection;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.config.AmbitCONSTANTS;
import ambit2.database.readers.DbStructureReader;
import ambit2.exceptions.AmbitException;
import ambit2.ui.UITools;

/**
 * Chemical formula search in database. See example in  {@link ambit2.ui.actions.search.DbCASSearchAction}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class DbFormulaSearchAction extends DbCASSearchAction {
	protected String sql = "select structure.idstructure,structure.idsubstance,uncompress(structure) as ustructure,casno from substance join structure using(idsubstance) "+
				"\nleft join cas using(idstructure)"+
				"\nwhere formula=";
	public DbFormulaSearchAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Molecular formula");
	}

	public DbFormulaSearchAction(Object userData, JFrame mainFrame, String arg0) {
		this(userData, mainFrame, arg0,UITools.createImageIcon("ambit2/ui/images/search.png"));
	}

	public DbFormulaSearchAction(Object userData, JFrame mainFrame,
			String arg0, Icon arg1) {
		super(userData, mainFrame, arg0, arg1);
		putValue(AbstractAction.SHORT_DESCRIPTION,"Search by formula");
	}
	public IIteratingChemObjectReader getSearchReader(Connection connection, Object query,int page, int pagesize) throws AmbitException {
		String formula = "C6H6";
		if (query instanceof Molecule ) {
			Object c = ((Molecule) query).getProperty(AmbitCONSTANTS.FORMULA);
			if (c != null)
				formula = c.toString();
		}	
		formula = getSearchCriteria("Search database by molecular formula", 
				"Enter molecular formula\n",formula);
			//If a string was returned, say so.
		if ((formula != null) && (formula.length() > 0)) {
				return new DbStructureReader(connection,sql + '"' + formula + "\"  limit " + page + "," + pagesize);

		}
		return null;
	}
	
	/*
	public ChemObjectWriter getWriter() {
		if (userData instanceof AmbitDatabaseToolsData) {
				AmbitDatabaseToolsData dbaData = ((AmbitDatabaseToolsData) userData);		
				DataContainer studyList = (DataContainer) dbaData.getMolecules();
				studyList.clear();
				return new ListOfMoleculesWriter(studyList);
		} else return null;
	}
	*/
}

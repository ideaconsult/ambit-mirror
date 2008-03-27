package ambit2.ui.actions.search;

import java.sql.Connection;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.smiles.SmilesParser;

import ambit2.config.AmbitCONSTANTS;
import ambit2.smiles.SmilesParserWrapper;
import ambit2.database.data.AmbitDatabaseToolsData;
import ambit2.database.search.DbExactSearchReader;
import ambit2.exceptions.AmbitException;
import ambit2.ui.UITools;

/**
 * SMILES search in database. See example in  {@link ambit2.ui.actions.search.DbCASSearchAction}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class DbSMILESSearchAction extends DbExactSearchAction {
    
	public DbSMILESSearchAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"SMILES search",null);
	}

	public DbSMILESSearchAction(Object userData, JFrame mainFrame, String arg0) {
		this(userData, mainFrame, arg0,UITools.createImageIcon("ambit2/ui/images/search.png"));
	}

	public DbSMILESSearchAction(Object userData, JFrame mainFrame, String arg0,
			Icon arg1) {
		super(userData, mainFrame, arg0, arg1);
		putValue(AbstractAction.SHORT_DESCRIPTION,"Exact search by SMILES");
	}
	public IIteratingChemObjectReader getSearchReader(Connection connection, Object query, int page, int pagesize) throws AmbitException {
		Object c = null;	
		String smiles = "c1ccccc1";
		if (query instanceof Molecule ) {
			c = ((Molecule) query).getProperty(AmbitCONSTANTS.SMILES);
			if (c != null)
				smiles = c.toString();
		}	
		if (c == null) {
			IMolecule m = ((AmbitDatabaseToolsData) userData).getMolecule();
			if (m != null) {
				c = m.getProperty(AmbitCONSTANTS.SMILES);
				if (c != null) smiles = c.toString();
			}
			
		} 			
		smiles = (String)JOptionPane.showInputDialog(
                mainFrame,
                "Enter SMILES\n",
                "Search database by SMILES",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                smiles);

			//If a string was returned, say so.
		if ((smiles != null) && (smiles.length() > 0)) {
			SmilesParserWrapper sp =  SmilesParserWrapper.getInstance();
		    try {
			    IMolecule mol = sp.parseSmiles(smiles);
				mol.setProperty(AmbitCONSTANTS.SMILES, smiles);
				return new DbExactSearchReader(connection,mol,null, page,pagesize);
		    } catch (InvalidSmilesException x) {
		        throw new AmbitException(x);
		    }

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

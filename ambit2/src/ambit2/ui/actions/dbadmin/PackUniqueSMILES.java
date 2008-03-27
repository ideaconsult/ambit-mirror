package ambit2.ui.actions.dbadmin;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.database.core.DbSQL;
import ambit2.database.data.ISharedDbData;
import ambit2.database.processors.ReadSMILESProcessor;
import ambit2.database.readers.DbStructureReader;
import ambit2.database.writers.PackSubstancesWriter;
import ambit2.exceptions.AmbitException;
import ambit2.processors.IAmbitProcessor;
import ambit2.ui.UITools;
import ambit2.ui.actions.BatchAction;

/**
 * Runs {@link ambit2.database.writers.PackSubstancesWriter}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class PackUniqueSMILES extends BatchAction {
    protected static String caption = "Cleans up redundant information from database.";
	public PackUniqueSMILES(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Pack unique SMILES");
	}

	public PackUniqueSMILES(Object userData, JFrame mainFrame, String name) {
		this(userData, mainFrame, name,UITools.createImageIcon("ambit2/ui/images/database.png"));

	}

	public PackUniqueSMILES(Object userData, JFrame mainFrame, String name,
			Icon icon) {
		super(userData, mainFrame, name, icon);
		putValue(AbstractAction.SHORT_DESCRIPTION,caption);
	}

	public IIteratingChemObjectReader getReader() {
		if (userData instanceof ISharedDbData)
			try {
			return new DbStructureReader(((ISharedDbData) userData).getDbConnection().getConn(),
					DbSQL.AMBIT_getDuplicateSmiles);
			} catch (AmbitException x) {
				logger.error(x);
				return null;
			}
			else return null;	
	}

	public IChemObjectWriter getWriter() {
		if (userData instanceof ISharedDbData)
			return new PackSubstancesWriter(((ISharedDbData) userData).getDbConnection().getConn(),
					((ISharedDbData) userData).getDbConnection().getUser());
		else return null;
	}
	public IAmbitProcessor getProcessor() {
		if (userData instanceof ISharedDbData)
			try {
				return new ReadSMILESProcessor(((ISharedDbData) userData).getDbConnection().getConn()) {
				    /* (non-Javadoc)
                     * @see ambit2.database.processors.ReadSMILESProcessor#toString()
                     */
                    public String toString() {
                        return caption;
                    }
				  
				};
			} catch (Exception x) {
				logger.error(x);
			}
		return null;
	}

}

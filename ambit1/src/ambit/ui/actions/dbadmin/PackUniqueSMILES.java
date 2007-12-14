package ambit.ui.actions.dbadmin;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit.database.core.DbSQL;
import ambit.database.data.ISharedDbData;
import ambit.database.processors.ReadSMILESProcessor;
import ambit.database.readers.DbStructureReader;
import ambit.database.writers.PackSubstancesWriter;
import ambit.exceptions.AmbitException;
import ambit.processors.IAmbitProcessor;
import ambit.ui.UITools;
import ambit.ui.actions.BatchAction;

/**
 * Runs {@link ambit.database.writers.PackSubstancesWriter}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class PackUniqueSMILES extends BatchAction {
    protected static String caption = "Cleans up redundant information from database.";
	public PackUniqueSMILES(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Pack unique SMILES");
	}

	public PackUniqueSMILES(Object userData, JFrame mainFrame, String name) {
		this(userData, mainFrame, name,UITools.createImageIcon("ambit/ui/images/database.png"));

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
                     * @see ambit.database.processors.ReadSMILESProcessor#toString()
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

package ambit2.ui.actions.search;

import java.awt.event.KeyEvent;
import java.sql.Connection;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;

import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.database.data.ISharedDbData;
import ambit2.database.processors.ReadSubstanceProcessor;
import ambit2.database.search.DbSubstructurePrescreenReader;
import ambit2.exceptions.AmbitException;
import ambit2.processors.IAmbitProcessor;

/**
 * Search for structures that can undergo hydrolysis.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class GetMetabolizableCompoundsAction extends DbSubstructureSearchAction {
	protected IMolecule mol = null;
	public GetMetabolizableCompoundsAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Search",null);
	}

	public GetMetabolizableCompoundsAction(Object userData, JFrame mainFrame,
			String arg0) {
		this(userData, mainFrame, arg0,null);
	}

	public GetMetabolizableCompoundsAction(Object userData, JFrame mainFrame,
			String arg0, Icon arg1) {
		super(userData, mainFrame, arg0, arg1);
		putValue(AbstractAction.MNEMONIC_KEY,new Integer(KeyEvent.VK_Z));
		putValue(AbstractAction.SHORT_DESCRIPTION,"Search for compounds that could be metabolized");
	}
	protected IMolecule getMolecule() {
		if (mol == null) {
			mol = new org.openscience.cdk.Molecule();
			Atom a1 = new Atom("C"); mol.addAtom(a1);
			Atom a2 = new Atom("C");mol.addAtom(a2);
			Atom a3 = new Atom("O");mol.addAtom(a3);
			Atom a4 = new Atom("O");mol.addAtom(a4);
			Atom a5 = new Atom("C");mol.addAtom(a5);
			mol.addBond(new org.openscience.cdk.Bond(a1,a2,CDKConstants.BONDORDER_SINGLE));
			mol.addBond(new org.openscience.cdk.Bond(a2,a3,CDKConstants.BONDORDER_DOUBLE));
			mol.addBond(new org.openscience.cdk.Bond(a2,a4,CDKConstants.BONDORDER_SINGLE));
			mol.addBond(new org.openscience.cdk.Bond(a4,a5,CDKConstants.BONDORDER_SINGLE));
		} 
		return mol;
	}
	/*
	public IteratingChemObjectReader getSearchReader(Connection connection, Object query, String limit) throws AmbitException{
		return new DbSubstructureSearchReader(connection,getMolecule(),null, limit);
	}
	*/
	public IIteratingChemObjectReader getSearchReader(Connection connection, Object query, int page, int pagesize) throws AmbitException{
		return new DbSubstructurePrescreenReader(connection,getMolecule(),null, page,pagesize);
	}	
	/* (non-Javadoc)
     * @see ambit2.ui.actions.BatchAction#getProcessor()
     */
    public IAmbitProcessor getProcessor() {
        if (userData instanceof ISharedDbData) {
            ISharedDbData dbaData = (ISharedDbData) userData;
            try {
                return new ReadSubstanceProcessor(dbaData.getDbConnection().getConn());
            } catch (AmbitException x) {
                return null;
            }
		} else return null;

    }
}

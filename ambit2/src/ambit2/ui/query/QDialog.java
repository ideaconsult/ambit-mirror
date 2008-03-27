/**
 * Created on 2005-2-2
 *
 */
package ambit2.ui.query;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import org.openscience.cdk.interfaces.IMolecule;

import ambit2.data.molecule.Compound;
import ambit2.exceptions.AmbitException;
import ambit2.ui.AmbitColors;
import ambit2.ui.CoreDialog;
import ambit2.ui.data.QCompoundPanel;


/**
 * A Dialog to show a {@link ambit2.data.molecule.Compound} 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class QDialog extends CoreDialog {
	QCompoundPanel qpanel;
	/**
	 * @throws HeadlessException
	 */
	public QDialog() throws HeadlessException {
		super();
	}

	/**
	 * @param owner
	 * @throws HeadlessException
	 */
	public QDialog(Dialog owner) throws HeadlessException {
		super(owner);

	}

	/**
	 * @param owner
	 * @param modal
	 * @throws HeadlessException
	 */
	public QDialog(Dialog owner, boolean modal) throws HeadlessException {
		super(owner, modal);

	}

	/**
	 * @param owner
	 * @throws HeadlessException
	 */
	public QDialog(Frame owner) throws HeadlessException {
		super(owner);

	}

	/**
	 * @param owner
	 * @param modal
	 * @throws HeadlessException
	 */
	public QDialog(Frame owner, boolean modal) throws HeadlessException {
		super(owner, modal);

	}

	/**
	 * @param owner
	 * @param title
	 * @throws HeadlessException
	 */
	public QDialog(Dialog owner, String title) throws HeadlessException {
		super(owner, title);

	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws HeadlessException
	 */
	public QDialog(Dialog owner, String title, boolean modal)
			throws HeadlessException {
		super(owner, title, modal);

	}

	/**
	 * @param owner
	 * @param title
	 * @throws HeadlessException
	 */
	public QDialog(Frame owner, String title) throws HeadlessException {
		super(owner, title);

	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws HeadlessException
	 */
	public QDialog(Frame owner, String title, boolean modal)
			throws HeadlessException {
		super(owner, title, modal);

	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 * @throws HeadlessException
	 */
	public QDialog(Dialog owner, String title, boolean modal,
			GraphicsConfiguration gc) throws HeadlessException {
		super(owner, title, modal, gc);

	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 */
	public QDialog(Frame owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);

	}

	/* (non-Javadoc)
	 * @see ambit2.ui.CoreDialog#cancelAction()
	 */
	protected void cancelAction() {
		super.cancelAction();
		setVisible(false);

	}

	/* (non-Javadoc)
	 * @see ambit2.ui.CoreDialog#okAction()
	 */
	protected void okAction() {
		super.okAction();
		setVisible(false);

	}
	protected void addWidgets() {
		super.addWidgets();
		setTitle("Search for");	
		qpanel = new QCompoundPanel("Query",AmbitColors.BrightClr,AmbitColors.DarkClr);
		qpanel.setEditable(true);
		Container contentPane = getContentPane();
		contentPane.add(qpanel, BorderLayout.CENTER);
	}
	
	public IMolecule getMolecule() {
		return qpanel.getMol(); 
	}
	public void setMolecule(IMolecule mol) throws AmbitException {
		qpanel.setMol(mol); 
	}
	public Compound getCompound() {
		return qpanel.getCompound();
	}
	public void setCompound(Compound mol) {
		qpanel.setCompound(mol); 
	}	
	
}

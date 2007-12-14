/**
 * Created on 2005-4-4
 *
 */
package ambit.ui.domain;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import ambit.ui.CoreDialog;


/**
 * A Threshold dialog
 * TODO to implement it :) 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class ThresholdDialog extends CoreDialog {

	/**
	 * @throws HeadlessException
	 */
	public ThresholdDialog() throws HeadlessException {
		super();
	}

	/**
	 * @param owner
	 * @throws HeadlessException
	 */
	public ThresholdDialog(Dialog owner) throws HeadlessException {
		super(owner);
	}

	/**
	 * @param owner
	 * @param modal
	 * @throws HeadlessException
	 */
	public ThresholdDialog(Dialog owner, boolean modal)
			throws HeadlessException {
		super(owner, modal);

	}

	/**
	 * @param owner
	 * @throws HeadlessException
	 */
	public ThresholdDialog(Frame owner) throws HeadlessException {
		super(owner);

	}

	/**
	 * @param owner
	 * @param modal
	 * @throws HeadlessException
	 */
	public ThresholdDialog(Frame owner, boolean modal) throws HeadlessException {
		super(owner, modal);

	}

	/**
	 * @param owner
	 * @param title
	 * @throws HeadlessException
	 */
	public ThresholdDialog(Dialog owner, String title) throws HeadlessException {
		super(owner, title);

	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws HeadlessException
	 */
	public ThresholdDialog(Dialog owner, String title, boolean modal)
			throws HeadlessException {
		super(owner, title, modal);

	}

	/**
	 * @param owner
	 * @param title
	 * @throws HeadlessException
	 */
	public ThresholdDialog(Frame owner, String title) throws HeadlessException {
		super(owner, title);

	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws HeadlessException
	 */
	public ThresholdDialog(Frame owner, String title, boolean modal)
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
	public ThresholdDialog(Dialog owner, String title, boolean modal,
			GraphicsConfiguration gc) throws HeadlessException {
		super(owner, title, modal, gc);

	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 */
	public ThresholdDialog(Frame owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);

	}
	/* (non-Javadoc)
	 * @see ambit.ui.CoreDialog#addWidgets()
	 */
	protected void addWidgets() {
		super.addWidgets();
		Container contentPane = getContentPane();
		contentPane.add(new ThresholdPanel("Threshold"), BorderLayout.CENTER);

	}
}

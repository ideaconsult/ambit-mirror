/**
 * Created on 2005-2-3
 *
 */
package ambit2.ui.domain;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JOptionPane;

import ambit2.domain.QSARDataset;
import ambit2.ui.CoreDialog;

/**
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class ADDataSetDialog extends CoreDialog {
	protected ADDataSetPanel dpanel;
	/**
	 * @throws HeadlessException
	 */
	public ADDataSetDialog() throws HeadlessException {
		super();
	}

	/**
	 * @param owner
	 * @throws HeadlessException
	 */
	public ADDataSetDialog(Dialog owner) throws HeadlessException {
		super(owner);
	}

	/**
	 * @param owner
	 * @param modal
	 * @throws HeadlessException
	 */
	public ADDataSetDialog(Dialog owner, boolean modal)
			throws HeadlessException {
		super(owner, modal);

	}

	/**
	 * @param owner
	 * @throws HeadlessException
	 */
	public ADDataSetDialog(Frame owner) throws HeadlessException {
		super(owner);
	}

	/**
	 * @param owner
	 * @param modal
	 * @throws HeadlessException
	 */
	public ADDataSetDialog(Frame owner, boolean modal) throws HeadlessException {
		super(owner, modal);
	}

	/**
	 * @param owner
	 * @param title
	 * @throws HeadlessException
	 */
	public ADDataSetDialog(Dialog owner, String title) throws HeadlessException {
		super(owner, title);
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws HeadlessException
	 */
	public ADDataSetDialog(Dialog owner, String title, boolean modal)
			throws HeadlessException {
		super(owner, title, modal);
	}

	/**
	 * @param owner
	 * @param title
	 * @throws HeadlessException
	 */
	public ADDataSetDialog(Frame owner, String title) throws HeadlessException {
		super(owner, title);
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws HeadlessException
	 */
	public ADDataSetDialog(Frame owner, String title, boolean modal)
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
	public ADDataSetDialog(Dialog owner, String title, boolean modal,
			GraphicsConfiguration gc) throws HeadlessException {
		super(owner, title, modal, gc);
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 */
	public ADDataSetDialog(Frame owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);

	}

	protected void addWidgets() {
		super.addWidgets();
		dpanel = new ADDataSetPanel("Data set");
		Container contentPane = getContentPane();		
		contentPane.add(dpanel,BorderLayout.CENTER);
	}
	
	public void setDataset(QSARDataset ds) {
		dpanel.setDataset(ds);
	}
	
	public static void editDataset(QSARDataset ds, String title) {
		if (ds == null) return;
		Frame frame = JOptionPane.getFrameForComponent(null);
		ADDataSetDialog d = new ADDataSetDialog(frame,title,true);
		ds.setReadonly(false);
		d.setDataset(ds);
		d.centerParent(frame);
		d.setVisible(true);
		
		if (d.getResult() == JOptionPane.OK_OPTION) {
			
		}
		d = null;
		ds.setReadonly(true);		
	}	
}

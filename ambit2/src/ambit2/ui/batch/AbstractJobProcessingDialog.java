/*
 * Created on 2005-9-5
 *
 */
package ambit2.ui.batch;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * A dialog with start, pause and cancel buttons.
 * @author Nina Jeliazkova
 * <b>Modified</b> 2005-9-5
 */
public abstract class AbstractJobProcessingDialog extends JDialog {
	protected JButton cancelButton, okButton, pauseButton;
	protected boolean pausePressed = false;
	protected boolean okPressed = false;
	protected boolean cancelPressed = false;
	protected int result;
	protected JPanel buttonPane, mainPanel;
	
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -5641683445720590852L;

	/**
	 * @throws java.awt.HeadlessException
	 */
	public AbstractJobProcessingDialog() throws HeadlessException {
		super();
		addWidgets();
	}

	/**
	 * @param owner
	 * @throws java.awt.HeadlessException
	 */
	public AbstractJobProcessingDialog(Dialog owner) throws HeadlessException {
		super(owner);
		addWidgets();
	}

	/**
	 * @param owner
	 * @param modal
	 * @throws java.awt.HeadlessException
	 */
	public AbstractJobProcessingDialog(Dialog owner, boolean modal)
			throws HeadlessException {
		super(owner, modal);
		addWidgets();
	}

	/**
	 * @param owner
	 * @throws java.awt.HeadlessException
	 */
	public AbstractJobProcessingDialog(Frame owner) throws HeadlessException {
		super(owner);
		addWidgets();
	}

	/**
	 * @param owner
	 * @param modal
	 * @throws java.awt.HeadlessException
	 */
	public AbstractJobProcessingDialog(Frame owner, boolean modal)
			throws HeadlessException {
		super(owner, modal);
		addWidgets();
	}

	/**
	 * @param owner
	 * @param title
	 * @throws java.awt.HeadlessException
	 */
	public AbstractJobProcessingDialog(Dialog owner, String title)
			throws HeadlessException {
		super(owner, title);
		addWidgets();
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws java.awt.HeadlessException
	 */
	public AbstractJobProcessingDialog(Dialog owner, String title, boolean modal)
			throws HeadlessException {
		super(owner, title, modal);
		addWidgets();
	}

	/**
	 * @param owner
	 * @param title
	 * @throws java.awt.HeadlessException
	 */
	public AbstractJobProcessingDialog(Frame owner, String title)
			throws HeadlessException {
		super(owner, title);
		addWidgets();
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws java.awt.HeadlessException
	 */
	public AbstractJobProcessingDialog(Frame owner, String title, boolean modal)
			throws HeadlessException {
		super(owner, title, modal);
		addWidgets();
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 * @throws java.awt.HeadlessException
	 */
	public AbstractJobProcessingDialog(Dialog owner, String title, boolean modal,
			GraphicsConfiguration gc) throws HeadlessException {
		super(owner, title, modal, gc);
		addWidgets();
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 */
	public AbstractJobProcessingDialog(Frame owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		addWidgets();
	}
	protected void addWidgets() {
		int minW = 400; int minH = 200;

		cancelButton = new JButton("Cancel");
		okButton = new JButton("OK");
		pauseButton = new JButton("Pause");
		pauseButton.setVisible(false);
		pauseButton.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
			 	pauseAction();
			 }
			});		
		cancelButton.addActionListener(new ActionListener() {
		 public void actionPerformed(ActionEvent e) {
		 	cancelAction();
		 }
		});
		okButton.addActionListener(new ActionListener() {
		 public void actionPerformed(ActionEvent e) {
		     okAction();
		 }
		});
		getRootPane().setDefaultButton(okButton);

//		Lay out the buttons from left to right.
		buttonPane = new JPanel();
		
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(okButton);

		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(pauseButton);
		
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(cancelButton);
		
		buttonPane.setPreferredSize(new Dimension(minW,48));
		buttonPane.setMinimumSize(new Dimension(minW,48));
//		Put everything together, using the content pane's BorderLayout.
		//Container contentPane = getContentPane();
		
		
		JPanel panel = new JPanel();
		
		panel.setLayout(new BorderLayout());
		//panel.setPreferredSize(new Dimension(300,300));
		panel.setMinimumSize(new Dimension(minW,minH));		
		
		panel.add(buttonPane, BorderLayout.SOUTH);
		mainPanel = createMainPanel();
		panel.add(mainPanel, BorderLayout.CENTER);
		
		setContentPane(panel);
		//contentPane.add(panel);
		pack();
	}
	protected abstract JPanel createMainPanel();
	
	protected  void cancelAction() {
		cancelPressed = true;
	    result = JOptionPane.CANCEL_OPTION;
		setVisible(false);
	}
	
	protected void okAction() {
		cancelPressed = false;
		pausePressed = false;
		okButton.setVisible(false);
		pauseButton.setVisible(true);
		cancelButton.setVisible(true);
		
	}
	/*
		result = JOptionPane.OK_OPTION;
		setVisible(false);
	}
		*/
	protected void pauseAction() {
		pausePressed = ! pausePressed;
	}
		
	/**
	 * @return Returns the result.
	 */
	public synchronized int getResult() {
		return result;
	}
	/**
	 * @param result The result to set.
	 */
	public synchronized void setResult(int result) {
		this.result = result;
	}
	/*
//	 centers the dialog within the parent container [1.1]
	public void centerParent () {
	  int x;
	  int y;

	  // Find out our parent 
	  Container myParent = getParent();
	  Point topLeft = myParent.getLocationOnScreen();
	  Dimension parentSize = myParent.getSize();

	  Dimension mySize = getSize();

	  if (parentSize.width > mySize.width) 
	    x = ((parentSize.width - mySize.width)/2) + topLeft.x;
	  else 
	    x = topLeft.x;
	   
	  if (parentSize.height > mySize.height) 
	    y = ((parentSize.height - mySize.height)/2) + topLeft.y;
	  else 
	    y = topLeft.y;
	   
	  setLocation (x, y);
	  }  
*/
	public void centerScreen() {
		  Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		  Rectangle abounds = this.getBounds();
		  this.setLocation((dim.width - abounds.width) / 2,
		      (dim.height - abounds.height) / 2);
	}
	public abstract void enableDataControls(boolean enable);
	
	public void setPanel(JComponent panel) {
		if (panel == null) setContentPane(panel);
		else setContentPane(panel);
	}
}

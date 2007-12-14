/*
 * Created on 2005-4-9

 * @author Nina Jeliazkova nina@acad.bg
 *
 * Project : ambit
 * Package : ambit.applets
 * Filename: CompoundViewer2D.java
 */
package ambit.applets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JApplet;
import javax.swing.JLabel;

import ambit.data.molecule.Compound;
import ambit.ui.AmbitColors;
import ambit.ui.data.QCompoundPanel;


/**
 * Uses QCompoundPanel to display Compound 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-9
 */
public class CompoundViewer2D extends JApplet implements Runnable, MouseListener, MouseMotionListener {
	protected QCompoundPanel panel = null;
	protected JLabel status = null;
	protected Compound compound = null;
	/**
	 * Constructor
	 * @throws java.awt.HeadlessException
	 */
	public CompoundViewer2D() throws HeadlessException {
		super();
	}

	public static void main(String[] args) {
	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		//TODO read smiles as parameter or CML inputstream
		compound = new Compound();
		compound.setSMILES("CCCCCCC");
		
		compound.updateMolecule();
		compound.setEditable(true);

		panel.setCompound(compound);
		repaint();

	}
	/* (non-Javadoc)
	 * @see java.applet.Applet#start()
	 */
    public void start() {
    	if (compound == null) {
    		new Thread(this).start();
    	}
    }  
   public void stop() {
   }	
	   /* (non-Javadoc)
	 * @see java.applet.Applet#init()
	 */
	private void createGUI() {
		super.init();
		getContentPane().setLayout(new BorderLayout());
		panel = new QCompoundPanel("",Color.WHITE,AmbitColors.DarkClr);
		getContentPane().add(panel,BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(200,200));
		panel.setMinimumSize(new Dimension(200,200));		
		status = new JLabel("");
		status.setToolTipText("enter smiles here");
		getContentPane().add(status,BorderLayout.SOUTH);
		
	}
	
	public void init() {
	    //Execute a job on the event-dispatching thread:
	    //creating this applet's GUI.
	    try {
	        javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
	            public void run() {
	                createGUI();
	            }
	        });
	    } catch (Exception e) {
	        System.err.println("createGUI didn't successfully complete");
	    }
	}
	
}

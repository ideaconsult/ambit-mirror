/*
 * Created on 2005-12-18
 *
 */
package ambit.ui.actions.file;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.openscience.cdk.interfaces.IMolecule;

import ambit.data.ISharedData;
import ambit.ui.UITools;
import ambit.ui.actions.process.MoleculeEditAction;

/**
 * New file.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2005-12-18
 */
public class FileNewAction extends MoleculeEditAction  {

    /**
     * @param userData
     * @param mainFrame
     */
    public FileNewAction(IMolecule molecule,Object userData, JFrame mainFrame) {
        this(molecule,userData, mainFrame,"New");
    }

    /**
     * @param userData
     * @param mainFrame
     * @param arg0
     */
    public FileNewAction(IMolecule molecule,Object userData, JFrame mainFrame, String arg0) {
        this(molecule,userData, mainFrame, arg0,UITools.createImageIcon("ambit/ui/images/new_document_16_h.png"));
    }

    /**
     * @param userData
     * @param mainFrame
     * @param arg0
     * @param arg1
     */
    public FileNewAction(IMolecule molecule,Object userData, JFrame mainFrame, String arg0,
            Icon arg1) {
        super(molecule,userData, mainFrame, arg0, arg1);
        // TODO Auto-generated constructor stub
    }
    /* (non-Javadoc)
     * @see ambit.ui.actions.AmbitAction#run(java.awt.event.ActionEvent)
     */
  
    public void run(ActionEvent arg0) {
		if (userData instanceof ISharedData) {
			try {
			    ((ISharedData) userData).newMolecule();
			} catch (Exception x) {
				logger.error(x);
			    JOptionPane.showMessageDialog(mainFrame,x.getMessage());
			}
		}
        
    }
 }

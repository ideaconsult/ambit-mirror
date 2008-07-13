package ambit.ui.actions.process;

import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;

import ambit.processors.IAmbitProcessor;
import ambit.processors.descriptors.MopacProcessor;
import ambit.processors.descriptors.MopacShell;
import ambit.ui.UITools;

/**
 * Launches {@link ambit.processors.descriptors.MopacProcessor} for the current structure.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class MoleculeMOPACAction extends MoleculeCalculationAction {

	public MoleculeMOPACAction(Object userData, JFrame mainFrame) {
		this(userData, mainFrame,"Calculate electronic parameters by WinMOPAC");
	}

	public MoleculeMOPACAction(Object userData, JFrame mainFrame, String name) {
		this(userData, mainFrame, name,UITools.createImageIcon("ambit/ui/images/molecule.png"));
		
	}

	public MoleculeMOPACAction(Object userData, JFrame mainFrame, String name,
			Icon icon) {
		super(userData, mainFrame, name, icon);
		interactive = false;
		putValue(AbstractAction.MNEMONIC_KEY,new Integer(KeyEvent.VK_E));
		putValue(AbstractAction.SHORT_DESCRIPTION,"Calculates electronic parameters by WinMOPAC");
	}
	public IAmbitProcessor getProcessor() {
		MopacShell shell = new MopacShell();
		MopacProcessor p =  new MopacProcessor(shell);
		p.setFrame(mainFrame);
		return p;
	}

}

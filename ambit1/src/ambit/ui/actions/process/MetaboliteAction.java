package ambit.ui.actions.process;

import java.awt.Container;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import ambit.processors.IAmbitProcessor;
import ambit.processors.toxtree.MetaboliteProcessor;
import ambit.ui.UITools;

/**
 * Generates metabolites by applying few hydrolysis reactions.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class MetaboliteAction extends MoleculeCalculationAction {

	public MetaboliteAction(Object userData, Container mainFrame) {
		this(userData, mainFrame,"Metabolites");
	}

	public MetaboliteAction(Object userData, Container mainFrame, String name) {
		this(userData, mainFrame, name,UITools.createImageIcon("ambit/ui/images/metabolite.png"));
	}

	public MetaboliteAction(Object userData, Container mainFrame, String name,
			Icon icon) {
		super(userData, mainFrame, name, icon);
		interactive = false;
		putValue(AbstractAction.MNEMONIC_KEY,new Integer(KeyEvent.VK_M));
		putValue(AbstractAction.SHORT_DESCRIPTION,"Generates metabolites by applying few predefined reactions");
	}

	public IAmbitProcessor getProcessor() {
		MetaboliteProcessor p = new MetaboliteProcessor();
		p.setFrame(mainFrame);
		return p;
	}
}

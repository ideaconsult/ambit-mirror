package ambit2.ui.actions.process;

import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFrame;

import toxTree.core.IDecisionMethod;

import ambit2.processors.IAmbitProcessor;
import ambit2.processors.toxtree.ToxTreeProcessor;
import ambit2.ui.UITools;

/**
 * Runs {@link ambit2.processors.toxtree.ToxTreeProcessor} on the current molecule.

 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Sep 1, 2006
 */
public class ToxTreeAction extends MoleculeCalculationAction {
	protected IDecisionMethod method;
	public ToxTreeAction(IDecisionMethod method,Object userData, JFrame mainFrame) {
		this(method,userData, mainFrame,method.toString());
	}

	public ToxTreeAction(IDecisionMethod method,Object userData, JFrame mainFrame, String name) {
		this(method,userData, mainFrame, name,UITools.createImageIcon("ambit2/ui/images/toxtree.png"));
	}

	public ToxTreeAction(IDecisionMethod method,Object userData, JFrame mainFrame, String name,
			Icon icon) {
		super(userData, mainFrame, name, icon);
        this.method = method;
		interactive = false;
		putValue(AbstractAction.MNEMONIC_KEY,new Integer(KeyEvent.VK_V));
		putValue(AbstractAction.SHORT_DESCRIPTION,method.getExplanation());
	}
	
	public IAmbitProcessor getProcessor() {
	    try {
			ToxTreeProcessor p = new ToxTreeProcessor(method);
			putValue(AbstractAction.NAME,p.getMethod().toString());
			return p;
	    } catch (Exception x) {
	        logger.error(x);
	        return null;
	    }
	}
}

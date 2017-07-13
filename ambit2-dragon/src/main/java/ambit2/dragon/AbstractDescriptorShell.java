package ambit2.dragon;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.base.external.CommandShell;
import ambit2.base.external.ShellException;

public abstract class AbstractDescriptorShell extends CommandShell<IAtomContainer, IAtomContainer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 846520553482086575L;

	public AbstractDescriptorShell() throws ShellException {
		super();

	}

	@Override
	protected IAtomContainer transform(IAtomContainer mol) {
		return mol;
	}

	@Override
	public synchronized IAtomContainer runShell(IAtomContainer mol) throws ShellException {
		if (canApply(mol))
			return super.runShell(mol);
		else
			return mol;
	}

	protected synchronized boolean canApply(IAtomContainer atomcontainer) throws ShellException {
		if ((atomcontainer == null) || (atomcontainer.getAtomCount() == 0))
			throw new ShellException(this, "Undefined structure");
		return true;
	}

	protected abstract String getHome() throws ShellException;

	@Override
	protected String getPath(File file) {
		return String.format("%s", getHomeDir(null));
	}


}

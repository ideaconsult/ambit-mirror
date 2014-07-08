package ambit2.mopac;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.balloon.ShellBalloon;
import ambit2.balloon.ShellBalloonWeb;
import ambit2.base.external.ShellException;

public class MopacShellBalloon extends AbstractMopacShell {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3625737861693976094L;
	protected ShellBalloon shellBalloon;
	
	public MopacShellBalloon() throws ShellException {
		this(new ShellBalloonWeb());
	}
	
	public MopacShellBalloon(ShellBalloon balloon) throws ShellException {
		super();
		this.shellBalloon = balloon;
	}
	@Override
	protected IAtomContainer generate3DStructure(IAtomContainer mol) throws AmbitException {
		return shellBalloon.process(mol);
	}

}

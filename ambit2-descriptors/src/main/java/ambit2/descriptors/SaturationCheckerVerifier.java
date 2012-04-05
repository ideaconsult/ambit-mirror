package ambit2.descriptors;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.SaturationChecker;

public class SaturationCheckerVerifier extends AbstractKekulizationVerifier {
	protected SaturationChecker typer = new SaturationChecker();
	@Override
	protected IAtomContainer transform2Kekule(IAtomContainer mol)
			throws CDKException {
		typer.saturate(mol);
		return mol;
	}

	@Override
	public String getTyperClass() {
		return SaturationChecker.class.getName();
	}

}

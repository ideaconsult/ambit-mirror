package ambit2.descriptors;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.DeduceBondSystemTool;

public class KekulizationVerifier extends AbstractKekulizationVerifier {
	
	protected DeduceBondSystemTool typer = new DeduceBondSystemTool();
	
	public KekulizationVerifier() {
		super();
		//typer.setTimeout(30*60*1000); //15 min
	}
	@Override
	public String getTyperClass() {
		return DeduceBondSystemTool.class.getName();
	}
	@Override
	protected IAtomContainer transform2Kekule(IAtomContainer mol) throws CDKException {
		return typer.fixAromaticBondOrders((IMolecule)mol);
	}

}

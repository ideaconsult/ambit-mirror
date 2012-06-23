package ambit2.descriptors;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.FixBondOrdersTool;

public class KekulizationVerifier extends AbstractKekulizationVerifier {
	
	protected FixBondOrdersTool typer = new FixBondOrdersTool();
	
	public KekulizationVerifier() {
		super();
		//typer.setTimeout(30*60*1000); //15 min
	}
	@Override
	public String getTyperClass() {
		return FixBondOrdersTool.class.getName();
	}
	@Override
	protected IAtomContainer transform2Kekule(IAtomContainer mol) throws CDKException {
		return typer.kekuliseAromaticRings((IMolecule)mol);
	}

}

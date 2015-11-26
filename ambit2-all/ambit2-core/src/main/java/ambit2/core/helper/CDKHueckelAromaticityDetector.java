package ambit2.core.helper;

import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * For compatibility . Uses {@link Aromaticity} class
 * @author nina
 *
 */
public class CDKHueckelAromaticityDetector {

	public static boolean detectAromaticity(IAtomContainer molecule)
			throws CDKException {
		Aromaticity aromaticity = new Aromaticity(ElectronDonation.cdk(),
				Cycles.cdkAromaticSet());
		return aromaticity.apply(molecule);
	}

}

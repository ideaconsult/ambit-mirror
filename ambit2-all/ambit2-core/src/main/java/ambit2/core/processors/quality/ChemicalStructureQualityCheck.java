package ambit2.core.processors.quality;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.IsomorphismTester;

import ambit2.base.data.AmbitUser;
import ambit2.base.data.QLabel;
import ambit2.base.data.QLabel.QUALITY;

/**
 * Verifies consistency between multiple structures, coming from different origins
 * 
 * 2)coherence of multiple origins - compare all structures of same idchemical - assign OK if same, notOK if not same
<ul>
<li>retrieve a chemical (by idchemical)
<li>retrieve structures per chemical
<li>calculate fingerprints per structure
<li>compare structure fingerprints with fingerprints from fp1024 (per chemical), if different, assign "ERROR", otherwise assign "ProbablyOK"
<li>(slow) select one structure and compare all other structures (same chemical) with isomrphism test; assign "ERROR" if not same. 
</ul>   
 * @author nina
 *
 */
public class ChemicalStructureQualityCheck extends	DefaultAmbitProcessor<IMolecule, QLabel> {
	protected AmbitUser qualityVerifier = new AmbitUser("quality");
	protected IMolecule groundTruth = null;
	protected IsomorphismTester isoTester = new IsomorphismTester();
	public IMolecule getGroundTruth() {
		return groundTruth;
	}

	public void setGroundTruth(IMolecule groundTruth) {
		this.groundTruth = groundTruth;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -518931054923701570L;

	public QLabel process(IMolecule target) throws AmbitException {
		if ((groundTruth ==null) || (target==null)) return new QLabel(QUALITY.Unknown);
		QLabel label = new QLabel(isoTester.isIsomorphic(groundTruth,target)?QUALITY.OK:QUALITY.ERROR);
		label.setUser(qualityVerifier);
		return label;
	}
	
}

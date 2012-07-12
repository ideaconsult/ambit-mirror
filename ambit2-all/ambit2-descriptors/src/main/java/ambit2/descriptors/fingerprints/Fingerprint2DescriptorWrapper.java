package ambit2.descriptors.fingerprints;

import java.util.BitSet;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;

import ambit2.core.data.StringDescriptorResultType;

public class Fingerprint2DescriptorWrapper implements IMolecularDescriptor {
	IFingerprinter fingerprinter;
	
	public IFingerprinter getFingerprinter() {
		return fingerprinter;
	}


	public Fingerprint2DescriptorWrapper(IFingerprinter fingerprint) {
		super();
		this.fingerprinter = fingerprint;
	}
	

	@Override
	public DescriptorSpecification getSpecification() {
		String ref = String.format("http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#%s", fingerprinter.getClass().getName());
        return new DescriptorSpecification(
                ref,
                getClass().getName(),
                getClass().getName(),
                "The Chemistry Development Kit");
	}

	@Override
	public String[] getParameterNames() {
		return null;
	}

	@Override
	public Object getParameterType(String name) {
		return null;
	}

	@Override
	public void setParameters(Object[] params) throws CDKException {
		
	}

	@Override
	public Object[] getParameters() {
		return null;
	}

	@Override
	public String[] getDescriptorNames() {
		return new String[] {fingerprinter.getClass().getName()};
	}

	/**
	 * TODO this is inefficient, convert to hex or use org.openscience.cdk.fingerprint.io.FPSWriter
	 * @param bitset
	 * @return
	 */
	public String bitset2String(BitSet bitset) {
		return bitset.toString();
		
	}
	@Override
	public DescriptorValue calculate(IAtomContainer mol)  {
		if (fingerprinter == null)  {
			return new DescriptorValue(
					getSpecification(),
					getParameterNames(),
					getParameters(),
					null,
					getDescriptorNames(),
					new CDKException("org.openscience.cdk.fingerprint.IFingerprinter not assigned!")
					);	
		}

		try {
			BitSet bitset = fingerprinter.getFingerprint(mol);
			
			StringDescriptorResultType value = new StringDescriptorResultType(bitset2String(bitset)); 
			
			return new DescriptorValue(
						getSpecification(),
						getParameterNames(),
						getParameters(),
						value,
						getDescriptorNames()
						);					

		} catch (CDKException x) {
			return new DescriptorValue(
					getSpecification(),
					getParameterNames(),
					getParameters(),
					null,
					getDescriptorNames(),
					x
					);		
		}
	}	

	@Override
	public IDescriptorResult getDescriptorResultType() {
		return new StringDescriptorResultType("");
	}
}

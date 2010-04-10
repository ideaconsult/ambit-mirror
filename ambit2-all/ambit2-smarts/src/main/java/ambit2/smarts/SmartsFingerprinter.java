package ambit2.smarts;

import java.util.BitSet;

import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;


public class SmartsFingerprinter 
{
	Fingerprinter fp = new Fingerprinter();
	SmartsToChemObject convertor = new SmartsToChemObject(); 
	
	public BitSet getFingerprint(QueryAtomContainer query ) throws Exception
	{
		IAtomContainer ac = convertor.extractAtomContainer(query);
		BitSet bs = fp.getFingerprint(ac);
		return(bs);
	}
}

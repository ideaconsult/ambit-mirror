package ambit2.core.io;

import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.MDLV2000Format;

public class ReaderFactoryExtended extends ReaderFactory {
	@Override
	public ISimpleChemObjectReader createReader(IChemFormat arg0) {
		if (MDLV2000Format.getInstance().equals(arg0))
				return new MDLV2000ReaderExtended();
		else
			return super.createReader(arg0);
	}
	
}

package ambit2.smarts;

import java.util.List;

import org.openscience.cdk.interfaces.IAtom;

public interface IAcceptable {
	boolean accept( List<IAtom> atoms);
}

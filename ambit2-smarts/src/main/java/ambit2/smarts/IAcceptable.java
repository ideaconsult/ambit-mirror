package ambit2.smarts;

import java.util.Vector;

import org.openscience.cdk.interfaces.IAtom;

public interface IAcceptable {
	boolean accept( Vector<IAtom> atoms);
}

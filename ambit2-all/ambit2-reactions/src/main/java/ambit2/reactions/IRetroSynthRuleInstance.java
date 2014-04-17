package ambit2.reactions;

import java.util.Vector;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;


public interface IRetroSynthRuleInstance 
{	
	public Vector<IAtom> getAtoms();
	public IRetroSynthRule getRule();
	public IAtomContainer getContainer();
	public double getScore();
	public void setScore(double score);
	public double addScore(double scoreIncrement);
}

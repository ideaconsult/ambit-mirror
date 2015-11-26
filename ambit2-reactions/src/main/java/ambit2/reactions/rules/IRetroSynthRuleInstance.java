package ambit2.reactions.rules;

import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;


public interface IRetroSynthRuleInstance 
{	
	public List<IAtom> getAtoms();
	public void setAtoms(List<IAtom> atoms);
	public IRetroSynthRule getRule();
	public void setRule(IRetroSynthRule rule);
	public IAtomContainer getContainer();
	public double getScore();
	public void setScore(double score);
	public double addScore(double scoreIncrement);
}

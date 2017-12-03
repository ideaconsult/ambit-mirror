package ambit2.reactions.retrosynth;

import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.reactions.Reaction;

public class ReactionSequenceStep 
{
	IAtomContainer inputMolecule = null;
	IAtomContainer outputMolecule = null;
	Reaction reaction = null;
	List<IAtom> reactionMap = null;
		
	public IAtomContainer getInputMolecule() {
		return inputMolecule;
	}
	public void setInputMolecule(IAtomContainer inputMolecule) {
		this.inputMolecule = inputMolecule;
	}
	public IAtomContainer getOutputMolecule() {
		return outputMolecule;
	}
	public void setOutputMolecule(IAtomContainer outputMolecule) {
		this.outputMolecule = outputMolecule;
	}
	public Reaction getReaction() {
		return reaction;
	}
	public void setReaction(Reaction reaction) {
		this.reaction = reaction;
	}
	public List<IAtom> getReactionMap() {
		return reactionMap;
	}
	public void setReactionMap(List<IAtom> reactionMap) {
		this.reactionMap = reactionMap;
	}	
}

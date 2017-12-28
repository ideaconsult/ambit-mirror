package ambit2.reactions.retrosynth;

import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;

import ambit2.reactions.Reaction;

public class ReactionSequenceStep 
{
	IAtomContainer inputMolecule = null;
	List<IAtomContainer> outputMolecules = null;
	int inputMoleculeLevelIndex = 0;
	int outMoleculesLevelIndices[] = null;
	Reaction reaction = null;
	List<IAtom> reactionInstance = null;
		
	public IAtomContainer getInputMolecule() {
		return inputMolecule;
	}
	public void setInputMolecules(IAtomContainer inputMolecule) {
		this.inputMolecule = inputMolecule;
	}
	public List<IAtomContainer> getOutputMolecules() {
		return outputMolecules;
	}
	public void setOutputMolecules(List<IAtomContainer> outputMolecules) {
		this.outputMolecules = outputMolecules;		
	}
	public int getInputMoleculeLevelIndex() {
		return inputMoleculeLevelIndex;
	}
	public void setInputMoleculeLevelIndex(int inputMoleculeLevelIndex) {
		this.inputMoleculeLevelIndex = inputMoleculeLevelIndex;
	}
	public int[] getOutMoleculesLevelIndices() {
		return outMoleculesLevelIndices;
	}
	public void setOutMoleculesLevelIndices(int[] outMoleculesLevelIndices) {
		this.outMoleculesLevelIndices = outMoleculesLevelIndices;
	}
	public Reaction getReaction() {
		return reaction;
	}
	public void setReaction(Reaction reaction) {
		this.reaction = reaction;
	}
	public List<IAtom> getReactionInstance() {
		return reactionInstance;
	}
	public void setReactionInstance(List<IAtom> reactionInstance) {
		this.reactionInstance = reactionInstance;
	}	
}

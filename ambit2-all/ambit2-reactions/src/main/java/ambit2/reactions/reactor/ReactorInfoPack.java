package ambit2.reactions.reactor;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.reactions.GenericReaction;

public class ReactorInfoPack 
{
	public Reactor reactor = null;
	public ReactorNode node = null;
	public GenericReaction reaction = null;
	public IAtomContainer reagent = null;
}

package ambit2.smarts.smirks;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;

import ambit2.smarts.IsomorphismTester;
import ambit2.smarts.Node;
import ambit2.smarts.QuerySequenceElement;

public class SmartsIsomorphismTester 
{
	protected boolean FlagCheckStereoElements = false; 
	
	protected IQueryAtomContainer query;
	protected IAtomContainer target;
	protected boolean isomorphismFound;
	protected boolean FlagStoreIsomorphismNode = false;
	protected List<Node> isomorphismNodes = new ArrayList<Node>(); 
	protected Stack<Node> stack = new Stack<Node>();
	protected List<IAtom> targetAt = new ArrayList<IAtom>(); //a work container
	protected List<QuerySequenceElement> sequence = new ArrayList<QuerySequenceElement>();
	protected List<IQueryAtom> sequencedAtoms = new ArrayList<IQueryAtom>();
	protected List<IQueryAtom> sequencedBondAt1 = new ArrayList<IQueryAtom>();
	protected List<IQueryAtom> sequencedBondAt2 = new ArrayList<IQueryAtom>();
	
	public SmartsMatch smartsMatch = new SmartsMatch();
	
	boolean singleAtomSmartsIsomorphism()
	{	
		SMARTSAtom qa = (SMARTSAtom)query.getAtom(0);
		isomorphismFound = false;
		for (int i = 0; i < target.getAtomCount(); i++)
		{	
			if (smartsMatch.match(qa, target.getAtom(i)))
			{	
				isomorphismFound = true;
				break;
			}
		}
		return(isomorphismFound);
	}
}

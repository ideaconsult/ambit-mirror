/*
Copyright (C) 2007-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.smarts;


import org.openscience.cdk.Atom;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
//import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyOrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;
import org.openscience.cdk.isomorphism.mcss.RMap;

//import java.util.TreeMap;
import java.util.List;
import java.util.Vector;
import java.util.Stack;

/** 
 * Implements utilities needed for handling searching with SMARTS  
 * utilizing JDK library.
 * Some complex manipulations are encapsulated here (such as recursive smarts)   
 */

public class SmartsManager 
{
	int recursiveStrategy = 1; //0 - subqueries, 1 - recursive substructure search (default one)
	SmartsParser parser = new SmartsParser();
	String querySmarts;
	String errorMsg = "";
	QueryAtomContainer query;
	
	//The recursive atoms
	Vector<SmartsAtomExpression> recAtoms = new Vector<SmartsAtomExpression>();	
	
	//Pointers to the new atoms that substitute the original recurvive atoms in the 
	//generated sub queries
	IAtom newAtoms[];
	
	int recAtomNumSubSmarts[];
	int recAtomLastLoAnd[];
	int curComb[];
	Vector<QueryAtomContainer> subQueryList = new Vector<QueryAtomContainer>();	
	
	//The atoms connected to the recursive atoms and the corresponding bond orders.
	//The bonds between two recursive atoms are not put in topLayers.
	//The atom indexes for such bonds are stored separately in bondRecAt1 and bondRecAt2
	TopLayer topLayers[];  
	Vector<Integer> bondRecAt1 = new Vector<Integer>();
	Vector<Integer> bondRecAt2 = new Vector<Integer>();
	Vector<IBond> bondRecBo = new Vector<IBond>(); //It contains actually a bond expression
	Vector<Integer> compFrags = new Vector<Integer>(); //The indexes of the query frags which are in a component
	boolean fragMaps[][];
	int components[];
	QueryAtomContainer baseStr;	
	public boolean mGenerateSubQueries;
	
	
	public SmartsManager()
	{
		parser.setComponentLevelGrouping(true);
	}
	
	public void setQuery(String smQuery)
	{
		querySmarts = smQuery;
		query = parser.parse(querySmarts);
		parser.setNeededDataFlags();
		errorMsg = parser.getErrorMessages();
		if (!errorMsg.equals(""))
		{
			System.out.println("Smarts Parser errors:\n" + errorMsg);
			query = null;
			return;
		}
		
		if (parser.hasRecursiveSmarts)
		{	
			getRecursiveAtoms();
			if (recursiveStrategy == 0)
			{
				analyseRecursiveAtoms();			
				if(mGenerateSubQueries)
					genSubQueries();
			}
		}
		
		//Print debug info
		//System.out.println("query.getAtomCount() = "+query.getAtomCount());
		//System.out.println("query.getBondCount() = "+query.getBondCount());
		//System.out.println(query.toString());
		//System.out.println(SmartsHelper.getAtomsString(query));
		//System.out.println(SmartsHelper.getBondsString(query));
		
		/*
		System.out.println("numFragments = " + parser.numFragments);
		System.out.println("maxCompNumber = " + parser.maxCompNumber);
		SmartsHelper sh = new SmartsHelper();
		System.out.println("container " + sh.toSmarts(parser.container));
		System.out.println("Fragments: ");		
		
		for (int i = 0; i < parser.fragments.size(); i++)
		{
			String frag = sh.toSmarts(parser.fragments.get(i));			
			System.out.println(					 
					"  Comp = " + parser.fragmentComponents.get(i).intValue() +
					"  NA = " + parser.fragments.get(i).getAtomCount() +
					"  NB = " + parser.fragments.get(i).getBondCount() +
					"  " +  frag 
					);	
		}
		*/
		
	}
	
	public String getErrors()
	{
		return(errorMsg);
	}
	
	public QueryAtomContainer getQueryContaner()
	{
		return(query);
	}
	
	public boolean searchIn(IAtomContainer target)
	{
		if (query == null)
			return(false);		
		
		parser.setSMARTSData(target);
		
		if (parser.numFragments > 1)
			return(fragmentSearchIn(target));
		
		if (parser.hasRecursiveSmarts)
		{	
			return(recursiveSearchIn1(target));
		}	
		else
		{
			return(mappingIn(target));
		}	
	}
	
	boolean mappingIn(IAtomContainer target)
	{
		try
		{
			boolean res = UniversalIsomorphismTester.isSubgraph(target, query);
			return(res);
		}
		catch (CDKException e)
		{
			System.out.println(e.getMessage());
		}
		return(false);
	}
		
	boolean fragmentSearchIn(IAtomContainer target)
	{
		boolean noComponentsSpecified = true;
		for (int i = 0; i < parser.fragmentComponents.size(); i++)
		{	
			if (parser.fragmentComponents.get(i).intValue() > 0)
			{
				noComponentsSpecified = false;
				break;
			}
		}
		
		if (parser.hasRecursiveSmarts)
		{
			clearQueryRecMatches();
			getQueryRecMatches(target);
		}
		
		if (noComponentsSpecified)  //There are no zero level brackets i.e. each fragments maps anywere
		{	
			for (int i = 0; i < parser.fragments.size(); i++)
			{
				query = parser.fragments.get(i);
				if (!mappingIn(target))
					return(false);
			}
			return(true);
		}
		
		//Handle Component Level Grouping (zero level brackets are used)
		IMoleculeSet ms =  ConnectivityChecker.partitionIntoMolecules(target);			
		//This is a simple preliminary check
		if (ms.getAtomContainerCount() < parser.maxCompNumber)
			return(false);
		
		//Each query fragment which is in a component with number > 0 is searched individually against
		//each target component.
		//The query fragments which are not in a zero level brackets (comp. number = 0) are searched 
		//against the "whole target" i.e. to found it at least in on the of the target components				
		compFrags.clear();
		boolean res;
		
		for (int i = 0; i < parser.fragments.size(); i++)
		{	
			if (parser.fragmentComponents.get(i).intValue() == 0)
			{	
				query = parser.fragments.get(i);
				res = mappingIn(target);
				if (!res)
					return(false); //This non-component fragment is not found in the target
			}
			else
				compFrags.add(new Integer(i)); //parser.fragmentComponents.get(i));
		}
		
		fragMaps = new boolean[compFrags.size()][ms.getAtomContainerCount()];
		components = new int[compFrags.size()];
		for (int i = 0; i < compFrags.size(); i++)
		{
			components[i] = parser.fragmentComponents.get(compFrags.get(i).intValue()).intValue();
			for (int j = 0; j < ms.getAtomContainerCount(); j++)
			{	
				query = parser.fragments.get(compFrags.get(i).intValue());
					fragMaps[i][j] = mappingIn(ms.getAtomContainer(j));
			}	
		}
		
		//printComponentFrags();
		return(checkComponentMapings());
	}
		
	
	void printComponentFrags()  //A test helper procedure
	{
		System.out.println("---------------------------");
		SmartsHelper sh = new SmartsHelper();
		for (int i = 0; i < compFrags.size(); i++)
		{
			for (int k = 0; k < fragMaps[i].length; k++)				
				System.out.print(fragMaps[i][k]?"1 ":"0 ");
			
			String frag = sh.toSmarts(parser.fragments.get(compFrags.get(i).intValue()));
			System.out.print(frag);
			System.out.println("  " + components[i]);
		}
		System.out.println("---------------------------");
	}
	
	boolean checkComponentMapings()
	{
		//Combination is defined as an array a[] where:
		//i-th fragment is mapped onto target component a[i]
		//In order to return 'true' there must be found a combination
		//where all elements are different from -1 (i.e. each fragment is mapped onto a target component)
		//First depth-search algorithm is applied to search such a combination.
		Stack<int[]> comb = new Stack<int[]>();
		
		//Generation of the initial combinations
		for (int k = 0; k < fragMaps[0].length; k++)
		{
			if (!fragMaps[0][k])
				continue;
			int a[] = new int[components.length];
			resetCombination(a);
			a[0] = k;
			if (setComponentInCombination(0,a) != null)
				comb.push(a);
		}					
		boolean compMapResult = false;
		
		//Tree search of the mapping combination (depth-first search)
		while ((!comb.empty()) && (!compMapResult))
		{
			int a[] = comb.pop();
			compMapResult = addComponentToCombination(a, comb);
		}
		
		return(compMapResult);
	}
	
	void resetCombination(int a[])
	{
		for (int i = 0; i < a.length; i++)
			a[i] = -1;
	}
	
	boolean addComponentToCombination(int a[], Stack<int[]> comb)
	{
		int n = firstElementInCombination(-1,a);
		if (n == -1)
			return(true); //No elements equal to -1 were found i.e. all fragments are mapped
		
		for (int k = 0; k < fragMaps[n].length; k++)
		{
			if (!fragMaps[n][k])
				continue;
			int newC[] = a.clone();			
			newC[n] = k;
			if (setComponentInCombination(n,newC) != null)
				comb.push(newC);
		}
		
		return(false);
	}
	
	int[] setComponentInCombination(int n, int a[])
	{
		//All fragments that are in the component of n-fragment
		//must to be mapped onto target component a[n] as well
		//If NOT, null pointer  is returned		
		for (int i = n+1; i < components.length; i++)
		{	
			if(components[i] == components[n])
			{	
				if (fragMaps[i][a[n]]) //i-th fragment maps target component a[n]
					a[i] = a[n];
				else
					return(null);
			}
			else
				break; //It is applied because the fragments for a given component are sequential 
		}	
		
		return (a);
	}
	
	int firstElementInCombination(int el, int a[])
	{
		for (int i =0; i < a.length; i++)
			if (a[i] == el)
				return(i);
		return(-1);
	}
	
	void getRecursiveAtoms()
	{
		recAtoms.clear();
		for (int i = 0; i < query.getAtomCount(); i++)
		{
			if (query.getAtom(i) instanceof SmartsAtomExpression)
			{
				SmartsAtomExpression sa = (SmartsAtomExpression) query.getAtom(i); 
				if (sa.recSmartsStrings.size() > 0)
					recAtoms.add(sa);
			}
		}
	}
	
	//---------- Implementting the Strategy 0 for recursive atoms -----------NOT REALY USED----
	
	void analyseRecursiveAtoms()
	{
		//Checks whether: 
		//(1) all recursive expressions are separated only with OR logical operations and
		//(2) all recursive expressions are after the last ';' operand
		//(3) all expression after last ';' are recursive smarts
		//
		//For example [...;$(C),$(CC),$(CCO)] will be equivalent to the combination
		//of following ordinary smarts: [...;$(C)], [...;$(CC)], [...;$(CCO)]   
		
		mGenerateSubQueries = true;
		recAtomNumSubSmarts = new int[recAtoms.size()];
		recAtomLastLoAnd = new int[recAtoms.size()];
		
		for (int i = 0; i < recAtoms.size(); i++)
		{	
			SmartsAtomExpression sa = recAtoms.get(i);
			int lastLoAnd = getLastLoAndOperation(sa);
			if (getFirstRecursiveSmarts(sa) < lastLoAnd)
			{
				mGenerateSubQueries = false;
				return;
			}
			
			for (int j = lastLoAnd+1; j < sa.tokens.size(); j++)
			{
				SmartsExpressionToken token = (SmartsExpressionToken)sa.tokens.get(j);
				if (token.isLogicalOperation())
				{
					if (token.getLogOperation() != SmartsConst.LO_OR)
					{
						mGenerateSubQueries = false;
						return;
					}
				}
				else
				{
					if (token.type != SmartsConst.AP_Recursive)
					{
						mGenerateSubQueries = false;
						return;
					}
				}
			}
			recAtomNumSubSmarts[i] = sa.recSmartsStrings.size();
			recAtomLastLoAnd[i] = lastLoAnd;
		}
	}
	
	int getLastLoAndOperation(SmartsAtomExpression sa)
	{
		int lastLoAnd = -1;
		for (int j = 0; j < sa.tokens.size(); j++)
		{
			SmartsExpressionToken token = (SmartsExpressionToken)sa.tokens.get(j);
			if (token.isLogicalOperation())
			{
				if (token.getLogOperation() == SmartsConst.LO_ANDLO)
					lastLoAnd = j;
			}
		}
		return(lastLoAnd);
	}
	
	int getFirstRecursiveSmarts(SmartsAtomExpression sa)
	{
		for (int j = 0; j < sa.tokens.size(); j++)
		{
			SmartsExpressionToken token = (SmartsExpressionToken)sa.tokens.get(j);
			if (token.type != SmartsConst.AP_Recursive)
				return(j);
		}
		return(sa.tokens.size());
	}
	
	void firstComb()
	{
		for(int i = 0; i < curComb.length; i++)
			curComb[i] = 0;
	}
	
	void incComb(int pos)
	{
		curComb[pos]++;
		if (curComb[pos] >= recAtomNumSubSmarts[pos])
		{
			if (pos == curComb.length-1)
				curComb = null;
			else
			{	
				curComb[pos] = 0;
				incComb(pos+1);
			}	
		}
	}
	
	
	void genSubQueries()
	{
		setRecAtomNeighbours();
		subQueryList.clear();
		curComb = new int[recAtomNumSubSmarts.length];
		firstComb();
		
		//Base structure is obtaned from the original structure by removing recAtoms
		gatBaseStructure();
		
		while (curComb != null)
		{	
			newAtoms = new IAtom [curComb.length]; 
			QueryAtomContainer subQuery = new QueryAtomContainer();
			subQuery.add(baseStr);
			for(int i=0; i < curComb.length; i++)
				expandBaseStruct(subQuery,i);
			restoreRecAtomBonds(subQuery);
			subQueryList.add(subQuery);
			incComb(0);
		}
	}
	
	void gatBaseStructure()
	{
		//SmartsWriter smwriter = new SmartsWriter();
		baseStr = new QueryAtomContainer();
		
		baseStr.add(query);
		//System.out.println("base0:   " + smwriter.toSmarts(baseStr));
		for (int i = 0; i < recAtoms.size(); i++)
			baseStr.removeAtomAndConnectedElectronContainers(recAtoms.get(i));
		
		//System.out.println("base1:   " + smwriter.toSmarts(baseStr));
	}
	
	void setRecAtomNeighbours()
	{	
		topLayers = new TopLayer[recAtoms.size()];
		for (int i = 0; i < recAtoms.size(); i++)
			topLayers[i] = new TopLayer();
		
		for (int i = 0; i < query.getBondCount(); i++)
		{
			IBond bond = query.getBond(i);
			int k0 = getRecAtomIndex(bond.getAtom(0));
			int k1 = getRecAtomIndex(bond.getAtom(1));
			if ( k0 >= 0)
			{
				if (k1 >= 0)
				{	
					bondRecAt1.add(new Integer(k0));
					bondRecAt2.add(new Integer(k1));
					bondRecBo.add(bond);
				}	
				else
				{	
					topLayers[k0].atoms.add(bond.getAtom(1));
					topLayers[k0].bonds.add(bond);
				}	
			}
			else
			{
				if (k1 >= 0)
				{	
					topLayers[k1].atoms.add(bond.getAtom(0));
					topLayers[k1].bonds.add(bond);
				}	
			}
		}	
	}
	
	int getRecAtomIndex(Object o)
	{
		for (int i = 0; i < recAtoms.size(); i++)
			if (recAtoms.get(i) == o) 
				return(i);
		return(-1);
	}
	
	void expandBaseStruct(QueryAtomContainer struct, int pos)
	{	
		SmartsAtomExpression sa = recAtoms.get(pos);		
		QueryAtomContainer fragment = (QueryAtomContainer)sa.recSmartsContainers.get(curComb[pos]);
		
		//Adding the first atom of the fragment 
		if (recAtomLastLoAnd[pos] > -1)
		{	
			//Does nothing in this version
		}
		else
		{	
			struct.addAtom(fragment.getFirstAtom());
			newAtoms[pos] = fragment.getFirstAtom();
		}
		//restoring the bonds of the first atom from the fragment
		TopLayer tl = topLayers[pos];
		for (int i = 0; i < tl.atoms.size(); i++)
			addBond(struct, fragment.getFirstAtom(), (IAtom)tl.atoms.get(i),(IBond)tl.bonds.get(i));
		
		//Adding the rest of the fragment
		for (int i = 1; i < fragment.getAtomCount(); i++)
			struct.addAtom(fragment.getAtom(i));		
		for (int i = 0; i < fragment.getBondCount(); i++)		
			struct.addBond(fragment.getBond(i));
	}
	
	void addBond(QueryAtomContainer container, IAtom atom0, IAtom atom1, IBond bond)
	{
		IBond newBond = null;
		if (bond instanceof AnyOrderQueryBond)		
			newBond = new AnyOrderQueryBond();
		else
			if (bond instanceof OrderQueryBond)
			{	
				newBond = new AnyOrderQueryBond();
				newBond.setOrder(bond.getOrder());
			}
			else
				if (bond instanceof AromaticQueryBond)		
					newBond = new AromaticQueryBond();
				else
					if (bond instanceof RingQueryBond)		
						newBond = new RingQueryBond();	
					else
						if (bond instanceof SmartsBondExpression)
						{	
							newBond = new SmartsBondExpression();
							((SmartsBondExpression)newBond).tokens = ((SmartsBondExpression)bond).tokens;
						}
						else
						{
							//by default single bond. Any way this should not be called
							newBond = new AnyOrderQueryBond();
							newBond.setOrder(IBond.Order.SINGLE);
						}
		
		Atom[] atoms = new Atom[2];
	    atoms[0] = (Atom)atom0;
	    atoms[1] = (Atom)atom1;
	    newBond.setAtoms(atoms);
	    container.addBond(newBond);
	}
	
	void restoreRecAtomBonds(QueryAtomContainer container)
	{
		for(int i = 0; i < bondRecAt1.size(); i++)
		{
			int k1 = (bondRecAt1.get(i)).intValue();
			int k2 = (bondRecAt2.get(i)).intValue();
			addBond(container, newAtoms[k1], newAtoms[k2], bondRecBo.get(i));
		}
	}
	
	boolean recursiveSearchIn0(IAtomContainer target)
	{	
		//Strategy 0:
		//Implements the following strategy for SSS with recursive SMARTS
		//The recursive smarts is replaced with equivalent list of ordinary smarts (sub queries)
		//obtained from all possible combinations by varing of recursive expressions
		//
		//IMPORTANT NOTE !!!
		//It is not allways possible to substitute the original recursive SMARTS with 
		//a list of ordinary smarts. 
		//Success is always guaranteed when only OR operations (',') are used with the 
		//recursive conditions.
		try
		{	
			for (int i = 0; i < subQueryList.size(); i++)
			{	
				QueryAtomContainer subQuery = subQueryList.get(i);
				boolean res  = UniversalIsomorphismTester.isSubgraph(target, subQuery);
				if(res)
					return(true);
			}	
		}
		catch (CDKException e)
		{
			System.out.println(e.getMessage());
		}
		return(false);
	}
	
	
	//---------- Implementting the Strategy 1 for recursive atoms ---------------
	
	
	
	boolean recursiveSearchIn1(IAtomContainer target)
	{
		//Strategy 1:
		//Each reursive SMARTS is mapped against the target
		//and all mappings are stored in the query itself.
		//Afterwards this info is used by the main substructure search		
		clearQueryRecMatches();
		getQueryRecMatches(target);	
		return(mappingIn(target));		
	}
	
	public Vector<IAtom> getAtomMappings(IAtomContainer target)
	{				
		if (query == null)
			return(null);		
		
		parser.setSMARTSData(target);
		if (parser.hasRecursiveSmarts)
		{	
			clearQueryRecMatches();
			getQueryRecMatches(target);
		}			
		
		return(getAtomMappings(target,query));
	}
	
	public List getBondMappings(IAtomContainer target)
	{				
		if (query == null)
			return(null);		
		
		parser.setSMARTSData(target);
		if (parser.hasRecursiveSmarts)
		{	
			clearQueryRecMatches();
			getQueryRecMatches(target);
		}			
		
		List bondList;
		try
		{
			bondList = UniversalIsomorphismTester.getSubgraphMaps(target, query);
		}
		catch (CDKException e)
		{
			bondList = null; 
		}
		return(bondList);
	}
	
	
	
	
	void clearQueryRecMatches()
	{
		for (int i = 0; i < recAtoms.size(); i++)
			recAtoms.get(i).recSmartsMatches = new Vector<Vector<IAtom>>();
	}
	
	void getQueryRecMatches(IAtomContainer target)
	{
		Vector<QueryAtomContainer> vRecCon;				
		for (int i = 0; i < recAtoms.size(); i++)
		{	
			vRecCon = recAtoms.get(i).recSmartsContainers;
			for (int j = 0; j < vRecCon.size(); j++)			
				recAtoms.get(i).recSmartsMatches.add(getAtomMappings(target,vRecCon.get(j)));
			
			//for (int j = 0; j < vRecCon.size(); j++)
			//	System.out.println(recAtoms.get(i).recSmartsStrings.get(j) + "  "+
			//			matchesToString(target,recAtoms.get(i).recSmartsMatches.get(j)));
		}		
	}
	
	String matchesToString(IAtomContainer target, Vector<IAtom> atomMaps)
	{
		StringBuffer sb = new StringBuffer(); 
		for (IAtom at : atomMaps)		
			sb.append(" "+target.getAtomNumber(at));
		return(sb.toString());
	}
	
	public Vector<IAtom> getAtomMappings(IAtomContainer target, IAtomContainer recQuery)
	{
		List bondMaps;		  
		try
		{	
			if (recQuery.getAtomCount() == 1)
				return(getAtomMappingsFor1AtomQuery(target, recQuery));
			else				
				if (recQuery.getAtomCount() == 2)
					return(getAtomMappingsFor2AtomQuery(target, recQuery));
				else
				{
					bondMaps = UniversalIsomorphismTester.getSubgraphMaps(target, recQuery);
					return(getAtomMapsFromBondMaps(bondMaps, target, recQuery));
				}
		}
		catch (CDKException e)
		{
			System.out.println(e.getMessage());
		}		
		return(new Vector<IAtom>());
	}
	
	public Vector<IAtom> getAtomMappingsFor1AtomQuery(IAtomContainer target, IAtomContainer recQuery)
	{
		SMARTSAtom qAtom = (SMARTSAtom)recQuery.getAtom(0);
		Vector<IAtom> atomMaps  = new Vector<IAtom>();
		for (int i = 0; i < target.getAtomCount(); i++)
			if (qAtom.matches(target.getAtom(i)))
				atomMaps.add(target.getAtom(i));
		return(atomMaps);
	}
	
	public Vector<IAtom> getAtomMappingsFor2AtomQuery(IAtomContainer target, IAtomContainer recQuery)
	{
		Vector<IAtom> atomMaps  = new Vector<IAtom>();
		if (recQuery.getBondCount() == 0)
			return(atomMaps); //The two atoms must be connected otherwise substr. search returns no matches
		
		SMARTSAtom qAtom0 = (SMARTSAtom)recQuery.getAtom(0);
		SMARTSAtom qAtom1 = (SMARTSAtom)recQuery.getAtom(1);
		SMARTSBond qBond = (SMARTSBond)recQuery.getBond(0);
		
		for (int i = 0; i < target.getAtomCount(); i++)
		{	
			IAtom at = target.getAtom(i);
			if (qAtom0.matches(at))
			{	
				List ca = target.getConnectedAtomsList(at);
				for (int j = 0; j < ca.size(); j++)
					if (qAtom1.matches((IAtom)ca.get(j)))
					{
						IBond bo = target.getBond(at,(IAtom)ca.get(j));
						if (bo != null)
							if (qBond.matches(bo))
							{	
								atomMaps.add(at);
								//It is not needed to register all maps corresponding to a same
								//target atom. Hence j-cycle is breaked
								break; //for j-cycle
							}	
					}
			}	
		}	
		return(atomMaps);
	}
	
	
	public Vector<IAtom> getAtomMapsFromBondMaps(List bondMapping, IAtomContainer target, IAtomContainer recQuery)
	{
		//The query must contain  at least 3 atoms and 2 bonds.
		//The first bonds is alway 0-1
		//Two cases are considered for the second bond:
		//  a0-a1-a2     (e.g. CCC)
		//  a0(-a1)-a2   (e.g. C(C)C)
		//So the common atom  of bonds 0 and 1 is either atom 1 or atom 0
		//The latter assumptions are based on the way recursive SMARTS are parsed
		Vector<IAtom> atomMaps  = new Vector<IAtom>();
		if (recQuery.getBondCount() < 2)
			return(atomMaps);
		
		SMARTSBond qBond0 = (SMARTSBond)recQuery.getBond(0);
		SMARTSBond qBond1 = (SMARTSBond)recQuery.getBond(1);
		int qID0 = recQuery.getBondNumber(qBond0);
		int qID1 = recQuery.getBondNumber(qBond1);
		SMARTSAtom qAtom0 = (SMARTSAtom)recQuery.getAtom(0);
		SMARTSAtom qAtom1 = (SMARTSAtom)recQuery.getAtom(1);
		IAtom commonQAt = getBondsCommonAtom(qBond0, qBond1);
		
		if ((commonQAt != qAtom0)&&(commonQAt != qAtom1))
			return(atomMaps); //This should never happen		
		IBond tBond0 = null;
		IBond tBond1 = null;		
		IAtom commonTAt;
		RMap map;
		List list;		
		for (Object aBondMapping : bondMapping) 
		{
			list = (List) aBondMapping;
			//Search RMaps to find the corresponding target bonds (qBond0<-->tBond0, qBond1<-->tBond1)
			//Note: map.getId2 corresponds to the query; map.getId1 corresponds to the target 
			int found = 0;
			for (Object aList : list) 
			{
				map = (RMap) aList;
				if (map.getId2() == qID0 )
				{
					found++;
					tBond0 = target.getBond(map.getId1());
				}
				if (map.getId2() == qID1 )
				{
					found++;
					tBond1 = target.getBond(map.getId1());
				}
				if (found >= 2)
					break;
			}
			if (found < 2) //This should never happen
				continue;
			
			commonTAt = getBondsCommonAtom(tBond0, tBond1);
			if (commonQAt == qAtom0)			
				atomMaps.add(commonTAt);			
			else
			{  
				//The common atom of bond 0 and bond 1 is atom 1
				//So the other atom (not commonTAt) must be added to atomMaps 
				if (commonTAt == tBond0.getAtom(0))
					atomMaps.add(tBond0.getAtom(1));
				else
					atomMaps.add(tBond0.getAtom(0));
			}				
		}				
		return(atomMaps);
	}
	
	
	public IAtom getBondsCommonAtom(IBond b1, IBond b2)
	{
		if (b1.contains(b2.getAtom(0)))
			return(b2.getAtom(0));
		else
			if (b1.contains(b2.getAtom(1)))
				return(b2.getAtom(1));
		return(null);
	}
	
	//------------------------------- some tests -------------------
	
	public void testCombinations()
	{
		recAtomNumSubSmarts = new int[] {3,4,5};
		curComb = new int[recAtomNumSubSmarts.length];
		firstComb();
		while (curComb != null)
		{	
			for(int i=curComb.length-1; i >= 0; i--)
				System.out.print(curComb[i]+" ");
			System.out.println();
			incComb(0);
		}
	}
	
	public void testTopLayers()
	{
		for (int i = 0; i < topLayers.length; i++)
		{
			TopLayer tl = topLayers[i];
			System.out.print(" " + " : ");
			for (int j = 0; j < tl.atoms.size(); j++)
				System.out.print(SmartsHelper.atomToString((SMARTSAtom)tl.atoms.get(j))+
				" " + ((IBond)tl.bonds.get(j)).getOrder()+ "   ");
			System.out.println();
		}
	}
	
	public void testSubQueries()
	{
		SmartsHelper smwriter = new SmartsHelper();
		for (int i = 0; i < subQueryList.size(); i++)
		{
			System.out.println("subQuery " + i + "  " + 
					smwriter.toSmarts(subQueryList.get(i)));
		}
	}
	
}







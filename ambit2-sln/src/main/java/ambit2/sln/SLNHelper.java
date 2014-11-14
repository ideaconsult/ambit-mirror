package ambit2.sln;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

import ambit2.smarts.TopLayer;



public class SLNHelper 
{	
	public boolean FlagPreserveOriginalAtomID = true;
	//public boolean FlagRingColusuresBeforeBrunches = true;
	
	class AtomSLNNode
	{
		public IAtom parent;
		public IAtom atom;
	}
	
	private HashMap<IAtom,AtomSLNNode> nodes = new HashMap<IAtom,AtomSLNNode>();
	private HashMap<IAtom,TopLayer> firstSphere = new HashMap<IAtom,TopLayer>();
	List<IBond> ringClosures = new ArrayList<IBond>();
	private HashMap<IAtom,Integer> newAtomIDs = new HashMap<IAtom,Integer>();
	private int maxAtomID = -1;
	private int nAtom;
	private int nBond;
		
	static public String getAtomsAttributes(SLNContainer container)
	{
		StringBuffer sb = new StringBuffer();	
		
		for (int i = 0; i < container.getAtomCount(); i++)
		{	
			SLNAtom at = (SLNAtom)container.getAtom(i); 
			sb.append("  #" + i + "  ");
			sb.append(at.atomType + "  " + at.atomName + "  H" + at.numHAtom + "  " + at.toString());
			//TODO print atom attributes 
			sb.append("\n");
		}	
		return(sb.toString());
	}
	
	static public String getBondsAttributes(SLNContainer container)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < container.getBondCount(); i++)
		{
			SLNBond bo = (SLNBond)container.getBond(i);  
			SLNAtom at0 = (SLNAtom)bo.getAtom(0);
			SLNAtom at1 = (SLNAtom)bo.getAtom(1);
			int at0_num = container.getAtomNumber(at0);
			int at1_num = container.getAtomNumber(at1);
			sb.append("  #" + i + " atoms (" + at0_num + "," + at1_num + ") order "  + bo.toString());

			sb.append("\n");
		}
		return(sb.toString());
	}
	
	static public String getMolAttributes(SLNContainer container)
	{
		SLNContainerAttributes attr = container.getAttributes();
		if (attr.getNumOfAttributes() == 0)
			return "";
		
		StringBuffer sb = new StringBuffer();
		if (attr.name != null)
			sb.append("   name = " + attr.name + "\n");
		if (attr.regid != null)
			sb.append("   regid = " + attr.regid + "\n");
		if (attr.type != null)
			sb.append("   type = " + attr.type + "\n");
		
		Set<String> keys = attr.userDefiendAttr.keySet();
		for (String key : keys)
		{	
			sb.append("   " + key);
			String value = attr.userDefiendAttr.get(key);
			if (value != null)
				sb.append(" = " + value);
			sb.append("\n");
		}	
		return(sb.toString());
	}
	
	static public void clearAtomIDs(SLNContainer container)
	{
		for (IAtom at : container.atoms())
		{
			SLNAtom a = (SLNAtom)at;
			if (a.atomID != -1)
			{
				a.atomID = -1;
				if (a.atomExpression != null)
					a.atomExpression.atomID = -1;
			}
		}
	}
	
	static public void setAtomID(SLNAtom at, int ID)
	{
		at.atomID = ID;
		if (at.atomExpression != null)
			at.atomExpression.atomID = ID;
	}
	
	private void analyzeAtomIDs(SLNContainer container)
	{
		//Determine maxAtomID 
		maxAtomID = -1;
		int curID;
		for (IAtom at : container.atoms())
		{	
			curID = ((SLNAtom)at).atomID;
			if (maxAtomID < curID)
				maxAtomID = curID;
		}
		
		if (maxAtomID == -1)
			maxAtomID = 0;
	}
	
	public String toSLN(SLNContainer container)
	{	
		determineFirstSheres(container);
		nodes.clear();
		ringClosures.clear();
		newAtomIDs.clear();
		maxAtomID = -1;
		
		if (FlagPreserveOriginalAtomID)
			analyzeAtomIDs(container);
		else
		{	
			clearAtomIDs(container);
			maxAtomID = 0;
		}	
		
		AtomSLNNode node = new AtomSLNNode();
		node.parent = null;
		node.atom = container.getAtom(0);
		nodes.put(node.atom, node);
		String attr = container.getAttributes().toString();
		return(nodeToString(node.atom) + attr);
	}
	
	String nodeToString(IAtom atom)
	{
		//System.out.println("-->nodeToString for atom: " + curSLNContainer.getAtomNumber(atom));
		
		StringBuffer sb = new StringBuffer();
		TopLayer afs = firstSphere.get(atom);
		AtomSLNNode curNode = nodes.get(atom);
		List<String> branches = new ArrayList<String>();
		List<String> closureStrings = new ArrayList<String>();
		
		for (int i=0; i<afs.atoms.size(); i++)
		{
			IAtom neighborAt = afs.atoms.get(i);
			if (neighborAt == curNode.parent)
				continue;
			
			AtomSLNNode neighborNode = nodes.get(neighborAt);
			if (neighborNode == null) // This node has not been registered yet
			{
				//Registering a new Node and a new branch
				AtomSLNNode newNode = new AtomSLNNode();
				newNode.atom = neighborAt;
				newNode.parent = atom;
				nodes.put(newNode.atom, newNode); 
				
				String bond_str = afs.bonds.get(i).toString();				
				String newBranch = bond_str + nodeToString(neighborAt);
				branches.add(newBranch);
			}
			else
			{	
				IBond neighborBo = afs.bonds.get(i);
				//This check is needed. Otherwise the ring closure will be described twice (for both atoms of the bond)
				if (ringClosures.contains(neighborBo))  
					continue;
				
				ringClosures.add(neighborBo);
				
				//Handle a new ring closure
				//Set the ID of the first atom from the ring closure
				//Because of the recursion approach the atoms are not yet added converted to strings
				int neighID  = ((SLNAtom)neighborAt).atomID;
				if (neighID == -1) //The neighbor atom does not have ID 
				{
					//A new atom ID is generated
					maxAtomID++;
					neighID = maxAtomID;
					setAtomID((SLNAtom)neighborAt, neighID);
				}
				String newClosure = afs.bonds.get(i).toString() + "@" + neighID;
				closureStrings.add(newClosure);
				
				//System.out.println("  #add ring closure: atom " + curSLNContainer.getAtomNumber(atom) + " to atom " 
				//		+ curSLNContainer.getAtomNumber(neighborAt) + " with ID " + neighID);
			}
		}
		
		//Add atom from the current node
		sb.append(atom.toString());
		
		//Add ring closures
		for (int i = 0; i < closureStrings.size(); i++)
			sb.append(closureStrings.get(i));
		
		//Add branches
		if (branches.size() > 0)
		{	
			for(int i = 0; i < branches.size()-1; i++)
				sb.append("("+branches.get(i).toString()+")");

			sb.append(branches.get(branches.size() - 1).toString());
		}
		return(sb.toString());
	}
	
	void determineFirstSheres(SLNContainer container)
	{
		firstSphere.clear();
		nAtom =  container.getAtomCount();
		nBond =  container.getBondCount();
		
		for (int i = 0; i < nAtom; i++)
		{				
			firstSphere.put(container.getAtom(i), new TopLayer());
		}	
			
		for (int i = 0; i < nBond; i++)
		{
			IBond bond = container.getBond(i);
			IAtom at0 = bond.getAtom(0);
			IAtom at1 = bond.getAtom(1);
			firstSphere.get(at0).atoms.add(at1);
			firstSphere.get(at0).bonds.add(bond);
			firstSphere.get(at1).atoms.add(at0);
			firstSphere.get(at1).bonds.add(bond);			
		}
	}
	
}

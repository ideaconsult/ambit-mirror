package ambit2.structure2name;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;

import ambit2.structure2name.components.AcyclicComponent;
import ambit2.structure2name.components.ComponentConnection;
import ambit2.structure2name.components.CyclicComponent;
import ambit2.structure2name.components.IIUPACComponent;
import ambit2.structure2name.rules.IUPACRuleDataBase;

public class IUPACNameGenerator 
{	
	protected IUPACRuleDataBase ruleDataBase = null;
	
	protected IAtomContainer originalMolecule = null;
	protected IAtomContainer molecule = null;
	protected int cyclomaticNum = 0;
	protected Cycles cycles = null;
	protected IRingSet ringSet = null;
	protected Map<IAtom,int[]> atomRingNumbers = new HashMap<IAtom,int[]>(); 
	protected List<IAtom> spiroAtoms = new ArrayList<IAtom>();
	protected List<IAtom> acyclicAtoms = new ArrayList<IAtom>();
	protected List<IAtom> cyclicAtoms = new ArrayList<IAtom>();
		
	//protected List<IIUPACComponent> initialComponents = new ArrayList<IIUPACComponent>();
	protected List<CyclicComponent> cyclicComponents = new ArrayList<CyclicComponent>();
	protected List<AcyclicComponent> acyclicComponents = new ArrayList<AcyclicComponent>();
	protected List<IIUPACComponent> components = new ArrayList<IIUPACComponent>();
	protected List<ComponentConnection> connections = new ArrayList<ComponentConnection>();
	
	public IUPACNameGenerator() throws Exception
	{
		ruleDataBase = IUPACRuleDataBase.getDefaultRuleDataBase();
	}
	
	public IUPACRuleDataBase getRuleDataBase() {
		return ruleDataBase;
	}
	
	public String generateIUPACName(IAtomContainer mol) throws Exception
	{	
		originalMolecule = mol;
		molecule = mol;
		nullify();
		init();
		
		generateComponents();
		
		IIUPACComponent mainComp = getBestRankComponent();
		
		String iupac = ""; //mainComp.getMainToken();
		
		return iupac;
	}
	
	protected void nullify()
	{
		//initialComponents.clear();
		cyclicComponents.clear();
		acyclicComponents.clear();
		components.clear();
		connections.clear();
		cycles = null;
		ringSet = null;
		atomRingNumbers.clear();
		spiroAtoms.clear();
		acyclicAtoms.clear();
		cyclicAtoms.clear();
	}
	
	protected void init()
	{
		cyclomaticNum = molecule.getBondCount() - molecule.getAtomCount() + 1;
		
		//Prepare ring data
		if (cyclomaticNum > 0)
		{
			cycles = Cycles.sssr(molecule);
			ringSet = cycles.toRingSet();
			makeRindData();
			findSpiroAtoms();
		}
	}
	
	protected void makeRindData() 
	{
		IRingSet atomRings;
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			IAtom atom = molecule.getAtom(i);
			atomRings = ringSet.getRings(atom);
			int n = atomRings.getAtomContainerCount();
			if (n > 0) 
			{	
				int ringNumbers[] = new int[n];
				for (int k = 0; k < n; k++)
					ringNumbers[k] = getRingNumber(atomRings.getAtomContainer(k));
				atomRingNumbers.put(atom,ringNumbers);
				cyclicAtoms.add(atom);
			}
			else
				acyclicAtoms.add(atom);
		}
	}
	
	protected void findSpiroAtoms()
	{
		for (IAtom at: molecule.atoms())
		{
			int ringNums[] = atomRingNumbers.get(at);
			if (ringNums == null)
				continue;
			if (ringNums.length != 2)
				continue;
						
			if (molecule.getConnectedBondsCount(at) == 4)
			{	
				//This check is correct only for molecules with implicit hydrogens
				spiroAtoms.add(at);
			}	
		}
	}
	
	protected int getRingNumber(IAtomContainer ring) 
	{
		for (int i = 0; i < ringSet.getAtomContainerCount(); i++) {
			if (ring == ringSet.getAtomContainer(i))
				return (i);
		}
		return (-1);
	}
	
	protected void generateComponents()
	{		
		if (cyclomaticNum == 0)
		{
			//This is an acyclic molecule
			AcyclicComponent acomp = new AcyclicComponent();
			List<IAtom> atoms = new ArrayList<IAtom>();
			for (IAtom a : molecule.atoms())
				atoms.add(a);
			acomp.setAtoms(atoms);
			acyclicComponents.add(acomp);
		}
		else
		{
			//The molecule contains at least one cycle
			findCyclicAndAcyclicComponets();			
		}
		
		processAcyclicComponets();
		
		//makeComponentLogicalRelations();
	}

	protected void findCyclicAndAcyclicComponets()
	{
		//Generate cyclic components
		for (int i = 0; i < ringSet.getAtomContainerCount(); i++)
		{
			IAtomContainer ring = ringSet.getAtomContainer(i);
			CyclicComponent comp = getCyclicComponentFusedToRing(ring);
			if (comp == null)
			{	
				comp = new CyclicComponent();
				cyclicComponents.add(comp);
			}	
			comp.ringNumbers.add(i);
		}
		
		//Fill cyclic components atoms
		for (CyclicComponent c : cyclicComponents) 
		{
			List<IAtom> atoms = new ArrayList<IAtom>();
			for (int k : c.ringNumbers)
			{
				IAtomContainer ring = ringSet.getAtomContainer(k);
				for (IAtom at : ring.atoms())
				{
					int ringNums[] = atomRingNumbers.get(at);
					if (ringNums.length == 1)
						atoms.add(at);
					else
					{
						if (!atoms.contains(at))
							atoms.add(at);
					}
				}
			}
			c.setAtoms(atoms);
		}
		
		//Handle spiro connected rings !!!
		//Use preliminary detected spiro atoms 
		//TODO
		
		//Generate acyclic components		
		for (CyclicComponent c : cyclicComponents) 
		{
			List<IAtom> atoms = c.getAtoms();
			for (IAtom at : atoms)
			{
				//Searching for ring substituents
				List<IAtom> conAtoms = molecule.getConnectedAtomsList(at);
				for (IAtom conAt : conAtoms)
				{
					if (atoms.contains(conAt))
						continue;
					
					if (acyclicAtoms.contains(conAt))					
					{
						//Detect a new acyclic component
						//Mark acyclic atoms as scanned
						//TODO
					}
					else
					{
						//conAt is part of another cyclic system
						CyclicComponent c0 = getCyclicComponentForAtom(conAt);
						
						//Check for connection duplication 
						ComponentConnection con = getConnection(c,c0);
						if (con != null)
							continue;
						
						//registering new connection
						con = new ComponentConnection();
						con.components[0] = c0;
						con.components[1] = c;
						con.componentAtoms[0] = conAt;
						con.componentAtoms[1] = at;
						IBond bo = molecule.getBond(at, conAt);
						con.connectionBondOrder = bo.getOrder();						
						connections.add(con); 
					}
					
				}
			}
			
		}
		
		//Check for component connection anomalies (sophisticated ring systems??)
		//TODO
	}
	
	
	
	protected CyclicComponent getCyclicComponentFusedToRing(IAtomContainer ring)
	{
		for (CyclicComponent c : cyclicComponents) 
		{
			for (IAtom atom : ring.atoms())
			{	
				int ringNums[] = atomRingNumbers.get(atom);
				for (int i = 0; i < ringNums.length; i++)
					if (c.ringNumbers.contains(ringNums[i]))
						return c; //c is fused to ring
			}	
		}
		return null;
	}
	
	protected CyclicComponent getCyclicComponentForAtom(IAtom at)
	{
		for (CyclicComponent c : cyclicComponents) 
		{
			if (c.getAtoms().contains(at))
				return c;
		}
		return null;
	}
	
	
	protected ComponentConnection getConnection(IIUPACComponent c0, IIUPACComponent c1)
	{
		for (ComponentConnection con: connections)
		{
			if ((c0 == con.components[0] && c1 == con.components[1])
				||(c0 == con.components[1] && c1 == con.components[0]))
				return con;
		}
		return null;
	}
	
	protected void processAcyclicComponets()
	{
		
		//TODO
	}
	
	/*
	protected void makeComponentLogicalRelations()
	{
		//TODO
	}
	*/
	
	protected IIUPACComponent getBestRankComponent()
	{
		if (components.isEmpty())
			return null;
		
		double maxRank = components.get(0).getRank();
		int maxIndex = 0;
		
		for (int i = 1; i < components.size(); i++) 
		{
			if (components.get(i).getRank() > maxRank)
			{
				maxRank = components.get(i).getRank();
				maxIndex = i;
			}
		}
		
		return components.get(maxIndex);
	}
	
}

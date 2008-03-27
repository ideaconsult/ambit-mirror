/**
 * Created on 2004-11-11
 *
 */
package ambit2.data.molecule;

import java.util.Iterator;
import java.util.Map;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.graph.SpanningTree;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.MFAnalyser;

import ambit2.benchmark.Benchmark;

/**
 * @deprecated
 * Performs some analysis of the structure.  
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class MoleculeConvertor   {
	private SpanningTree spanningTree = null;
	private IAtomContainer molecule = null;
	
	private SmilesGenerator gen = null;
	private AllRingsFinder arf = null;
	private IRingSet rings = null;
	private float molMass = 0;
	private String formula = "";
	
	static int atomsListN = 12;
	static String[] atomsID = {"C","O","N","S","P","Cl","F","Br","I","Si","B","aa"};
	private int[] atomsNo = {0,0,0,0,0,0,0,0,0,0,0,0};
	static int bondTypeN = 7;
	static String[] bondsID = {"b1","bar","b2","b3","ab","cb","st"};
	private int[] bondsTypeNo = {0,0,0,0,0,0,0};
	static int atomsOrderNo = 5;
	static String[] atomsOrderID = {"o1","o2","o3","o4","oo"};
	private int[] atomsOrder = {0,0,0,0,0};
	private boolean disconnected = false;
	
	private long minTime, maxTime, meanTime = 0;
	private boolean firstTime = true;
	private long processed = 0;
	private Benchmark b;
	/**
	 * 
	 */
	public MoleculeConvertor() {
		super();
		b = new Benchmark();

	}
	public IAtomContainer getMolecule() {
		return molecule;
	}
	
	public String getFormula() {
		return formula;
	}
	
	public String gamutToSubSearchString() {
		return gamutToString("<=");
	}
	public String gamutToEqualString() {
		return gamutToString("=");
	}	
	private String gamutToString(String condition) {
		if (molecule == null) return "";
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < atomsListN; i++) {
			
			b.append(atomsID[i]);
			b.append(condition);
			b.append(atomsNo[i]);
			b.append(" and ");
		}
		for (int i = 0; i < bondTypeN; i++) {
			//if (i > 0) b.append(" and ");			
			b.append(bondsID[i] + condition + bondsTypeNo[i]);
			b.append(" and ");
		}
		for (int i = 0; i < atomsOrderNo; i++) {
			if (i > 0) b.append(" and ");			
			b.append(atomsOrderID[i] + condition + atomsOrder[i]);
		}		
		/*
		b.append("and (rno="+getRingsCount()); 
		b.append(") and (rmax= "+getLargestRingsSize()+")"); 		
		*/
		return b.toString();
	}
	
	private void atomOrder(IAtomContainer ac) {
		Iterator<IBond> bonds = ac.bonds();
		IAtom[] atoms = null;
		int acount = ac.getAtomCount();
		int[] aorder = new int[acount];
		for (int i = 0; i < acount; i++) aorder[i] = 0;
		while (bonds.hasNext()) {
			IBond bond = bonds.next();
			for (int j = 0; j < bond.getAtomCount(); j++) {
				int n = ac.getAtomNumber(bond.getAtom(j));
				aorder[n] ++;
			}	
		}
		for (int i=0; i < 5; i++) atomsOrder[i] = 0;
		
		for (int j = 0; j < acount; j++)  {
			for (int i=4; i >= 0; i--)
				if (aorder[j] >= (i+1)) {
  				   atomsOrder[i]++;
				   break;
				}
		}	
		
	    //for (int i=0; i < 5; i++)
//	    	System.out.print("["+(i+1)+"] "+atomsOrder[i]+"\t");
		//System.out.println();
	}
	public void createGamut() throws NoSuchAtomException {
		if (molecule == null) return;
		MFAnalyser mfa =new MFAnalyser(molecule);
		IAtomContainer ac;
		try {
			ac = mfa.removeHydrogensPreserveMultiplyBonded();
		} catch (Exception e) {
			//TODO if no Hydrogens - NullPointerException
			e.printStackTrace();
			ac = molecule;
		}
		Map<String,Integer> ht = mfa.getFormulaHashtable();
		ht.remove("H");
		Integer c;
		String element;
		for (int i =0; i < (atomsListN-1); i++) {
			c = (Integer) ht.remove(atomsID[i]);
			if (c != null) {
				atomsNo[i] = c.intValue();
				
			}
		}
		//Iterator<String> elements = ht.keySet().iterator();
		
		atomOrder(ac);
		
  	    atomsNo[atomsListN-1] = ac.getAtomCount();        		   
		ht = null;
		mfa = null;
		Iterator<IBond> bonds = ac.bonds();
		Order order;
		while (bonds.hasNext()) {
			IBond bond = bonds.next();
			order = bond.getOrder();
			if (order == Order.SINGLE) bondsTypeNo[0]++;
			else if (order == Order.DOUBLE) bondsTypeNo[2]++;
			else if (order == Order.TRIPLE) bondsTypeNo[3]++;
			bondsTypeNo[4]++;
			if (bond.getFlag(CDKConstants.ISAROMATIC)) bondsTypeNo[1]++;			
		}
		//if (spanningTree == null)
			spanningTree = new SpanningTree(ac);
		//spanningTree.buildSpanningTree();
		disconnected = spanningTree.isDisconnected();
		
		/*
		try {
			spanningTree.identifyBonds();
		} catch (NoSuchAtomException e) {
			e.printStackTrace();
		}
		*/
		bondsTypeNo[5] = spanningTree.getBondsCyclicCount();		
		bondsTypeNo[6] = spanningTree.getSpanningTreeSize();
		//spanningTree.clear();
	}
	public String createSMILES() {
		if (molecule == null) return "";
		
		if (gen == null)
			gen = new SmilesGenerator();

		molecule.setProperty("TIME_ELAPSED",Long.toString(0));		
		b.clear();
		b.mark();
      	MFAnalyser mfa;
      	IAtomContainer ac;
      	String smiles = "";
      	try {
      		mfa = new MFAnalyser(molecule);
      		ac = mfa.removeHydrogensPreserveMultiplyBonded();
      		smiles = gen.createSMILES((IMolecule) ac).toString();


      	} catch (Exception e) {
      		smiles = "defaultError";
      		System.out.println("molecule ID = " +molecule.getProperty("PRODUCER").toString());
      		e.printStackTrace();
      	}
		b.record();
		
		molecule.setProperty("SMILES",smiles); 
		molecule.setProperty("TIME_ELAPSED",Long.toString(b.getElapsedTime()));
		updateStatistics(b.getElapsedTime());
		return smiles;
	}
	
	public void countRings() throws Exception {
		if (molecule == null) return;
		if (arf == null) 
			arf = new AllRingsFinder();
 
		rings = DefaultChemObjectBuilder.getInstance().newRingSet();
		rings = arf.findAllRings(molecule);
			
	}
	
	public int getRingsCount() {
		if (rings == null) return 0;
		else return rings.getAtomContainerCount();
	}
	public int getLargestRingsSize() {
		
		int ringsNo = getRingsCount();
		if (ringsNo > 0) {
			AtomContainer cc = (AtomContainer) rings.getAtomContainer(rings.getAtomContainerCount()-1);
			return cc.getAtomCount();
		} return 0;
		
	}
	public void setMolecule(IAtomContainer mol) {
		this.molecule = mol;
		if (spanningTree != null) spanningTree = null;
		disconnected = false;
		rings = DefaultChemObjectBuilder.getInstance().newRingSet();
		for (int i = 0; i < atomsListN; i++) atomsNo[i] = 0;
		for (int i = 0; i < bondTypeN; i++) bondsTypeNo[i] = 0;		
		if (mol == null) {
			formula = "";
			molMass = 0;
		} else {
			MFAnalyser mfa = new MFAnalyser(molecule);
			formula = mfa.getMolecularFormula();
			molMass = mfa.getMass();
			}
	}
	
	private void updateStatistics(long time_elapsed) {
		if (firstTime) {
			firstTime = false;
			minTime = time_elapsed;
			maxTime = time_elapsed;
			meanTime = time_elapsed;			
		} else {
			if (minTime > time_elapsed) minTime = time_elapsed;
			if (maxTime < time_elapsed) maxTime = time_elapsed;
			meanTime += time_elapsed;			
		}
		processed++;
	}
	public void printStatisticsTitle() {
		System.out.println("Processed\tMin time(ms)\tMax time(ms)\tMean time(ms)\tTotal\tFree");		
	}
	
	public void printStatistics() {
		if (processed > 0)
		System.out.print(processed+"\t"+ minTime + "\t"+maxTime + "\t" + meanTime / processed + "\t");
		System.out.println(Runtime.getRuntime().totalMemory() + "\t"+ Runtime.getRuntime().freeMemory());
	}
	/**
	 * @return Returns the molMass.
	 */
	public float getMolMass() {
		return molMass;
	}
	/**
	 * @return Returns the atomsNo.
	 */
	public int[] getAtomsNo() {
		return atomsNo;
	}
	/**
	 * @return Returns the bondsOrderNo.
	 */
	public int[] getBondsTypeNo() {
		return bondsTypeNo;
	}
	/**
	 * @return Returns the atomsID.
	 */
	public static String[] getAtomsID() {
		return atomsID;
	}
	/**
	 * @return Returns the bondsOrder.
	 */
	public static String[] getBondsType() {
		return bondsID;
	}

	/**
	 * @return Returns the atomsOrderID.
	 */
	public static String[] getAtomsOrderID() {
		return atomsOrderID;
	}
	/**
	 * @return Returns the atomsOrder.
	 */
	public int[] getAtomsOrder() {
		return atomsOrder;
	}
	public int getCyclicBonds() {
		return bondsTypeNo[5];
	}
	public boolean isDisconnected() {
		return disconnected;
	}
	public void setDisconnected(boolean disconnected) {
		this.disconnected = disconnected;
	}
	public static int getCyclicBondsBySSSR(IMolecule mol) {
	    SSSRFinder sssr = new SSSRFinder(mol);
	    IRingSet rs = sssr.findSSSR();
	    for (int i =0; i < mol.getBondCount(); i++)  
	        mol.getBond(i).setFlag(CDKConstants.VISITED,false);
	    
	    for (int i=0; i < rs.getAtomContainerCount(); i++) {
	        IRing ring = (IRing) rs.getAtomContainer(i);
	        for (int j=0; j < ring.getBondCount(); j++) {
	            //this works if the ring contains a reference and not a copy of a Bond object
	            ring.getBond(j).setFlag(CDKConstants.VISITED,true);
	        }    
	    }
	    int cb = 0; //cyclic bonds count
	    for (int i =0; i < mol.getBondCount(); i++)  
	        if (mol.getBond(i).getFlag(CDKConstants.VISITED)) cb++;
	    
	    return cb;

	}
	public static void main(String[] args) {
		//TODO to make test instead of main method
		try {
			IMolecule mol = MoleculeFactory.make4x3CondensedRings();
			MoleculeConvertor mc = new MoleculeConvertor();
			mc.setMolecule(mol);
			mc.createGamut();
			System.out.println(mc.gamutToEqualString());
			System.out.println(mc.gamutToSubSearchString());
			
			mol = MoleculeFactory.makeBranchedAliphatic();
			mc.setMolecule(mol);
			mc.createGamut();
			System.out.println(mc.gamutToEqualString());
			System.out.println(mc.gamutToSubSearchString());
			
			mol = null;
			mc = null;
		} catch (Exception x) {
			System.out.println(x);
		}
	}	
	
}	
/**
 * <b>Filename</b> AtomEnvironmentDescriptorTest.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-6-15
 * <b>Project</b> ambit
 */
package ambit.test.data.descriptors;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import junit.framework.TestCase;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.atomtype.HybridizationStateATMatcher;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.CDKSourceCodeWriter;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.HydrogenAdder;

import ambit.data.descriptors.AtomEnvironment;
import ambit.data.descriptors.AtomEnvironmentDescriptor;
import ambit.data.descriptors.AtomEnvironmentList;
import ambit.data.molecule.IntArrayResult;
import ambit.misc.AmbitCONSTANTS;
import ambit.processors.structure.AtomEnvironmentGenerator;
import ambit.similarity.AtomEnvironmentsDistance;
import ambit.ui.data.descriptors.AtomEnvironmentListTableModel;
import ambit.ui.data.molecule.Panel2D;

/**
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-6-15
 */
/**
 * i have a problem with HybridizationStateATMatcher. 
findMatchingAtomType method doesn't recognise some atom types defined in /cdk/config/data/hybridization_atomtypes.xml

i think this is due to cdk:formalNeighbourCount entry missing for some of the types and therefore zero neighbors are assumed.
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-6-16
 */
public class AtomEnvironmentDescriptorTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AtomEnvironmentDescriptorTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for AtomEnvironmentDescriptorTest.
     * @param arg0
     */
    public AtomEnvironmentDescriptorTest(String arg0) {
        super(arg0);
    }
    protected IMolecule getMol() {
            IMolecule mol = new org.openscience.cdk.Molecule();
            IAtom a1 = new org.openscience.cdk.Atom("C");
            mol.addAtom(a1);
            IAtom a2 = new org.openscience.cdk.Atom("C");
            mol.addAtom(a2);
            IAtom a3 = new org.openscience.cdk.Atom("O");
            mol.addAtom(a3);
            IAtom a4 = new org.openscience.cdk.Atom("O");
            mol.addAtom(a4);
            IAtom a5 = new org.openscience.cdk.Atom("H");
            mol.addAtom(a5);
            IAtom a6 = new org.openscience.cdk.Atom("H");
            mol.addAtom(a6);
            IAtom a7 = new org.openscience.cdk.Atom("H");
            mol.addAtom(a7);
            IAtom a8 = new org.openscience.cdk.Atom("H");
            mol.addAtom(a8);
            IBond b1 = new org.openscience.cdk.Bond(a2, a1, 1.0);
            mol.addBond(b1);
            IBond b2 = new org.openscience.cdk.Bond(a3, a2, 1.0);
            mol.addBond(b2);
            IBond b3 = new org.openscience.cdk.Bond(a4, a2, 2.0);
            mol.addBond(b3);
            IBond b4 = new org.openscience.cdk.Bond(a1, a5, 1.0);
            mol.addBond(b4);
            IBond b5 = new org.openscience.cdk.Bond(a1, a6, 1.0);
            mol.addBond(b5);
            Bond b6 = new org.openscience.cdk.Bond(a1, a7, 1.0);
            mol.addBond(b6);
            Bond b7 = new org.openscience.cdk.Bond(a3, a8, 1.0);
            mol.addBond(b7);
            return mol;
    }
    public void testHybridizationStateATMatcher() {
	    IAtomContainer mol = getMol();
	    HybridizationStateATMatcher h = new HybridizationStateATMatcher();
		for (int i = 0; i < mol.getAtomCount(); i++) {
		    try {
		        IAtomType a = h.findMatchingAtomType(mol,mol.getAtomAt(i));
		        assertTrue(a != null);
		    } catch (CDKException x) {
		        fail();
		        x.printStackTrace();
		    }
		}    
    }
    public void testAtomEnvironmentDescriptor() {
		IMolecularDescriptor descriptor = new AtomEnvironmentDescriptor();
		int maxLevel = 1;
		Object[] params = {null,new Integer(maxLevel),new Boolean(true),new Boolean(true)};

		try {
		    SmilesParser sp = new SmilesParser();
		    //AtomContainer mol = sp.parseSmiles("CCCCCC=CCC=CCCCCCCCC(=O)NCCO");
		    AtomContainer mol = sp.parseSmiles("CC(O)=O");
		    
		    //AtomContainer mol = sp.parseSmiles("N#CC(=Cc1ccc(O)c(O)c1)C(=O)NCCCNC(=O)C(C#N)=Cc2ccc(O)c(O)c2"); 
		    HydrogenAdder hAdder = new HydrogenAdder();
		    hAdder.addExplicitHydrogensToSatisfyValency((Molecule)mol);
		    
		    CDKSourceCodeWriter w = new CDKSourceCodeWriter(new FileOutputStream("logs/mol.logger"));
		    w.writeMolecule((Molecule) mol);
		    w.close();

			for (int i = 0; i < mol.getAtomCount(); i++) {
				//System.out.println(mol.getAtomAt(i));
				try {
					params[0] = new Integer(i);
				    descriptor.setParameters(params);
				} catch (CDKException x) {
				    x.printStackTrace();
				    fail();
				}		
			    IntArrayResult r = (IntArrayResult) descriptor.calculate(mol).getValue();
			    //System.out.println(r.size());
			    if (i==0) { // just for test
				    Object[] parameters = descriptor.getParameters();
				    AtomTypeFactory factory = (AtomTypeFactory)parameters[4];
	
					IAtomType[] at = factory.getAllAtomTypes();
			    	System.out.print("Sum\tAtomType\t");
					for (int j=0; j < factory.getSize(); j++) {
						System.out.print(j);
						System.out.print(".");
				    	System.out.print(at[j].getAtomTypeName());
				    	System.out.print("\t");
				    }			    
				    System.out.println("");
				    assertEquals(maxLevel * (factory.getSize()+1)+2,r.size());
				}    
			    System.out.println(r.toString());
		    }
		    
		} catch (Exception x) {
		    x.printStackTrace();
		    fail();
		}
    }
    /*
     * Test how AE differ by adding two methyl groups
     *
     */
    public void testCH3difference() {
		IMolecularDescriptor descriptor = new AtomEnvironmentDescriptor();
		int maxLevel = 3;
		Object[] params = {null,new Integer(maxLevel),new Boolean(true),new Boolean(true)};

		try {
		    SmilesParser sp = new SmilesParser();
		    
		    //IMolecule mol = sp.parseSmiles("O=N(=O)c1ccc(O)c(N)c1");
		    //Amino-2nitro-3.4-5 hydroxy-methyl benzene (ANMB) 
		    //IMolecule mol1 = sp.parseSmiles("Cc1c(C)c(cc(N)c1(O))N(=O)=O");
		    
		    IMolecule mol = sp.parseSmiles("Nc1c(C)cccc1");
		    //Amino-2nitro-3.4-5 hydroxy-methyl benzene (ANMB) 
		    IMolecule mol1 = sp.parseSmiles("Nc1ccccc1");

		    
		    //Amino-2nitro-3.4-5 hydroxy-methyl benzene (ANMB) 
		    

		    
		    AtomEnvironmentGenerator g = new AtomEnvironmentGenerator();
		    g.setNoDuplicates(false);
		    g.setUseHydrogens(true);
		    g.setMaxLevels(maxLevel);
		   
		    g.process(mol);
		    g.process(mol1);
		    
		    AtomEnvironmentList ae = (AtomEnvironmentList) mol.getProperty(AmbitCONSTANTS.AtomEnvironment);
		    AtomEnvironmentList ae1 = (AtomEnvironmentList) mol1.getProperty(AmbitCONSTANTS.AtomEnvironment);
		    
		    Collections.sort(ae);
		    Collections.sort(ae1);
		    
		    AtomEnvironmentsDistance d = new AtomEnvironmentsDistance();
		    System.out.println("distance " + d.getDistance(ae,ae1));
		    
		    /*
		    Set<AtomEnvironment> set = new TreeSet<AtomEnvironment>();
		    Set<AtomEnvironment> set1 = new TreeSet<AtomEnvironment>();
		    
		    for (int i=0; i < ae.size(); i++) set.add(ae.get(i));
		    for (int i=0; i < ae1.size(); i++) set1.add(ae1.get(i));
		    
		    System.out.println(set);
		    System.out.println();
		    System.out.println(set1);
		    
		    assertNotSame(set,set1);
		    Set intersection = (Set) ((TreeSet) set1).clone();
		    intersection.retainAll(set);
		    
		    System.out.println("Intersection\t"+intersection.size());
		    System.out.println(intersection);
		    
		    */
		    displayAE(mol,ae,mol1,ae1);
		    assertNotNull(ae);
		    assertNotNull(ae1);
		    
		    
		    
		} catch (Exception x) {
		    x.printStackTrace();
		    fail();
		}
    }
    
    public void  displayAE(IMolecule mol1, AtomEnvironmentList aelist1,IMolecule mol2, AtomEnvironmentList aelist2) {
    	
    	AEComparator aec = new AEComparator();
    	Collections.sort(aelist1,aec);
    	Collections.sort(aelist2,aec);
    	final AtomEnvironmentListTableModel m1 = new AtomEnvironmentListTableModel(aelist1);
    	final AtomEnvironmentListTableModel m2 = new AtomEnvironmentListTableModel(aelist2);
    	
    	final Panel2D p1 = new Panel2D(mol1); p1.setAtomContainer(mol1,true);
    	//p1.r2dm.setDrawNumbers(true);
    	//p1.r2dm.setHoverOverColor(Color.blue);
    	
    	p1.r2dm.setColorAtomsByType(true);
    	p1.r2dm.setHighlightedAtom(mol1.getAtomAt(0));
    	p1.r2dm.setShowAromaticity(true);
    	p1.setPreferredSize(new Dimension(100,100));
    	
    	final Panel2D p2 = new Panel2D(mol2); 
    	p2.setAtomContainer(mol2,true);
    	p2.r2dm.setDrawNumbers(true);
    	
		p2.setAtomContainer(mol2,true);
    	
    	p2.setPreferredSize(new Dimension(100,100));
    	
    	final JTable t1 = new JTable(m1);
    	t1.addMouseListener(new MouseAdapter() {
    		@Override
    		public void mouseClicked(MouseEvent e) {
    			int row = t1.rowAtPoint(e.getPoint());
    			int natom = ((Integer)m1.getValueAt(row, 0)).intValue()-1;
    			    			
    			p1.setHighlightedAtoms(new int[] {
    					natom }
    					);
    			p1.repaint();
    		};
    		
    	});
    	JTable t2 = new JTable(m2);
    	
    	JOptionPane.showMessageDialog(null,
    			new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
    					
    			new JSplitPane(JSplitPane.VERTICAL_SPLIT,
    					p1,
    					new JScrollPane(t1)
    			),
    			new JSplitPane(JSplitPane.VERTICAL_SPLIT,
    					p2,
    					new JScrollPane(t2,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS)
    			)
    			));
    }

}

class AEComparator implements Comparator<AtomEnvironment> {

	public int compare(AtomEnvironment o1, AtomEnvironment o2) {
		int r = o1.getAtomno() - o2.getAtomno();
		if (r == 0) return o1.getSublevel() - o2.getSublevel();
		else return r;
	}

	
}

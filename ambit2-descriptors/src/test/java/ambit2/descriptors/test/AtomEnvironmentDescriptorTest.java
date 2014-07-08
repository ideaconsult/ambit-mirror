/**
 * <b>Filename</b> AtomEnvironmentDescriptorTest.java 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Created</b> 2005-6-15
 * <b>Project</b> ambit
 */
package ambit2.descriptors.test;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.Bond;
import org.openscience.cdk.atomtype.SybylAtomTypeMatcher;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;

import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.smiles.SmilesParserWrapper;
import ambit2.descriptors.AtomEnvironmentDescriptor;

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
public class AtomEnvironmentDescriptorTest {

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
            IBond b1 = new org.openscience.cdk.Bond(a2, a1, IBond.Order.SINGLE);
            mol.addBond(b1);
            IBond b2 = new org.openscience.cdk.Bond(a3, a2, IBond.Order.SINGLE);
            mol.addBond(b2);
            IBond b3 = new org.openscience.cdk.Bond(a4, a2, IBond.Order.DOUBLE);
            mol.addBond(b3);
            IBond b4 = new org.openscience.cdk.Bond(a1, a5, IBond.Order.SINGLE);
            mol.addBond(b4);
            IBond b5 = new org.openscience.cdk.Bond(a1, a6, IBond.Order.SINGLE);
            mol.addBond(b5);
            Bond b6 = new org.openscience.cdk.Bond(a1, a7, IBond.Order.SINGLE);
            mol.addBond(b6);
            Bond b7 = new org.openscience.cdk.Bond(a3, a8, IBond.Order.SINGLE);
            mol.addBond(b7);
            return mol;
    }
    
    @Test
    public void testHybridizationStateATMatcher() throws Exception {
	    IAtomContainer mol = getMol();
	    SybylAtomTypeMatcher h = SybylAtomTypeMatcher.getInstance(SilentChemObjectBuilder.getInstance());
		for (int i = 0; i < mol.getAtomCount(); i++) {
		        IAtomType a = h.findMatchingAtomType(mol,mol.getAtom(i));
		        Assert.assertTrue(a != null);
		}    
    }
    @Test
    public void testAtomEnvironmentDescriptor() throws Exception {
    	AtomEnvironmentDescriptor descriptor = new AtomEnvironmentDescriptor();
		int maxLevel = 1;
		Object[] params = {new Integer(maxLevel)};

	    SmilesParserWrapper sp = SmilesParserWrapper.getInstance();
		    //IAtomContainer mol = sp.parseSmiles("CCCCCC=CCC=CCCCCCCCC(=O)NCCO");
		    IAtomContainer mol = sp.parseSmiles("C#N");
		    //IAtomContainer mol = sp.parseSmiles("CCCC(CCC(CCCC(CCCCCCC(CCCCCCC(CCC(CCCC(CCCCCCC(CCCCC)CCC)CCC(CC(CCCC(CCC(CCCC(CCCCCCC(CCCCC)CCC)CCC(CCCCC)CCC)CCCC(CCC(CCCC(CCCCCCC(CCCCC)CCC)CCC(CCCCC)CCC)CC)CCC)CCC(CCCCC)CCC)CCC)CCC)CCCC(CCC(CCCC(CCCCCCC(CCCCC)CCC)CCC(CCCCC)CCC)CC)CCC)CCC(CCCCC)CCCCC)CCC)CCC(CC(CCCC(CCC(CCCC(CCCCCCC(CCCCC)CCC)CCC(CCCCC)CCC)CCCC(CCC(CCCC(CCCCCCC(CCCCC)CCC)CCC(CCCCC)CCC)CC)CCC)CCC(CCCCC)CCC)CCC)CCC)CCCC(CCC(CCCC(CCCCCCC(CCCCC)CCC)CCC(CCCCC)CCC)CC)CCC)CCC(CCCCC)CCC");
		    AtomConfigurator typer = new AtomConfigurator();
		    typer.process(mol);
		    
		    //AtomContainer mol = sp.parseSmiles("N#CC(=Cc1ccc(O)c(O)c1)C(=O)NCCCNC(=O)C(C#N)=Cc2ccc(O)c(O)c2"); 
		    CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
		    hAdder.addImplicitHydrogens(mol);

		    descriptor.setParameters(params);
		    IntegerArrayResult r = (IntegerArrayResult) descriptor.calculate(mol).getValue();
		    
			for (int i = 0; i < mol.getAtomCount(); i++) {
				
			    if (i==0) { // just for test
				    Object[] parameters = descriptor.getParameters();
				    AtomTypeFactory factory = (AtomTypeFactory)parameters[AtomEnvironmentDescriptor._param.atomTypeFactory.ordinal()];
	
					IAtomType[] at = factory.getAllAtomTypes();
					/*
			    	System.out.print("Sum\tAtomType\t");
					for (int j=0; j < factory.getSize(); j++) {
						System.out.print(j);
						System.out.print(".");
				    	System.out.print(at[j].getAtomTypeName());
				    	System.out.print("\t");
				    }			  
				    */  
				   // System.out.println("");
				    Assert.assertTrue(factory.getSize()>0);
				   // System.out.println(r.toString());

				    Assert.assertEquals(mol.getAtomCount() * (maxLevel * (factory.getSize()+1)+2),r.length());
				}    
			    
		    }
		    
    }

}



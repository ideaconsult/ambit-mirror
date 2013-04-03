/* MengineCrashTest.java
 * Author: Nina Jeliazkova
 * Date: Mar 18, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.smi23d.test;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;

import ambit2.base.config.Preferences;
import ambit2.core.io.MyIteratingMDLReader;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.processors.structure.HydrogenAdderProcessor;
import ambit2.smi23d.ShellMengine;
import ambit2.smi23d.ShellSmi2SDF;

/**
 * Test for Mengine - moved here from Toxtree source code
 * @author nina
 *
 */
public class MengineCrashTest {
 
	protected static Logger logger = Logger.getLogger(MengineCrashTest.class.getName());
    protected ShellSmi2SDF smi2sdf;
    protected ShellMengine mengine;
    
    @Before
    public void setUp() throws Exception {
        smi2sdf = new ShellSmi2SDF();
        smi2sdf.setGenerateSmiles(true);
        smi2sdf.setDropHydrogens(false);
        mengine = new ShellMengine();   
        Logger tempLogger = logger;
        Level level = Level.ALL;
        while(tempLogger != null) {
           tempLogger.setLevel(level);
           for(Handler handler : tempLogger.getHandlers())
              handler.setLevel(level);
           tempLogger = tempLogger.getParent();
        }
    }

    @Test
    public void test1() throws  Exception {
        IAtomContainer a = getChemical("ambit2/data/mengine/problem-001.sdf");
        IAtomContainer b = getChemical("ambit2/data/mengine/problem-001-chemidplus.sdf");        
        isIsomorph(a,b);   
    }
    @Test
    public void test1_1() throws  Exception {
        IAtomContainer a = getChemical("ambit2/data/mengine/problem-001.sdf");
        IAtomContainer b = getChemical("ambit2/data/mengine/smi2sdf_generated.sdf");        
        isIsomorph(a,b);   
    }    
    @Test
    public void test2() throws  Exception {
        IAtomContainer a = getChemical("ambit2/data/mengine/problem-002.sdf");
        IAtomContainer b = getChemical("ambit2/data/mengine/problem-002-chemidplus.sdf");        
        isIsomorph(a,b);   
    }    
    /*
     * The two compounds are not the same!
     * @throws Exception
     */
    public void test3() throws  Exception {
        IAtomContainer a = getChemical("ambit2/data/mengine/problem-003.sdf");
        IAtomContainer b = getChemical("ambit2/data/mengine/problem-003-chemidplus.sdf");        
        isIsomorph(a,b);   
    }  
    @Test
    public void test4() throws  Exception {
        IAtomContainer a = getChemical("ambit2/data/mengine/problem-004.sdf");
        IAtomContainer b = getChemical("ambit2/data/mengine/problem-004-chemidplus.sdf");        
        isIsomorph(a,b);   
    }   

    public void isIsomorph(IAtomContainer a,IAtomContainer b) throws  Exception {
        
        SmilesGenerator g = new SmilesGenerator(true);
        AtomConfigurator c= new AtomConfigurator();
        HydrogenAdderProcessor h = new HydrogenAdderProcessor();
        CDKHueckelAromaticityDetector.detectAromaticity(c.process(h.process(a)));
        CDKHueckelAromaticityDetector.detectAromaticity(c.process(h.process(b)));
        Assert.assertEquals(a.getAtomCount(),b.getAtomCount());
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(a,b));

        
        String s1= g.createSMILES(a);
        String s2= g.createSMILES(b);
        
        SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer a1 = p.parseSmiles(s1);
        IAtomContainer b1 = p.parseSmiles(s2);
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(a1,b1));
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(a,a1));
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(b,b1));
        
        CDKHueckelAromaticityDetector.detectAromaticity(h.process(c.process(a)));
        CDKHueckelAromaticityDetector.detectAromaticity(h.process(c.process(b)));
        Assert.assertTrue(UniversalIsomorphismTester.isIsomorph(a,b));


    }
    @Test
    public void testCrash1() throws  Exception {
        smi2sdf.setGenerateSmiles(true);
        smi2sdf.setDropHydrogens(false);        
        IAtomContainer a = getChemical("ambit2/data/mengine/problem-001.sdf");
        Assert.assertEquals(0,goCrash(a,"GENERATED_SMILES"));
    }    
    @Test    
    public void testCrash2() throws  Exception {
        smi2sdf.setGenerateSmiles(true);
        smi2sdf.setDropHydrogens(false);        
        IAtomContainer a = getChemical("ambit2/data/mengine/problem-002.sdf");
        Assert.assertEquals(0,goCrash(a,"GENERATED_SMILES"));
    }
    @Test    
    public void testCrash3() throws  Exception {
        smi2sdf.setGenerateSmiles(true);
        smi2sdf.setDropHydrogens(false);        
        IAtomContainer a = getChemical("ambit2/data/mengine/problem-003.sdf");
        Assert.assertEquals(0,goCrash(a,"GENERATED_SMILES"));
    }
    @Test
    public void testCrash4() throws  Exception {
        smi2sdf.setGenerateSmiles(true);
        smi2sdf.setDropHydrogens(false);        
        IAtomContainer a = getChemical("ambit2/data/mengine/problem-004.sdf");
        Assert.assertEquals(0,goCrash(a,"GENERATED_SMILES"));
    }   
    @Test
    public void testCrash5() throws  Exception {
        smi2sdf.setGenerateSmiles(true);
        smi2sdf.setDropHydrogens(false);
        IAtomContainer a = getChemical("ambit2/data/mengine/problem-005.sdf");
        Assert.assertEquals(0,goCrash(a,"GENERATED_SMILES"));
    } 
    @Test
    public void testCrash6() throws  Exception {
        smi2sdf.setGenerateSmiles(true);
        smi2sdf.setDropHydrogens(false);
        IAtomContainer a = getChemical("ambit2/data/mengine/problem-006.sdf");
        Assert.assertEquals(0,goCrash(a,"GENERATED_SMILES"));
    } 
    @Test
    public void testCrash7() throws  Exception {
        smi2sdf.setGenerateSmiles(true);
        smi2sdf.setDropHydrogens(false);
        IAtomContainer a = getChemical("ambit2/data/mengine/problem-007.sdf");
        Assert.assertEquals(0,goCrash(a,"GENERATED_SMILES"));
    }    
    @Test
    public void testCrashNoH1() throws  Exception {
        smi2sdf.setGenerateSmiles(true);
        smi2sdf.setDropHydrogens(true);
        IAtomContainer a = getChemical("ambit2/data/mengine/problem-001.sdf");
        Assert.assertEquals(0,goCrash(a,"SMILES"));
    }  
    @Test
    public void testCrashNoH2() throws  Exception {
        smi2sdf.setGenerateSmiles(true);
        smi2sdf.setDropHydrogens(true);
        IAtomContainer a = getChemical("ambit2/data/mengine/problem-002.sdf");
        Assert.assertEquals(0,goCrash(a,"GENERATED_SMILES"));
    }
    @Test
    public void testCrashNoH3() throws  Exception {
        smi2sdf.setGenerateSmiles(true);
        smi2sdf.setDropHydrogens(true);
        IAtomContainer a = getChemical("ambit2/data/mengine/problem-003.sdf");
        Assert.assertEquals(0,goCrash(a,"GENERATED_SMILES"));
    }
    @Test
    public void testCrashNoH4() throws  Exception {
        smi2sdf.setGenerateSmiles(true);
        smi2sdf.setDropHydrogens(true);
        IAtomContainer a = getChemical("ambit2/data/mengine/problem-004.sdf");
        Assert.assertEquals(0,goCrash(a,"GENERATED_SMILES"));
    }
    @Test
    public void testCrashNoH5() throws  Exception {
        smi2sdf.setGenerateSmiles(true);
        smi2sdf.setDropHydrogens(true);
        IAtomContainer a = getChemical("ambit2/data/mengine/problem-005.sdf");
        Assert.assertEquals(0,goCrash(a,"GENERATED_SMILES"));
    }
    @Test
    public void testCrashNoH6() throws  Exception {
        smi2sdf.setGenerateSmiles(true);
        smi2sdf.setDropHydrogens(true);
        IAtomContainer a = getChemical("ambit2/data/mengine/problem-006.sdf");
        Assert.assertEquals(0,goCrash(a,"GENERATED_SMILES"));
    }
    @Test
    public void testCrashNoH7() throws  Exception {
        smi2sdf.setGenerateSmiles(true);
        smi2sdf.setDropHydrogens(true);
        IAtomContainer a = getChemical("ambit2/data/mengine/problem-007.sdf");
        Assert.assertEquals(0,goCrash(a,"GENERATED_SMILES"));
    }         
    
    @Test
    public void testCrash8() throws  Exception {
        smi2sdf.setGenerateSmiles(true);
        smi2sdf.setDropHydrogens(true);
        SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer a = p.parseSmiles("N#N=CC(=O)NCC(=O)NN");
        Assert.assertEquals(0,goCrash(a,"GENERATED_SMILES"));
    }         
    
    /**
     * https://sourceforge.net/tracker/?func=detail&aid=3138563&group_id=152702&atid=785126
     */
  
    public void test_Toxtree_bug_3138563() throws  Exception {
        smi2sdf.setGenerateSmiles(true);
        smi2sdf.setDropHydrogens(true);
        SmilesParser p = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer a = p.parseSmiles("Cc2c(NS(=O)(=O)c1ccc(N)cc1)onc2C");
        Assert.assertEquals(0,goCrash(a,"GENERATED_SMILES"));
    } 
    
    
    public int goCrash(IAtomContainer a, String smilesfield) throws  Exception {
        AtomConfigurator c= new AtomConfigurator();
        HydrogenAdderProcessor h = new HydrogenAdderProcessor();
        CDKHueckelAromaticityDetector.detectAromaticity(h.process(c.process(a)));
        Preferences.setProperty(Preferences.SMILES_FIELD, smilesfield);
        smi2sdf.setOutputFile("test.sdf");
        smi2sdf.runShell(a);
        mengine.setInputFile("test.sdf");
        mengine.setOutputFile("good.sdf");
        IAtomContainer newmol = mengine.runShell(a);
        return mengine.getExitCode();
    }    
    
    public IAtomContainer getChemical(String file) throws Exception {
    	IAtomContainer a = null;
        IIteratingChemObjectReader reader = new MyIteratingMDLReader(
        		getClass().getClassLoader().getResourceAsStream(file),SilentChemObjectBuilder.getInstance());
        while (reader.hasNext()) {
            Object o = reader.next();
            if (o instanceof IAtomContainer) {
                a = (IAtomContainer) o;
                break;
            }
        }
        reader.close();
        return a;
    }
    /*
    public static void analyse(IAtomContainer mol) throws Exception {
    	
    	if (mol==null) throw new Exception("Null molecule!");
    	if (mol.getAtomCount()==0) throw new Exception("No atoms!");
    	getElements();

        
    	try {
    		//New - 16 Jan 2008 - configure atom types
    		logger.debug("Configuring atom types ...");
	        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
	        Iterator<IAtom> atoms = mol.atoms();
	        while (atoms.hasNext()) {
	           IAtom atom = atoms.next();
	           IAtomType type = matcher.findMatchingAtomType(mol, atom);
	           try {
	        	   AtomTypeManipulator.configure(atom, type);
                   logger.debug("Found " + atom.getSymbol() + " of type " + type.getAtomTypeName());                   
	           } catch (Exception x) {
	        	   logger.error(x.getMessage() + " " + atom.getSymbol(),x);
                   
                   if ("true".equals(Preferences.getProperty(Preferences.STOP_AT_UNKNOWNATOMTYPES))) {
                       throw new Exception(atom.getSymbol());
                   }
                   
	           }
	        }    	        
	        //adding hydrogens
	        CDKHydrogenAdder h = CDKHydrogenAdder.getInstance(mol.getBuilder());
	        
        	if (mol instanceof IAtomContainer) {
                try {
    	            h.addImplicitHydrogens(mol);
    	            logger.debug("Adding implicit hydrogens; atom count "+mol.getAtomCount());
    	            AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
    	            logger.debug("Convert explicit hydrogens; atom count "+mol.getAtomCount());
                } catch (Exception x) {
                    logger.error(x);
                    if ("true".equals(Preferences.getProperty(Preferences.STOP_AT_UNKNOWNATOMTYPES))) {
                        throw new MolAnalyseException(x);
                    }
                }
        	} else {
        		IAtomContainerSet moleculeSet = ConnectivityChecker.partitionIntoMolecules(mol);
        	      
        	      for (int k = 0; k < moleculeSet.getMoleculeCount(); k++) {
        	    	  IAtomContainer molPart = moleculeSet.getMolecule(k);
      		          h.addImplicitHydrogens(molPart);
      		          logger.debug("Adding implicit hydrogens; atom count "+molPart.getAtomCount());
    		          AtomContainerManipulator.convertImplicitToExplicitHydrogens(molPart);
    		          logger.debug("Convert explicit hydrogens; atom count "+molPart.getAtomCount());    		          
        	      }
        	}
        	atoms = mol.atoms();
	        while (atoms.hasNext()) {
		           IAtom atom = atoms.next();
		           if (atom.getAtomicNumber() == 0) {
		        	   Integer no = elements.get(atom.getSymbol());
		        	   if (no != null)
		        		   atom.setAtomicNumber(no.intValue());
		           }	   
		        }            	
	        //logger.debug("MolAnalyser\t",mol.getID());
    		Object o = mol.getProperty(MolFlags.MOLFLAGS);
    		MolFlags mf = null;
    		if ((o!=null) && (o instanceof MolFlags))       	mf = (MolFlags)o;
    		else mf = null;
	        if (mf == null) mf = new MolFlags(); else mf.clear();
	        mol.setProperty(MolFlags.MOLFLAGS,mf);
	
	        IRingSet ringSet = null;
	        try {
	        	ringSet = arf.findAllRings(mol);
	        } catch (CDKException x) {
	        	//timeout on AllRingsFinder, will try SSSR
	        	logger.warn(x.getMessage());
	        	SSSRFinder ssrf = new SSSRFinder(mol);
	        	ringSet = ssrf.findEssentialRings();
	        }
	        int size = ((IRingSet) ringSet).getAtomContainerCount();
	        mf.setOpenChain(size == 0);
	        if (size == 0) {
	        	mf.setAliphatic(true);
		        for (int i=0; i < mol.getBondCount(); i++)
		            if ((mol.getBond(i)).getOrder() == CDKConstants.BONDORDER_TRIPLE) {
		                mf.setAliphatic(false);
		                mf.setAcetylenic(true);
		                break;
		            }
	        } else {
	        	mf.setRingset(ringSet);
	        	//HueckelAromaticityDetector.detectAromaticity(mol,ringSet,true);
	        	
	        	
		        if (CDKHueckelAromaticityDetector.detectAromaticity(mol)) 
		        	logger.debug("Aromatic\t","YES");
		        else logger.debug("Aromatic\t","NO");
		        
		        IRing ring = null;
		        IAtom a;
		        int aromaticRings = 0;
		        boolean heterocyclicRing, heteroCyclic = false, heteroCyclic3 = false;
		        //boolean heteroaromaticRing, 
		        boolean heteroAromatic = false;
		        for (int i =0; i < ringSet.getAtomContainerCount(); i++) {
		        	ring = (IRing)ringSet.getAtomContainer(i);
		        	heterocyclicRing = false;
		        	//boolean isaromatic = ring.getFlag(CDKConstants.ISAROMATIC);
		        	//if (isaromatic) logger.debug("Aromatic ring found");
		        	for (int j=0; j < ring.getBondCount(); j++) {
		        		IBond b = ring.getBond(j);
		                b.setFlag(CDKConstants.ISINRING,true);
		                //if (isaromatic)
		                	//b.setFlag(CDKConstants.ISAROMATIC,true);	        		
		        	}
		        	int aromatic_atoms = 0;
	          	    for (int j=0; j < ring.getAtomCount(); j++) {
		                 a = ring.getAtom(j);
		                 a.setFlag(CDKConstants.ISINRING,true);
		                 if (a.getFlag(CDKConstants.ISAROMATIC)) aromatic_atoms++;

		                 if (!a.getSymbol().equals("C")) {
		                     heterocyclicRing = true;
		                     heteroCyclic = true;	                     
		                 }
		            }
	          	    boolean isaromaticRing = (aromatic_atoms == ring.getAtomCount());
	          	    if (heterocyclicRing) {
	          	        ring.setProperty(HETEROCYCLIC,new Boolean(true));
	          	        if (ring.getRingSize() == 3) {
	          	        	heteroCyclic3 = true;
	              	        ring.setProperty(HETEROCYCLIC3,new Boolean(true));
	          	        }
	          	    } 
	          	    ring.setFlag(CDKConstants.ISAROMATIC, isaromaticRing);
		        	if (isaromaticRing) { 
		        		
		        	    aromaticRings++;
		        	    if (heterocyclicRing)  {
	              	        ring.setProperty(HETEROAROMATIC,new Boolean(true));
	              	        heteroAromatic = true;
		        	    }
		        	}
		        }
	   	        mf.setHeterocyclic(heteroCyclic);
	   	        mf.setHeterocyclic3(heteroCyclic3);
	   	        mf.setHeteroaromatic(heteroAromatic);
	   	        mf.setAromatic(aromaticRings > 0);
		        mf.setAromaticRings(aromaticRings);
		        mol.setFlag(CDKConstants.ISAROMATIC,aromaticRings>0);	        
	        }
	          
	        
	        try {
	        	FunctionalGroups.associateIonic(mol);
	        } catch (CDKException x) {
	        	
	        	logger.warn(x);
	        }        
	        clearVisitedFlags(mol);
    	} catch (Exception x) {
    		//just in case ....
            System.out.println(mol);
            x.printStackTrace();
    		throw new MolAnalyseException(x);
    		
    	}
    }
*/
}



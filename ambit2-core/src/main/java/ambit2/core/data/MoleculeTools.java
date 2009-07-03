/**
 * Created on 2005-2-24
 *
 */
package ambit2.core.data;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.BitSet;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.MFAnalyser;

import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.io.MyIteratingMDLReader;
import ambit2.core.processors.structure.StructureTypeProcessor;
import ambit2.core.smiles.SmilesParserWrapper;



/**
 * Various structure processing. 
 * TODO refactor.
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class MoleculeTools {
	public final static int _FPLength = 1024;
	protected static HydrogenAdder adder = null;
	protected static Fingerprinter fingerprinter = null;
	public static final String[] substanceType = 
	{"organic","inorganic","mixture/unknown","organometallic"};
	public static final int substTypeOrganic = 1;	
	public static final int substTypeInorganic = 2;
	public static final int substTypeMixture = 3;
	public static final int substTypeMetallic = 4;	
	protected static StructureTypeProcessor sp = new StructureTypeProcessor();
	/**
	 * 
	 */
	public MoleculeTools() {
		super();
	}
//CMLReader(java.lang.String url)  
	public static BitSet getFingerPrint(String smiles, int fpLength) throws Exception  {
	    SmilesParserWrapper sp = SmilesParserWrapper.getInstance();
	    IMolecule mol = sp.parseSmiles(smiles);
	    if (fingerprinter == null) fingerprinter = new Fingerprinter(fpLength);
	    return fingerprinter.getFingerprint(mol);
		    
	}	

	public static BitSet getFingerPrint(String smiles) throws Exception  {
		return getFingerPrint(smiles,_FPLength);
	}

	public static long bitset2Long(BitSet bs) {
		long h = 0;
		for (int i = 0; i < 64; i++ ) {
			 h = h << 1;	
			 if (bs.get(i)) {
			 	h |= 1;
			 }
	    }
		return h;
	}
	
	public static void bitset2bigint16(BitSet bs,int size,BigInteger[] h16) {
		if (bs == null) 
			for (int i=0; i <h16.length;i++) h16[i] = BigInteger.ZERO;
		else {
			int L = h16.length;
			for (int j = 0; j < L; j++) {
				StringBuilder b = new StringBuilder();
				for (int i = (size-1); i >=0; i-- ) 
					b.append(bs.get(j*size+i)?"1":"0");
				
				h16[j] = new BigInteger(b.toString(),2);
			}
		}
	}
	/*
    protected static byte[] toByteArray(BitSet bits) {
        byte[] bytes = new byte[bits.length()/8+1];
        for (int i=0; i<bits.length(); i++) {
            if (bits.get(i)) {
                bytes[bytes.length-i/8-1] |= 1<<(i%8);
            }
        }
        return bytes;
    }
    */


	public static boolean analyzeSubstance(IAtomContainer molecule) {
		if ((molecule == null) || (molecule.getAtomCount()==0)) return false;
		int noH = 0;
		MFAnalyser mfa = new MFAnalyser(molecule);
		String formula = "";
		if (mfa.getAtomCount("H") == 0) {
			noH = 1;
			//TODO to insert H if necessary
			//TODO this uses new SaturationChecker() which relies on cdk/data/config
			if (adder == null)
				adder = new HydrogenAdder();
			try {
				adder.addImplicitHydrogensToSatisfyValency(molecule);
				int atomCount = molecule.getAtomCount();
				formula = mfa.analyseAtomContainer(molecule);
			} catch (CDKException x) {
				x.printStackTrace();
				//TODO exception
				formula = "defaultError";
			}
			
		} else formula = mfa.getMolecularFormula();
		molecule.setProperty(AmbitCONSTANTS.FORMULA,formula);
		double mass = mfa.getMass();
		molecule.setProperty(AmbitCONSTANTS.MOLWEIGHT,new Double(mass));
		try {
		molecule.setProperty(AmbitCONSTANTS.STRUCTURETYPE,sp.process(molecule));
		} catch (Exception x) {};
		molecule.setProperty(AmbitCONSTANTS.SUBSTANCETYPE,getSubstanceType(formula));
		return true;
	}
	
	protected static int getSubstanceType(String formula) {
		//TODO add mixture/organometallic recognition
		if (formula.equals("")) return substTypeOrganic;
		else if (formula.startsWith("C"))
			return substTypeOrganic;
		else return substTypeInorganic;		
	}
	

	public static IMolecule readMolfile(Reader molfile) throws Exception {
		IIteratingChemObjectReader mReader = new MyIteratingMDLReader(molfile,NoNotificationChemObjectBuilder.getInstance());
		IMolecule molecule = null;
		while (mReader.hasNext()) {
			Object mol = mReader.next();
			if (mol instanceof IMolecule) {
				molecule =  (IMolecule)mol;
				break;
			}
		}
		mReader.close();
        return molecule;
        
    }    	
	public static IMolecule readMolfile(String molfile) throws Exception {
		StringReader reader = new StringReader(molfile);
		IMolecule mol= readMolfile(reader);
		reader.close();
		return mol;
    }    
    public static IMolecule readCMLMolecule(String cml) throws Exception {
        IMolecule mol = null;
        StringReader strReader = null;
        try {
            strReader= new StringReader(cml);
            mol = readCMLMolecule(strReader);
            strReader.close();
            return mol;
        } catch (Exception e) {
            return mol;
        }

        
    }     
    public static IMolecule readCMLMolecule(Reader cmlReader) throws Exception {
        IMolecule mol = null;
        
         CMLReader reader = new CMLReader(cmlReader);
         IChemFile obj = null;
         
            obj = (IChemFile) reader.read(NoNotificationChemObjectBuilder.getInstance().newChemFile());
            int n = obj.getChemSequenceCount();
            if (n > 1)
                System.out.println("> 1 sequence in a record");      
            for (int j=0; j < n; j++) {
                IChemSequence seq = obj.getChemSequence(j);
                int m = seq.getChemModelCount();
                if (m > 1) 
                    System.out.println("> 1 model in a record");            
                for (int k = 0; k < m; k++) {
                    IChemModel mod = seq.getChemModel(k);
                    IMoleculeSet som = mod.getMoleculeSet();
                    if (som.getMoleculeCount() > 1)
                        System.out.println("> 1 molecule in a record");
                    for (int l=0; l < som.getMoleculeCount(); l++) {
                        mol = som.getMolecule(l);
                    
                    return mol;
                    }
            
                }
            }

         reader = null;
         return mol;
    }        

}

package ambit.processors.structure;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.MFAnalyser;

import ambit.data.IAmbitEditor;
import ambit.data.molecule.MoleculeTools;
import ambit.data.molecule.StructureType;
import ambit.exceptions.AmbitException;
import ambit.log.AmbitLogger;
import ambit.misc.AmbitCONSTANTS;
import ambit.processors.DefaultAmbitProcessor;
import ambit.processors.IAmbitResult;

/**
 * A processor to generates SMILES. SMILES are assigned as {@link AmbitCONSTANTS#UNIQUE_SMILES} property.
 * The time elapsed for generation is assigned as {@link AmbitCONSTANTS#UNIQUE_SMILES_TIME} property.
 * <br>See exapmle at {@link ambit.database.writers.DbSubstanceWriter}.  
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 30, 2006
 */
public class SmilesGeneratorProcessor extends DefaultAmbitProcessor  {
	protected static AmbitLogger logger = new AmbitLogger(SmilesGeneratorProcessor.class);
	protected SmilesGenerator  generator = new SmilesGenerator(DefaultChemObjectBuilder.getInstance());
	protected AllRingsFinder arf  = new AllRingsFinder();
	public void close() {
		// TODO Auto-generated method stub
		
	}

	public SmilesGeneratorProcessor() {
		this(30 * 60 * 1000);// 30 min
	}
	public SmilesGeneratorProcessor(long timeout) {
		super();
      	arf.setTimeout(timeout);
      	generator.setRingFinder(arf);
	}	
	public Object process(Object object) throws AmbitException {
		if (object == null) return null;
		else if (object instanceof Molecule) {
			String smiles = null;
			long smiles_time;
			IMolecule mol = null;
			try {
			    mol =(IMolecule)((IMolecule)object).clone();
			} catch (CloneNotSupportedException x) {
			    throw new AmbitException(x);
			}
			
	      	MFAnalyser mfa = new MFAnalyser(mol);
	      	try {
      			mol = (Molecule)mfa.removeHydrogensPreserveMultiplyBonded();
      		} catch (Exception e) {
      			//TODO verify what's going on if no H
      			//logger.error(e);
      		}
      		mfa = null;
      		Object smi = ((Molecule)object).getProperty(AmbitCONSTANTS.UNIQUE_SMILES);
      		if (smi != null) smiles = smi.toString();
      		if ((smiles == null) || smiles.equals("")) {
	      		smiles_time = System.currentTimeMillis();
	      		
	      		try {
	    			int strucType = MoleculeTools.getStrucType(mol,1);
	    			if (strucType > StructureType.strucType2DH) 
	    			    for (int i =0; i < mol.getAtomCount();i++) {
	    			        IAtom a = mol.getAtomAt(i);
	    			        Point3d p = a.getPoint3d();
	    			        a.setPoint2d(new Point2d(p.x,p.y));
	    			    }
	    			        
	    			boolean chiral = (strucType > StructureType.strucTypeSmiles);
	    			logger.debug(StructureType.strucType[strucType]);
	      			smiles = generator.createSMILES(mol, 
	      			        chiral, 
	      			        new boolean[mol.getBondCount()]);
	      		} catch (CDKException x) {
	      			logger.error(x);
	      			smiles = "error " + x.getMessage();
	      		}
      			logger.debug("Unique SMILES generated\t"+smiles+"\twas\t"+((Molecule)object).getProperty(AmbitCONSTANTS.SMILES));
	      		smiles_time = System.currentTimeMillis() - smiles_time;
	      		
	      		
	      		mol = null;
	  			((Molecule)object).setProperty(AmbitCONSTANTS.UNIQUE_SMILES,smiles);
	  			((Molecule)object).setProperty(AmbitCONSTANTS.UNIQUE_SMILES_TIME,new Long(smiles_time));
  			
      		} else logger.debug("Unique SMILES already exists\t"+smiles);
      		return object;
		} else return null;
	}

	public IAmbitResult createResult() {
		// TODO Auto-generated method stub
		return null;
	}

	public IAmbitResult getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setResult(IAmbitResult result) {
		// TODO Auto-generated method stub

	}

	public long getTimeout() {
		return arf.getTimeout();
	}

	public void setTimeout(long timeout) {
		arf.setTimeout(timeout);
	}
	   public IAmbitEditor getEditor() {

	    	return new AllRingsFinderTimeoutEditor(arf);
	    }
	public String toString() {
		return "Generate unique SMILES (timeout " + arf.getTimeout() + " ms.)";
	}   
}

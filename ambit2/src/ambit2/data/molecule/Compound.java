/**
 * Created on 2005-3-24
 *
 */
package ambit2.data.molecule;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.MFAnalyser;

import ambit2.config.AmbitCONSTANTS;
import ambit2.data.AmbitObject;
import ambit2.exceptions.AmbitException;
import ambit2.processors.IAmbitProcessor;
import ambit2.smiles.SmilesParserWrapper;
import ambit2.ui.data.QCompoundPanel;
import ambit2.ui.editors.IAmbitEditor;

/**
 * A chemical compound. Designed to encapsulate certain properties and functionality not available in {@link Molecule}
 * May disapear in future releases!
 * 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class Compound extends AmbitObject {
	
	protected static HydrogenAdder adder = null;
	protected static SmilesParserWrapper sp = null;
	protected static SmilesGenerator gen = null;
	

	protected CompoundType substanceType = null;
	protected StructureType structureType = null;	
	protected IMolecule molecule = null;
	
	protected String CAS_RN = "";
	protected String SMILES = "";
	protected String formula = "";
	protected double mass = 0;
	protected String note = "";
	protected int idsubstance = -1;
	protected boolean uniqueSMILES = false;
	protected int orderInModel = -1;

	/**
	 * 
	 * @param idsubstance
	 * @param idstructure
	 * @param type_structure
	 */
	public Compound(int idsubstance, int idstructure, int type_structure) {
		super();
		setId(idstructure);
		this.idsubstance = idsubstance;
		substanceType = new CompoundType();
		structureType = new StructureType(type_structure);		
	}
	public Compound(IMolecule mol) {
		super();
		substanceType = new CompoundType();
		structureType = new StructureType();
		try {
		    setMolecule(mol,null);
		} catch (AmbitException x) {
		    x.printStackTrace();
		}
	}		
	/**
	 * 
	 * @param mol
	 */
	public Compound(Molecule mol, IAmbitProcessor processor) throws AmbitException {
		super();
		substanceType = new CompoundType();
		structureType = new StructureType();
		setMolecule(mol,processor);
	}	
	/**
	 * 
	 */
	public Compound() {
		super();
		substanceType = new CompoundType();
		structureType = new StructureType();
		molecule = null;
	}

	/**
	 * @param name
	 */
	public Compound(String name) {
		super(name);
		substanceType = new CompoundType();
		structureType = new StructureType();		
	}

	/**
	 * @param name
	 * @param id
	 */
	public Compound(String name, int id) {
		super(name, id);
		substanceType = new CompoundType();
		structureType = new StructureType();		
	}

	public void clear() {
		super.clear();
		CAS_RN = "";
		SMILES = "";
		formula = "";
		mass = 0;
		note = "";
		idsubstance = -1;
		uniqueSMILES = false;
		substanceType.clear();
		structureType.clear();
		molecule = null;
		setNotModified();
	}
	/**
	 * 
	 * @return CAS registry number
	 */
	public String getCAS_RN() {
		return CAS_RN;
	}
	/**
	 * sets CAS registry number
	 * @param cas_rn
	 */
	public void setCAS_RN(String cas_rn) {
		boolean m = !this.CAS_RN.equals(cas_rn);
		CAS_RN = cas_rn;
		setModified(m);
	}
	/**
	 * 
	 * @return {@link IMolecule}
	 */
	public IMolecule getMolecule() {
		return molecule;
	}
	/**
	 * sets the molecule
	 * @param molecule
	 */
	public void setMolecule(IMolecule molecule, IAmbitProcessor processor) throws AmbitException {
		this.molecule = molecule;
		
		parseProperties(processor);
		analyzeSubstance();
		uniqueSMILES = false;
		
		setModified(true);
	}
	/**
	 * 
	 * @return SMILES
	 */
	public String getSMILES() {
		
		return SMILES;
	}
	/**
	 * set SMILES
	 * @param smiles
	 */
	public void setSMILES(String smiles) {
		boolean m =  !this.SMILES.equals(smiles);
		SMILES = smiles;
		uniqueSMILES = false;
		setModified(m);
	}
	public String toString() {
		return toString(',');
	}
	public String toString(char delimiter) {
		
		StringBuffer buf = new StringBuffer();
		buf.append(CAS_RN); buf.append(delimiter);
		buf.append('"'); buf.append(name); buf.append('"'); buf.append(delimiter);
		buf.append(SMILES);buf.append(delimiter);
		buf.append(formula);
		return buf.toString();
	}
	public boolean equals(Object obj) {
		Compound m = (Compound) obj; 
		return super.equals(obj) && 
			CAS_RN.equals(m.CAS_RN) &&
			SMILES.equals(m.SMILES);
			
	}
	public Object clone()  throws CloneNotSupportedException {
		Compound c = (Compound) super.clone();
		c.CAS_RN = CAS_RN;
		c.SMILES = SMILES;
		c.formula = formula;
		c.mass = mass;
		c.note = note;
		c.uniqueSMILES = uniqueSMILES;
		try {
		if (molecule != null) c.setMolecule((IMolecule) molecule.clone(),null);
		} catch (AmbitException x) {
		    throw new CloneNotSupportedException(x.getMessage());
		}
		c.idsubstance = idsubstance;
		c.substanceType = (CompoundType)substanceType.clone();
		c.structureType = (StructureType)structureType.clone();		
		return c;
	}
	
	/**
	 * @return Returns the uniqueSMILES.
	 */
	public boolean isUniqueSMILES() {
		return uniqueSMILES;
	}
	/**
	 * @param uniqueSMILES The uniqueSMILES to set.
	 */
	public void setUniqueSMILES(boolean uniqueSMILES) {
		this.uniqueSMILES = uniqueSMILES;
	}
	/**
	 * @return Returns the formula.
	 */
	public String getFormula() {
		return formula;
	}
	/**
	 * @param formula The formula to set.
	 */
	public void setFormula(String formula) {
		boolean m = !this.formula.equals(formula);
		this.formula = formula;
		setModified(m);
	}
	public CompoundType getSubstanceType() {
		return substanceType;
	}
	protected int updateSubstanceType() {
		//TODO add mixture/organometallic recognition
		if (formula.equals("")) return CompoundType.substTypeOrganic;
		else if (formula.startsWith("C"))
			return CompoundType.substTypeOrganic;
		else return CompoundType.substTypeInorganic;		
	}

	public boolean analyzeSubstance() {
		if (molecule == null) return false;
		int noH = 0;
		MFAnalyser mfa = new MFAnalyser(molecule);
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
		mass = mfa.getMass();

		structureType.setId(StructureType.strucType3DH);
		
		int has3D = 0;
		int has2D = 0;		
		for (int i = 0 ; i < molecule.getAtomCount(); i++) {
			IAtom a = molecule.getAtom(i);
			if (a.getPoint3d() != null) has3D++;
			if (a.getPoint2d() != null) has2D++;			
		}
		if (molecule.getAtomCount() == has3D) 
			structureType.setId(StructureType.strucType3DH - noH);
		else if (molecule.getAtomCount() == has2D) 
			structureType.setId(StructureType.strucType2DH - noH);
		else structureType.setId(StructureType.strucTypeSmiles);

		substanceType.setId(updateSubstanceType());
		return true;
	}

	
	public StructureType getStructureType() {
		return structureType;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public boolean createFromSMILES(String smiles) {
		molecule = null;
		if (!SMILES.equals(smiles)) {
			SMILES = smiles;
			uniqueSMILES = false;
			id = -1;
			return updateMolecule();
		} else return true;
	}
	public boolean updateMolecule() {
		//create molecule if null
		if (molecule == null) {
			if (sp== null)
				sp = SmilesParserWrapper.getInstance();
			try {
				molecule = sp.parseSmiles(SMILES);
			} catch (InvalidSmilesException x) {
				molecule = null;
				return false;
			}	
		}		
		//create smiles if not unique
			if (!uniqueSMILES) {
				if (gen == null)
					gen = new SmilesGenerator();
				try {
					SMILES = gen.createSMILES(molecule).toString();
					uniqueSMILES = true;
				} catch (Exception x) {
					//TODO exception
					System.err.println(SMILES);
					x.printStackTrace();
					uniqueSMILES = false;
					return false;
				}
				gen = null;
			} 
		
		return true;
	}
	
	public void compoundToMolecule() {
		if (molecule == null) return;
		molecule.setProperty(AmbitCONSTANTS.CASRN,CAS_RN);
		molecule.setProperty(AmbitCONSTANTS.NAMES,name);		
		molecule.setProperty(AmbitCONSTANTS.SMILES,SMILES);

	}	
	
	public void parseProperties(IAmbitProcessor processor) throws AmbitException {
		String s = "";
		if (molecule == null) return;
		if (molecule.getProperties().isEmpty()) return;
		if (processor != null)
		    processor.process(molecule);
		Object o = molecule.getProperty(CDKConstants.CASRN);
		if (o != null) CAS_RN = o.toString(); else CAS_RN = "";
		
		o = molecule.getProperty(CDKConstants.NAMES);
		if (o != null) name = o.toString(); else name = "";
		o = molecule.getProperty(AmbitCONSTANTS.SMILES);
		if (o != null) SMILES = o.toString(); else SMILES = "";
	}

	
	public double getMass() {
		return mass;
	}
	public int readIdsubstance() {
		return idsubstance;
	}
	public void writeIdsubstance(int idsubstance) {
		this.idsubstance = idsubstance;
	}

	public int getOrderInModel() {
		return orderInModel;
	}
	public void setOrderInModel(int orderInModel) {
		this.orderInModel = orderInModel;
	}
	/* (non-Javadoc)
     * @see ambit2.data.AmbitObject#getEditor()
     */

	public IAmbitEditor editor() {
	    QCompoundPanel p = new QCompoundPanel("Molecule");
	    p.setCompound(this);
	    return p;
	}    
	}	


/**
mol.setsetProperty(AmbitCONSTANTS.SMILES,"Nc2ccc3c1ccccc1Cc3(c2)");
mol.setProperty(AmbitCONSTANTS.CASRN,"153-78-6");
mol.setProperty(AmbitCONSTANTS.NAMES,"2-AMINOFLUORENE");
*/
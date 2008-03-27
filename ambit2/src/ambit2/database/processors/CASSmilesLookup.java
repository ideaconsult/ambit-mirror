package ambit2.database.processors;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.index.CASNumber;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.smiles.SmilesParser;

import ambit2.config.AmbitCONSTANTS;
import ambit2.exceptions.AmbitException;
import ambit2.log.AmbitLogger;
import ambit2.processors.DefaultAmbitProcessor;
import ambit2.processors.IAmbitResult;
import ambit2.ui.editors.DefaultProcessorEditor;
import ambit2.ui.editors.IAmbitEditor;
import ambit2.ui.editors.IdentifiersProcessor;

/**
 * Looks for a structure with property CDKConstants.CASRN and returns SMILES and structure (if readStructure)
 * Doesn't generate SMILES !
 * See exapmle at {@link ambit2.database.writers.DbSubstanceWriter}.
 * @author Nina Jeliazkova
 */
public class CASSmilesLookup extends DefaultAmbitProcessor {
	public static final String ERR_NOCAS="CAS RN not available";
	
	public static final String ERR_CASNOTFOUND="CAS RN NOT found";
	public static final String ERR_CASFOUND="CAS RN Found";
	protected static final String FIELD_IDSUBSTANCE="idsubstance";
	protected static final String FIELD_IDSTRUCTURE="idstructure";
	protected static final String FIELD_STRUCTURE="ustructure";
	protected static final String FIELD_SMILES="smiles";
	protected static final String FIELD_UNIQUESMILES="usmiles";
	
    
	//protected SmilesGenerator g = null;
	protected SmilesParser sparser = null;
	protected static transient AmbitLogger logger =  new AmbitLogger(CASSmilesLookup.class);
	protected Connection connection = null;
	protected static final String psStruc = "SELECT structure.idstructure,structure.idsubstance,casno,smiles,usmiles,uncompress(structure) as ustructure FROM substance,cas,structure WHERE casno=? and structure.idstructure=cas.idstructure and substance.idsubstance=structure.idsubstance order by type_structure desc;"; 
//		"SELECT structure.idstructure,structure.idsubstance,casno,uncompress(structure) as ustructure,type_structure FROM structure,cas WHERE casno=? and structure.idstructure=cas.idstructure order by type_structure desc;"; 
	//protected static final String psSMILES = "SELECT structure.idstructure,structure.idsubstance,casno,smiles,usmiles FROM substance,cas,structure WHERE casno=? and structure.idstructure=cas.idstructure and substance.idsubstance=structure.idsubstance;";
	protected PreparedStatement ps = null;
	protected boolean readStructure = false;
	
	public CASSmilesLookup(Connection connection, boolean readStructure) {
		super();
		this.connection = connection;
		ps = null;
		this.readStructure = readStructure;
	}

	public Object process(Object object) throws AmbitException {
	    if (object == null) return null;
		if (object instanceof IMolecule) {
			IMolecule mol = (IMolecule) object;
			Object smiles = mol.getProperty(AmbitCONSTANTS.SMILES);
			if ((smiles !=null) && (smiles.equals(""))) smiles = null;
			Object cas = mol.getProperty(CDKConstants.CASRN);
			if (cas == null) {
				mol.setProperty(this.getClass().getName(),ERR_NOCAS);
			}	else if (cas.toString().trim().equals("")) {
				mol.setProperty(this.getClass().getName(),ERR_NOCAS);
			} else {
			    try {
			    cas = IdentifiersProcessor.hyphenateCAS(cas.toString());
			    } catch ( Exception x) {
			        mol.setProperty(this.getClass().getName(),IdentifiersProcessor.ERR_INVALIDCAS);
			    }
			    if (CASNumber.isValid(cas.toString())) {
					String newSmiles = lookup(cas.toString(),mol);
					if (newSmiles != null) {
						mol.setProperty(this.getClass().getName(),ERR_CASFOUND);
						if (smiles == null) {
							mol.setProperty(AmbitCONSTANTS.SMILES,newSmiles);
						}
						
					} else 
					    mol.setProperty(this.getClass().getName(),ERR_CASNOTFOUND);
					    
			    } else mol.setProperty(this.getClass().getName(),IdentifiersProcessor.ERR_INVALIDCAS);

			}
			if (logger.isDebugEnabled()) {
				logger.debug(AmbitCONSTANTS.AMBIT_IDSUBSTANCE+"="+mol.getProperty(AmbitCONSTANTS.AMBIT_IDSUBSTANCE) + "\t"+mol.getProperty(this.getClass().getName()) + "\t" + cas + "\t"+mol.getProperty(AmbitCONSTANTS.SMILES));
			}			
		}
		
		return object;
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
	public String lookup(String casRN, IMolecule mol) {
		String smiles = null;
		try {
			if (ps == null)
				ps = connection.prepareStatement(psStruc);

			ps.clearParameters();
			ps.setString(1,casRN);
			ResultSet rs = ps.executeQuery();
			IMolecule m = null;
			while (rs.next()) {
				try {

					if ((readStructure) || (mol.getAtomCount()==0)) {
						m =  readMolecule(rs.getAsciiStream(FIELD_STRUCTURE));
						mol.removeAllElements();
						mol.add(m);
					    Object idstructure = rs.getObject(FIELD_IDSTRUCTURE);
					    if (idstructure!=null) mol.setProperty(AmbitCONSTANTS.AMBIT_IDSTRUCTURE,new Integer(idstructure.toString()));						
					} 

					smiles= rs.getString(FIELD_SMILES);
					if (smiles != null) {
						boolean usmiles= rs.getBoolean(FIELD_UNIQUESMILES);
						mol.setProperty(AmbitCONSTANTS.SMILES,smiles);
						if (usmiles)
							mol.setProperty(AmbitCONSTANTS.UNIQUE_SMILES,smiles);
					}
					
					Object idsubstance = rs.getObject(FIELD_IDSUBSTANCE);
                    if (idsubstance != null) 
                     mol.setProperty(AmbitCONSTANTS.AMBIT_IDSUBSTANCE,  new Integer(idsubstance.toString()));
					if (mol != null) {
						mol.setProperty(CDKConstants.CASRN,casRN);
					}
					break;				    
				} catch (CDKException x) {
					logger.error(x);
					smiles = null;
					m = null;
				}

			}	
			rs.close();
			rs = null;
			//TODO make it persistent
			//ps.close();
			//ps = null;
		} catch (SQLException x ) {
			x.printStackTrace();
			smiles = null;
		}
		return smiles;

	}
	public static IMolecule readMolecule(InputStream cmlStream) throws CDKException {
		IMolecule mol = null;

		 CMLReader reader = new CMLReader(cmlStream);
		 IChemFile obj = null;
		 
		 	obj = (IChemFile) reader.read(new org.openscience.cdk.ChemFile());
		 	int n = obj.getChemSequenceCount();
		 	if (n > 1)
		 		logger.warn("> 1 sequence in a record");		 
		 	for (int j=0; j < n; j++) {
		 		IChemSequence seq = obj.getChemSequence(j);
		 		int m = seq.getChemModelCount();
		 		if (m > 1) 
		 			logger.warn("> 1 model in a record");		 	
		 		for (int k = 0; k < m; k++) {
		 			IChemModel mod = seq.getChemModel(k);
		 			IMoleculeSet som = mod.getMoleculeSet();
		 			if (som.getMoleculeCount() > 1)
		 				logger.warn("> 1 molecule in a record");
		 			for (int l=0; l < som.getMoleculeCount(); l++) {
		 				mol = som.getMolecule(l);
		 		    
		 		    return mol;
		 			}
		 	
		 		}
		 	}

		 reader = null;

   		 return mol;
	}

	/* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#close()
     */
    public void close() {
    	try {
        if (ps != null) ps.close(); ps = null;
    	} catch (Exception x) {
    		
    	}

    }


	   public IAmbitEditor getEditor() {

	    	return new DefaultProcessorEditor(this);
	    }
	 public String toString() {
		return "CAS RN-> SMILES Lookup";
	}  
}

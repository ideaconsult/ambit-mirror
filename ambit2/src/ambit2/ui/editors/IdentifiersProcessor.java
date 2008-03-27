package ambit2.ui.editors;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.index.CASNumber;
import org.openscience.cdk.interfaces.IChemObject;

import ambit2.config.AmbitCONSTANTS;
import ambit2.exceptions.AmbitException;
import ambit2.io.IColumnTypeSelection;
import ambit2.processors.DefaultAmbitProcessor;
import ambit2.processors.IdentifiersProcessorEditor;

/**
 * Looks up molecule identifiers from a predefined list and "normalizes" them.
 * Molecule identifiers are simple some predefined properties, like CAS, NAME. These comes in many different flavours and it is useful to have them "standardized". 
 * Example:  <br>
 * If the molecule has property CAS#="50-00-0" , after processing the molecule will have property CAS_RN="50-00-0"
 * The lookup table can be set by {@link #setIdentifiers(Hashtable)} or The lookup table can be set by {@link #addIdentifiers(Hashtable)}.<br>
 * See exapmle at {@link ambit2.database.writers.DbSubstanceWriter}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public class IdentifiersProcessor extends DefaultAmbitProcessor {
	protected Hashtable identifiers;
	public static final char casDelimiter = '-';
	public static final String ERR_INVALIDCAS="Invalid CAS RN";
	
	/**
	 * Creates {@link IdentifiersProcessor} with the default lookup table
	 *
	 */
    public IdentifiersProcessor() {
        super();
        identifiers = new Hashtable();
        identifiers.put("CAS",CDKConstants.CASRN);
        identifiers.put("CAS_RN",CDKConstants.CASRN);
        identifiers.put("CAS#",CDKConstants.CASRN);
        

        identifiers.put("ChemName",CDKConstants.NAMES);
        identifiers.put("Name",CDKConstants.NAMES);
        identifiers.put("NAME",CDKConstants.NAMES);
        identifiers.put("CHEMICAL NAME",CDKConstants.NAMES);
        identifiers.put("Chemical",CDKConstants.NAMES);
        identifiers.put("NAME",CDKConstants.NAMES);
        identifiers.put("Chemical Name",CDKConstants.NAMES);
        
        
        identifiers.put("SMILES",AmbitCONSTANTS.SMILES);
        identifiers.put("Smiles",AmbitCONSTANTS.SMILES);
        identifiers.put("smiles",AmbitCONSTANTS.SMILES);
        
        identifiers.put("INChI",AmbitCONSTANTS.INCHI);
        
        
    }
    /**
     * tries to guess the type of the property
     * @param property 
     * @return one of {@link IColumnTypeSelection#_ctCAS},{@link IColumnTypeSelection#_ctSMILES},{@link IColumnTypeSelection#_ctChemName},{@link IColumnTypeSelection#_ctRowID}
     */
    public int guessProperty(Object property) {
        Object o = identifiers.get(property);
        if (o==null) return -1;
        else if (o.equals(AmbitCONSTANTS.SMILES)) return IColumnTypeSelection._ctSMILES;
        else if (o.equals(CDKConstants.CASRN)) return IColumnTypeSelection._ctCAS;
        else if (o.equals(CDKConstants.NAMES)) return IColumnTypeSelection._ctChemName;
        else return IColumnTypeSelection._ctRowID;
        
    }    
    /**
     * Transforms the properties according to the internal lookup table
     * @param properties
     */
    public void guessProperties(Map properties) {
        Iterator e = identifiers.keySet().iterator();
		while (e.hasNext()) {
			Object key = e.next();
			Object value = properties.get(key);
			if (value != null) {
				properties.remove(key);
				properties.put(identifiers.get(key).toString(),value);
			}	
		}        
    }
	public Object process(Object object) throws AmbitException {
		IChemObject co = (IChemObject) object;
		guessProperties(co.getProperties());
		/*
		Enumeration e = identifiers.keys();
		while (e.hasMoreElements()) {
			Object key = e.nextElement();
			Object value = co.getProperty(key);
			if (value != null) {
				co.removeProperty(key);
				co.setProperty(identifiers.get(key),value);
			}	
		}
		*/
		Object cas = co.getProperty(CDKConstants.CASRN);
		if (cas != null ) {
				String casno = cas.toString().trim();
				int nonzero = -1;
				for (int a=0;a < casno.length();a++)
					if ((nonzero ==-1) && (casno.charAt(a)!='0')) {
						nonzero=a;
					}
				if (nonzero > 0)
					cas = casno.substring(nonzero);		
			    try {
				    cas = hyphenateCAS(cas.toString());
				    } catch ( Exception x) {
				        co.setProperty(this.getClass().getName(),ERR_INVALIDCAS);
				    }				
				co.setProperty(CDKConstants.CASRN,cas.toString());
				
		}
	

		return object;
	}
	public String toString() {
		return "Formats identifiers (CAS RN, chemical names, etc.) ";
	}

	public IAmbitEditor getEditor() {
		return new IdentifiersProcessorEditor(this);
	}
	/**
	 * 
	 * @return the internal lookup table
	 */
	public Hashtable getIdentifiers() {
		return identifiers;
	}
	/**
	 * Sets the internal lookup table
	 * @param identifiers
	 */
	public void setIdentifiers(Hashtable identifiers) {
		this.identifiers = identifiers;
	}
	/**
	 * adds Hashtable to the internal lookup table
	 * @param identifiers
	 */
	public void addIdentifiers(Hashtable identifiers) {
	    
		this.identifiers.putAll(identifiers);
	}	
	/**
	 * Hyphenates CAS registry number
	 * @param cas
	 * @return hyphenated cas
	 * @throws Exception 
	 */
	public static String hyphenateCAS(String cas) throws Exception {
	    if (cas.indexOf(casDelimiter) == -1) {
	        //50000
	        int p = cas.length();
	        return cas.substring(0,p-3) +  casDelimiter + 	      
	            cas.substring(p-3,p-1) +  casDelimiter + cas.charAt(p-1);
	    } else return cas;    
	}
	/**
	 * 
	 * @param cas
	 * @return true if the cas is valid, otherwise tries to hyphenate it and returns true if it is valid
	 */
	public static boolean isCASValid(String cas) {
	    try {
		    if (CASNumber.isValid(cas)) return true;
		    else return (CASNumber.isValid(hyphenateCAS(cas)));
	    } catch (Exception x) {
	        return true;
	    }
	    
	}	
}



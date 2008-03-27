/**
 * Created on 2005-3-24
 *
 */
package ambit2.data.descriptors;

import java.util.List;

import ambit2.io.IColumnTypeSelection;
import ambit2.ui.editors.IdentifiersProcessor;
import ambit2.data.AmbitObject;

/**
 * Column type (the role in a model) as defined in {@link ambit2.io.IColumnTypeSelection}
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class AmbitColumnType extends AmbitObject implements IColumnTypeSelection {
    protected static IdentifiersProcessor processor = null;
    /**
     *  
     * @param id integer column type identifier as defined in {@link ambit2.io.IColumnTypeSelection} 
     */
	public AmbitColumnType(int id) {
		super();
		setId(id);
	}
	
	/**
	 * 
	 *
	 */
	public AmbitColumnType() {
		super();
		setId(IColumnTypeSelection._ctX);
	}

	/**
	 * @param name string column type identifier as defined in {@link ambit2.io.IColumnTypeSelection}
	 */
	public AmbitColumnType(String name) {
		super(name);
		setId(guessType(name));
	}
	public AmbitColumnType(String name, Object value) {
		super(name);
		setId(guessType(name,value));
	}
	/**
	 * @param name
	 * @param id
	 */
	public AmbitColumnType(String name, int id) {
		super(name, id);
	}
	public DescriptorsList getDescriptors(List xnames) {
		return null;
	}
	public String typeToString(int type) {
		return _columnTypesS[type];
	}
	public void setId(int id) {
		this.id = id;
		if ((id < 0) || (id > 9)) id = 0;
		name = _columnTypesS[id];
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		for (int i = 0; i < _columnTypesS.length; i++)
			if (name.equals(_columnTypesS[i])) {
				setId(i);
			}
	}
	public boolean isPredefined() {
		return true;
	}
	public String[] predefinedvalues() {
		return _columnTypesS;
	}
    public int guessType(String descrName) {
        if (processor==null) processor = new IdentifiersProcessor();
        int id = processor.guessProperty(descrName);
        
        if (id == IColumnTypeSelection._ctUnknown) {
            /*
	        if (descrName.equals("SMILES")) return IColumnTypeSelection._ctSMILES;
	        if (descrName.equals("CAS")) return IColumnTypeSelection._ctCAS;
	        if (descrName.equals("CASRN")) return IColumnTypeSelection._ctCAS;        
	        if (descrName.equals("CASNo")) return IColumnTypeSelection._ctCAS;
	        */        
	        if (descrName.equals("Predicted")) return IColumnTypeSelection._ctYpredicted;        
	        if (descrName.equals("Observed")) return IColumnTypeSelection._ctYobserved;
	        if (descrName.equals("Obs")) return IColumnTypeSelection._ctYobserved;
	        if (descrName.equals("Experimental")) return IColumnTypeSelection._ctYobserved;
	        if (descrName.equals("Exp")) return IColumnTypeSelection._ctYobserved;
	        if (descrName.equals("Calc")) return IColumnTypeSelection._ctYpredicted;                
	        if (descrName.equals("Calculated")) return IColumnTypeSelection._ctYpredicted;
	        if (descrName.equals("Pred")) return IColumnTypeSelection._ctYpredicted;  
	        /*
	        if (descrName.equals("Name")) return IColumnTypeSelection._ctChemName;        
	        if (descrName.equals("NAME")) return IColumnTypeSelection._ctChemName;        
	        if (descrName.equals("Chemical")) return IColumnTypeSelection._ctChemName;
	        if (descrName.equals("ChemName")) return IColumnTypeSelection._ctChemName;
	        */
	        if (descrName.equals("ID")) return IColumnTypeSelection._ctRowID;
	        if (descrName.equals("INChi")) return IColumnTypeSelection._ctRowID;         
	        if (descrName.equals("Formula")) return IColumnTypeSelection._ctRowID;
        }
        return id;
    }
    public int guessType(String descrName, Object value) {
        int id =  guessType(descrName);
        if (id == -1) {
            try {
                Double.parseDouble(value.toString());
                return IColumnTypeSelection._ctX;
            } catch (Exception x) {
                return IColumnTypeSelection._ctUnknown;
            }
        } else return id;
    }
}

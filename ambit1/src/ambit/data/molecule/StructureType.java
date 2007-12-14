/**
 * Created on 2005-3-28
 *
 */
package ambit.data.molecule;

import ambit.data.AmbitObject;


/**
 * Structure type 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class StructureType extends AmbitObject implements Comparable {
	public static final String[] strucType = {"SMILES","2D no H","2D with H","3D no H",
		"3D with H","optimized","experimental"};
	public static final int strucTypeSmiles = 0;
	public static final int strucType2DnoH = 1;
	public static final int strucType2DH = 2;
	public static final int strucType3DnoH = 3;
	public static final int strucType3DH = 4;
	public static final int strucType3DOptimized = 5;
	public static final int strucType3Dexperimental = 6;	

	/**
	 * 
	 */
	public StructureType() {
		super("SMILES");
	}

	/**
	 * 
	 */
	public StructureType(int id) {
		super("");
		setId(id);
	}
	
	/**
	 * @param name
	 */
	public StructureType(String name) {
		super(name);
		setName(name);
	}

	/**
	 * @param name
	 * @param id
	 */
	public StructureType(String name, int id) {
		super(name, id);
	}
	public boolean isPredefined() {
		return true;
	}
	public String[] predefinedvalues() {
		return strucType;
	}	
	public void setId(int id) {
		if ((id < 0) || (id > strucType.length)) id = 0;
		super.setId(id);
		name = strucType[id];
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		for (int i = 0; i < strucType.length; i++)
			if (name.equals(strucType[i])) {
				setId(i);
			}
	}	
	public int compareTo(Object o) {
		if (o instanceof StructureType) {
			return getId() - ((StructureType) o).getId() ;
		} else if (o instanceof String) {
			
			for (int i = 0; i < strucType.length; i++)
				if (name.equals(strucType[i])) {
					return getId() - i; 
				}
			return 1;
		} else return -1;
	}
}

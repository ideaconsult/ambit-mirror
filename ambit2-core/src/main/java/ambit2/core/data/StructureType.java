/**
 * Created on 2005-3-28
 *
 */
package ambit2.core.data;

import ambit2.base.data.AmbitBean;



/**
 * Structure type 
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class StructureType extends AmbitBean implements Comparable<StructureType> {
	public static final String[] strucType = {"SMILES","2D no H","2D with H","3D no H",
		"3D with H","optimized","experimental"};
	public static final int strucTypeSmiles = 0;
	public static final int strucType2DnoH = 1;
	public static final int strucType2DH = 2;
	public static final int strucType3DnoH = 3;
	public static final int strucType3DH = 4;
	public static final int strucType3DOptimized = 5;
	public static final int strucType3Dexperimental = 6;	
	protected int id;
	protected String name;

	/**
	 * 
	 */
	public StructureType() {
		super();
		setName("SMILES");
	}

	/**
	 * 
	 */
	public StructureType(int id) {
		super();
		setName("");
		setId(id);
	}
	
	/**
	 * @param name
	 */
	public StructureType(String name) {
		super();
		setName(name);
	}

	/**
	 * @param name
	 * @param id
	 */
	public StructureType(String name, int id) {
		this(name);
		setId(id);
	}
	public boolean isPredefined() {
		return true;
	}
	public String[] predefinedvalues() {
		return strucType;
	}	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		if ((id < 0) || (id > strucType.length)) id = 0;
		this.id = id;
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
	
    public int compareTo(StructureType o) {
    	return getId() - o.getId() ;
    }

}

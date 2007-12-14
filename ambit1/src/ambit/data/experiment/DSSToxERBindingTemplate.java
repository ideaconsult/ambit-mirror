package ambit.data.experiment;

import java.util.Collection;

/**
 * A template with fields extracted from DSSTox estrogen binding database.
 * Use when importing DSSTox estrogen binding SDF file.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class DSSToxERBindingTemplate extends DSSToxLC50Template {

	public DSSToxERBindingTemplate(String name) {
		super(name);

	}

	public DSSToxERBindingTemplate(int initialCapacity) {
		super(initialCapacity);

	}

	public DSSToxERBindingTemplate(Collection c) {
		super(c);
		// TODO Auto-generated constructor stub
	}
	public void init() {
		setName("DSSTox-ERBinding");
		addFields("StudyType","",false,false);
		addFields("Species","",false,false);
		addFields("Endpoint","",false,false);
		addFields("ChemClass ERB","",false,true);
		addFields("ER_RBA","",true,true);
		addFields("LOG ER_RBA","",true,true);
		addFields("ActivityCategory ER_RBA","",false,true);		
		addFields("Mean ER_RBA ChemClass","",true,true);
		addFields("Rationale ChemClass ERB","",false,true);		
	}
}

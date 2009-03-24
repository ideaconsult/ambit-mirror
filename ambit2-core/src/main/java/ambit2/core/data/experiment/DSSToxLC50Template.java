package ambit2.core.data.experiment;

import ambit2.base.data.Template;

/**
 * A template with fields extracted from DSSTox EPA Fathead Minnow database.
 * Use when importing DSSTox Fathead Minnow SDF file.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class DSSToxLC50Template extends Template {

	public DSSToxLC50Template(String name) {
		super(name);
		init();
	}


	public void init() {
		setName("DSSTox-EPAFHM");
		addFields("StudyType","",false,false);
		addFields("Species","",false,false);
		addFields("Endpoint","",false,false);
		addFields("ChemClass FHM","",false,true);
		addFields("LC50","",true,true);
		addFields("LC50NOTE","",false,true);
		addFields("LC50RATIO","",true,true);
		addFields("MOA","",false,true);		
		addFields("MOACONF","",false,true);
		addFields("MIXMOA","",false,true);		
		addFields("TOXINDEX","",true,true);
		addFields("FATS","",false,true);		
		addFields("BEHAVIOR","",false,true);
		
	}

}


package ambit2.core.data.experiment;

import ambit2.base.data.Template;


/**
 * Template for LLNA data.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class LLNATemplate extends Template {
	/**
     * 
     */
    private static final long serialVersionUID = -6042721963039457662L;

    public LLNATemplate(String name) {
		super(name);
		init();

	}

	public void init() {
		setName("LLNA");
		addFields("Ref.","",false,true);
		addFields("LLNA EC3 %","",false,true);
		addFields("EC3 est.","",false,true);		
		addFields("\"LLNA  SIs\"_3","",false,true);
		addFields("\"LLNA  SIs\"_2","",false,true);		
		addFields("\"LLNA  SIs\"_1","",false,true);
		addFields("Potency_numeric","",true,true);		
		addFields("Potency category ","",false,true);
		addFields("LLNA %_1","",true,true);
		addFields("LLNA %_2","",true,true);		
		addFields("LLNA %_3","",true,true);
		addFields("Vehicle1","",false,true);
		addFields("Vehicle","",false,false);
		
		
		
	}
	
}

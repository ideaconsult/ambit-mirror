package ambit.data.experiment;

import java.util.Collection;

/**
 * A template with fields extracted from DSSTox carcinogenicity database.
 * Use when importing DSSTox carcnogenicity SDF file.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 31, 2006
 */
public class DSSToxCarcinogenicityTemplate extends DSSToxLC50Template {

	public DSSToxCarcinogenicityTemplate(String name) {
		super(name);

	}

	public DSSToxCarcinogenicityTemplate(int initialCapacity) {
		super(initialCapacity);

	}

	public DSSToxCarcinogenicityTemplate(Collection c) {
		super(c);

	}
	public void init() {
		setName("DSSTox-carcinogenicity");
		addFields("StudyType","",false,false);
		addFields("Species","",false,false);
		addFields("Endpoint","",false,false);
		addFields("SAL CPDB","",false,true);
		addFields("TD50 Mouse notext","",true,true);
		addFields("TD50 Mouse Note","",false,true);
		addFields("Target Sites Mouse Male","",false,true);
		addFields("Target Sites Mouse Female","",false,true);
		
		addFields("TD50 Rat","",true,true);
		addFields("Target Sites Rat Female","",false,true);
		addFields("Target Sites Mouse Female","",false,true);
		addFields("Target Sites Rat Male","",false,true);
		addFields("Target Sites Rat Both Sexes","",false,true);
		addFields("Target Sites Cynomolgus","",false,true);
		addFields("TD50 Cynomolgus","",true,true);
		addFields("TD50 Hamster notext","",true,true);
		addFields("TD50 Rat notext","",true,true);

		addFields("Target Sites Dog","",false,true);
		addFields("Target Sites Hamster Female","",false,true);
		addFields("TD50 Rat","",false,true);
		addFields("TD50 Rhesus","",false,true);
		addFields("ToxNote","",false,true);
		addFields("Target Sites Hamster Male","",false,true);
		addFields("TD50 Dog","",false,true);
		addFields("TD50 Hamster","",false,true);
		addFields("Target Sites Rhesus","",false,true);

	}
}

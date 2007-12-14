/**
 * Created on 2005-3-29
 *
 */
package ambit.domain;

import ambit.data.AmbitObjectFixedValues;


/**
 * Applicability domain method type
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class ADomainMethodType extends AmbitObjectFixedValues {
	public static final int _modeRANGE = 0;
	public static final int _modeLEVERAGE = 1;	
	public static final int _modeEUCLIDEAN = 2;
	public static final int _modeCITYBLOCK = 3;	
	public static final int _modeMAHALANOBIS = 4;
	public static final int _modeDENSITY = 5;
	public static final int _modeFINGERPRINTS = 6;	
	public static final int _modeATOMENVIRONMENT = 7;
	public static final int _modeATOMENVIRONMENTRANK = 8;
	public static final String[] methodName = 
	{"Ranges","Leverage","Euclidean distance","City-block distance",
	    "Mahalanobis distance","Probability density","Fingerprints",
	    "Atom environments","Atom environments ranking"};
	//,"Atom environments Ranking"};
	public static final String[] methodClass = 
	{	"ambit.domain.DataCoverageDescriptors.class",
	    "ambit.domain.DataCoverageDistance.class",
	    "ambit.domain.DataCoverageDistance.class",
	    "ambit.domain.DataCoverageDistance.class",
	    "ambit.domain.DataCoverageDistance.class",
	    "ambit.domain.DataCoverageDensity.class",
	    "ambit.domain.DataCoverageFingerprints.class",
	    "ambit.domain.DataCoverageAtomEnvironment.class",
	    "ambit.ranking.DataCoverageAtomEnvironmentRank.class"};	
	/**
	 * 
	 */
	public ADomainMethodType() {
		super(methodName[0]);
	}
	/**
	 * 
	 * @param id
	 */
	public ADomainMethodType(int id) {
		super("");
		setId(id);
	}

	/**
	 * @param name
	 */
	public ADomainMethodType(String name) {
		super(name);
	}

	/**
	 * @param name
	 * @param id
	 */
	public ADomainMethodType(String name, int id) {
		super(name, id);
	}

	public String[] predefinedvalues() {
		return methodName;
	}		
	public void setId(int id) {
		super.setId(id);
		if ((id < 0) || (id > methodName.length)) id = _modeRANGE;
		if (id == _modeLEVERAGE) id = _modeRANGE;
		
		name = methodName[id];
	}
	
	public boolean isDensity() {
		return id == _modeDENSITY;
	}	
	public boolean isRange() {
		return id == _modeRANGE;
	}
	public boolean isDistance() {
		return (id == _modeEUCLIDEAN) || 
			   (id == _modeCITYBLOCK) ||
			   (id == _modeMAHALANOBIS);
	}
	public void setDensityMode() {
		setId(_modeDENSITY);
	}
}

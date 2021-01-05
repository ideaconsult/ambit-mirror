package ambit2.sln;

import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import ambit2.sln.dictionary.SLNDictionary;

public class SLNContainer extends QueryAtomContainer 
{
	public static enum SLNObjectType {
		STRUCTURE, ENSAMBLE, QUERY, REACTION 
	}
	
	
	static final long serialVersionUID = 345274336895563284L;

	private SLNDictionary localDictionary = null;
	private SLNDictionary globalDictionary = null;

	// Flags that determine what types of objects are stored in SLNContainer
	private boolean IsStructureOnly = false;
	private SLNObjectType slnObjectType =  SLNObjectType.QUERY;
		

	private SLNContainerAttributes attributes = new SLNContainerAttributes();
	
	
	public SLNContainer(IChemObjectBuilder builder) {
		super(builder);
	}
	public SLNContainerAttributes getAttributes() {
		return attributes;
	}

	public void setAttributes(SLNContainerAttributes attributes) {
		this.attributes = attributes;
	}

	public boolean getIsStructureOnly() {
		return IsStructureOnly;
	}

	public void setIsStructureOnly(boolean IsStructureOnly) {
		this.IsStructureOnly = IsStructureOnly;
	}

	public boolean checkIsStructureOnly() {
		SLNContainer container = new SLNContainer(SilentChemObjectBuilder.getInstance());
		for (int i = 0; i < container.atomCount; i++) {
			for (int j = 0; j < container.bondCount; j++) {
				if (/*atomExpression != null || bondExpression != null
						|| */  attributes != null)
					return false;
			}
		}
		return false;
	}

	public SLNDictionary getLocalDictionary() {
		return localDictionary;
	}

	public SLNDictionary getGlobalDictionary() {
		return globalDictionary;
	}

	public void setLocalDictionary(SLNDictionary dict) {
		localDictionary = dict;
	}

	public void setGlobalDictionary(SLNDictionary dict) {
		globalDictionary = dict;
	}
	
	public SLNObjectType getSlnObjectType() {
		return slnObjectType;
	}
	
	public void recognizeSlnObjectType() {
		//TODO 
		//may be remove function checkIsStructureOnly()
	}
	
	

}

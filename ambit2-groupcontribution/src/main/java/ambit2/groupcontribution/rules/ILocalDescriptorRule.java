package ambit2.groupcontribution.rules;

import ambit2.groupcontribution.descriptors.ILocalDescriptor;

public interface ILocalDescriptorRule 
{
	public enum RuleType{
		USAGE, VALUE
	}
	
	public RuleType getType();
	public void applyRule(ILocalDescriptor locDescr);
	
}

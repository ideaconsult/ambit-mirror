package ambit2.reactions.generator.score.components;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.reactions.generator.score.IScoreComponent;

public class DescriptorRangeScore implements IScoreComponent 
{
	private RangeProfile profile = null;
	private Double minValue = null;
	private Double maxValue = null;
	
	private double weight = 1.0;
	
	public double getWeight()  {
		return weight;
	};
	
	public void setWeight(double weight) {
		this.weight = weight;
	};
	
	public double getScore(IAtomContainer mol)
	{
		//TODO
		return 0.0;
	}

	public Double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	public Double getMinValue() {
		return minValue;
	}

	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}

	public RangeProfile getProfile() {
		return profile;
	}

	public void setProfile(RangeProfile profile) {
		this.profile = profile;
	};
}

package ambit2.reactions.syntheticaccessibility;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.reactions.retrosynth.ReactionSequence;
import ambit2.reactions.rules.SyntheticStrategyDescriptorSolver;
import ambit2.rules.weight.DescriptorWeight;


public class SyntheticAccessibilityManager 
{	
	public static class DescrData
	{
		public double value = 0.0;
		public double transformedValue = 0.0;
		public DescrData(double value, double transformedValue){
			this.value = value;
			this.transformedValue = transformedValue; 
		}
	}
	
	
	protected  SyntheticAccessibilityStrategy strategy = null;
	protected SyntheticStrategyDescriptorSolver solver = new SyntheticStrategyDescriptorSolver();
	protected List<DescrData> calculatedDescrData = new ArrayList<DescrData>();
	
	public SyntheticAccessibilityStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(SyntheticAccessibilityStrategy strategy) {
		this.strategy = strategy;
	}
	
	public List<DescrData> getCalculatedDescrData() {
		return calculatedDescrData;
	}

	public double calcSyntheticAccessibility(IAtomContainer mol)
	{	
		calculatedDescrData.clear();
		double sa = 0.0;
		
		for (int i = 0; i < strategy.descirptors.size(); i++)
		{
			DescriptorWeight dw = strategy.descirptors.get(i);
			Double c = (Double)solver.calculateDescriptor(dw.descriptorName, mol);
			Double f_c = c;
			if (dw.valueTrnasformation != null)
				f_c = dw.valueTrnasformation.getFunctionValue(c);
			sa += f_c * dw.weight;
			DescrData descrData = new DescrData(c, f_c); 
		}
		
		if (strategy.startMaterialSimilarityWeight > 0.0)
		{	
			sa += strategy.startMaterialSimilarityWeight * getStartMaterialSimilarityScore(mol);
		}	
		
		if (strategy.retroSynthesisWeight > 0.0)
		{	
			sa += strategy.retroSynthesisWeight * getRetroSyntheticScore(mol);
		}
		
		return sa;
	}
	
	public String getCalculationDetailsAsString()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strategy.descirptors.size(); i++)
		{
			DescriptorWeight dw = strategy.descirptors.get(i);
			DescrData descrDat = calculatedDescrData.get(i);
			sb.append(dw.descriptorName);
			sb.append("  "); 
			sb.append(descrDat.value);
			sb.append("  ");
			sb.append(descrDat.transformedValue);
			sb.append("\n");
		}
		return sb.toString();
	}
	
	
	double getStartMaterialSimilarityScore(IAtomContainer mol)
	{
		//TODO
		return 0.0;
	}
	
	double getRetroSyntheticScore(IAtomContainer mol)
	{
		//TODO
		return 0.0;
	}
	
	/*
	double getSyntheticAccessibilityScore(ReactionSequence reactSeq)
	{
		//TODO
		return 0.0;
	}
	
	double getSyntheticAccessibilityScore(List<ReactionSequence> reactSeqList)
	{
		//TODO
		return 0.0;
	}
	*/
	
}

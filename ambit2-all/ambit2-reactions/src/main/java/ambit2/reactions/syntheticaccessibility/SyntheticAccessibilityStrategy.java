package ambit2.reactions.syntheticaccessibility;

import java.util.ArrayList;
import java.util.List;

import ambit2.reactions.rules.scores.ReactionSequenceScoreSchema;
import ambit2.rules.functions.LinearFunction;
import ambit2.rules.weight.DescriptorWeight;

public class SyntheticAccessibilityStrategy 
{	
	public List<DescriptorWeight> descirptors = new ArrayList<DescriptorWeight>();
	public double startMaterialSimilarityWeight = 0.0;
	public double retroSynthesisWeight = 0.0;
	public ReactionSequenceScoreSchema reactionSequenceScoreSchema = null;
	
	public static SyntheticAccessibilityStrategy getDefaultStrategy()
	{
		SyntheticAccessibilityStrategy saStrategy = new SyntheticAccessibilityStrategy();
		
		//set Molecule Complexity Descriptor
		DescriptorWeight dw = new DescriptorWeight();
		dw.descriptorName = "MOL_COMPLEXITY_01";
		//convert complexity interval [a,b] into score ranging [100,0]
		//fun(x) = 100*(b-x)/(b-a) = -100/(b-a)*x + 100*b/(b-a)
		double a = 0;
		double b = 150; 
		dw.valueTrnasformation = new LinearFunction(new double[]{-100.0/(b-a), 100.0*b/(b-a)});
		dw.weight = 1.0;
		saStrategy.descirptors.add(dw);

		return saStrategy;
	}
	
}

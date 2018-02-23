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
		
		//(1) Set Molecule Complexity Descriptor
		DescriptorWeight dw = new DescriptorWeight();
		dw.descriptorName = "MOL_COMPLEXITY_01";
		//convert complexity interval [a,b] into score ranging [100,0]
		//fun(x) = 100*(b-x)/(b-a) = -100/(b-a)*x + 100*b/(b-a)
		double a = 0;
		double b = 150; 
		dw.valueTrnasformation = new LinearFunction(new double[]{-100.0/(b-a), 100.0*b/(b-a)});
		dw.weight = 0.5;
		saStrategy.descirptors.add(dw);
		
		//(2) Set Stereo Elements Descriptor
		dw = new DescriptorWeight();
		dw.descriptorName = "NUMBER_OF_STEREO_ELEMENTS";
		//-30% for each stereo element
		//fun(x) = -30x + 100
		dw.valueTrnasformation = new LinearFunction(new double[]{-30.0, 100.0});
		dw.weight = 0.3;
		saStrategy.descirptors.add(dw);
		
		//(3) Cyclomatic number
		dw = new DescriptorWeight();
		dw.descriptorName = "CYCLOMATIC_NUMBER";
		//-20% for each ring
		//fun(x) = -20x + 100
		dw.valueTrnasformation = new LinearFunction(new double[]{-20.0, 100.0});
		dw.weight = 0.2;
		saStrategy.descirptors.add(dw);

		return saStrategy;
	}
	
}

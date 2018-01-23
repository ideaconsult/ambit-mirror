package ambit2.reactions.retrosynth;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ambit2.reactions.GenericReaction;
import ambit2.reactions.GenericReactionInstance;
import ambit2.reactions.rules.IRetroSynthRuleInstance;
import ambit2.reactions.rules.SyntheticStrategyDescriptorSolver;
import ambit2.reactions.rules.scores.AtomComplexity;
import ambit2.reactions.rules.scores.ReactionScore;
import ambit2.reactions.rules.scores.ReactionScoreSchema;
import ambit2.rules.functions.LinearFunction;
import ambit2.rules.weight.DescriptorWeight;

public class SyntheticStrategy 
{
	public static enum ReactionCenterComplexityMethod {
		FIRST_ATOM, ATOMS_AVERAGE
	}
	
	public ReactionScoreSchema reactionScoreSchema = new ReactionScoreSchema();
	public List<DescriptorWeight> productComplexityDescirptors = new ArrayList<DescriptorWeight>();
	//public List<DescriptorWeight> productSimilarityDescirptors = new ArrayList<DescriptorWeight>();
	public Map<String, Double> reactionClassScores = new HashMap<String, Double>();
	public Map<String, Double> trasformTypeScores = new HashMap<String, Double>();
	public ReactionCenterComplexityMethod reactionCenterComplexityMethod = ReactionCenterComplexityMethod.ATOMS_AVERAGE;
	
	protected SyntheticStrategyDescriptorSolver solver = new SyntheticStrategyDescriptorSolver();
	protected AtomComplexity atomComplexity = new AtomComplexity();
	
	public SyntheticStrategy() 
	{	
	}
	
	public SyntheticStrategy(File file) 
	{	
		//TODO
	}
	
	public ReactionScore calcReactionScore(GenericReactionInstance gri)
	{
		ReactionScore rscore = new ReactionScore();
		
		if (reactionScoreSchema.basicScoreWeight > 0.0)
		{	
			rscore.basicScore = gri.reaction.getBasicScore();
			rscore.totalScore += rscore.basicScore * reactionScoreSchema.basicScoreWeight;
		}
		
		if (reactionScoreSchema.experimentalConditionsScoreWeight > 0.0)
		{	
			rscore.experimentalConditionsScore = gri.reaction.getExperimentalConditionsScore();
			rscore.totalScore += rscore.experimentalConditionsScore * reactionScoreSchema.experimentalConditionsScoreWeight;
		}
		
		if (reactionScoreSchema.productComplexityWeight > 0.0)
		{
			rscore.productComplexityScore = calcProductComplexity(gri.products);
			rscore.totalScore += rscore.productComplexityScore * reactionScoreSchema.productComplexityWeight;	
		}
		
		if (reactionScoreSchema.reactionCenterComplexityWeight > 0.0)
		{
			rscore.reactionCenterComplexityScore = calcReactionCenterComplexity(gri);
			rscore.totalScore += rscore.reactionCenterComplexityScore * reactionScoreSchema.reactionCenterComplexityWeight;	
		}
		
		if (reactionScoreSchema.yieldScoreWeight > 0.0)
		{
			//average of lo and hi reaction yield
			rscore.yieldScore = 0.5*(gri.reaction.getYieldHi() + gri.reaction.getYieldLo());
			rscore.totalScore += rscore.yieldScore * reactionScoreSchema.yieldScoreWeight;	
		}
		
		return rscore;
	}
	
	double calcProductComplexity(IAtomContainer product)
	{
		double complexity = 0.0;
		for (DescriptorWeight dw : productComplexityDescirptors)
		{
			Double c = (Double)solver.calculateDescriptor(dw.descriptorName, product);
			complexity += c * dw.weight;
		}
		return complexity;
	}
	
	double calcReactionCenterComplexity(GenericReactionInstance gri)
	{	
		switch (reactionCenterComplexityMethod)
		{
		case FIRST_ATOM:
			IAtom at = gri.instanceAtoms.get(0);
			return atomComplexity.calcAtomComplexity(at, gri.target);
			
		case ATOMS_AVERAGE:
			double complexity = 0.0;
			for (IAtom a : gri.instanceAtoms)
				complexity += atomComplexity.calcAtomComplexity(a, gri.target);
			return complexity;
		}
		return 0.0;
	}
	
	public static SyntheticStrategy getDefaultSyntheticStrategy()
	{
		SyntheticStrategy synthStrategy = new SyntheticStrategy();
		synthStrategy.reactionScoreSchema = ReactionScoreSchema.getDefaultReactionScoreSchema();
		
		//set productComplexityDescirptors
		DescriptorWeight dw = new DescriptorWeight();
		dw.descriptorName = "MOL_COMPLEXITY_01";
		//convert complexity interval [a,b] into score ranging [100,0]
		//fun(x) = 100*(b-x)/(b-a) = -100/(b-a)*x + 100*b/(b-a)
		double a = 0;
		double b = 150; 
		dw.valueTrnasformation = new LinearFunction(new double[]{-100.0/(b-a), 100.0*b/(b-a)});
		dw.weight = 1.0;
		synthStrategy.productComplexityDescirptors.add(dw);
		
		/*
		DescriptorWeight dw2 = new DescriptorWeight();
		dw.descriptorName = "MOL_COMPLEXITY_02";
		//convert complexity interval [a,b] into score ranging [100,0]
		//fun(x) = 100*(b-x)/(b-a) = -100/(b-a)*x + 100*b/(b-a)
		a = 0;
		b = 10; 
		dw2.valueTrnasformation = new LinearFunction(new double[]{-100.0/(b-a), 100.0*b/(b-a)});
		dw2.weight = 1.0;
		synthStrategy.productComplexityDescirptors.add(dw2);
		*/
		
		return synthStrategy;
	}
	
	public void loadFromJSON(File jsonFile) throws Exception
	{
		InputStream fin = new FileInputStream(jsonFile); 
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = null;
		
		try {
			
			root = mapper.readTree(fin);
		} catch (Exception x) {
			throw x;
		} finally {
			try {fin.close();} catch (Exception x) {}	
		}
		
		//TODO
		
		fin.close();
	}
	
	public String getAsJSONString() 
	{
		StringBuffer sb = new StringBuffer();
		//TODO
		return sb.toString();
	}
	public ArrayList<IRetroSynthRuleInstance> applyStrategy(IAtomContainer target, ArrayList<IRetroSynthRuleInstance> ruleInstances)
	{
		//Not used
		//TODO
		return ruleInstances;
	}
	
	public static Object[] getRandomSelection(Map<GenericReaction,List<List<IAtom>>> instances)
	{
		Random rn = new Random();
		rn.setSeed(1234555);
		Set<GenericReaction> grSet = instances.keySet();
		int grNum = rn.nextInt(grSet.size());
		int n = 0;
		for (GenericReaction gr : instances.keySet())
		{
			if (n == grNum)
			{	
				List<List<IAtom>> rInst = instances.get(gr);
				int rInstNum = rn.nextInt(rInst.size());
				//System.out.println("grNum = " + grNum + "  rInstNum = " + rInstNum + " rInst.size() = " + rInst.size());
				List<IAtom> inst = rInst.get(rInstNum);
				Object obj[] = new Object[2];
				obj[0] = gr;
				obj[1] = inst;
				return obj;
			}
			else
				n++;
		}
		return null;
	}
	
}

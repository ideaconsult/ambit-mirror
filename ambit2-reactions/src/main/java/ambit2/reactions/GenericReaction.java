package ambit2.reactions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import com.fasterxml.jackson.databind.JsonNode;

import ambit2.reactions.rules.ReactionParser;
import ambit2.rules.conditions.ICondition;
import ambit2.rules.conditions.IDescriptorValueCondition;
import ambit2.rules.conditions.parser.ConditionJsonParser;
import ambit2.rules.conditions.parser.ConditionParsingUtils;
import ambit2.rules.json.JSONParsingUtils;
import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SMIRKSReaction;
import ambit2.smarts.SmartsConst;
import ambit2.smarts.SmartsParser;
import ambit2.smarts.SmartsConst.SSM_MODE;
import ambit2.smarts.smirks.SmirksTransformationFlag;

public class GenericReaction 
{	
	public static enum ReactionType {
		REACTION, TRANSFORM, MOLECULE_TRANSFORMATION, UNDEFINED
	}
	
	public static enum TransformType {
		FG_A, FG_R
	}
	
	public static enum ReactionConfigStatus {
		NON_CONFIGURED, CONFIGURED, ERROR
	}
	
	protected boolean FlagUse = true;
	protected int id = 0;
	protected String externId = "";
	protected String name = null;
	protected String smirks = null;
	protected Map<SmirksTransformationFlag,Boolean> smirksFlag = null;
	protected List<ICondition> conditions = null;
	protected List<Double> conditionScores = null; 
	protected List<ICondition> applicationConditions = null;
	protected String reactionClass = null;
	protected String info = null;
	protected String experimentalConditionsInfo = null;
	protected int reactionCenterIndices[] = null;
	protected ReactionType reactionType = ReactionType.UNDEFINED;
	protected TransformType transformType = null;
	protected double experimentalConditionsScore = 0.0;
	protected double basicScore = 0.0;
	protected double reliabilityScore = 0.0;
	protected double yieldLo = 0.0;
	protected double yieldHi = 0.0;
	protected double priorityScore = 0.0;
	
	//Work data
	protected SMIRKSReaction smirksReaction = null;
	protected ReactionConfigStatus configStatus = ReactionConfigStatus.NON_CONFIGURED;
	
	public boolean isFlagUse() {
		return FlagUse;
	}

	public void setFlagUse(boolean flagUse) {
		FlagUse = flagUse;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
			
	public String getExternId() {
		return externId;
	}

	public void setExternId(String externId) {
		this.externId = externId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSmirks() {
		return smirks;
	}

	public void setSmirks(String smirks) {
		this.smirks = smirks;
	}
	
	public Map<SmirksTransformationFlag, Boolean> getSmirksFlag() {
		return smirksFlag;
	}

	public void setSmirksFlag(Map<SmirksTransformationFlag, Boolean> smirksFlag) {
		this.smirksFlag = smirksFlag;
	}
	
	public List<ICondition> getConditions() {
		return conditions;
	}

	public void setConditions(List<ICondition> conditions) {
		this.conditions = conditions;
	}
	
	public List<Double> getConditionScores() {
		return conditionScores;
	}

	public void setConditionScores(List<Double> conditionScores) {
		this.conditionScores = conditionScores;
	}

	public List<ICondition> getApplicationConditions() {
		return applicationConditions;
	}

	public void setApplicationConditions(List<ICondition> applicationConditions) {
		this.applicationConditions = applicationConditions;
	}

	public String getReactionClass() {
		return reactionClass;
	}

	public void setReactionClass(String reactionClass) {
		this.reactionClass = reactionClass;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	public String getExperimentalConditionsInfo() {
		return experimentalConditionsInfo;
	}

	public void setExperimentalConditionsInfo(String experimentalConditionsInfo) {
		this.experimentalConditionsInfo = experimentalConditionsInfo;
	}

	public int[] getReactionCenterIndices() {
		return reactionCenterIndices;
	}

	public void setReactionCenterIndices(int[] reactionCenterIndices) {
		this.reactionCenterIndices = reactionCenterIndices;
	}
	
	public ReactionType getReactionType() {
		return reactionType;
	}

	public void setReactionType(ReactionType reactionType) {
		this.reactionType = reactionType;
	}

	public TransformType getTransformType() {
		return transformType;
	}

	public void setTransformType(TransformType transformType) {
		this.transformType = transformType;
	}

	public double getExperimentalConditionsScore() {
		return experimentalConditionsScore;
	}

	public void setExperimentalConditionsScore(
			double experimentalConditionsScore) {
		this.experimentalConditionsScore = experimentalConditionsScore;
	}
	
	public double getBasicScore() {
		return basicScore;
	}

	public void setBasicScore(double basicScore) {
		this.basicScore = basicScore;
	}

	public double getReliabilityScore() {
		return reliabilityScore;
	}

	public void setReliabilityScore(double reliabilityScore) {
		this.reliabilityScore = reliabilityScore;
	}

	public double getYieldLo() {
		return yieldLo;
	}

	public void setYieldLo(double yieldLo) {
		this.yieldLo = yieldLo;
	}

	public double getYieldHi() {
		return yieldHi;
	}

	public void setYieldHi(double yieldHi) {
		this.yieldHi = yieldHi;
	}
	
	public double getPriorityScore() {
		return priorityScore;
	}

	public void setPriorityScore(double priorityScore) {
		this.priorityScore = priorityScore;
	}

	public SMIRKSReaction getSmirksReaction() {
		return smirksReaction;
	}
	
	public void setSmirksReaction(SMIRKSReaction smirksReaction) {
		this.smirksReaction = smirksReaction;
	}
	
	public ReactionConfigStatus getConfigStatus() {
		return configStatus;
	}

	public boolean checkConditionsForTarget(Object target)
	{
		if (conditions == null)
			return true;
		if (conditions.isEmpty())
			return true;
		
		//Each condition should be true in order to return true result
		for (ICondition condition: conditions)
			if (!condition.isTrue(target))
				return false;
		
		return true;
	}
	

	public void configure(SMIRKSManager smrkMan) throws Exception
	{
		smirksReaction = smrkMan.parse(smirks);
		if (smrkMan.getErrors().equals(""))
			configStatus = ReactionConfigStatus.CONFIGURED;
		else	
		{	
			configStatus = ReactionConfigStatus.ERROR;
			throw (new Exception ("There are errors in SMIRKS: " + smirks + "  " + smrkMan.getErrors()));
		}
	}
	
	public static GenericReaction getReactionFromJsonNode(JsonNode node) throws Exception
	{
		GenericReaction reaction = new GenericReaction();
		
		Boolean use = JSONParsingUtils.extractBooleanKeyword(node, "USE", false);
		if (use != null)
			reaction.setFlagUse(use);
		
		reaction.name = JSONParsingUtils.extractStringKeyword(node, "NAME", true);
		reaction.smirks = JSONParsingUtils.extractStringKeyword(node, "SMIRKS", true);
		reaction.reactionClass = JSONParsingUtils.extractStringKeyword(node, "CLASS", false);
		
		Object obj[] = JSONParsingUtils.extractArrayKeyword(node, "USE_CONDITIONS", false, true);
		if (obj != null)
		{	
			List<ICondition> conditions = new ArrayList<ICondition>();
			for (int i = 0; i < obj.length; i++)
			{
				if (obj[i] == null)
					continue;
				
				if (obj[i] instanceof String)
				{
					 ICondition condition = ReactionParser.getConditionFromString((String) obj[i]);
					 conditions.add(condition);
				}
				else
				{	
					if (obj[i] instanceof JsonNode)
					{
						IDescriptorValueCondition dvc = ConditionJsonParser.getDescriptorValueCondition((JsonNode)obj[i]);
						 conditions.add(dvc);
					}
					else
						throw new Exception("Incorrect USE_CONDITIONS["+(i+1)+"]. It is not a string! " + obj[i] );
				}	
			}
			
			reaction.setConditions(conditions);
		}	
		
		
		return reaction;
	}
	
	public static GenericReaction getReactionFromTokens(String tokens[], Map<String,Integer> indices) throws Exception
	{	
		GenericReaction r = new GenericReaction();
		Integer ind;
		
		ind = indices.get("Id");
		if (ind != null && ind < tokens.length)
		{	
			Integer i = getInt(tokens[ind]);
			if (i != null)
				r.id = i;
		}
		
		ind = indices.get("ExternId");
		if (ind != null && ind < tokens.length)
			r.externId = tokens[ind];
		
		ind = indices.get("Name");
		if (ind != null && ind < tokens.length)
			r.name = tokens[ind];
		
		ind = indices.get("SMIRKS");
		if (ind != null && ind < tokens.length)
			r.smirks= tokens[ind];
		
		//TODO handle SMIRKSflags and SMILES
		
		ind = indices.get("Class");
		if (ind != null && ind < tokens.length)
			r.reactionClass= tokens[ind];
		
		//TODO handle ReactionCenter, ReactionType and TransformType
		
		ind = indices.get("BasicScore");
		if (ind != null && ind < tokens.length)
		{	
			Double d= getDouble(tokens[ind]);
			if (d != null)
				r.basicScore = d;
		}
		
		ind = indices.get("ExperimentalConditions");
		if (ind != null && ind < tokens.length)
		{	
			Double d= getDouble(tokens[ind]);
			if (d != null)
				r.experimentalConditionsScore = d;
		}
		
		ind = indices.get("ReliabilityScore");
		if (ind != null && ind < tokens.length)
		{	
			Double d= getDouble(tokens[ind]);
			if (d != null)
				r.reliabilityScore = d;
		}
		
		ind = indices.get("Conditions");
		if (ind != null && ind < tokens.length)
		{	
			ReactionParser.parseReactionConditionsFromString(r,tokens[ind]);
		}
		
		ind = indices.get("YieldInterval");
		if (ind != null && ind < tokens.length)
		{	
			String s = tokens[ind];
			String s_tok[] = s.split("-");
			if (s_tok.length == 2)
			{
				try
				{
					r.yieldLo = Double.parseDouble(s_tok[0]);
					r.yieldHi = Double.parseDouble(s_tok[1]);
					//System.out.println("*** --> " + r.yieldLo + " - " + r.yieldHi);
				}
				catch (Exception e) {};
			}
		}	
		
		ind = indices.get("Info");
		if (ind != null && ind < tokens.length)
			r.info = tokens[ind];
		
		ind = indices.get("ExperimentalConditionsInfo");
		if (ind != null && ind < tokens.length)
			r.experimentalConditionsInfo = tokens[ind];
		
		ind = indices.get("PriorityScore");
		if (ind != null && ind < tokens.length)
		{	
			Double d= getDouble(tokens[ind]);
			if (d != null)
				r.priorityScore = d;
		}
		
		return r;
	}
	
	
	
	public List<List<IAtom>> findReactionInstances(IAtomContainer target, SMIRKSManager smrkMan)
	{
		return findReactionInstances(target, smrkMan, SmartsConst.SSM_MODE.SSM_NON_OVERLAPPING);
	}
	
	
	public List<List<IAtom>> findReactionInstances(IAtomContainer target, SMIRKSManager smrkMan, SSM_MODE SSMode)
	{	
		SmartsParser.prepareTargetForSMARTSSearch(smirksReaction.reactantFlags, target);
		if (smirksReaction.reactantFlags.hasRecursiveSmarts)
			smrkMan.mapRecursiveAtomsAgainstTarget(smirksReaction.reactantRecursiveAtoms, target);
		
		// It is absolutely needed that setQuery() function is called after
		// recursive atom mapping
		// because the recursive mapping calls setQuery() as well
		smrkMan.getIsomorphismTester().setQuery(smirksReaction.reactant);
		
		switch (SSMode)
		{
		case SSM_NON_OVERLAPPING:
			return smrkMan.getNonOverlappingMappings(target);
		
		case SSM_NON_IDENTICAL:
			return smrkMan.getNonIdenticalMappings(target);
		
		//TODO
			
		default:
			return null;
		}
		
	}
	
	public IAtomContainer applyAtInstance(IAtomContainer target, List<IAtom> instance, SMIRKSManager smrkMan) throws Exception
	{
		return applyAtInstance(target, instance, smrkMan, true);
	}
	
	public IAtomContainer applyAtInstance(IAtomContainer target, 
											List<IAtom> instance, 
											SMIRKSManager smrkMan, 
											boolean FlagClone) throws Exception
	{
		List<List<IAtom>> instances = new ArrayList<List<IAtom>>();
		instances.add(instance);
		return applyAtInstancesSimultaneously(target, instances, smrkMan, FlagClone);
	}
	
	public IAtomContainer applyAtInstancesSimultaneously(IAtomContainer target, 
											List<List<IAtom>> instances, 
											SMIRKSManager smrkMan, 
											boolean FlagClone)  throws Exception
	{
		
		IAtomContainer product = smrkMan.applyTransformationsAtLocationsWithCloning(target, instances, smirksReaction);
		//TODO handle non-clone case
		return product;
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("Reaction: " + name);
		sb.append(" id = " + id);
		sb.append(" externId = " + externId);
		sb.append(" use = " + FlagUse);
		sb.append(" smirks = " + smirks + "  ");
		if (reactionClass != null)
			sb.append(" class = " + reactionClass);
		if (info != null)
			sb.append(" info = " + info);
		if (experimentalConditionsInfo != null)
			sb.append(" experimentalConditionsInfo = " + experimentalConditionsInfo);
		if (transformType !=null);
			sb.append(" transformType = " + transformType);
		sb.append(" expCondScore = " + experimentalConditionsScore);
		sb.append(" basicScore = " + basicScore);
		sb.append(" priorityScore = " + priorityScore);
		sb.append(" reliabilityScore = " + reliabilityScore);
		sb.append(" yieldLo = " + yieldLo);
		sb.append(" yieldHi = " + yieldHi);
		
		//TODO conditions, reactionCenterIndices[]
		
		return sb.toString();
	}
	
	//Helpers
	public static final Integer getInt(String s) {

		try
		{
			Integer i = Integer.parseInt(s);
			return i;
		}
		catch (Exception x) {
			return null;
		}
	}

	public static final Double getDouble(String s) {
		
		try
		{
			Double d = Double.parseDouble(s);
			return d;
		}
		catch (Exception x) {
			return null;
		}
	}
	
}

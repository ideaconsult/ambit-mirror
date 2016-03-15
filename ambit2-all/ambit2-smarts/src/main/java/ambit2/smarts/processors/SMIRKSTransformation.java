package ambit2.smarts.processors;

import java.util.HashSet;
import java.util.Set;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.smarts.SMIRKSManager;
import ambit2.smarts.SMIRKSReaction;
import ambit2.smarts.SmartsConst;
import ambit2.smarts.SmartsConst.SSM_MODE;

/**
 * Placeholder for set of SMIRKS with additional options
 * 
 * @author nina
 * 
 */
public class SMIRKSTransformation implements Comparable<SMIRKSTransformation> {

	protected boolean enabled = true;
	protected String smirks = null;
	protected int id = 0;
	protected SSM_MODE mode = SmartsConst.SSM_MODE.SSM_NON_OVERLAPPING;
	protected Set<String> precondition_atom = null;
	protected Set<String> precondition_atomtype = null;
	protected boolean applicable = true;
	protected transient String example = null;
	protected int order = Integer.MAX_VALUE;

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}

	public boolean isApplicable() {
		return applicable;
	}

	public void setApplicable(boolean applicable) {
		this.applicable = applicable;
	}

	public SSM_MODE getMode() {
		return mode;
	}

	public void setMode(SSM_MODE mode) {
		this.mode = mode;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getSMIRKS() {
		return smirks;
	}

	public void setSMIRKS(String smirks) {
		this.smirks = smirks;
		this.smirksReaction = null;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	protected String name = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReactionClass() {
		return reactionClass;
	}

	public void setReactionClass(String reactionClass) {
		this.reactionClass = reactionClass;
	}

	public void addPreconditionAtom(String atomSymbol) {
		if (precondition_atom == null)
			precondition_atom = new HashSet<String>();
		precondition_atom.add(atomSymbol);
	}

	public void addPreconditionAtomtype(String atomtype) {
		if (precondition_atomtype == null)
			precondition_atomtype = new HashSet<String>();
		precondition_atomtype.add(atomtype);
	}

	public Boolean hasPreconditionAtomtype(String atomtype) {
		return (precondition_atomtype == null)?null:
				precondition_atomtype.contains(atomtype);
	}
	public Boolean hasPreconditionAtomDefined() {
		return precondition_atom != null;
	}
	public Boolean hasPreconditionAtomTypeDefined() {
		return precondition_atomtype != null;
	}
	public Boolean hasPreconditionAtom(String atomSymbol) {
		return (precondition_atom == null)?null:precondition_atom.contains(atomSymbol);
	}

	protected String reactionClass = null;
	protected transient SMIRKSReaction smirksReaction = null;

	public void configure(SMIRKSManager smrkMan) throws Exception {
		if (smirksReaction != null)
			return;
		smirksReaction = smrkMan.parse(smirks);
		if (!smrkMan.getErrors().equals(""))
			throw new Exception("SMIRKS errors: " + smirks + "  "
					+ smrkMan.getErrors());
	}

	public boolean applyTransformation(SMIRKSManager smrkMan,
			IAtomContainer target) throws Exception {
		return smrkMan.applyTransformation(target, smirksReaction);
	}

	@Override
	public int hashCode() {
		return getSMIRKS().hashCode();
	}

	@Override
	public int compareTo(SMIRKSTransformation o) {
		return getSMIRKS().compareTo(o.getSMIRKS());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		return toString().equals(obj.toString());
	}

	@Override
	public String toString() {
		return getSMIRKS();
	}
}
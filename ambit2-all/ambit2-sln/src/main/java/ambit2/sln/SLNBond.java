package ambit2.sln;

import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;

public class SLNBond extends SMARTSBond {
	public SLNBond(IChemObjectBuilder builder) {
		super(builder);
	}

	public int bondType = 0;
	public SLNBondExpression bondExpression = null;

	public boolean matches(IBond bond) {
		// 1. Matching the bond type
		boolean FlagMatchBondType = SLNBondExpression.match_type(bondType, bond);
		
		if (!FlagMatchBondType)
			return false;

		// 2. Matching the bond expression
		if (bondExpression != null)
			return bondExpression.matches(bond);

		return true;
	}

	/*
	 * public String getBTString() { switch(bondType) { case
	 * SLNConst.B_TYPE_ANY://any bond return "any"; case SLNConst.B_TYPE_1:
	 * //single bond return "single" ; case SLNConst.B_TYPE_2: //double bond
	 * return "double"; case SLNConst.B_TYPE_3: //triple bond return "triple";
	 * case SLNConst.B_TYPE_aromatic: //aromatic bond return "aromatic";
	 * 
	 * default: return "userDeff"; } }
	 */
	public String toString() {
		if (bondExpression == null)
			return (SLNConst.bondTypeAttributeToSLNString(bondType, true));
		else
			return (SLNConst.bondTypeAttributeToSLNString(bondType) + bondExpression
					.toString());
	}
	
	public SLNBond clone() {
		SLNBond bond = new SLNBond(getBuilder());
		bond.bondType = bondType;
		bond.bondExpression = (bondExpression==null) ? null : bondExpression.clone();
		return bond;
	}
}

package ambit2.plugin.analogs;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.workflow.DBWorkflowContext;
import ambit2.workflow.UserInteraction;

import com.microworkflow.process.Sequence;

/**
 * Encapsulates the initial definition of a chemical structure
 * @author nina
 *
 */
public class DefineStructure extends Sequence {
	public DefineStructure() {
        addStep(new UserInteraction<IAtomContainer>(
        		DefaultChemObjectBuilder.getInstance().newMolecule(),
        		DBWorkflowContext.STRUCTURES,
        		"Define structure"));		
	}
}

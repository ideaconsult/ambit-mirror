package ambit2.rest.routers.misc;

import org.restlet.Context;

import ambit2.rest.routers.MyRouter;
import ambit2.rest.structure.diagram.AbstractDepict;
import ambit2.rest.structure.diagram.CDKDepict;
import ambit2.rest.structure.diagram.CDKDepictVariants;
import ambit2.rest.structure.diagram.CSLSDepict;
import ambit2.rest.structure.diagram.DaylightDepict;
import ambit2.rest.structure.diagram.OpenBabelDepict;
import ambit2.rest.structure.diagram.PubChemDepict;
import ambit2.rest.structure.diagram.SimilarityDepict;
import ambit2.rest.structure.smirks.ReactionDepict;
import ambit2.rest.structure.smirks.SMIRKSDepict;
import ambit2.rest.structure.tautomers.TautomersDepict;

/**
 * 2Dstructure diagram demo
 * /depict
 * /depict/cdk
 * /depict/daylight
 * /depict/cactvs
 * @author nina
 *
 */
public class DepictDemoRouter extends MyRouter {

	public DepictDemoRouter(Context context) {
		super(context);
		attachDefault(AbstractDepict.class);
		attach("/daylight",DaylightDepict.class);
		attach("/cdk",CDKDepict.class);
		attach("/cdk/{mode}",CDKDepictVariants.class);
		attach("/cactvs",CSLSDepict.class);
		attach("/obabel",OpenBabelDepict.class);
		attach("/pubchem",PubChemDepict.class);
		attach(ReactionDepict.resource,ReactionDepict.class);
		attach(SMIRKSDepict.resource,SMIRKSDepict.class);
		attach("/reaction/reactant",CDKDepictVariants.class);
		attach(TautomersDepict.resource,TautomersDepict.class);
		attach(String.format("%s/{%s}",TautomersDepict.resource,TautomersDepict.resourceKey),TautomersDepict.class);
		attach(SimilarityDepict.resource,SimilarityDepict.class);
		
	}

}

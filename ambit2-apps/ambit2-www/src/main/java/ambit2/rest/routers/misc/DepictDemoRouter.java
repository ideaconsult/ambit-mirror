package ambit2.rest.routers.misc;

import org.restlet.Context;

import ambit2.rest.routers.MyRouter;
import ambit2.rest.structure.diagram.CDKDepictVariants;
import ambit2.rest.structure.diagram.DepictionResource;
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
		
		attach(ReactionDepict.resource,ReactionDepict.class);
		attach(SMIRKSDepict.resource,SMIRKSDepict.class);
		attach("/reaction/reactant",CDKDepictVariants.class);
		attach(TautomersDepict.resource,TautomersDepict.class);
		attach(String.format("%s/{%s}",TautomersDepict.resource,TautomersDepict.resourceKey),TautomersDepict.class);
		attach(SimilarityDepict.resource,SimilarityDepict.class);
		

	}

}

package ambit2.rest.structure.tautomers;

import java.util.Vector;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.namestructure.Name2StructureProcessor;
import ambit2.rest.AmbitResource;
import ambit2.rest.structure.diagram.AbstractDepict;
import ambit2.tautomers.TautomerManager;

public class TautomersDepict extends AbstractDepict {
	public static final String resource = "/tautomer";
	protected SmilesParser parser = null;
	
	protected String getTitle(Reference ref, String smiles) throws ResourceException {
		if ((smiles==null) || "".equals(smiles)) return "<p>Empty SMILES</p>";
		StringBuilder b = new StringBuilder();
		
		IAtomContainer mol = null;
		try {
			if (parser == null) parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
			smiles = smiles.trim();
			mol = parser.parseSmiles(smiles);
			

		} catch (Exception x) {
        	Name2StructureProcessor processor = new Name2StructureProcessor();
        	try {
				mol = processor.process(smiles);
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        	} catch (Exception xx) {
        		mol = null;
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
        	}
		}
		
		b.append("<table width='100%'>");
		b.append("<tr>");
		b.append("<td>");
		String url = String.format("%s/depict/cdk/any?search=%s",
				getRequest().getRootRef(),
				Reference.encode(smiles),
				smarts==null?"":"&smarts=",
				smarts==null?"":Reference.encode(smarts)
				);
		
		b.append(AmbitResource.printWidget(
				String.format("<a href='%s' title='%s'>%s</a>",url,smiles,"SOURCE"), 
				String.format("<img id='smiles' src='%s' alt='%s' title='%s' onError=\"hideDiv('smiles')\">", 
						url,smiles==null?"":smiles,smiles==null?"":smiles)));
		b.append("</td>");
		
		Vector<IAtomContainer> resultTautomers=null;
		TautomerManager tman = new TautomerManager();
		try {
			
			tman.setStructure(mol);
			resultTautomers = tman.generateTautomersIncrementaly();
			
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
		}
		SmilesGenerator gen = new SmilesGenerator(true);
		
		for (int i = 0; i < resultTautomers.size(); i++) {		
			if (((i+1) %3 ) == 0) b.append("<tr>");
			b.append("<td>");
			
			String tautomerSmiles = gen.createSMILES(resultTautomers.get(i));
			url = String.format("%s/depict/cdk/any?search=%s%s%s",
					getRequest().getRootRef(),
					Reference.encode(tautomerSmiles),
					smarts==null?"":"&smarts=",
					smarts==null?"":Reference.encode(smarts)
					);
			
			b.append(AmbitResource.printWidget(
					String.format("<a href='%s' title='Tautomer: %s'>%d. %s</a>",url,tautomerSmiles,(i+1),"Tautomer"), 
					String.format("<img id='t%d' src='%s' alt='%s' title='%s' onError=\"hideDiv('t%d')\">", 
							i+1,url,tautomerSmiles==null?"":tautomerSmiles,
									tautomerSmiles==null?"":tautomerSmiles,i+1)));
			
			b.append("</td>");
			if (((i+1) %3)== 2) b.append("</tr>");
		}
		b.append("</table>");
		return b.toString();
	}
}

/**
 * TODO 
 * option to generate via cactvs  http://cactus.nci.nih.gov/chemical/structure/tautomers:warfarin/smiles
 * and cdk-inchi
*/
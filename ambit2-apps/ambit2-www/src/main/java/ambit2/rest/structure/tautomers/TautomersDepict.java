package ambit2.rest.structure.tautomers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.FixBondOrdersTool;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tautomers.InChITautomerGenerator;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.opentox.rest.HTTPClient;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.core.config.AmbitCONSTANTS;
import ambit2.namestructure.Name2StructureProcessor;
import ambit2.rest.AmbitResource;
import ambit2.rest.structure.diagram.AbstractDepict;
import ambit2.tautomers.TautomerManager;

/**
 * C3=C(C(C1=C(OC2=C(C1=O)C=CC=C2)O)CC(=O)C)C=CC=C3
 * @author nina
 *
 */
public class TautomersDepict extends AbstractDepict {
	public static final String resource = "/tautomer";
	public static final String resourceKey = "method";
	protected SmilesParser parser = null;
	protected _method method = _method.ambit;
	protected enum _method {
		ambit,
		cactvs,
		inchi
	}
	
	@Override
	public Representation get(Variant variant) {
		try { method = _method.valueOf(getRequest().getAttributes().get(resourceKey).toString()); } 
		catch (Exception x) { method = null; }
		return super.get(variant);
	}
	
	protected IAtomContainer getAtomContainer(String smiles) throws ResourceException {
		IAtomContainer mol = null;
		try {
	   		if (smiles.startsWith(AmbitCONSTANTS.INCHI)) {
    			InChIGeneratorFactory f = InChIGeneratorFactory.getInstance();
    			InChIToStructure c =f.getInChIToStructure(smiles, SilentChemObjectBuilder.getInstance());
    			
    			if ((c==null) || (c.getAtomContainer()==null) || (c.getAtomContainer().getAtomCount()==0)) 
    				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format("%s %s %s", c.getReturnStatus(),c.getMessage(),c.getLog()));
    			
    			mol = c.getAtomContainer();
    			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
    			mol = AtomContainerManipulator.removeHydrogens(mol);
				CDKHydrogenAdder.getInstance(mol.getBuilder()).addImplicitHydrogens(mol);
    			
	   		} else {	
				if (parser == null) parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
				smiles = smiles.trim();
				mol = parser.parseSmiles(smiles);
				boolean aromatic = false;
				if (mol!=null) 
					for (IAtom atom:mol.atoms()) if (atom.getFlag(CDKConstants.ISAROMATIC)) {
						aromatic=true;
						break;
					}
				if (aromatic) try {
					FixBondOrdersTool fbt = new FixBondOrdersTool();
					mol = fbt.kekuliseAromaticRings((IMolecule)mol);
					if (mol!=null) {
						for (IAtom atom:mol.atoms()) if (atom.getFlag(CDKConstants.ISAROMATIC)) 
							atom.setFlag(CDKConstants.ISAROMATIC,false);
						for (IBond bond: mol.bonds()) if (bond.getFlag(CDKConstants.ISAROMATIC))
							bond.setFlag(CDKConstants.ISAROMATIC,false);
					}
				} catch (Exception x) {
					//keep old mol
				}
	   		}

		} catch (Exception x) {
        	Name2StructureProcessor processor = new Name2StructureProcessor();
        	try {
				mol = processor.process(smiles);
				AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
				AtomContainerManipulator.removeHydrogens(mol);
				CDKHydrogenAdder.getInstance(mol.getBuilder()).addImplicitHydrogens(mol);

        	} catch (Exception xx) {
        		mol = null;
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
        	}
		}

		return mol;
	}
	
	@Override
	protected String getTitle(Reference ref, String... smiles) throws ResourceException {
		if ((smiles==null) || smiles.length<1 || "".equals(smiles[0])) smiles = new String[] {"warfarin"}; //demo
		String visibleSmiles = smiles==null?"":(smiles.length==0?"":smiles[0]);
		StringBuilder b = new StringBuilder();
		if (method== null ) {
			b.append("<div class='tabs' style='padding-left:0;margin-left:0px;border-width:0px'><ul style='padding-left:0;margin-left:0px'>");
			for (_method m : _method.values()) {
				b.append(String.format("<li  style='padding-left:0;margin-left:0px'><a href='%s/depict%s/%s?search=%s&headless=true'>%s tautomers</a></li>",
						getRequest().getRootRef(),TautomersDepict.resource,m,Reference.encode(visibleSmiles),m));
			}
			b.append("</ul></div>");

			return b.toString();
		}
		if ((smiles==null) || "".equals(smiles)) return "<p>Empty SMILES</p>";
		b.append("<table width='100%' >");
		/*
		b.append("<thead>");
		b.append("<caption>");
		for (_method m : _method.values()) {
			if (method.equals(m)) b.append(String.format("<h3>%s tautomers</h3>",m));
			else b.append(String.format("<a href=''>%s tautomers</a>",m));
			b.append("&nbsp;");
		}
		b.append("</caption>");
		b.append("</thead>");
				*/
		b.append("<tbody>");
		b.append("<tr>");

		IAtomContainer mol = null;
		switch (method) {
		case ambit : { 
			//ok, this is for a demo
			if ("warfarin".equals(smiles[0])) smiles[0] = "C3=C(C(C1=C(OC2=C(C1=O)C=CC=C2)O)CC(=O)C)C=CC=C3";
			mol = getAtomContainer(smiles[0]);
			b.append("<td>");
			String url = String.format("%s/depict/cdk/any?search=%s",
					getRequest().getRootRef(),
					Reference.encode(smiles[0]),
					smarts==null?"":"&smarts=",
					smarts==null?"":Reference.encode(smarts)
					);
			
			b.append(AmbitResource.printWidget(
					String.format("<a href='%s' title='%s'>%s</a>",url,smiles,"SOURCE"), 
					String.format("<img id='smiles' src='%s' alt='%s' title='%s' onError=\"hideDiv('smiles')\">", 
							url,visibleSmiles,visibleSmiles),"depictBox"));
			b.append("</td>");
			b.append(generateTautomersAmbit(mol));
			break;
		}
		case cactvs: {
			b.append("<td>");
			String url = String.format("%s/depict/cactvs?search=%s",
					getRequest().getRootRef(),
					Reference.encode(smiles[0]),
					smarts==null?"":"&smarts=",
					smarts==null?"":Reference.encode(smarts)
					);
			
			b.append(AmbitResource.printWidget(
					String.format("<a href='%s' title='%s'>%s</a>",url,smiles,"SOURCE"), 
					String.format("<img id='smiles' src='%s' alt='%s' title='%s' onError=\"hideDiv('smiles')\">", 
							url,visibleSmiles,visibleSmiles),"depictBox"));
			b.append("</td>");			
			List<String> resultTautomers = generateTautomersCactvs(smiles[0]);
			if (resultTautomers!=null)
				for (int i = 0; i < resultTautomers.size(); i++) {		
					if (((i+1) %3 ) == 0) b.append("<tr>");
					b.append("<td>");
					
					String tautomerSmiles = resultTautomers.get(i);
					
					b.append(getWidget(tautomerSmiles, i));
					
					b.append("</td>");
					if (((i+1) %3)== 2) b.append("</tr>");
				}			
			break;
		}
		case inchi: {
			if ("warfarin".equals(smiles)) smiles[0] = "C3=C(C(C1=C(OC2=C(C1=O)C=CC=C2)O)CC(=O)C)C=CC=C3";
			b.append("<td>");
			String url = String.format("%s/depict/cdk/any?search=%s",
					getRequest().getRootRef(),
					Reference.encode(smiles[0]),
					smarts==null?"":"&smarts=",
					smarts==null?"":Reference.encode(smarts)
					);
			
			b.append(AmbitResource.printWidget(
					String.format("<a href='%s' title='%s'>%s</a>",url,smiles,"SOURCE"), 
					String.format("<img id='smiles' src='%s' alt='%s' title='%s' onError=\"hideDiv('smiles')\">", 
							url,visibleSmiles,visibleSmiles),"depictBox"));
			b.append("</td>");			
			mol = getAtomContainer(visibleSmiles);
			b.append(generateTautomersInChI(mol));
		}
		}
		b.append("</tbody>");
		b.append("</table>");
		return b.toString();
	}
	protected String getWidget(String tautomerSmiles, int index) {
		String url = String.format("%s/depict/cdk/any?search=%s%s%s",
				getRequest().getRootRef(),
				Reference.encode(tautomerSmiles),
				smarts==null?"":"&smarts=",
				smarts==null?"":Reference.encode(smarts)
				);
		
		return AmbitResource.printWidget(
				String.format("<a href='%s' title='Tautomer: %s'>%d. %s</a>",url,tautomerSmiles,(index+1),"Tautomer"), 
				String.format("<img id='t%d' src='%s' alt='%s' title='%s' onError=\"hideDiv('t%d')\">", 
						index+1,url,tautomerSmiles==null?"":tautomerSmiles,
								tautomerSmiles==null?"":tautomerSmiles,index+1),"depictBox");
	}
	protected String generateTautomersAmbit(IAtomContainer mol) throws ResourceException {
		StringBuilder b = new StringBuilder();
		List<IAtomContainer> resultTautomers=null;
		TautomerManager tman = new TautomerManager();
		try {
			
			tman.setStructure(mol);
			resultTautomers = tman.generateTautomersIncrementaly();
			
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
		}
		SmilesGenerator gen = new SmilesGenerator(false);
		
		for (int i = 0; i < resultTautomers.size(); i++) {		
			if (((i+1) %3 ) == 0) b.append("<tr>");
			b.append("<td>");
			
			String tautomerSmiles = gen.createSMILES(resultTautomers.get(i));
			
			b.append(getWidget(tautomerSmiles, i));
			
			b.append("</td>");
			if (((i+1) %3)== 2) b.append("</tr>");
		}

		return b.toString();
	}
	
	
	protected List<String> generateTautomersCactvs(String value) throws ResourceException {
		final String cactvsURI = "http://cactus.nci.nih.gov/chemical/structure/tautomers:%s/smiles";
		HTTPClient cli=null;
		InputStream in = null;
		List<String> smiles = new ArrayList<String>();
		try {
			cli = new HTTPClient(String.format(cactvsURI,Reference.encode(value)));
			cli.get();
			if (cli.getStatus()==200) {
				in = cli.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while ((line = reader.readLine())!=null) {
					if (!"".equals(line.trim())) 
						smiles.add(line.trim());
				}
				
			}
			return smiles;
		} catch (Exception x) {
			return null;
		} finally {
			try { if (in != null) in.close();} catch (Exception x) {}
			try { if (cli != null) cli.release();} catch (Exception x) {}
		}		
	}
	protected String generateTautomersInChI(IAtomContainer mol) throws ResourceException {
		 InChITautomerGenerator itg = new InChITautomerGenerator(); 
			StringBuilder b = new StringBuilder();
		 try {
			 
			 List<IAtomContainer> resultTautomers = itg.getTautomers(mol);
			 SmilesGenerator gen = new SmilesGenerator(false);
				for (int i = 0; i < resultTautomers.size(); i++) {		
					if (((i+1) %3 ) == 0) b.append("<tr>");
					b.append("<td>");
					
					String tautomerSmiles = gen.createSMILES(resultTautomers.get(i));
					
					b.append(getWidget(tautomerSmiles, i));
					
					b.append("</td>");
					if (((i+1) %3)== 2) b.append("</tr>");
				}

				return b.toString();
		 } catch (Exception x) {
			 b.append("<td>");
			 b.append(AmbitResource.printWidget("Error",x.getMessage(),"depictBox"));
			 b.append("</td>");
			 //throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
		 }
		 return b.toString();
	}
}

/**
 * TODO 
 * option to generate via cactvs  http://cactus.nci.nih.gov/chemical/structure/tautomers:warfarin/smiles
 * and cdk-inchi
*/
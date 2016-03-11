package ambit2.rest.structure.tautomers;

import java.util.List;

import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.core.config.AmbitCONSTANTS;
import ambit2.namestructure.Name2StructureProcessor;
import ambit2.rest.AmbitResource;
import ambit2.rest.structure.diagram.AbstractDepict;
import ambit2.tautomers.TautomerConst;
import ambit2.tautomers.processor.TautomerProcessor;

/**
 * C3=C(C(C1=C(OC2=C(C1=O)C=CC=C2)O)CC(=O)C)C=CC=C3
 * 
 * @author nina
 * 
 */
public class TautomersDepict extends AbstractDepict {
	public static final String resource = "/tautomer";
	public static final String resourceKey = "method";
	protected SmilesParser parser = null;
	protected _method method = _method.ambit;

	protected enum _method {
		ambit
		// , cactvs, inchi
	}

	@Override
	public Representation get(Variant variant) {
		try {
			method = _method.valueOf(getRequest().getAttributes()
					.get(resourceKey).toString());
		} catch (Exception x) {
			method = null;
		}
		return super.get(variant);
	}

	protected IAtomContainer getAtomContainer(String smiles)
			throws ResourceException {
		IAtomContainer mol = null;
		try {
			if (smiles.startsWith(AmbitCONSTANTS.INCHI)) {
				InChIGeneratorFactory f = InChIGeneratorFactory.getInstance();
				InChIToStructure c = f.getInChIToStructure(smiles,
						SilentChemObjectBuilder.getInstance());

				if ((c == null) || (c.getAtomContainer() == null)
						|| (c.getAtomContainer().getAtomCount() == 0))
					throw new ResourceException(
							Status.CLIENT_ERROR_BAD_REQUEST, String.format(
									"%s %s %s", c.getReturnStatus(),
									c.getMessage(), c.getLog()));

				mol = c.getAtomContainer();
				AtomContainerManipulator
						.percieveAtomTypesAndConfigureAtoms(mol);
				mol = AtomContainerManipulator.removeHydrogens(mol);
				CDKHydrogenAdder.getInstance(mol.getBuilder())
						.addImplicitHydrogens(mol);

			} else {
				if (parser == null)
					parser = new SmilesParser(
							SilentChemObjectBuilder.getInstance());
				smiles = smiles.trim();
				mol = parser.parseSmiles(smiles);
				AtomContainerManipulator
						.percieveAtomTypesAndConfigureAtoms(mol);
			}

		} catch (Exception x) {
			Name2StructureProcessor processor = new Name2StructureProcessor();
			try {
				mol = processor.process(smiles);
				AtomContainerManipulator
						.percieveAtomTypesAndConfigureAtoms(mol);
				AtomContainerManipulator.removeHydrogens(mol);
				CDKHydrogenAdder.getInstance(mol.getBuilder())
						.addImplicitHydrogens(mol);

			} catch (Exception xx) {
				mol = null;
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
						x.getMessage(), x);
			}
		}

		return mol;
	}

	@Override
	protected String getTitle(Reference ref, String... smiles)
			throws ResourceException {
		if ((smiles == null) || smiles.length < 1 || "".equals(smiles[0]))
			smiles = new String[] { "warfarin" }; // demo
		String visibleSmiles = smiles == null ? "" : (smiles.length == 0 ? ""
				: smiles[0]);
		StringBuilder b = new StringBuilder();
		if (method == null) {
			b.append("<div class='tabs' style='padding-left:0;margin-left:0px;border-width:0px'><ul style='padding-left:0;margin-left:0px'>");
			for (_method m : _method.values()) {
				b.append(String
						.format("<li  style='padding-left:0;margin-left:0px'><a href='%s/demo%s/%s?search=%s&headless=true'>%s tautomers</a></li>",
								getRequest().getRootRef(),
								TautomersDepict.resource, m,
								Reference.encode(visibleSmiles), m));
			}
			b.append("</ul></div>");

			return b.toString();
		}
		if ((smiles == null) || "".equals(smiles))
			return "<p>Empty SMILES</p>";

		IAtomContainer mol = null;
		switch (method) {
		case ambit: {
			b.append("<table id='ttm' class='ambittable jtoxkit' width='100%' style='font-size:75%'>");
			b.append("<thead><th>Diagram</th><th>Rank</th><th>InChIKey</th><th>InChI</th><th>SMILES</th></thead>");
			b.append("<tbody>");
			b.append("<tr>");

			mol = getAtomContainer(smiles[0]);
			b.append("<td>");
			String url = String.format(
					"%s/depict/cdk?search=%s&media=image/png", getRequest()
							.getRootRef(), Reference.encode(smiles[0]),
					smarts == null ? "" : "&smarts=", smarts == null ? ""
							: Reference.encode(smarts));

			b.append(String
					.format("<a href='%s&w=400&h=400' target=_blank ><img id='smiles' style='border-color: red;' border='3' src='%s&w=200&h=200' alt='%s' title='%s' onError=\"hideDiv('smiles')\"></a>",
							url,url, visibleSmiles, "source"));
			b.append("</td><td></td><td></td>");
			b.append("<td>");
			b.append(smiles[0].startsWith("InChI")?smiles[0]:"");
			b.append("</td>");
			b.append("<td>");
			b.append(smiles[0].startsWith("InChI")?"":smiles[0]);
			b.append("</td>");
			b.append("</tr>");
			b.append(generateTautomersAmbit(mol));
			break;
		}
		default: {
			b.append("<table width='100%' >");
			b.append("<tbody>");
			b.append("<tr>");
		}
		}
		b.append("</tbody>");
		b.append("</table>");
		b.append("<script type='text/javascript'>$(document).ready(function(){$('.ambittable').DataTable();});</script>");
		return b.toString();
	}

	protected String getWidget(String tautomerSmiles, int index) {
		return getWidget(tautomerSmiles, index, "");
	}

	protected String getWidget(String tautomerSmiles, int index, String title) {
		String url = String.format(
				"%s/depict/cdk?search=%s%s%s&media=image/png", getRequest()
						.getRootRef(), Reference.encode(tautomerSmiles),
				smarts == null ? "" : "&smarts=", smarts == null ? ""
						: Reference.encode(smarts));

		return AmbitResource
				.printWidget(
						String.format(
								"<a href='%s' title='Tautomer: %s'>%d. %s %s</a>",
								url, tautomerSmiles, (index + 1), "Tautomer",
								title),
						String.format(
								"<img id='t%d' src='%s' alt='%s' title='%s' onError=\"hideDiv('t%d')\">",
								index + 1, url, tautomerSmiles == null ? ""
										: tautomerSmiles,
								tautomerSmiles == null ? "" : tautomerSmiles,
								index + 1), "depictBox");
	}

	protected String generateTautomersAmbit(IAtomContainer mol)
			throws ResourceException {
		final StringBuilder b = new StringBuilder();
		List<IAtomContainer> resultTautomers = null;
		TautomerProcessor tproc = new TautomerProcessor(null);
		tproc.getTautomerManager().FlagRegisterOnlyBestRankTautomers = false;
		IProcessor<IAtomContainer, IAtomContainer> callback = new DefaultAmbitProcessor<IAtomContainer, IAtomContainer>() {
			SmilesGenerator gen = SmilesGenerator.isomeric();
			int index = 0;

			@Override
			public IAtomContainer process(IAtomContainer ttm) throws Exception {
				b.append("<tr>");
				try {
					String p = ttm.getProperties().toString();
					String tautomerSmiles = gen.create(ttm);
					b.append("<td>");
					// b.append(getWidget(tautomerSmiles, index, ""));

					String url = String
							.format("%s/depict/cdk?search=%s%s%s&media=image/png",
									getRequest().getRootRef(),
									Reference.encode(tautomerSmiles),
									smarts == null ? "" : "&smarts=",
									smarts == null ? "" : Reference
											.encode(smarts));

					b.append(String
							.format("<a href='%s&w=400&h=400' target=_blank><img id='t%d' src='%s&w=200&h=200' alt='%s' title='%s' onError=\"hideDiv('t%d')\"></a>",
									url,index + 1, url, tautomerSmiles == null ? ""
											: tautomerSmiles,
									tautomerSmiles == null ? ""
											: tautomerSmiles, index + 1));

					b.append("</td>");
					b.append("<td>");
					b.append(ttm.getProperty(TautomerConst.CACTVS_ENERGY_RANK)==null?"":ttm.getProperty(TautomerConst.CACTVS_ENERGY_RANK));
					b.append("</td>");
					b.append("<td>");
					b.append(ttm.getProperty(Property.opentox_InChIKey)==null?"":ttm.getProperty(Property.opentox_InChIKey));
					b.append("</td>");
					b.append("<td>");
					b.append(ttm.getProperty(Property.opentox_InChI)==null?"":ttm.getProperty(Property.opentox_InChI));
					b.append("</td>");
					b.append("<td>");
					b.append(tautomerSmiles==null?"":tautomerSmiles);
					b.append("</td>");
				} catch (Exception x) {
					b.append(x.getMessage());
				}
				b.append("</tr>");
				index++;
				return ttm;
			}
		};
		tproc.setCallback(callback);
		try {
			IAtomContainer best = tproc.process(mol);
		} catch (Exception x) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
					x.getMessage(), x);
		}

		return b.toString();
	}

}

/**
 * TODO option to generate via cactvs
 * http://cactus.nci.nih.gov/chemical/structure/tautomers:warfarin/smiles and
 * cdk-inchi
 */

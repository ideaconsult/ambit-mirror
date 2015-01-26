package ambit2.rest.structure.diagram;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Writer;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.r.AbstractReporter;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.cdk.renderer.selection.SingleSelection;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.core.data.MoleculeTools;
import ambit2.rendering.CompoundImageTools;
import ambit2.rendering.CompoundImageTools.Mode2D;
import ambit2.rest.AmbitResource;
import ambit2.rest.StringConvertor;
import ambit2.rest.query.QueryResource;
import ambit2.rest.query.StructureQueryResource.QueryType;
import ambit2.smarts.query.ISmartsPattern;
import ambit2.smarts.query.SmartsPatternAmbit;

/**
 * 2D depiction based on CDK
 * 
 * @author nina
 * 
 */
public class CDKDepict extends AbstractDepict implements ISmartsDepiction {
	protected CompoundImageTools depict = new CompoundImageTools();

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		depict.setImageSize(new Dimension(410, 210));
		this.getVariants().add(new Variant(MediaType.IMAGE_PNG));
	}

	@Override
	protected BufferedImage getImage(String smiles, int w, int h,
			String recordType, QueryType type) throws ResourceException {
		try {
			depict.setImageSize(new Dimension(w, h));
			if (type != null && QueryType.mol.equals(type)) {
				IAtomContainer mol = MoleculeTools.readMolfile(smiles);
				return depict.getImage(mol,
						(smarts == null) || ("".equals(smarts.trim())) ? null
								: new SmartsPatternSelector(smarts), true,
						false, false);

			} else {
				if (depict.getParser() == null)
					depict.setParser(new SmilesParser(SilentChemObjectBuilder
							.getInstance()));

				if (displayMode == null) {
					return depict
							.generateImage(
									smiles,
									(smarts == null)
											|| ("".equals(smarts.trim())) ? null
											: new SmartsPatternSelector(smarts),
									false, false, null);
				} else
					return depict
							.generateImage(
									smiles,
									(smarts == null)
											|| ("".equals(smarts.trim())) ? null
											: new SmartsPatternSelector(smarts),
									false, false, displayMode);
			}
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
					x.getMessage(), x);
		}
	}

	@Override
	public Representation get(Variant variant) {
		try {
			Form form = getParams();
			smiles = form.getValuesArray(QueryResource.search_param);
			if ((smiles == null) || (smiles.length < 1))
				smiles = new String[] { null };
			setSmarts(form.getFirstValue("smarts"));

			if (MediaType.TEXT_HTML.equals(variant.getMediaType())) {
				StringConvertor convertor = new StringConvertor(
						new AbstractReporter<String, Writer>() {
							/**
						     * 
						     */
						    private static final long serialVersionUID = -1232605257681949242L;

							public void close() throws Exception {
							};

							public Writer process(String target)
									throws AmbitException {
								try {
									AmbitResource.writeTopHeader(
											output,
											smiles[0] == null ? "2D structure diagram"
													: smiles[0], getRequest(),
											getResourceRef(getRequest()), "",
											null);
									writeSearchForm(output, smiles[0],
											getRequest(), "", Method.GET,
											params);
									output.write(target);
									AmbitResource.writeHTMLFooter(output,
											smiles[0], getRequest());
								} catch (Exception x) {
								}
								return output;
							};
						}, MediaType.TEXT_HTML);
				return convertor.process(getTitle(getResourceRef(getRequest()),
						smiles));
			} else {
				return process(variant);
			}
		} catch (Exception x) {

		}
		return null;

	}

	@Override
	protected String getTitle(Reference ref, String... smiles)
			throws ResourceException {
		if (smiles == null)
			return getGPlusSnippet();
		StringBuilder b = new StringBuilder();
		b.append("<table width='100%'>");

		Reference uri = ((Reference) ref).clone();
		uri.setQuery(null);

		for (Mode2D mode : Mode2D.values()) {
			if ((mode.ordinal() % 2) == 0)
				b.append("<tr>");
			b.append("<td>");
			String url = String.format("%s%s%s?search=%s%s%s", uri, uri
					.toString().endsWith("/") ? "" : "/", mode.name(),
					Reference.encode(smiles[0]), smarts == null ? ""
							: "&smarts=",
					smarts == null ? "" : Reference.encode(smarts));

			b.append(AmbitResource.printWidget(
					String.format("<a href='%s' target='%s' title='%s'>%s</a>",
							url, mode.name(), mode.getDescription(),
							mode.toString()),
					String.format(
							"<img id='%s' src='%s' alt='%s' title='%s' onError=\"hideDiv('%s')\">",
							mode.name(), url, smiles == null ? "" : smiles,
							smiles == null ? "" : smiles, mode.name())));

			b.append("</td>");
			if ((mode.ordinal() % 2) == 1)
				b.append("</tr>");
		}
		b.append("</table>");
		b.append(getGPlusSnippet());
		return b.toString();

	}

	@Override
	public void writeSearchForm(Writer w, String title, Request request,
			String meta, Method method, Form params) throws IOException {
		Reference baseReference = request == null ? null : request.getRootRef();
		w.write("<table width='100%' bgcolor='#ffffff'>");
		w.write("<tr>");
		w.write("<td align='left' width='256px'>");
		w.write(String
				.format("<a href=\"http://ambit.sourceforge.net/intro.html\"><img src='%s/images/ambit-logo.png' width='256px' alt='%s' title='%s' border='0'></a>\n",
						baseReference, "AMBIT", baseReference));
		w.write("</td>");
		w.write("<td align='center'>");
		String query_smiles = "";
		try {
			Form form = getParams(params, request);
			if ((form != null) && (form.size() > 0))
				query_smiles = form.getFirstValue(QueryResource.search_param);
			else
				query_smiles = null;
		} catch (Exception x) {
			query_smiles = "";
		}

		w.write(String.format("<form action='' method='%s'>\n", method));
		w.write("<table width='100%'>");
		w.write("<tr>");
		w.write(String.format("<th><label for='%s'>%s</label></th>",
				QueryResource.search_param, "SMILES or InChI"));
		w.write("<td>");
		w.write(String.format("<input name='%s' size='80' value='%s'>\n",
				QueryResource.search_param, query_smiles == null ? ""
						: query_smiles));
		w.write("</td>");
		w.write("<td><input type='submit' value='Display'></td>");
		w.write("</tr>\n");

		w.write("<tr>");
		w.write(String.format("<th><label for='%s'>%s</label></th>", "smarts",
				"SMARTS (optional)"));
		w.write("<td>");
		w.write(String
				.format("<input name='%s' size='80' value='%s' title='Highlights the substructure, specified by SMARTS'>",
						"smarts", getSmarts() == null ? "" : getSmarts()));
		w.write("</td>");
		w.write("<td>");
		w.write("&nbsp;</td></tr>\n");
		w.write("</table>");
		// w.write(baseReference.toString());

		w.write("</form>\n");

		w.write("</td>");
		w.write("<td align='left' valign='bottom' width='256px'>");
		w.write(AmbitResource.disclaimer);
		w.write("</td>");
		w.write("</tr></table>");

		w.write("<hr>");

	}

	@Override
	protected String getGPlusSnippet() {
		return printGPlusSnippet(
				"The Chemistry Development Kit structure diagram",
				"Chemical structure diagram, generated by http://cdk.sf.net",
				null);
	}

}

class SmartsPatternSelector implements
		IProcessor<IAtomContainer, IChemObjectSelection> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2781667048103591227L;
	protected String smarts;

	public SmartsPatternSelector(String smarts) {
		this.smarts = smarts;
	}

	public long getID() {
		return 0;
	}

	public boolean isEnabled() {
		return true;
	}

	public IChemObjectSelection process(IAtomContainer target)
			throws AmbitException {
		ISmartsPattern pattern = new SmartsPatternAmbit(smarts);
		if (pattern.match(target) > 0) {
			IAtomContainer selected = pattern.getMatchingStructure(target);
			return new SingleSelection<IAtomContainer>(selected);
		} else
			return null;
	}

	public void setEnabled(boolean value) {

	}

	@Override
	public void open() throws Exception {
	}

	@Override
	public void close() throws Exception {
	}
}

package ambit2.rest.structure.diagram;

import java.io.IOException;
import java.io.Writer;
import java.util.BitSet;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.similarity.Tanimoto;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.resource.ResourceException;

import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.helper.CDKHueckelAromaticityDetector;
import ambit2.namestructure.Name2StructureProcessor;
import ambit2.rest.AmbitResource;
import ambit2.rest.query.QueryResource;

public class SimilarityDepict extends AbstractDepict {
	public static final String resource = "/pairwise";
	protected int max = 3;
	protected SmilesParser parser = null;
	protected Fingerprinter fingerprinter;

	@Override
	protected String getTitle(Reference ref, String... smiles)
			throws ResourceException {
		final Reference root = (Reference) ref.getParentRef().clone();
		root.setQuery(null);
		final String uri = String.format("%scdk", root);
		final String style = "depictBox";

		final StringBuilder b = new StringBuilder();
		b.append("<table>");
		int n = smiles.length > max ? max : smiles.length;

		BitSet[] bitset = getBitSet(smiles);

		for (int i = 0; i < (n - 1); i++) {

			for (int j = i + 1; j < n; j++) {
				b.append("<tr>");
				b.append("<td>");
				b.append(getSmilesWidget(smiles[i], uri, style, i + 1));
				b.append("</td>");
				b.append("<td>");
				b.append(getSmilesWidget(smiles[j], uri, style, j + 1));
				b.append("</td>");
				b.append("<td>");

				double similarity = Double.NaN;
				try {
					if (bitset[i] != null && bitset[j] != null)
						similarity = Tanimoto.calculate(bitset[i], bitset[j]);
				} catch (Exception x) {
				}
				b.append(AmbitResource.printWidget("Similarity",
						String.format("Tanimoto = %4.3f", similarity), style));
				b.append("</td>");
				b.append("</tr>");
			}

		}
		b.append("</table>");
		return b.toString();
	}

	protected String getSmilesWidget(String smiles, String uri, String style,
			int id) {

		return AmbitResource
				.printWidget(
						String.format(
								"<a href='%s?search=%s%s%s'>%s</a>&nbsp;<span style='float:right;'>%s</span>",
								uri,
								smiles == null ? "" : Reference.encode(smiles),
								smarts == null ? "" : "&smarts=",
								smarts == null ? "" : Reference.encode(smarts),
								"CDK depiction",
								String.format(AbstractDepict.gplus, uri)),
						String.format(
								"<img id='cdk_%d' src='%s/any?search=%s&smarts=%s' alt='%s' title='%s' onError=\"hideDiv('cdk_%d')\">",
								id, uri,
								smiles == null ? "" : Reference.encode(smiles),
								smarts == null ? "" : Reference.encode(smarts),
								smiles == null ? "" : smiles,
								smiles == null ? "" : smiles, id), style);
	}

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
		String[] query_smiles = null;
		try {
			Form form = getParams(params, request);
			if ((form != null) && (form.size() > 0))
				query_smiles = form.getValuesArray(QueryResource.search_param);
			else
				query_smiles = null;
		} catch (Exception x) {
			query_smiles = null;
		}

		if ((query_smiles == null) || (query_smiles.length < 2)) {
			w.write("<p>At least two smiles should be provided!</p>");
			query_smiles = new String[] { null, null };
		}
		int n = smiles.length > max ? max : smiles.length;
		w.write(String.format("<form action='' method='%s'>\n", method));
		w.write("<table width='100%'>");

		for (int i = 0; i < n; i++) {
			w.write("<tr>");
			w.write(String.format("<th width='20%%'>%s", "SMILES or InChI"));
			/*
			 * w.write(String.format(
			 * "&nbsp;<input type='image' src=\"%s/images/edit.png\" title='Draw molecule' onClick='startEditor(\"%s\");'>"
			 * , request.getRootRef(),request.getRootRef()));
			 */
			w.write("</th>");
			w.write("<td>");
			w.write(String.format("<input name='%s' size='70' value='%s'>\n",
					QueryResource.search_param, query_smiles[i] == null ? ""
							: query_smiles[i]));
			w.write("</td>");
			if (i == 0)
				w.write("<td><input type='submit' value='Calculate similarity'></td>");
			w.write("</tr>");
		}

		w.write("</table>");
		w.write("</form>\n");

		w.write("</td>");
		w.write("<td align='left' valign='bottom' width='256px'>");
		w.write(AmbitResource.disclaimer);

		w.write("</td>");
		w.write("</tr></table>");
		w.write("<hr>");

	}

	protected BitSet[] getBitSet(String[] smiles) {

		int n = smiles.length > max ? max : smiles.length;
		BitSet[] bitset = new BitSet[n];
		for (int i = 0; i < n; i++)
			try {
				IAtomContainer molecule = getMolecule(smiles[i]);
				AtomContainerManipulator
						.percieveAtomTypesAndConfigureAtoms(molecule);
				CDKHueckelAromaticityDetector.detectAromaticity(molecule);
				molecule = AtomContainerManipulator
						.removeHydrogensPreserveMultiplyBonded(molecule);
				if (fingerprinter == null)
					fingerprinter = new Fingerprinter();
				bitset[i] = fingerprinter.getBitFingerprint(molecule)
						.asBitSet();
			} catch (Exception x) {
				bitset[i] = null;
			}
		return bitset;

	}

	protected IAtomContainer getMolecule(String value) throws CDKException {
		if (value.startsWith(AmbitCONSTANTS.INCHI)) {
			InChIGeneratorFactory f = InChIGeneratorFactory.getInstance();
			InChIToStructure c = f.getInChIToStructure(value,
					SilentChemObjectBuilder.getInstance());

			if ((c == null) || (c.getAtomContainer() == null)
					|| (c.getAtomContainer().getAtomCount() == 0))
				return null;
			return c.getAtomContainer();
		} else {
			if (parser == null)
				parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
			IAtomContainer molecule = null;
			try {
				molecule = parser.parseSmiles(value);
			} catch (InvalidSmilesException x) {
				Name2StructureProcessor processor = new Name2StructureProcessor();
				try {
					molecule = processor.process(value);
				} catch (Exception xx) {
					molecule = null;
					throw x;
				}
			}
			return molecule;
		}
	}
}
package ambit2.rest.structure.tautomers;

import org.restlet.data.Reference;

import ambit2.rest.structure.diagram.AbstractDepict;

public class TautomersDepict extends AbstractDepict {
	public static final String resource = "/tautomer";
	protected String getTitle(Reference ref, String smiles) {
		return "<p>Coming soon</p>";

	}
}

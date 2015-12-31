package ambit2.rest;

public class DataResources {
	public final static String compound_resource = OpenTox.URI.compound
			.getURI();
	public final static String idcompound_resource = OpenTox.URI.compound
			.getKey();
	public final static String compoundID_resource = OpenTox.URI.compound
			.getResourceID();

	public final static String conformerKey_resource = "/conformer";
	public final static String conformer_resource = String.format("%s%s",
			DataResources.compoundID_resource, conformerKey_resource);
	public final static String idconformer_resource = "idconformer";
	// public final static String conformers =
	// String.format("%s%s",compoundID,conformerKey);
	public final static String conformerID_resource = String.format(
			"%s%s/{%s}", DataResources.compoundID_resource,
			conformerKey_resource, idconformer_resource);

	private DataResources() {
	}
}

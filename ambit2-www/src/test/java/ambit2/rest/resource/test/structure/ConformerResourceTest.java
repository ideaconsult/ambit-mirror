package ambit2.rest.resource.structure;


public class ConformerResourceTest extends CompoundResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/compound/10/conformer/100214", port);
	}
}

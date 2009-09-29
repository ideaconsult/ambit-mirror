package ambit2.rest.dataset;

import org.ho.yaml.YamlEncoder;
import org.restlet.data.Reference;

import ambit2.db.SourceDataset;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryURIReporter;

/**
 * Generates YAML representation for {@link SourceDataset}
 * @author nina
 *
 * @param <Q>
 */
public class DatasetYamlReporter<Q extends IQueryRetrieval<SourceDataset>> extends QueryYAMLReporter<SourceDataset, Q> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -42331029292913894L;
	
	public DatasetYamlReporter() {
		this(null);
	}
	public DatasetYamlReporter(Reference baseRef) {
		super();
	}	

	@Override
	protected QueryURIReporter createURIReporter(Reference reference) {
		return new DatasetURIReporter<IQueryRetrieval<SourceDataset>>(reference);
	}	
	@Override
	public void processItem(SourceDataset item, YamlEncoder output) {
		output.writeObject(item.getName());
		output.writeObject(uriReporter.getURI(item));
	}
	@Override
	public void footer(YamlEncoder output, Q query) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void header(YamlEncoder output, Q query) {
		// TODO Auto-generated method stub
		
	}
}

package ambit2.rest.dataset;

import net.idea.modbcum.i.exceptions.AmbitException;

import org.ho.yaml.YamlEncoder;
import org.restlet.Request;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;

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
	
	public DatasetYamlReporter(ResourceDoc doc) {
		this(null,doc);
	}
	public DatasetYamlReporter(Request request,ResourceDoc doc) {
		super(request,doc);
	}	

	@Override
	protected QueryURIReporter createURIReporter(Request reference,ResourceDoc doc) {
		return new DatasetURIReporter<IQueryRetrieval<ISourceDataset>>(reference,doc);
	}	
	@Override
	public Object processItem(SourceDataset item) throws AmbitException  {
		output.writeObject(item.getName());
		output.writeObject(uriReporter.getURI(item));
		return null;
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

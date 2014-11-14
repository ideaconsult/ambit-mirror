package ambit2.rest.substance.property;

import java.io.Writer;
import java.util.Iterator;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.restnet.c.reporters.CatalogURIReporter;

import org.restlet.Request;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.json.JSONUtils;
import ambit2.rest.substance.SubstanceURIReporter;

/**
 * Substance composition JSON serialization
 * @author nina
 *
 * @param <Q>
 */
public class EffectRecordJSONReporter extends CatalogURIReporter<EffectRecord<String, IParams,String>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 410930501401847402L;
	protected String comma = null;
	protected String jsonpCallback = null;

	protected final SubstanceURIReporter<IQueryRetrieval<SubstanceRecord>> substanceReporter;
	
	public EffectRecordJSONReporter(Request request, String jsonpCallback) {
		super();
		substanceReporter = new SubstanceURIReporter<IQueryRetrieval<SubstanceRecord>>(request);
		this.jsonpCallback = JSONUtils.jsonSanitizeCallback(jsonpCallback);
				
	}
	@Override
	public void processItem(EffectRecord<String, IParams, String> item,
			Writer output) {
		try {
			if (item==null) return;
			if (comma!=null) getOutput().write(comma);
			getOutput().write(item.toString());
			comma = ",";
		} catch (Exception x) {
			//logger.log(Level.WARNING,x.getMessage(),x);
		}
	}

	@Override
	public void footer(Writer output,
			Iterator<EffectRecord<String, IParams, String>> query) {
		try {
			output.write("\n]\n}");
			
			if (jsonpCallback!=null) {
				output.write(");");
			}			
		} catch (Exception x) {}
	};
	
	@Override
	public void header(Writer output,
			Iterator<EffectRecord<String, IParams, String>> query) {
		try {
			if (jsonpCallback!=null) {
				output.write(jsonpCallback);
				output.write("(");
			}					
			output.write("{\"effects\":[\n");
		} catch (Exception x) {
			x.printStackTrace();
		}
	};
	
	@Override
	public String getFileExtension() {
		return null;//"json";
	}
	@Override
	public void open() throws DbAmbitException {
		
	}
}

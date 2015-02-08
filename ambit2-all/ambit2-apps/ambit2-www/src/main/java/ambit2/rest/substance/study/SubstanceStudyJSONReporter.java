package ambit2.rest.substance.study;

import java.io.Writer;
import java.util.logging.Level;

import net.idea.modbcum.i.IParameterizedQuery;
import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import net.idea.modbcum.r.QueryReporter;

import org.restlet.Request;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.json.JSONUtils;
import ambit2.db.processors.MasterDetailsProcessor;
import ambit2.db.substance.study.ReadEffectRecord;
import ambit2.rest.substance.SubstanceURIReporter;

/**
 * Substance composition JSON serialization
 * 
 * @author nina
 * 
 * @param <Q>
 */
public class SubstanceStudyJSONReporter<Q extends IQueryRetrieval<ProtocolApplication>> extends
	QueryReporter<ProtocolApplication, Q, Writer> {
    /**
	 * 
	 */
    private static final long serialVersionUID = 410930501401847402L;
    protected String comma = null;
    protected String jsonpCallback = null;

    protected final SubstanceURIReporter<IQueryRetrieval<SubstanceRecord>> substanceReporter;

    public SubstanceStudyJSONReporter(Request request, String jsonpCallback) {
	super();
	substanceReporter = new SubstanceURIReporter<IQueryRetrieval<SubstanceRecord>>(request);
	this.jsonpCallback = JSONUtils.jsonSanitizeCallback(jsonpCallback);
	getProcessors().clear();
	IQueryRetrieval<EffectRecord<String, String, String>> queryP = new ReadEffectRecord();
	MasterDetailsProcessor<ProtocolApplication, EffectRecord<String, String, String>, IQueryCondition> effectReader = new MasterDetailsProcessor<ProtocolApplication, EffectRecord<String, String, String>, IQueryCondition>(
		queryP) {
	    /**
							     * 
							     */
	    private static final long serialVersionUID = 3254879775478674022L;

	    @Override
	    protected void configureQuery(
		    ProtocolApplication target,
		    IParameterizedQuery<ProtocolApplication, EffectRecord<String, String, String>, IQueryCondition> query)
		    throws AmbitException {
		super.configureQuery(target, query);
	    }

	    @Override
	    protected ProtocolApplication processDetail(ProtocolApplication master, EffectRecord detail)
		    throws Exception {
		if (detail != null)
		    master.addEffect(detail);
		return master;
	    }
	};
	effectReader.setCloseConnection(false);
	getProcessors().add(effectReader);
	getProcessors().add(new DefaultAmbitProcessor<ProtocolApplication, ProtocolApplication>() {
	    /**
		     * 
		     */
	    private static final long serialVersionUID = 3463829174956429308L;

	    public ProtocolApplication process(ProtocolApplication target) throws AmbitException {
		processItem(target);
		return target;
	    };
	});
    }

    @Override
    public Object processItem(ProtocolApplication item) throws AmbitException {
	try {
	    if (item == null)
		return null;
	    if (comma != null)
		getOutput().write(comma);
	    getOutput().write(item.toString());
	    comma = ",";
	} catch (Exception x) {
	    logger.log(Level.WARNING, x.getMessage(), x);
	}
	return item;
    }

    @Override
    public void footer(Writer output, Q query) {
	try {
	    output.write("\n]\n}");

	    if (jsonpCallback != null) {
		output.write(");");
	    }
	} catch (Exception x) {
	}
    };

    @Override
    public void header(Writer output, Q query) {
	try {
	    if (jsonpCallback != null) {
		output.write(jsonpCallback);
		output.write("(");
	    }
	    output.write("{\"study\":[\n");
	} catch (Exception x) {
	    x.printStackTrace();
	}
    };

    @Override
    public String getFileExtension() {
	return null;// "json";
    }

    @Override
    public void open() throws DbAmbitException {

    }
}

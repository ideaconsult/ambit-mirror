package ambit2.rest.bundle;

import java.io.OutputStream;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import net.idea.modbcum.r.QueryReporter;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.substance.study.SubstanceStudyDetailsProcessor;
import ambit2.export.isa.v1_0.ISAJsonExporter1_0;

/**
 * Uses{@link ISAJsonExporter1_0}
 * 
 * @author nina
 * 
 * @param <Q>
 */
public class BundleISA_JSONReporter<Q extends IQueryRetrieval<SubstanceRecord>>
		extends QueryReporter<SubstanceRecord, Q, OutputStream> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -550357518522507950L;
	private ISAJsonExporter1_0 exporter;
	protected SubstanceEndpointsBundle[] bundles;

	public BundleISA_JSONReporter(String baseRef,
			SubstanceEndpointsBundle[] bundles) {
		super();
		this.bundles = bundles;
		SubstanceStudyDetailsProcessor paReader = new SubstanceStudyDetailsProcessor();

		getProcessors().clear();
		getProcessors().add(paReader);
		getProcessors().add(
				new DefaultAmbitProcessor<SubstanceRecord, SubstanceRecord>() {
					@Override
					public SubstanceRecord process(SubstanceRecord target)
							throws Exception {
						processItem(target);
						return target;
					};
				});
	}

	@Override
	public void open() throws Exception {
		super.open();
	}

	@Override
	public void footer(OutputStream arg0, Q arg1) {
		if (exporter != null)
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(Inclusion.NON_EMPTY);
				mapper.writerWithDefaultPrettyPrinter().writeValue(getOutput(),
						exporter.getOutput());
			} catch (Exception x) {
				logger.log(Level.WARNING, x.getMessage());
			} finally {
				try {
					exporter.close();
				} catch (Exception x) {
				}
				exporter = null;
			}

	}

	@Override
	public void header(OutputStream arg0, Q arg1) {
		SubstanceEndpointsBundle endpointBundle=null;
		if (bundles == null || bundles.length == 0) {
			endpointBundle = new SubstanceEndpointsBundle();
			endpointBundle.setDescription("Test Bundle description");
			endpointBundle.setTitle("Test Bundle title");
		} else {
			endpointBundle=bundles[0];
		}
		try {
			exporter = new ISAJsonExporter1_0(null, null);
			exporter.init(endpointBundle);
		} catch (Exception x) {
			logger.log(Level.SEVERE, x.getMessage());
			exporter = null;
		}
	}

	@Override
	public void close() throws Exception {
		try {
			if (exporter != null)
				exporter.close();
		} catch (Exception x) {

		}
		super.close();
	}

	/*
	 * ISAJsonExporter1_0 exporter = new ISAJsonExporter1_0(records, outputDir,
	 * exportConfig, endpointBundle); exporter.export();
	 * System.out.println(exporter.getResultAsJson());
	 */
	@Override
	public Object processItem(SubstanceRecord record) throws Exception {
		if (exporter != null)
			exporter.process(record);
		return record;
	}

}

package ambit2.rest.bundle;

import java.io.OutputStream;
import java.util.logging.Level;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.reporters.QueryHeaderReporter;
import ambit2.db.substance.study.SubstanceStudyDetailsProcessor;
import ambit2.export.isa.v1_0.ISAJsonExporter1_0;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.p.DefaultAmbitProcessor;

/**
 * Uses{@link ISAJsonExporter1_0}
 * 
 * @author nina
 * 
 * @param <Q>
 */
public class BundleISA_JSONReporter<Q extends IQueryRetrieval<IStructureRecord>>
		extends QueryHeaderReporter<Q, OutputStream> {

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
		getProcessors()
				.add(new DefaultAmbitProcessor<IStructureRecord, IStructureRecord>() {
					@Override
					public IStructureRecord process(IStructureRecord target)
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
				mapper.setSerializationInclusion(Include.NON_EMPTY);
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
		SubstanceEndpointsBundle endpointBundle = null;
		if (bundles == null || bundles.length == 0) {
			endpointBundle = new SubstanceEndpointsBundle();
			endpointBundle.setDescription("Test Bundle description");
			endpointBundle.setTitle("Test Bundle title");
		} else {
			endpointBundle = bundles[0];
		}
		try {
			exporter = new ISAJsonExporter1_0();
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
	public Object processItem(IStructureRecord record) throws Exception {
		if (exporter != null)
			try {
				exporter.process((SubstanceRecord) record);
			} catch (Exception x) {
				logger.log(Level.FINE, x.getMessage());
			}
		return record;
	}

}

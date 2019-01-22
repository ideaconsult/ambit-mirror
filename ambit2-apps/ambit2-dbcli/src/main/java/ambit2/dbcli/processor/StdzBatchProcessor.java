package ambit2.dbcli.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.SDFWriter;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.smarts.processors.SMIRKSProcessor;
import ambit2.tautomers.processor.StructureStandardizer;
import net.idea.modbcum.p.DefaultAmbitProcessor;

public class StdzBatchProcessor extends DefaultAmbitProcessor<IStructureRecord, IStructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6642706695496417330L;
	protected MoleculeReader molReader = new MoleculeReader(true, false);
	protected StructureStandardizer standardprocessor;
	protected SMIRKSProcessor smirksProcessor;
	protected String[] tags_to_keep;
	boolean writesdf = false;
	protected IChemObjectWriter writer;
	protected String sdf_title;
	protected boolean debugatomtypes;

	public StdzBatchProcessor(StructureStandardizer standardprocessor, SMIRKSProcessor smirksProcessor,
			String[] tags_to_keep, Logger logger, IChemObjectWriter writer, String sdf_title, boolean debugatomtypes) {
		super();
		this.standardprocessor = standardprocessor;
		this.smirksProcessor = smirksProcessor;
		this.tags_to_keep = tags_to_keep;
		this.logger = logger;
		this.writer = writer;
		this.writesdf = writer instanceof SDFWriter;
		this.sdf_title = sdf_title;
		this.debugatomtypes = debugatomtypes;
	}

	@Override
	public IStructureRecord process(IStructureRecord record) throws Exception {

		IAtomContainer mol;
		IAtomContainer processed = null;

		try {
			mol = molReader.process(record);
			if (mol != null) {
				for (Property p : record.getRecordProperties()) {
					Object v = record.getRecordProperty(p);

					// initially to get rid of smiles, inchi ,
					// etc, these are
					// already parsed
					if (tags_to_keep != null && Arrays.binarySearch(tags_to_keep, p.getName()) < 0)
						continue;
					if (p.getName().startsWith("http://www.opentox.org/api/1.1#"))
						continue;
					else
						mol.setProperty(p, v);
				}
				if (tags_to_keep != null) {
					List<String> toRemove = null;
					Iterator pi = mol.getProperties().keySet().iterator();
					while (pi.hasNext()) {
						Object p = pi.next();
						if (Arrays.binarySearch(tags_to_keep, p.toString()) < 0) {
							if (toRemove == null)
								toRemove = new ArrayList<String>();
							toRemove.add(p.toString());
						}

					}
					if (toRemove != null)
						for (String propertyToRemove : toRemove)
							mol.removeProperty(propertyToRemove);

				}

			} else {
				logger.log(Level.SEVERE, "MSG_STANDARDIZE",
						new Object[] { "Empty molecule See the ERROR tag in the output file", getIds(record) });
				return record;
			}
		} catch (Exception x) {
			x.printStackTrace();
			logger.log(Level.SEVERE, "MSG_ERR_MOLREAD", new Object[] { getIds(record), x.toString() });
			return record;
		} finally {

		}
		processed = mol;

		// CDK adds these for the first MOL line
		if (!writesdf) {
			// if (mol.getProperty(CDKConstants.TITLE) != null)
			// mol.removeProperty(CDKConstants.TITLE);
			if (mol.getProperty(CDKConstants.REMARK) != null)
				mol.removeProperty(CDKConstants.REMARK);
		}
		if ((smirksProcessor != null) && smirksProcessor.isEnabled()) {
			processed = smirksProcessor.process(processed);
		}

		try {
			processed = standardprocessor.process(processed);

		} catch (Exception x) {
			String err = processed.getProperty(StructureStandardizer.ERROR_TAG);
			logger.log(Level.SEVERE, x.getMessage(), x);
			if (processed != null) {
				err = processed.getProperty(StructureStandardizer.ERROR_TAG);
				processed.setProperty(StructureStandardizer.ERROR_TAG,
						String.format("%s %s %s", err == null ? "" : err, x.getClass().getName(), x.getMessage()));
			}
		} finally {
			if (processed != null) {
				Iterator<Entry<Object, Object>> p = mol.getProperties().entrySet().iterator();
				// don't overwrite properties from the source
				// molecule
				while (p.hasNext()) {
					Entry<Object, Object> entry = p.next();
					Object value = processed.getProperty(entry.getKey());
					if (value == null || "".equals(value.toString().trim()))
						processed.setProperty(entry.getKey(), entry.getValue());
				}
			}
		}
		if (processed != null)
			try {
				if (writesdf && sdf_title != null) {

					for (Entry<Object, Object> p : processed.getProperties().entrySet())
						if (sdf_title.equals(p.getKey().toString().toLowerCase())) {
							processed.setProperty(CDKConstants.TITLE, p.getValue());
							break;
						}
				}
				if (debugatomtypes) {
					Object debug = (processed == null) ? null : processed.getProperty("AtomTypes");

					if (debug != null && !"".equals(debug))
						writer.write(processed);
				} else
					writer.write(processed);
			} catch (Exception x) {
				logger.log(Level.SEVERE, x.getMessage());
			}
		return record;

	}

	protected String getIds(IStructureRecord record) {
		try {
			StringBuilder m = new StringBuilder();
			for (Property p : record.getRecordProperties()) {
				// m.append(p.getName());
				m.append(record.getRecordProperty(p));
				m.append("\t");
			}
			return m.toString();
		} catch (Exception x) {
			return "";
		}
	}
}

package ambit2.rest.substance;

import java.io.Writer;
import java.util.ArrayList;
import java.util.logging.Level;

import net.idea.modbcum.i.IParameterizedQuery;
import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.restlet.Request;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.ExternalIdentifier;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.facet.BundleRoleFacet;
import ambit2.base.facet.SubstanceStudyFacet;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.json.JSONUtils;
import ambit2.db.facets.bundle.SubstanceRoleByBundle;
import ambit2.db.processors.MasterDetailsProcessor;
import ambit2.db.substance.ids.ReadSubstanceIdentifiers;
import ambit2.db.substance.study.facet.SubstanceStudyFacetQuery;
import ambit2.db.update.bundle.substance.AddCompoundAsSubstanceToBundle;

public class SubstanceJSONReporter<Q extends IQueryRetrieval<SubstanceRecord>>
		extends SubstanceURIReporter<Q> {
	protected String comma = null;
	protected String jsonpCallback = null;
	protected SubstanceEndpointsBundle[] bundles;
	protected int records = 0;
	protected IStructureRecord dummySubstance;
	protected boolean retrieveStudySummary = false;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2315457985592934727L;

	public SubstanceJSONReporter(Request request, String jsonpCallback,
			SubstanceEndpointsBundle[] bundles, boolean retrieveStudySummary) {
		this(request, jsonpCallback, bundles, null, retrieveStudySummary);
	}

	public SubstanceJSONReporter(Request request, String jsonpCallback,
			SubstanceEndpointsBundle[] bundles,
			IStructureRecord dummySubstance, boolean retrieveStudySummary) {
		super(request);
		this.retrieveStudySummary = retrieveStudySummary;
		this.dummySubstance = dummySubstance;
		this.bundles = bundles;
		this.jsonpCallback = JSONUtils.jsonSanitizeCallback(jsonpCallback);

		getProcessors().clear();
		IQueryRetrieval<ExternalIdentifier> queryP = new ReadSubstanceIdentifiers();
		MasterDetailsProcessor<SubstanceRecord, ExternalIdentifier, IQueryCondition> idReader = new MasterDetailsProcessor<SubstanceRecord, ExternalIdentifier, IQueryCondition>(
				queryP) {
			/**
							     * 
							     */
			private static final long serialVersionUID = 5246468397385927943L;

			@Override
			protected void configureQuery(
					SubstanceRecord target,
					IParameterizedQuery<SubstanceRecord, ExternalIdentifier, IQueryCondition> query)
					throws AmbitException {
				query.setFieldname(target);
			}

			@Override
			protected SubstanceRecord processDetail(SubstanceRecord target,
					ExternalIdentifier detail) throws Exception {
				if (target.getExternalids() == null)
					target.setExternalids(new ArrayList<ExternalIdentifier>());
				target.getExternalids().add(detail);
				return target;
			}
		};
		idReader.setCloseConnection(false);
		getProcessors().add(idReader);

		if (bundles != null && bundles.length > 0) {
			SubstanceRoleByBundle q = new SubstanceRoleByBundle(request
					.getRootRef().toString());
			q.setValue(bundles[0]);
			MasterDetailsProcessor<SubstanceRecord, BundleRoleFacet, IQueryCondition> bundleReader = new MasterDetailsProcessor<SubstanceRecord, BundleRoleFacet, IQueryCondition>(
					q) {
				/**
			     * 
			     */
				private static final long serialVersionUID = -6901213143255746760L;

				@Override
				protected void configureQuery(
						SubstanceRecord target,
						IParameterizedQuery<SubstanceRecord, BundleRoleFacet, IQueryCondition> query)
						throws AmbitException {
					query.setFieldname(target);
				}

				@Override
				protected SubstanceRecord processDetail(SubstanceRecord master,
						BundleRoleFacet detail) throws Exception {
					master.clearFacets();
					master.addFacet(detail);
					return master;
				}
			};
			bundleReader.setCloseConnection(false);
			getProcessors().add(bundleReader);
		} else if (retrieveStudySummary) {
			SubstanceStudyFacetQuery q = new SubstanceStudyFacetQuery(request
					.getRootRef().toString());
			q.setReuseRecord(false);
			q.setGroupByInterpretationResult(true);

			MasterDetailsProcessor<SubstanceRecord, SubstanceStudyFacet, IQueryCondition> studyReader = new MasterDetailsProcessor<SubstanceRecord, SubstanceStudyFacet, IQueryCondition>(
					q) {
				@Override
				protected void configureQuery(
						SubstanceRecord master,
						IParameterizedQuery<SubstanceRecord, SubstanceStudyFacet, IQueryCondition> query)
						throws AmbitException {
					master.clearFacets();
					query.setFieldname(master);
				}

				@Override
				protected SubstanceRecord processDetail(SubstanceRecord master,
						SubstanceStudyFacet detail) throws Exception {
					master.addFacet(detail);
					return master;
				}
			};

			getProcessors().add(studyReader);
		}

		getProcessors().add(
				new DefaultAmbitProcessor<SubstanceRecord, SubstanceRecord>() {
					/**
		     * 
		     */
					private static final long serialVersionUID = 7466417307554206003L;

					public SubstanceRecord process(SubstanceRecord target)
							throws AmbitException {
						processItem(target);
						return target;
					};
				});
	}

	public void header(java.io.Writer output, Q query) {
		records = 0;
		try {
			if (jsonpCallback != null) {
				output.write(jsonpCallback);
				output.write("(");
			}
			output.write("{\n");
			output.write("\n\"substance\":[");

		} catch (Exception x) {
			logger.log(Level.WARNING, x.getMessage(), x);
		}
	};

	public void footer(java.io.Writer output, Q query) {
		if (records == 0 && dummySubstance != null
				&& dummySubstance.getIdchemical() > 0) {
			SubstanceRecord dummy = AddCompoundAsSubstanceToBundle
					.structure2substance(dummySubstance);
			dummy.setSubstanceUUID(null);
			try {
				processItem(dummy);
			} catch (Exception x) {
			}
		}
		try {
			output.write("\n\t]");

		} catch (Exception x) {
		}
		try {
			output.write(",\n\"records\":" + records);
		} catch (Exception x) {

		}
		try {
			output.write("\n}\n");

			if (jsonpCallback != null) {
				output.write(");");
			}
		} catch (Exception x) {
		}
	};

	@Override
	public Object processItem(SubstanceRecord item) throws AmbitException {
		try {
			Writer writer = getOutput();
			if (comma != null)
				writer.write(comma);
			writer.write(item.toJSON(getBaseReference().toString()));
			comma = ",";
			records++;
		} catch (Exception x) {
			logger.log(java.util.logging.Level.SEVERE, x.getMessage(), x);
		}
		return item;
	}

}

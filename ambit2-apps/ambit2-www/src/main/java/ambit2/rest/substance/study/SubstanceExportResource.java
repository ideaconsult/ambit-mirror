package ambit2.rest.substance.study;

import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.substance.ExternalIdentifier;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.base.ro.SubstanceRecordAnnotationProcessor;
import ambit2.db.substance.ids.ReadChemIdentifiersByComposition;
import ambit2.db.substance.ids.ReadSubstanceIdentifiers;
import ambit2.db.substance.relation.ReadSubstanceComposition;
import ambit2.db.substance.study.SubstanceStudyDetailsProcessor;
import ambit2.rest.AmbitFreeMarkerApplication;
import ambit2.rest.substance.SubstanceResource;
import ambit2.rest.substance.study.Substance2BucketJsonReporter._JSON_MODE;
import net.idea.modbcum.i.IParameterizedQuery;
import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.modbcum.p.MasterDetailsProcessor;
import net.idea.modbcum.r.QueryAbstractReporter;
import net.idea.restnet.db.convertors.OutputWriterConvertor;

/**
 * /admin/export/substance?media=application%2Fjson
 * 
 * @author nina
 * 
 * @param <Q>
 * @param <T>
 */
public class SubstanceExportResource<Q extends IQueryRetrieval<SubstanceRecord>, T extends SubstanceRecord>
		extends SubstanceResource<Q, T> {

	protected _JSON_MODE jsonmode = _JSON_MODE.experiment;
	protected String summaryMeasurement = null;
	protected String dbTag = "ENM";

	@Override
	public String getTemplateName() {
		return "jsonplaceholder.ftl";
	}

	@Override
	protected Q createQuery(Context context, Request request, Response response) throws ResourceException {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		try {
			jsonmode = _JSON_MODE.valueOf(form.getFirstValue("json_mode"));
		} catch (Exception x) {
			jsonmode = _JSON_MODE.experiment;
		}
		try {
			summaryMeasurement = form.getFirstValue("measurement").toUpperCase();
		} catch (Exception x) {
			summaryMeasurement = null;
		}
		try {
			dbTag = form.getFirstValue("dbtag").toUpperCase();
		} catch (Exception x) {
			dbTag = "ENM";
		}
		return super.createQuery(context, request, response);
	}

	@Override
	protected Representation post(Representation entity) throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected Representation post(Representation entity, Variant variant) throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected Representation put(Representation entity, Variant variant) throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected Representation put(Representation representation) throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected Representation delete() throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected IProcessor<Q, Representation> createJSONReporter(String filenamePrefix) {
		String jsonpcallback = getParams().getFirstValue("jsonp");
		if (jsonpcallback == null)
			jsonpcallback = getParams().getFirstValue("callback");

		String command = "results";
		try {
			if (Boolean.parseBoolean(getParams().getFirstValue("array").toString()))
				command = null;
		} catch (Exception x) {
		}
		ProcessorsChain chain = new ProcessorsChain<>();
		chain.add(new SubstanceStudyDetailsProcessor());
		getCompositionProcessors(chain);
		SubstanceRecordAnnotationProcessor annotator = null;
		try {
			annotator = new SubstanceRecordAnnotationProcessor(new File(((AmbitFreeMarkerApplication) getApplication()).getProperties().getMapFolder()),
				false);
		} catch (Exception x) {
			Logger.getGlobal().log(Level.WARNING,x.getMessage());
			annotator = null;
		}
		return new OutputWriterConvertor<SubstanceRecord, Q>(
				(QueryAbstractReporter<SubstanceRecord, Q, Writer>) new Substance2BucketJsonReporter(command, chain,
						jsonmode, summaryMeasurement, dbTag,annotator),
				jsonpcallback == null ? MediaType.APPLICATION_JSON : MediaType.APPLICATION_JAVASCRIPT, filenamePrefix);

	}

	@Override
	protected void getCompositionProcessors(ProcessorsChain chain) {
		final SubstanceEndpointsBundle bundle = null;
		final ReadSubstanceComposition q = new ReadSubstanceComposition();
		q.setExcludeHidden(true);
		MasterDetailsProcessor<SubstanceRecord, CompositionRelation, IQueryCondition> compositionReader = new MasterDetailsProcessor<SubstanceRecord, CompositionRelation, IQueryCondition>(
				q) {
			/**
			 * 
			 */
			private static final long serialVersionUID = -4012709744454255487L;

			@Override
			public SubstanceRecord process(SubstanceRecord target) throws Exception {
				if (target == null || (target.getIdsubstance() <= 0))
					return target;
				q.setBundle(bundle);
				if (target.getRelatedStructures() != null)
					target.getRelatedStructures().clear();
				return super.process(target);
			}

			protected SubstanceRecord processDetail(SubstanceRecord target, CompositionRelation detail)
					throws Exception {
				
				target.addStructureRelation(detail);
				q.setRecord(null);
				
				return target;
			};
		};
		chain.add(compositionReader);
		final ReadChemIdentifiersByComposition qids = new ReadChemIdentifiersByComposition();
		MasterDetailsProcessor<SubstanceRecord, IStructureRecord, IQueryCondition> idsReader = new MasterDetailsProcessor<SubstanceRecord, IStructureRecord, IQueryCondition>(
				qids) {
			private static final long serialVersionUID = -3547633994853667140L;

			@Override
			protected SubstanceRecord processDetail(SubstanceRecord target, IStructureRecord detail) throws Exception {
				return qids.processDetail(target, detail);
			}

			@Override
			public SubstanceRecord process(SubstanceRecord target) throws AmbitException {
				try {
					return super.process(target);
				} catch (Exception x) {
					logger.log(Level.FINEST, x.getMessage());
					return target;
				}
			}
		};
		chain.add(idsReader);

		IQueryRetrieval<ExternalIdentifier> queryP = new ReadSubstanceIdentifiers();
		MasterDetailsProcessor<SubstanceRecord, ExternalIdentifier, IQueryCondition> substanceIdentifiers = new MasterDetailsProcessor<SubstanceRecord, ExternalIdentifier, IQueryCondition>(
				queryP) {
			/**
							     * 
							     */
			private static final long serialVersionUID = 5246468397385927943L;

			@Override
			protected void configureQuery(SubstanceRecord target,
					IParameterizedQuery<SubstanceRecord, ExternalIdentifier, IQueryCondition> query)
					throws AmbitException {
				query.setFieldname(target);
			}

			@Override
			protected SubstanceRecord processDetail(SubstanceRecord target, ExternalIdentifier detail)
					throws Exception {
				if (target.getExternalids() == null)
					target.setExternalids(new ArrayList<ExternalIdentifier>());
				target.getExternalids().add(detail);
				return target;
			}
		};
		substanceIdentifiers.setCloseConnection(false);
		chain.add(substanceIdentifiers);

	}
}

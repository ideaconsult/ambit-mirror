package ambit2.rest.bundle.dataset;

import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.ProtocolEffectRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.data.substance.SubstanceName;
import ambit2.base.data.substance.SubstanceOwner;
import ambit2.base.data.substance.SubstancePublicName;
import ambit2.base.data.substance.SubstanceUUID;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.db.processors.MasterDetailsProcessor;
import ambit2.db.substance.relation.ReadSubstanceComposition;
import ambit2.db.update.bundle.effects.ReadEffectRecordByBundle;
import ambit2.db.update.bundle.substance.ReadSubstancesByBundle;
import ambit2.rest.OpenTox;
import ambit2.rest.substance.SubstanceDatasetResource;

public class BundleDatasetResource extends SubstanceDatasetResource<ReadSubstancesByBundle> {
	protected SubstanceEndpointsBundle bundle; 
	@Override
	protected ReadSubstancesByBundle createQuery(Context context, Request request, Response response) throws ResourceException {
		Object idbundle = request.getAttributes().get(OpenTox.URI.bundle.getKey());
		if (idbundle==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		try {
			bundle = new SubstanceEndpointsBundle(Integer.parseInt(idbundle.toString()));
			return new ReadSubstancesByBundle(bundle) {
				public ambit2.base.data.SubstanceRecord getObject(java.sql.ResultSet rs) throws AmbitException {
					ambit2.base.data.SubstanceRecord record = super.getObject(rs);
					record.setProperty(new SubstancePublicName(),record.getPublicName());
					record.setProperty(new SubstanceName(),record.getCompanyName());
					record.setProperty(new SubstanceUUID(), record.getCompanyUUID());
					record.setProperty(new SubstanceOwner(), record.getOwnerName());
					return record;
				}
			};
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}

	}
	@Override
	protected IQueryRetrieval<ProtocolEffectRecord<String, String, String>> getEffectQuery() {
		return new ReadEffectRecordByBundle(bundle);
	}
	@Override
	protected IProcessor getCompositionProcessors() {
		final ReadSubstanceComposition q = new ReadSubstanceComposition();
		MasterDetailsProcessor<SubstanceRecord,CompositionRelation,IQueryCondition> compositionReader = 
				new MasterDetailsProcessor<SubstanceRecord,CompositionRelation,IQueryCondition>(q) {
			@Override
					public SubstanceRecord process(SubstanceRecord target)
							throws AmbitException {
						q.setBundle(bundle);
						if (target.getRelatedStructures()!=null) target.getRelatedStructures().clear();
						return super.process(target);
					}
			protected SubstanceRecord processDetail(SubstanceRecord target, CompositionRelation detail) throws Exception {
				target.addStructureRelation(detail);
				return target;
			};
		};
		return compositionReader;
	}
}

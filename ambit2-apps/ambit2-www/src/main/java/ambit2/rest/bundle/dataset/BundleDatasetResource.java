package ambit2.rest.bundle.dataset;

import java.sql.ResultSet;
import java.util.Map;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.ProcessorsChain;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.ProtocolEffectRecord;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.base.data.substance.SubstanceName;
import ambit2.base.data.substance.SubstanceOwner;
import ambit2.base.data.substance.SubstancePublicName;
import ambit2.base.data.substance.SubstanceUUID;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.relation.composition.CompositionRelation;
import ambit2.db.processors.MasterDetailsProcessor;
import ambit2.db.substance.ids.ReadChemIdentifiersByComposition;
import ambit2.db.substance.relation.ReadSubstanceComposition;
import ambit2.db.update.bundle.effects.ReadEffectRecordByBundle;
import ambit2.db.update.bundle.substance.ReadSubstanceChemicalsUnionByBundle;
import ambit2.db.update.bundle.substance.ReadSubstancesByBundle;
import ambit2.rest.OpenTox;
import ambit2.rest.substance.SubstanceDatasetResource;

public class BundleDatasetResource<Q extends ReadSubstancesByBundle> extends SubstanceDatasetResource<Q> {
    protected SubstanceEndpointsBundle bundle;

    @Override
    protected Q createQuery(Context context, Request request, Response response) throws ResourceException {
	Object idbundle = request.getAttributes().get(OpenTox.URI.bundle.getKey());
	if (idbundle == null)
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	Boolean filterBySubstance = isFilterBySubstance();
	if (filterBySubstance == null)
	    filterBySubstance = true;
	try {
	    bundle = new SubstanceEndpointsBundle(Integer.parseInt(idbundle.toString()));

	    if (filterBySubstance) {
		ReadSubstancesByBundle q = new ReadSubstancesByBundle(bundle) {
		    private static final long serialVersionUID = -2845110519109843254L;

		    public ambit2.base.data.SubstanceRecord getObject(java.sql.ResultSet rs) throws AmbitException {
			ambit2.base.data.SubstanceRecord record = super.getObject(rs);
			record.setProperty(new SubstancePublicName(), record.getPublicName());
			record.setProperty(new SubstanceName(), record.getSubstanceName());
			record.setProperty(new SubstanceUUID(), record.getSubstanceUUID());
			record.setProperty(new SubstanceOwner(), record.getOwnerName());
			return record;
		    }
		};
		return (Q) q;
	    } else {
		ReadSubstanceChemicalsUnionByBundle q = new ReadSubstanceChemicalsUnionByBundle(bundle) {
		    /**
		     * 
		     */
		    private static final long serialVersionUID = -1938712635877102937L;

		    @Override
		    public SubstanceRecord getObject(ResultSet rs) throws AmbitException {
			ambit2.base.data.SubstanceRecord record = super.getObject(rs);
			record.setProperty(new SubstancePublicName(), record.getPublicName());
			record.setProperty(new SubstanceName(), record.getSubstanceName());
			record.setProperty(new SubstanceUUID(), record.getSubstanceUUID());
			record.setProperty(new SubstanceOwner(), record.getOwnerName());
			return record;
		    }
		};
		return (Q) q;
	    }

	} catch (Exception x) {
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	}

    }

    @Override
    protected IQueryRetrieval<ProtocolEffectRecord<String, String, String>> getEffectQuery() {
	return new ReadEffectRecordByBundle(bundle);
    }

    @Override
    protected void getCompositionProcessors(ProcessorsChain chain) {
	final ReadSubstanceComposition q = new ReadSubstanceComposition();
	MasterDetailsProcessor<SubstanceRecord, CompositionRelation, IQueryCondition> compositionReader = new MasterDetailsProcessor<SubstanceRecord, CompositionRelation, IQueryCondition>(
		q) {
	    /**
		     * 
		     */
	    private static final long serialVersionUID = -4012709744454255487L;

	    @Override
	    public SubstanceRecord process(SubstanceRecord target) throws AmbitException {
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
	ReadChemIdentifiersByComposition qids = new ReadChemIdentifiersByComposition();
	MasterDetailsProcessor<SubstanceRecord, IStructureRecord, IQueryCondition> idsReader = new MasterDetailsProcessor<SubstanceRecord, IStructureRecord, IQueryCondition>(
		qids) {
	    /**
		     * 
		     */
	    private static final long serialVersionUID = -3547633994853667140L;

	    @Override
	    protected SubstanceRecord processDetail(SubstanceRecord target, IStructureRecord detail) throws Exception {
		for (CompositionRelation r : target.getRelatedStructures())
		    if (detail.getIdchemical() == r.getSecondStructure().getIdchemical()) {
			for (Property p : detail.getProperties()) {
			    r.getSecondStructure().setProperty(p, detail.getProperty(p));
			}
			if (target.getIdsubstance() == -1) {
			    
			    r.getRelation().setReal_lower("=");
			    r.getRelation().setReal_lowervalue(100.0);
			    r.getRelation().setReal_uppervalue(100.0);
			    r.getRelation().setReal_upper("=");
			    r.getRelation().setReal_unit("%");
			    r.getRelation().setTypical("=");
			    r.getRelation().setTypical_value(100.0);
			    r.getRelation().setTypical_unit("%");
			    
			    for (Property p : detail.getProperties()) {
				if (Property.opentox_Name.equals(p.getLabel())) {
				    target.setProperty(new SubstancePublicName(), detail.getProperty(p));
				} else if (Property.opentox_TradeName.equals(p.getLabel())) {
				    target.setProperty(new SubstanceName(), detail.getProperty(p));
				}
			    }
			}
			break;
		    }
		return target;
	    }

	    @Override
	    public SubstanceRecord process(SubstanceRecord target) throws AmbitException {
		try {
		    return super.process(target);
		} catch (Exception x) {
		    logger.log(Level.FINE, x.getMessage());
		    return target;
		}
	    }
	};
	chain.add(idsReader);
    }

    @Override
    protected SubstanceEndpointsBundle[] getBundles() {
	return null;
	// return new SubstanceEndpointsBundle[] {bundle};
    }

    @Override
    protected Map<String, Object> getMap(Variant variant) throws ResourceException {
	Map<String, Object> map = super.getMap(variant);
	Object idbundle = getRequest().getAttributes().get(OpenTox.URI.bundle.getKey());
	try {
	    map.put("bundleid", Integer.toString(Integer.parseInt(idbundle.toString())));
	} catch (Exception x) {
	}
	return map;
    }
}

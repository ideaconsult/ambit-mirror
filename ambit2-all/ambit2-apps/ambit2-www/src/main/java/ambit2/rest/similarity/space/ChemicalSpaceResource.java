package ambit2.rest.similarity.space;

import java.awt.Dimension;
import java.util.Iterator;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.r.QueryAbstractReporter;

import org.restlet.data.Form;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.db.reporters.CSVReporter;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.structure.pairwise.ChemSpaceCell;
import ambit2.db.search.structure.pairwise.ChemicalSpaceQuery;
import ambit2.db.search.structure.pairwise.ChemicalSpaceQuery.ChemSpaceMethod;
import ambit2.rest.DisplayMode;
import ambit2.rest.dataset.DatasetStructuresResource;
import ambit2.rest.error.InvalidResourceIDException;
import ambit2.rest.similarity.AbstractPairwiseResource;

public class ChemicalSpaceResource<Q extends IQueryRetrieval<ChemSpaceCell>> extends AbstractPairwiseResource<ChemSpaceCell,Q> { 
	public final static String resource =  "/space";

	public ChemicalSpaceResource() {
		super();
		setHtmlbyTemplate(true);
	}
	@Override
	public String getTemplateName() {
		return "chemspace.ftl";
	}
	@Override
	protected QueryAbstractReporter createHTMLReporter(Dimension d) {
		return new ChemicalSpaceHTMLReporter(getRequest(),DisplayMode.table,null,(ChemicalSpaceJSONReporter)createJSONReporter());
	}

	@Override
	protected CSVReporter createCSVReporter() {
		CSVReporter csvReporter = new CSVReporter(getRequest().getRootRef().toString(),null,null,
				String.format("%s%s",getRequest().getRootRef(),getCompoundInDatasetPrefix())
				);
		csvReporter.setSimilarityColumn(Property.getInstance("metric",queryObject==null?"":queryObject.toString(),"http://ambit.sourceforge.net"));
		return csvReporter;
	}
	
	@Override
	protected QueryAbstractReporter createJSONReporter(String callback) {
		return new ChemicalSpaceJSONReporter<IQueryRetrieval<ChemSpaceCell>>(getRequest(),getProperty());
	}
	
	@Override
	protected Q getQueryById(Integer key) throws ResourceException {
		Q query = null;
		datasetID = key;
		ChemicalSpaceQuery q = new ChemicalSpaceQuery();
		q.setValue(getProperty());
		SourceDataset dataset = new SourceDataset();
		dataset.setID(key);
		q.setFieldname(dataset);
		q.setFieldname(dataset);
		Form form = getResourceRef(getRequest()).getQueryAsForm();
		try {
			q.setMaxPoints(Integer.parseInt(form.getFirstValue("resolution")));
		} catch (Exception x) {
			//default
		}		
		try {
			q.setMethod(ChemSpaceMethod.valueOf(form.getFirstValue("method")));
		} catch (Exception x) {
			//default
		}		
		try {
			q.setThreshold_similarity(Double.parseDouble(form.getFirstValue("similarity")));
		} catch (Exception x) {
			//default
		}		
		try {
			q.setThreshold_dactivity(Double.parseDouble(form.getFirstValue("dactivity")));
		} catch (Exception x) {
			//default
		}		
		setPaging(form, q);
		return (Q)q;
	}
	
    @Override
	protected Q getQueryById(String key) throws ResourceException {
		if (key.startsWith(DatasetStructuresResource.QR_PREFIX)) {
			key = key.substring(DatasetStructuresResource.QR_PREFIX.length());
			try {
				queryResultsID = Integer.parseInt(key.toString());
			} catch (NumberFormatException x) {
				throw new InvalidResourceIDException(key);
			}
			ChemicalSpaceQuery q = new ChemicalSpaceQuery();
			q.setValue(getProperty());
			StoredQuery dataset = new StoredQuery();
			dataset.setID(queryResultsID);
			q.setFieldname(dataset);
			Form form = getResourceRef(getRequest()).getQueryAsForm();
			try {
				q.setMaxPoints(Integer.parseInt(form.getFirstValue("resolution")));
			} catch (Exception x) {
				//default
			}
			setPaging(form, q);
			return (Q)q;
		} //else return getDatasetByName(key);
		throw new InvalidResourceIDException(key);
	}
	
    protected Property getProperty() {
    	if (template==null) return null;
    	Iterator<Property> i = template.iterator();
    	while (i.hasNext()) {
    		return i.next();
    	}
    	return null;
    }

}

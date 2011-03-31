package ambit2.db.facets.datasets;

import java.net.URLEncoder;

import ambit2.base.facet.AbstractFacet;
import ambit2.base.interfaces.IStructureRecord;

public class DatasetPrefixFacet extends AbstractFacet<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3704763536776924159L;
	protected IStructureRecord structure;
	public IStructureRecord getStructure() {
		return structure;
	}

	public void setStructure(IStructureRecord structure) {
		this.structure = structure;
	}
	
	public DatasetPrefixFacet(String url) {
		super(url);
	}
	@Override
	public String getResultsURL(String... params) {
		String root = params.length>0?params[0]:"";
		String cmp= getStructureURI(root);
		if (cmp==null)
			return String.format("%s/dataset?search=^%s",root,getValue());
		return String.format("%s/dataset?search=^%s&compound_uri=%s",root,getValue(),URLEncoder.encode(cmp));
	}
	protected String getStructureURI(String root) {
		String cmp=null;
		if ((getStructure()==null) || ( getStructure().getIdchemical()<=0 && getStructure().getIdstructure()<=0  ))
			return null;
		if (getStructure().getIdstructure()>0)
			cmp = String.format("%s/compound/%d/conformer/%d",root,getStructure().getIdchemical(),getStructure().getIdstructure());		
		if (getStructure().getIdchemical()>0)
			cmp = String.format("%s/compound/%d",root,getStructure().getIdchemical());
		return cmp;
	}
	@Override
	public String getSubCategoryURL(String... params) {
		if (getURL()==null) return null;
		String root = params.length>0?params[0]:"";
		String cmp= getStructureURI(root);
		
		int pos = getURL().indexOf("?");
		if (cmp==null)
			return String.format("%s?search=%s",pos>0?getURL().substring(0,pos):getURL(),URLEncoder.encode(getValue()));
		else
			return String.format("%s?search=%s&compound_uri=%s",
					pos>0?getURL().substring(0,pos):getURL(),
							URLEncoder.encode(getValue()),
							URLEncoder.encode(cmp));
		
	}
	
}

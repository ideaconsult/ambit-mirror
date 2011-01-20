package ambit2.base.processors.search;

import java.util.Iterator;

import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.HttpException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.base.interfaces.IStructureRecord.STRUC_TYPE;
import ambit2.base.processors.DefaultAmbitProcessor;

public abstract class AbstractFinder<REQUEST,RESULT> extends DefaultAmbitProcessor<IStructureRecord, IStructureRecord>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2897055153094159861L;
	public enum MODE {
		emptyonly,
		replace,
		add
	}
	public enum SITE {
		CSLS,
		CHEMIDPLUS,
		PUBCHEM,
		CHEBI,
		OPENTOX
	}
	
	protected REQUEST request;
	protected Template profile;
	protected AbstractFinder.MODE mode;

	public AbstractFinder(Template profile,REQUEST request,AbstractFinder.MODE mode) {
		super();
		this.profile = profile;
		this.request = request;
		this.mode = mode;
	}
	public AbstractFinder.MODE getMode() {
		return mode;
	}
	public void setMode(AbstractFinder.MODE mode) {
		this.mode = mode;
	}

	protected abstract RESULT query(String value) throws AmbitException ;
	
	protected IStructureRecord transformResult(IStructureRecord record,IStructureRecord result) throws AmbitException {
		result.setIdchemical(record.getIdchemical());
		result.setIdstructure(record.getIdstructure());
		for (Property key : record.getProperties())
			result.setProperty(key, record.getProperty(key));
		return result;
	}
	
	protected IStructureRecord transformResult(IStructureRecord record,String result) throws AmbitException {
		record.setContent(result);
		record.setFormat(MOL_TYPE.SDF.toString());
		return record;
	}

	protected IStructureRecord transform(IStructureRecord record,RESULT result) throws AmbitException {
		if (result instanceof IStructureRecord) return transformResult(record, (IStructureRecord) result);
		else return transformResult(record, result.toString());
	}
	@Override
	public IStructureRecord process(IStructureRecord target) throws AmbitException {
		try {
			if (target ==null) return null;
			if (AbstractFinder.MODE.emptyonly.equals(mode) && !STRUC_TYPE.NA.equals(target.getType())) return null;
			
			Iterator<Property> keys = profile.getProperties(true);
			Object value = null;
			while (keys.hasNext()) {
				Property key = keys.next();
				try {
					value = target.getProperty(key);
					if ((value==null) || "".equals(value.toString().trim())) continue;
					RESULT content = query(value.toString());
					if (content!= null) { 
						
						target = transform(target,content);
						
						switch (mode) {
						case replace: {
							target.setUsePreferedStructure(false);
							break;
						}	
						default: 
							if (!STRUC_TYPE.NA.equals(target.getType())) {
								target.setIdstructure(-1) ;
							}
							target.setUsePreferedStructure(false);
							break;
						}
						
						System.out.println(value + " Found");
						return target;
					}
				} catch (HttpException x) {
					if (x.getCode()==404) System.out.println(value + x.getMessage());
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
			
		} catch (Exception x) {
			x.printStackTrace();
		}
		return null;
	}
}

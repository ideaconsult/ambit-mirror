package ambit2.search;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import ambit2.base.data.Template;
import ambit2.base.processors.search.AbstractFinder;
import ambit2.search.csls.CSLSStringRequest;

public class AllSourcesFinder  extends AbstractFinder<IProcessor<String, String>,String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7059985528732316425L;
	
	public AllSourcesFinder(Template profile) {
		this(profile,new CSLSStringRequest(),AbstractFinder.MODE.emptyonly);
	}
	public AllSourcesFinder(Template profile,IProcessor<String, String> request,AbstractFinder.MODE mode) {
		super(profile,request,mode);
	}
	@Override
	protected String query(String value) throws AmbitException {
		try {
			return request.process(value);
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	/*
	@Override
	public IStructureRecord process(IStructureRecord target)
			throws AmbitException {
		try {
			if (AbstractFinder.MODE.emptyonly.equals(mode) && !STRUC_TYPE.NA.equals(target.getType())) return null;
			
			Iterator<Property> keys = profile.getProperties(true);
			Object value = null;
			while (keys.hasNext()) {
				Property key = keys.next();
				try {
					value = target.getProperty(key);
					if ((value==null) || "".equals(value.toString().trim())) continue;
					String content = request.process(target.getProperty(key).toString());
					if (content!= null) { 
						
						target.setContent(content);
						target.setFormat(MOL_TYPE.SDF.toString());
						
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
	*/

}
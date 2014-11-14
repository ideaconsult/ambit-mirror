package ambit2.descriptors.processors;

import java.io.InputStream;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import ambit2.base.data.ClassHolder;

public abstract class AbstractDescriptorFactory<Result> extends DefaultAmbitProcessor<String,Result> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 275242996048077139L;
	protected abstract Result createResult();
	protected abstract void addToResult(String name,boolean enabled,int order,Result result) throws Exception;
	
	public Result process(String target)
			throws AmbitException {
		Result p = createResult();
		if (target==null)
			target="ambit2/descriptors/descriptors.txt";
		InputStream in = DescriptorsFactory.class.getClassLoader().getResourceAsStream(target);
		List<ClassHolder> classes = ClassHolder.load(in);
		for (int i=0; i < classes.size();i++) {
			try {
				boolean enabled = true;
				String name= classes.get(i).getClazz();
				if (classes.get(i).getClazz().indexOf(";")==0) {
					name = classes.get(i).getClazz().substring(1);
					enabled = false;
				}
				try {
					addToResult(name, enabled,i, p);
				} catch (Exception x) {
					java.util.logging.Logger.getLogger(getClass().getName()).severe(x.getMessage());
				}

			} catch (Exception x) {
				java.util.logging.Logger.getLogger(getClass().getName()).severe(x.getMessage());

			}
		}
		return p;
	}


}
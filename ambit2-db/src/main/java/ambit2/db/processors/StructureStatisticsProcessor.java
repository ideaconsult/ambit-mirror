package ambit2.db.processors;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;

public class StructureStatisticsProcessor  extends ConnectionStatisticsProcessor<IStructureRecord> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4688263934254733569L;
	public StructureStatisticsProcessor() {

	}
	protected String[] getSQL(IStructureRecord target) {
		String[] s = new String[] {
				"select Template,Property,value from (\n"+
				"select idtemplate,idproperty,template.name as Template,properties.name as Property,value from template join template_def using(idtemplate) join properties using(idproperty)\n"+
				"join property_values using(idproperty)\n"+
				"join property_string using(idvalue_string)\n"+
				"where idstructure=%s\n"+
				"union\n"+
				"select idtemplate,idproperty,template.name as Template,properties.name as Property,value from template join template_def using(idtemplate) join properties using(idproperty)\n"+
				"join property_values using(idproperty)\n"+
				"where idstructure=%s\n"+
				"union\n"+
				"select 0,0,\"Dataset\",src_dataset.name,\"\" from src_dataset join struc_dataset using(id_srcdataset) where idstructure=100\n"+
				"order by idtemplate,idproperty ) as L"
		};
		s[0] = String.format(s[0],target.getIdstructure(),target.getIdstructure(),target.getIdstructure());		
		return s;
	}
	@Override
	public StringBuffer process(IStructureRecord target) throws AmbitException {
		setSql(getSQL(target));
		return super.process(target);
	}
}

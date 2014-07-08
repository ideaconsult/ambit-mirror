package ambit2.db.readers;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.idea.modbcum.i.exceptions.AmbitException;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.StructureRecord;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;

public class RetrieveTemplateStructure extends	RetrieveTemplate<IStructureRecord> {
	protected IStructureRecord record;
	protected boolean addPropertiesToTemplate;
	public RetrieveTemplateStructure() {
		this(null);
	}
	public RetrieveTemplateStructure(Template template) {
		super(template);
	}
	public IStructureRecord getRecord() {
		return record;
	}
	public void setRecord(IStructureRecord record) {
		this.record = record;
	}
	@Override
	public void setFieldname(Template fieldname) {
		super.setFieldname(fieldname);
		addPropertiesToTemplate = (fieldname!=null) && (getFieldname().size()==0);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 5787823982680640645L;
	@Override
	public IStructureRecord getObject(ResultSet rs) throws AmbitException {
		try {
			IStructureRecord record = getRecord();
			if (record==null) record = new StructureRecord();
			if (chemicalsOnly && record.getIdstructure()>0) ; //skip
			else
				record.setIdstructure(rs.getInt(4));
			record.setIdchemical(rs.getInt(9));
			LiteratureEntry le = LiteratureEntry.getInstance(rs.getString(7),rs.getString(8),rs.getInt(2));
			Property p = Property.getInstance(rs.getString(1),le); 
			p.setOrder(rs.getInt(13));
			p.setUnits(rs.getString(11));
			if (addPropertiesToTemplate && (getFieldname()!=null) && (getFieldname().get(p)==null)) {
				p.setEnabled(true);
				getFieldname().add(p);
			}
			
			Object value = rs.getObject(5);
			if (value == null) {
				p.setClazz(Number.class);
				value = rs.getObject(6);
				record.setProperty(p,value==null?Double.NaN:rs.getFloat(6));
			}
			else 				
			if (NaN.equals(value.toString())) {
				record.setProperty(p,Double.NaN);
				p.setClazz(Number.class);
			} else {
				record.setProperty(p,rs.getString(5));
				p.setClazz(String.class);
			}
			
			return record;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
		
	}
}

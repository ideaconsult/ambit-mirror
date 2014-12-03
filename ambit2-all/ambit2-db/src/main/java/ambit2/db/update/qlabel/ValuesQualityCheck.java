package ambit2.db.update.qlabel;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;

/**
 *Identificators correspondence between different origins
<ul>
<li>the user selects key field (e.g. CAS) and fields to be compared (e.g. structure, names, descriptors)
<li>select field (e.g. CAS)
<li>select field to be compared (e.g. structure)
<li>for all structures having same CAS calculate Q metric 
<li>save metric under e.g. CAS-QA user
</ul>
+select field (e.g. smiles, structure)
+select field to be compared (e.g. CAS)
+ for all CAS having same structures calculate Q metric  - NOK if there are differences
+save metric under e.g. struc-CAS-QA user
<p>
 * @author nina
 *
 */

public class ValuesQualityCheck extends AbstractUpdate<IStructureRecord,String> {
	protected Property property; 
	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	protected static String[] sql = {
		//1
		"insert ignore into ausers (user_name,email,lastname,keywords,homepage) values (\"quality\",\"quality\",\"Automatic quality verifier\",\"quality\",\"http://ambit.sourceforge.net\");",
		//3
		//assigns OK if all structures of a chemical have the same value for properties with e.g. comments="CasRN", error if not
		"insert into quality_labels (id,user_name,`label`,`text`)\n"+
		"select id,'quality',Q,'Verifies if the value is different for the same chemical' from\n"+
		"(\n"+
		"select idchemical,if (min(idvalue_string)=max(idvalue_string),\"ProbablyOK\",\"ProbablyERROR\") as Q from  structure\n"+
		"join property_values using(idstructure)\n"+
		"join properties using(idproperty)\n"+
		"where idchemical=? and comments=?\n"+
		") as L\n"+
		"join structure using(idchemical)\n"+
		"join property_values using(idstructure)\n"+
		"join properties using(idproperty)\n"+
		"where idchemical=? and idstructure=? and comments=?\n"+
		"on duplicate key update `label`=values(`label`), `text`=values(`text`)\n",
		
		//4
		//assigns error if there are different chemicals with the same value for properties with comments="CasRN"
		"insert into quality_labels (id,user_name,`label`,`text`)\n"+
		"select id,'quality',Q,'Same value for a different chemical' from\n"+
		"(\n"+
		"select ? as chem,? as struc,if(min(idchemical)=max(idchemical),\"ProbablyOK\",\"ProbablyERROR\") as Q\n"+
		"from  structure\n"+
		"join property_values using(idstructure)\n"+
		"join properties using(idproperty)\n"+
		"join property_string using(idvalue_string)\n"+
		"where value=? and comments=?\n"+
		") as L\n"+
		"join property_values v on v.idstructure=L.struc\n"+
		"join properties using(idproperty)\n"+
		"where comments=? and Q=\"ProbablyError\"\n"+
		"on duplicate key update `label`=values(`label`), `text`=values(`text`)\n"
	};
		
	private final int n3 = 3;
	private final int n4 = 4;
	public List<QueryParam> getParameters(int index) throws AmbitException {
		switch (index) {
		case n3: {
			List<QueryParam> params = new ArrayList<QueryParam>();
			params.add(new QueryParam<Integer>(Integer.class, getGroup().getIdchemical()));
			params.add(new QueryParam<String>(String.class, getProperty().getLabel()));
			params.add(new QueryParam<Integer>(Integer.class, getGroup().getIdchemical()));
			params.add(new QueryParam<Integer>(Integer.class, getGroup().getIdstructure()));				
			params.add(new QueryParam<String>(String.class, getProperty().getLabel()));			
			return params;
		}
		case n4: {
			List<QueryParam> params = new ArrayList<QueryParam>();
			params.add(new QueryParam<Integer>(Integer.class, getGroup().getIdchemical()));
			params.add(new QueryParam<Integer>(Integer.class, getGroup().getIdstructure()));	
			params.add(new QueryParam<String>(String.class, getObject()));	
			params.add(new QueryParam<String>(String.class, getProperty().getLabel()));		
			params.add(new QueryParam<String>(String.class, getProperty().getLabel()));		
			return params;
		}
		default: return null;
		}
	}

	public String[] getSQL() throws AmbitException {
		return sql;
	}

	public void setID(int index, int id) {
		// TODO Auto-generated method stub
		
	}
}

package ambit2.db.facets.qlabel;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.facet.AbstractFacetQuery;
import ambit2.base.data.ConsensusLabel;
import ambit2.base.data.ConsensusLabel.CONSENSUS_LABELS;
import ambit2.base.data.SourceDataset;
import ambit2.db.search.StringCondition;

public class DatasetChemicalsQualityStats  extends AbstractFacetQuery<CONSENSUS_LABELS,SourceDataset,StringCondition,DatasetConsensusLabelFacet> {
	protected DatasetConsensusLabelFacet record;
	protected boolean summary = false;
	public boolean isSummary() {
		return summary;
	}

	public void setSummary(boolean summary) {
		this.summary = summary;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2657669483303463773L;
	protected static final String sql = 
		"SELECT id_srcdataset,name,count(distinct(idchemical)),q.label,q.text FROM src_dataset\n"+
		"join struc_dataset using(id_srcdataset)\n"+
		"join structure using(idstructure)\n"+
		"join quality_chemicals q using(idchemical)\n"+
		"%s\n"+
		"group by id_srcdataset,label,text with rollup\n";

	protected static final String sql_summary = 
		"SELECT null,null,count(idchemical),label,text FROM quality_chemicals\n"+
		"group by  label,text with rollup\n";

	public DatasetChemicalsQualityStats(String facetURL) {
		super(facetURL);
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public double calculateMetric(DatasetConsensusLabelFacet object) {
		return 1;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		if (getValue()==null) return null;	
		List<QueryParam> params = new ArrayList<QueryParam>();
		if ((getValue() !=null) && getValue().getId()>0)
			params.add(new QueryParam<Integer>(Integer.class,getValue().getId()));
		
		return params;
	}

	@Override
	public String getSQL() throws AmbitException {
		if ((getValue() !=null) && getValue().getId()>0) return String.format(sql,"where id_srcdataset=?");
		else return String.format(isSummary()?sql_summary:sql,"");
	}

	@Override
	protected DatasetConsensusLabelFacet createFacet(String facetURL) {
		return new DatasetConsensusLabelFacet(null);
	}
	@Override
	public DatasetConsensusLabelFacet getObject(ResultSet rs) throws AmbitException {
		if (record == null) 
			record = createFacet(null);
		
		try {
			SourceDataset dataset = null;
			Object iddataset = rs.getObject(1);
			String dbname = rs.getString(2);
			if (iddataset!=null) {
				dataset = new SourceDataset(dbname);
				dataset.setId(rs.getInt(1));
				record.setDataset(dataset);
			}
			String label = rs.getString("label");
			String text = rs.getString("text");
			record.setValue(String.format("[%s] %s %s",
						dataset==null?"Total":dataset.getName(),label==null?"All labels":label,text==null?"":text));
			
			record.setCount(rs.getInt(3));
			ConsensusLabel l = null;
			if(label!=null) {
				l = new ConsensusLabel(CONSENSUS_LABELS.valueOf(rs.getString("label")));
				l.setText(text);
			}
			record.setProperty(l);
			
			return record;
		} catch (Exception x) {
			record.setValue(x.getMessage());
			record.setCount(-1);
			return record;
		}
	}
	
	@Override
	public String toString() {
		return "Compound quality label";
	}

}

/*
SELECT
group_concat(id_srcdataset),group_concat(name),
idchemical,idstructure,
concat(
hex(fp1),'-',
hex(fp2),'-',
hex(fp3),'-',
hex(fp4),'-',
hex(fp5),'-',
hex(fp6),'-',
hex(fp7),'-',
hex(fp8),'-',
hex(fp9),'-',
hex(fp10),'-',
hex(fp11),'-',
hex(fp12),'-',
hex(fp13),'-',
hex(fp14),'-',
hex(fp15),'-',
hex(fp16)
) x,
count(*) c
from fp1024_struc
join struc_dataset using(idstructure)
join src_dataset d using(id_srcdataset)
where idchemical=216
and d.user_name="admin"
group by x,id_srcdataset,idstructure with rollup

Ambiguous 1:1:1:1

SELECT
concat(
hex(fp1),'-',
hex(fp2),'-',
hex(fp3),'-',
hex(fp4),'-',
hex(fp5),'-',
hex(fp6),'-',
hex(fp7),'-',
hex(fp8),'-',
hex(fp9),'-',
hex(fp10),'-',
hex(fp11),'-',
hex(fp12),'-',
hex(fp13),'-',
hex(fp14),'-',
hex(fp15),'-',
hex(fp16)
) x,
id_srcdataset,group_concat(name),
idstructure,idchemical,
count(*) c
from fp1024_struc
join struc_dataset using(idstructure)
join src_dataset d using(id_srcdataset)
where idchemical=42503
and d.user_name="admin"
group by x,id_srcdataset,idstructure with rollup

'0-0-0-0-100000004000-200040-0-0-40000000-2002000000000000-18-6000800000-8000000000000000-110-200000000-80000000000000', 84, 'NAME2STRUCTURE', 157689, 42503, 1
'0-0-0-0-100000004000-200040-0-0-40000000-2002000000000000-18-6000800000-8000000000000000-110-200000000-80000000000000', 84, 'NAME2STRUCTURE', , 42503, 1
'0-0-0-0-100000004000-200040-0-0-40000000-2002000000000000-18-6000800000-8000000000000000-110-200000000-80000000000000', , 'NAME2STRUCTURE', , 42503, 1
'0-0-0-0-100000004000-200040-1000-0-40000000-2002000000000000-18-6000800000-8000000000000000-110-200000000-80000000000000', 90, 'CHEMDRAW', 146602, 42503, 1
'0-0-0-0-100000004000-200040-1000-0-40000000-2002000000000000-18-6000800000-8000000000000000-110-200000000-80000000000000', 90, 'CHEMDRAW', , 42503, 1
'0-0-0-0-100000004000-200040-1000-0-40000000-2002000000000000-18-6000800000-8000000000000000-110-200000000-80000000000000', , 'CHEMDRAW', , 42503, 1
'0-0-0-4000000000-110000000000-40-1000-0-40000000-2002000000000000-10-6000800000-8000000000000000-0-380040000-80000000000000', 83, 'CIR', 152769, 42503, 1
'0-0-0-4000000000-110000000000-40-1000-0-40000000-2002000000000000-10-6000800000-8000000000000000-0-380040000-80000000000000', 83, 'CIR', , 42503, 1
'0-0-0-4000000000-110000000000-40-1000-0-40000000-2002000000000000-10-6000800000-8000000000000000-0-380040000-80000000000000', , 'CIR', , 42503, 1
'0-2000-0-0-100000000200-440-10001000-0-40000000-2402000000000000-10-6000800000-8000000000000000-0-200000000-80000000000000', 81, 'CHEMIDPLUS', 150545, 42503, 1
'0-2000-0-0-100000000200-440-10001000-0-40000000-2402000000000000-10-6000800000-8000000000000000-0-200000000-80000000000000', 81, 'CHEMIDPLUS', , 42503, 1
'0-2000-0-0-100000000200-440-10001000-0-40000000-2402000000000000-10-6000800000-8000000000000000-0-200000000-80000000000000', , 'CHEMIDPLUS', , 42503, 1
'', , 'NAME2STRUCTURE,CHEMDRAW,CIR,CHEMIDPLUS', , 42503, 4

Consensus 4
SELECT
concat(
hex(fp1),'-',
hex(fp2),'-',
hex(fp3),'-',
hex(fp4),'-',
hex(fp5),'-',
hex(fp6),'-',
hex(fp7),'-',
hex(fp8),'-',
hex(fp9),'-',
hex(fp10),'-',
hex(fp11),'-',
hex(fp12),'-',
hex(fp13),'-',
hex(fp14),'-',
hex(fp15),'-',
hex(fp16)
) x,
id_srcdataset,group_concat(name),
idstructure,idchemical,
count(*) c
from fp1024_struc
join struc_dataset using(idstructure)
join src_dataset d using(id_srcdataset)
where idchemical=23
and d.user_name="admin"
group by x,id_srcdataset,idstructure with rollup
'1000000000000-80000080D00840-240000008-20004000C0200400-181100000008000-4000000000000-0-4000000000000-4000000800000001-2000000000800020-80-E800000004-0-0-801200000000-8000000000020', 81, 'CHEMIDPLUS', 149792, 23, 1
'1000000000000-80000080D00840-240000008-20004000C0200400-181100000008000-4000000000000-0-4000000000000-4000000800000001-2000000000800020-80-E800000004-0-0-801200000000-8000000000020', 81, 'CHEMIDPLUS', , 23, 1
'1000000000000-80000080D00840-240000008-20004000C0200400-181100000008000-4000000000000-0-4000000000000-4000000800000001-2000000000800020-80-E800000004-0-0-801200000000-8000000000020', 83, 'CIR', 152019, 23, 1
'1000000000000-80000080D00840-240000008-20004000C0200400-181100000008000-4000000000000-0-4000000000000-4000000800000001-2000000000800020-80-E800000004-0-0-801200000000-8000000000020', 83, 'CIR', , 23, 1
'1000000000000-80000080D00840-240000008-20004000C0200400-181100000008000-4000000000000-0-4000000000000-4000000800000001-2000000000800020-80-E800000004-0-0-801200000000-8000000000020', 84, 'NAME2STRUCTURE', 157133, 23, 1
'1000000000000-80000080D00840-240000008-20004000C0200400-181100000008000-4000000000000-0-4000000000000-4000000800000001-2000000000800020-80-E800000004-0-0-801200000000-8000000000020', 84, 'NAME2STRUCTURE', , 23, 1
'1000000000000-80000080D00840-240000008-20004000C0200400-181100000008000-4000000000000-0-4000000000000-4000000800000001-2000000000800020-80-E800000004-0-0-801200000000-8000000000020', 90, 'CHEMDRAW', 147993, 23, 1
'1000000000000-80000080D00840-240000008-20004000C0200400-181100000008000-4000000000000-0-4000000000000-4000000800000001-2000000000800020-80-E800000004-0-0-801200000000-8000000000020', 90, 'CHEMDRAW', , 23, 1
'1000000000000-80000080D00840-240000008-20004000C0200400-181100000008000-4000000000000-0-4000000000000-4000000800000001-2000000000800020-80-E800000004-0-0-801200000000-8000000000020', , 'CHEMIDPLUS,CIR,NAME2STRUCTURE,CHEMDRAW', , 23, 4
'', , 'CHEMIDPLUS,CIR,NAME2STRUCTURE,CHEMDRAW', , 23, 4


Unconfirmed 
SELECT
concat(
hex(fp1),'-',
hex(fp2),'-',
hex(fp3),'-',
hex(fp4),'-',
hex(fp5),'-',
hex(fp6),'-',
hex(fp7),'-',
hex(fp8),'-',
hex(fp9),'-',
hex(fp10),'-',
hex(fp11),'-',
hex(fp12),'-',
hex(fp13),'-',
hex(fp14),'-',
hex(fp15),'-',
hex(fp16)
) x,
id_srcdataset,group_concat(name),
idstructure,idchemical,
count(*) c
from fp1024_struc
join struc_dataset using(idstructure)
join src_dataset d using(id_srcdataset)
where idchemical=32238
and d.user_name="admin"
group by x,id_srcdataset,idstructure with rollup

'200000802001042-14A2100802-400C010651018100-A0509001602008A5-3080200008188004-80002180008002-20004000421100-110400800400A400-C020840841900422-2000800018400200-8180000004402209-180E600084048-820001130802-8000E0008080C2-40C0120C02000008-82400020991A205', 81, 'CHEMIDPLUS', 156001, 32238, 1
'200000802001042-14A2100802-400C010651018100-A0509001602008A5-3080200008188004-80002180008002-20004000421100-110400800400A400-C020840841900422-2000800018400200-8180000004402209-180E600084048-820001130802-8000E0008080C2-40C0120C02000008-82400020991A205', 81, 'CHEMIDPLUS', , 32238, 1
'200000802001042-14A2100802-400C010651018100-A0509001602008A5-3080200008188004-80002180008002-20004000421100-110400800400A400-C020840841900422-2000800018400200-8180000004402209-180E600084048-820001130802-8000E0008080C2-40C0120C02000008-82400020991A205', , 'CHEMIDPLUS', , 32238, 1
'', , 'CHEMIDPLUS', , 32238, 1
 */

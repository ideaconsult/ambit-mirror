package ambit2.db.update.qlabel;

import java.util.ArrayList;
import java.util.List;

import ambit2.base.data.AmbitUser;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.search.QueryParam;
import ambit2.db.update.AbstractUpdate;

public class CreateQLabelPair extends AbstractUpdate<AmbitUser, String> {
	protected static String[] sql = {
		"insert ignore into roles (role_name) values (\"ambit_quality\");",
		"insert ignore into users (user_name,password,email,lastname,registration_date,registration_status,keywords,webpage) values (?,\"d66636b253cb346dbb6240e30def3618\",\"quality\",\"Automatic quality verifier\",now(),\"confirmed\",\"quality\",\"http://ambit.sourceforge.net\");",
		"insert ignore into user_roles (user_name,role_name) values (?,\"ambit_quality\");",
		
		"delete from quality_pair\n",
		
		"insert into quality_pair (idchemical,idstructure,rel,user_name,updated,`text`)\n"+
		"select s1.idchemical,s1.idstructure id1,\n"+
		"s1.fp1=s2.fp1 && s1.fp2=s2.fp2 && s1.fp3=s2.fp3 && s1.fp4=s2.fp4 && s1.fp5=s2.fp5 && s1.fp6=s2.fp6 &&\n"+
		"s1.fp7=s2.fp7 && s1.fp8=s2.fp8 && s1.fp9=s2.fp9 && s1.fp10=s2.fp10 && s1.fp11=s2.fp11 && s1.fp12=s2.fp12 &&\n"+
		"s1.fp13=s2.fp13 && s1.fp14=s2.fp14 && s1.fp15=s2.fp15 && s1.fp16=s2.fp16 as Q\n"+
		",?,CURRENT_TIMESTAMP(),\n"+
		"CASE 1 WHEN\n"+
		"(s1.fp1=s2.fp1 && s1.fp2=s2.fp2 && s1.fp3=s2.fp3 && s1.fp4=s2.fp4 && s1.fp5=s2.fp5 && s1.fp6=s2.fp6 &&\n"+
		"s1.fp7=s2.fp7 && s1.fp8=s2.fp8 && s1.fp9=s2.fp9 && s1.fp10=s2.fp10 && s1.fp11=s2.fp11 && s1.fp12=s2.fp12 &&\n"+
		"s1.fp13=s2.fp13 && s1.fp14=s2.fp14 && s1.fp15=s2.fp15 && s1.fp16=s2.fp16) = 0\n"+
		"THEN cast(s1.idstructure as char) ELSE concat_WS(',',cast(s1.idstructure as char),cast(s2.idstructure as char)) END\n"+
		"from fp1024_struc s1\n"+
		"join fp1024_struc s2\n"+
		"where s1.idchemical = s2.idchemical and s1.idstructure != s2.idstructure and s1.status='valid' && s2.status='valid'\n"+
		"on duplicate key update rel=rel+values(rel),\n"+
		"user_name=values(user_name),\n"+
		"updated=CURRENT_TIMESTAMP(),\n"+
		"`text`=CASE 1 WHEN values(rel)=0 THEN `text`  ELSE concat_WS(',',`text`,cast(s2.idstructure as char)) END",
		
		"update quality_pair set text=sortString(text)\n",
		
		"delete from quality_chemicals",
		
		"insert into quality_chemicals (idchemical,num_sources,label,num_structures,text)\n"+
		"SELECT idchemical,count(text) c,'Consensus',1,(rel+1) FROM quality_pair\n"+
		"group by idchemical,text\n"+
		"on duplicate key update\n"+
		"num_structures = num_structures+1,\n"+
		"`label`=CASE 1 WHEN values(num_sources)=num_sources THEN 'Ambiguous'  WHEN values(num_sources)<num_sources THEN 'Majority' ELSE 'Majority' END,\n"+
		"`text`=concat_ws(',',`text`,values(`text`))\n",
		
		"update quality_chemicals set text=replace(sortString(text),',',':')\n",

		
	};
		
/*
select idchemical,"Consensus",Sources from
(
SELECT idchemical,count(*) as "Sources", count(distinct(text)) as d FROM quality_pair
group by idchemical
) as L
where d=1


---

SELECT idchemical,count(*) as "Sources", count(distinct(text)) as "Distinct structures" FROM quality_pair
group by idchemical


setting labels
--
insert into chemicals (idchemical,label)
select * from (
SELECT idchemical,"Consensus", count(distinct(text)) as d FROM quality_pair
group by idchemical
) as L
where d=1
on duplicate key update label=values(label)
---
insert into test (idchemical,flag,label)
SELECT idchemical,count(text) c,"consensus" FROM quality_pair
group by idchemical,text
on duplicate key update
`label`=CASE 1 WHEN values(flag)=flag THEN "ambiguous"  WHEN values(flag)<flag THEN "majority" ELSE label END

update chemicals set label='Unknown',num_sources=0;

insert into chemicals (idchemical,num_sources,label)
SELECT idchemical,count(text) c,"Consensus" FROM quality_pair
group by idchemical,text
on duplicate key update
`label`=CASE 1 WHEN values(num_sources)=num_sources THEN "Ambiguous"
WHEN values(num_sources)<num_sources THEN "Majority" ELSE label END

 */
	public CreateQLabelPair() {
		super();
		setGroup(new AmbitUser("quality"));
	}
	public List<QueryParam> getParameters(int index) throws AmbitException {

		if ((index == 1) || (index == 2) || (index == 4)) {
			List<QueryParam> p = new ArrayList<QueryParam>();
			if (getGroup() ==null) setGroup(new AmbitUser("quality"));
			p.add(new QueryParam<String>(String.class,getGroup().getName()));
			return p;
		} else return null;
	}

	public String[] getSQL() throws AmbitException {
		for (String s: sql) System.out.println(s);
		return sql;
	}

	public void setID(int index, int id) {
		// TODO Auto-generated method stub
		
	}

}
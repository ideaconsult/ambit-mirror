package ambit2.db.processors;


public class QualityStatisticsProcessor extends	ConnectionStatisticsProcessor<String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9039738820989381748L;

	//
	public QualityStatisticsProcessor() {
		super();
		metadata = false;
		setSql(new String[] {
				//"Select 'Entire database' as 'Quality labels summary'",
				//"select label as Label,count(*) 'Number of chemicals' from quality_chemicals group by label",
				"SELECT count(*) as 'Number of datasets' from src_dataset",

				"select '\n'",
				"SELECT name as 'Dataset',count(*) as 'Number of compounds' from struc_dataset  join src_dataset using(id_srcdataset)\n"+
				"group by id_srcdataset",

				"select '\n'",
				"SELECT name as 'Dataset',count(idchemical) as 'Number of empty structures' from structure JOIN struc_dataset using(idstructure) join src_dataset using(id_srcdataset)\n"+
				"where type_structure='NA'"+
				"group by id_srcdataset",
				
				"select '\n'",
				
				"Select 'Entire database' as '\nQuality labels summary'",
				"select label as Label,text as Details,count(*) 'Number of chemicals' from quality_chemicals group by label,text",

				"select '\n'",
				
				"SELECT name as 'Dataset',q.user_name as 'Mode',label,count(*) as 'Number of compounds' from quality_structure q join struc_dataset using(idstructure) join src_dataset using(id_srcdataset)\n"+
				"group by id_srcdataset,q.user_name,label",
		
				"select '\n'",
				
				"SELECT group_concat(distinct(name)) as 'Datasets',q.sameas as 'Mode',label,count(distinct(idstructure)) as 'Number of compounds' from quality_labels q join property_values using(id) join struc_dataset using(idstructure) join src_dataset using(id_srcdataset)\n"+
				"group by id_srcdataset,q.user_name,label",
				
				"select '\n'",
				
				"SELECT name as 'Dataset',group_concat(distinct(q.sameas)) as 'Mode',label,count(distinct(idstructure)) as 'Number of compounds' from quality_labels q join property_values using(id) join struc_dataset using(idstructure) join src_dataset using(id_srcdataset)\n"+
				"group by id_srcdataset,label",
				
				"select '\n'",
				
				"SELECT group_concat(distinct(name)) as 'Datasets',group_concat(distinct(q.sameas)) as 'Mode',q.label,count(distinct(structure.idchemical)) as 'Number of compounds' from quality_labels q join property_values using(id) join structure using(idstructure) join struc_dataset using(idstructure) join src_dataset using(id_srcdataset)\n"+
				"group by q.label",		
				
				
				"SELECT user_name,label,text,count(*) FROM quality_structure	group by user_name,label,text\n",
				
				"select '\n'",

				"select name as 'Dataset',type_structure as 'Structure type',count(*) as 'Number of compounds' from src_dataset\n"+
				"join struc_dataset using(id_srcdataset)\n"+
				"join structure using(idstructure)\n"+
				"group by id_srcdataset,type_structure\n"
				
		});
		
	}

}

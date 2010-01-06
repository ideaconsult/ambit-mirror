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
				"Select 'Entire database' as 'Quality labels summary'",
				"select label as Label,text as Details,count(*) 'Number of chemicals' from quality_chemicals group by label,text",
				/*
				"Select 'by sources' as 'Quality labels summary'",
				"SELECT name as 'Source',ifnull(q.label,'Unknown')as Label,count(*) as 'Number of chemicals' FROM\n"+
				"chemicals left join quality_chemicals q using(idchemical)\n"+
				"join structure using(idchemical)\n"+
				"join struc_dataset using(idstructure)\n"+
				"join src_dataset using(id_srcdataset)\n"+
				"group by id_srcdataset,q.label\n"
				*/
				
				"SELECT name as 'Source dataset',q.user_name as 'Mode',label,count(*) as 'number of compounds' from quality_structure q join struc_dataset using(idstructure) join src_dataset using(id_srcdataset)\n"+
				//"where label='ProbablyERROR' and q.user_name='comparison'\n"+
				"group by id_srcdataset,q.user_name,label",
		
				/*
				"SELECT name as 'Dataset',count(*) as 'Minority' FROM quality_chemicals q\n"+
				"join quality_pair p using(idchemical)\n"+
				"join struc_dataset using(idstructure)\n"+
				"join src_dataset using(id_srcdataset)\n"+
				"where num_sources!=(rel+1) and q.label='Majority'\n"+
				"group by id_srcdataset\n"
				*/
				
				/* stat percent
select name,bad,E,100*bad/E from
(
(
SELECT count(*) as bad,id_srcdataset from quality_structure join struc_dataset using(idstructure)
where label="ProbablyERROR" and user_name="comparison"
group by id_srcdataset
) a join
(
select count(*) as E,id_srcdataset from struc_dataset group by id_srcdataset
) b using(id_srcdataset)
) join src_dataset using(id_srcdataset)
				 */
				
		});
		
	}

}

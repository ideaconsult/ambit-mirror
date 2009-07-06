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
				
				"SELECT name as 'Dataset',count(*) as 'Minority' FROM quality_chemicals q\n"+
				"join quality_pair p using(idchemical)\n"+
				"join struc_dataset using(idstructure)\n"+
				"join src_dataset using(id_srcdataset)\n"+
				"where num_sources!=(rel+1) and q.label='Majority'\n"+
				"group by id_srcdataset\n"
				
		});
		
	}

}

-- substance table
SELECT null,prefix,hex(uuid),documentType,format,name,
publicname,content,substanceType,rs_prefix,hex(rs_uuid),owner_prefix,hex(owner_uuid),owner_name 
into outfile "{TMP}substance_table.tsv"
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
FROM substance;

-- substance_protocolapplications table
SELECT 
document_prefix,hex(document_uuid),topcategory,endpointcategory,endpoint,guidance,substance_prefix,hex(substance_uuid),
params,interpretation_result,interpretation_criteria,reference,reference_year,reference_owner,updated,
reliability,isRobustStudy,isUsedforClassification,isUsedforMSDS,purposeFlag,studyResultType
into outfile "{TMP}substance_protocolapplication_table.tsv"
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
FROM substance_protocolapplication;

-- substance_experiments table
SELECT 
null,document_prefix,hex(document_uuid),topcategory,endpointcategory,null,endpoint,conditions,unit,
loQualifier,loValue,upQualifier,upValue,textValue,errQualifier,err,substance_prefix,hex(substance_uuid)
into outfile "{TMP}substance_experiment_table.tsv"
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
FROM substance_experiment;

-- substance_ids table
SELECT prefix,hex(uuid),type,id
into outfile "{TMP}substance_ids_table.tsv"
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
FROM substance_ids;

-- substance composition + chemicals + substance uuid
SELECT 
null,
cmp_prefix,hex(cmp_uuid),
null,null,
relation,function,
proportion_real_lower,proportion_real_lower_value,
proportion_real_upper,proportion_real_upper_value,
proportion_real_unit,
proportion_typical,proportion_typical_value,proportion_typical_unit,
c.rs_prefix,hex(c.rs_uuid),c.name,hidden,
prefix,hex(uuid),
inchi,smiles,formula,inchikey,label,lastmodified
into outfile "{TMP}substance_relation_chemicals_table.tsv"
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
FROM substance_relation c
join substance using(idsubstance)
left join chemicals using(idchemical);

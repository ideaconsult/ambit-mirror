-- ---------------------------------------------------------------------------
-- Exports substances and studies into tab delimited files into {TMP} folder
-- ---------------------------------------------------------------------------

-- substance table
SELECT null,prefix,hex(uuid),documentType,format,name,
publicname,content,substanceType,rs_prefix,hex(rs_uuid),owner_prefix,hex(owner_uuid),owner_name 
into outfile "{TMP}substance_table.tsv"
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
FROM substance order by idsubstance;

-- substance_protocolapplications table
SELECT 
document_prefix,hex(document_uuid),topcategory,endpointcategory,endpoint,guidance,substance_prefix,hex(substance_uuid),
params,interpretation_result,interpretation_criteria,reference,reference_year,reference_owner,updated,
reliability,isRobustStudy,isUsedforClassification,isUsedforMSDS,purposeFlag,studyResultType
into outfile "{TMP}substance_protocolapplication_table.tsv"
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
FROM substance_protocolapplication order by document_prefix,document_uuid;

-- substance_experiments table
SELECT 
null,document_prefix,hex(document_uuid),topcategory,endpointcategory,hex(endpointhash),endpoint,conditions,unit,
loQualifier,loValue,upQualifier,upValue,textValue,errQualifier,err,substance_prefix,hex(substance_uuid)
into outfile "{TMP}substance_experiment_table.tsv"
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
FROM substance_experiment order by document_prefix,document_uuid;

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
null,idchemical,
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

-- export only ids of chemcials which are associated with substances
-- properies: names
-- 242349
SELECT 
null,idchemical,name,units,comments,text, value,title,url,type FROM property_values 
join properties using(idproperty)
join property_string using(idvalue_string)
join catalog_references using(idreference)
join (SELECT idchemical FROM substance_relation
group by idchemical) as chem using(idchemical)
where name = "Names"
into outfile "{TMP}property_names.tsv"
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n';

-- cas
-- 165355 
SELECT 
null,idchemical,name,units,comments,text, value,title,url,type FROM property_values 
join properties using(idproperty)
join property_string using(idvalue_string)
join catalog_references using(idreference)
join (SELECT idchemical FROM substance_relation
group by idchemical) as chem using(idchemical)
where name = "CasRN"
into outfile "{TMP}property_casrn.tsv"
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n';

-- i5uuid
-- 95370
 SELECT 
null,idchemical,name,units,comments,text, value,title,url,type FROM property_values 
join properties using(idproperty)
join property_string using(idvalue_string)
join catalog_references using(idreference)
join (SELECT idchemical FROM substance_relation
group by idchemical) as chem using(idchemical)
where name = "I5UUID"
into outfile "{TMP}property_i5uuid.tsv"
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n';

-- tradenames
-- 7011 
SELECT 
null,idchemical,name,units,comments,text, value,title,url,type FROM property_values 
join properties using(idproperty)
join property_string using(idvalue_string)
join catalog_references using(idreference)
join (SELECT idchemical FROM substance_relation
group by idchemical) as chem using(idchemical)
where name regexp "^Trade"
into outfile "{TMP}property_tradename.tsv"
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n';

-- ec
-- 154643 
SELECT 
null,idchemical,name,units,comments,text, value,title,url,type FROM property_values 
join properties using(idproperty)
join property_string using(idvalue_string)
join catalog_references using(idreference)
join (SELECT idchemical FROM substance_relation
group by idchemical) as chem using(idchemical)
where name = "EC"
into outfile "{TMP}property_ec.tsv"
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n';
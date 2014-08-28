ALTER TABLE `substance_experiment` ADD COLUMN `topcategory` VARCHAR(32) NULL  AFTER `document_uuid` , ADD COLUMN `endpointcategory` VARCHAR(45) NULL  AFTER `topcategory` , ADD COLUMN `substance_prefix` VARCHAR(6) NULL  AFTER `err` , ADD COLUMN `substance_uuid` VARBINARY(16) NULL  AFTER `substance_prefix` 
, ADD INDEX `category-x` (`topcategory` ASC, `endpointcategory` ASC, `endpoint` ASC, `endpointhash` ASC) 
, ADD INDEX `substance-x` (`substance_prefix` ASC, `substance_uuid` ASC) ;

ALTER TABLE `substance_protocolapplication` DROP INDEX `topcategory`,
 ADD INDEX `topcategory` USING BTREE(`topcategory`, `endpointcategory`, `interpretation_result`);

insert into version (idmajor,idminor,comment) values (8,4,"AMBIT2 schema (substance_experiment)");

-- add the values into the denormalized table
insert into substance_experiment
SELECT 
idresult,
e.document_prefix,e.document_uuid,
p.topcategory,p.endpointcategory,
e.endpointhash,
e.endpoint,
e.conditions,
e.unit,
e.loQualifier,e.loValue,e.upQualifier,e.upValue,
e.textValue,e.errQualifier,e.err,
p.substance_prefix,p.substance_uuid 
FROM substance_protocolapplication p
join substance_experiment e
on (`p`.`document_prefix` = `e`.`document_prefix`) and (`p`.`document_uuid` = `e`.`document_uuid`)
on duplicate key update
topcategory=values(topcategory),
endpointcategory=values(endpointcategory),
substance_prefix=values(substance_prefix),
substance_uuid=values(substance_uuid);

-- update the view
CREATE  OR REPLACE VIEW `substance_study_view` AS
select idsubstance,p.substance_prefix,p.substance_uuid,documentType,format,
name,publicname,content,substanceType,
rs_prefix,rs_uuid,
owner_prefix,owner_uuid,owner_name,
p.document_prefix,p.document_uuid,
p.topcategory,p.endpointcategory,p.endpoint,
guidance,
reliability,isRobustStudy,purposeFlag,studyResultType,
params,interpretation_result,interpretation_criteria,
reference,updated,idresult,
e.endpoint as effectendpoint,conditions,unit, 
loQualifier, loValue, upQualifier, upValue, textValue, err from substance s
join substance_protocolapplication p on
s.prefix=p.substance_prefix and s.uuid=p.substance_uuid
left join substance_experiment e on
p.document_prefix=e.document_prefix and p.document_uuid=e.document_uuid
;
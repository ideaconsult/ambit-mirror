-- -----------------------------------------------------
-- experiment overview
-- -----------------------------------------------------
create view experiments_overview as
select  p.topcategory,count(*),"protocol",guidance,params from substance_protocolapplication p 
group by guidance
union
SELECT p.topcategory,count(*),"method",JSON_EXTRACT(replace(params,'E.','E_'),'$.E_method') cell,params 
FROM substance s join substance_protocolapplication p on uuid=substance_uuid 
join substance_experiment e using(document_uuid)  group by cell
union
SELECT p.topcategory,count(*),"sop",JSON_EXTRACT(replace(params,'E.','E_'),'$.E_sop_reference') cell ,params
FROM substance s join substance_protocolapplication p on uuid=substance_uuid 
join substance_experiment e using(document_uuid)  group by cell
union
SELECT p.topcategory,count(*),"species",JSON_EXTRACT(replace(params,'E.','E_'),'$.E_animal_model') cell ,params
FROM substance s join substance_protocolapplication p on uuid=substance_uuid 
join substance_experiment e using(document_uuid)  group by cell
union
SELECT p.topcategory,count(*),"cells",JSON_EXTRACT(replace(params,'E.','E_'),'$.E_cell_type') cell ,params
FROM substance s join substance_protocolapplication p on uuid=substance_uuid 
join substance_experiment e using(document_uuid)  group by cell
union
SELECT p.topcategory,count(*),"medium",JSON_EXTRACT(replace(params,'E.','E_'),'$.MEDIUM') cell ,params
FROM substance s join substance_protocolapplication p on uuid=substance_uuid 
join substance_experiment e using(document_uuid)  group by cell
union
SELECT p.topcategory,count(*),"concentration",JSON_EXTRACT(replace(conditions,'E.','E_'),'$.concentration') cell ,conditions
FROM substance s join substance_protocolapplication p on uuid=substance_uuid 
join substance_experiment e using(document_uuid)  group by cell
union
SELECT p.topcategory,count(*),"time",JSON_EXTRACT(replace(params,'E.','E_'),'$.E_exposure_time') cell ,params
FROM substance s join substance_protocolapplication p on uuid=substance_uuid 
join substance_experiment e using(document_uuid)  group by cell
;


insert into version (idmajor,idminor,comment) values (9,3,"AMBIT2 schema - experiment overview");
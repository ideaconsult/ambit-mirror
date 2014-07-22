-- The hash will be used as a lookup when generating dataset on the fly  

ALTER TABLE `substance_experiment` ADD COLUMN `endpointhash` VARBINARY(20) NULL DEFAULT NULL COMMENT 'SHA1 over endpoint, unit and conditions'  AFTER `document_uuid` ;
ALTER TABLE `substance_experiment` ADD INDEX `hash-x` (`endpointhash` ASC) ;

update substance_experiment set endpointhash= unhex(sha1(concat(ifnull(endpoint,""),ifnull(unit,""),ifnull(conditions,""))));

-- toxmatch qmaps
ALTER TABLE `qsasmap4` ADD COLUMN `g2rank` INT NULL  AFTER `g2` ;
ALTER TABLE `qsasmap4` ADD INDEX `g2rank` (`g2rank` ASC) ;

DROP PROCEDURE IF EXISTS `g2counts`;
DELIMITER $$

CREATE PROCEDURE `g2counts`(IN dataset INT, IN property INT, IN dactivity DOUBLE, IN simthreshold DOUBLE,IN laplacek DOUBLE)
BEGIN
  insert into qsasheader values(null,dactivity,simthreshold,dataset,property,now());  
  set @idsasmap = LAST_INSERT_ID();
  set @laplacek = ifnull(laplacek,1E-7);
  insert into qsasmap4
	select @idsasmap,idchemical,
	sum(IF ((act>=dactivity) and (sim>=simthreshold),1,0)) a,
	sum(IF ((act>=dactivity) and (sim<simthreshold),1,0)) b,
	sum(IF ((act<dactivity) and (sim>=simthreshold),1,0)) c,
	sum(IF ((act<dactivity) and (sim<simthreshold),1,0)) d,0,0,null
	from 
	(
	select null,@idsasmap,s1.idchemical,
		  abs(s1.value_num-s2.value_num)  act,
		  (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
		  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
		  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
		  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
		  /(f1.bc + f2.bc -
		  (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
		  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
		  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
		  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
		  ) sim
		  from   struc_dataset d1
		  join   struc_dataset d2
		  join property_values s2 on d2.idstructure=s2.idstructure
		  join property_values s1 on d1.idstructure=s1.idstructure
		  join fp1024 f1 on f1.idchemical=s1.idchemical
		  join fp1024 f2 on f2.idchemical=s2.idchemical
		  where s2.idproperty=property and d1.id_srcdataset=dataset
		  and d2.id_srcdataset= dataset and s1.idproperty=property
		  and s1.idchemical != s2.idchemical
	) pairwise
	group by idchemical;
	-- calculate g2
	update qsasmap4 set fisher= (a+c)/(a+c+d),
    	  g2 = 
    	  (a+@laplacek)* ln((a+@laplacek)*((c+@laplacek)+(d+@laplacek))/((c+@laplacek)*((a+@laplacek)+(b+@laplacek))))+
  		  (b+@laplacek)* ln((b+@laplacek)*((c+@laplacek)+(d+@laplacek))/((d+@laplacek)*((a+@laplacek)+(b+@laplacek))))
  		  where idsasmap=@idsasmap;
   -- assign rank		  
   SET @rownum := 0;
   insert into qsasmap4 
       (SELECT idsasmap,idchemical,a,b,c,d,fisher,g2,(@rownum := @rownum + 1) AS rank FROM qsasmap4 WHERE idsasmap=@idsasmap ORDER BY g2 DESC
        ) on duplicate key  update  g2rank=values(g2rank);
          		  
   -- return sasmap id
   SELECT idsasmap,threshold_dact,threshold_sim,id_srcdataset,idproperty,p.name,p.comments,p.units,d.name 
   from qsasheader join src_dataset d using(id_srcdataset) join properties p using(idproperty) where idsasmap=@idsasmap;   
   
END$$

DELIMITER ;

insert into version (idmajor,idminor,comment) values (8,2,"AMBIT2 schema");

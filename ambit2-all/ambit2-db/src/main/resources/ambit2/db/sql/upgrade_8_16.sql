-- support for qmaps from substance table
ALTER TABLE `qsasheader` ADD COLUMN `topcategory` VARCHAR(32) NULL  AFTER `created` , ADD COLUMN `endpointcategory` VARCHAR(45) NULL  AFTER `topcategory` , ADD COLUMN `guidance` VARCHAR(255) NULL  AFTER `endpointcategory` , ADD COLUMN `reference` TEXT NULL  AFTER `guidance` ;

DROP PROCEDURE IF EXISTS `g2studycounts`;
DELIMITER $$

CREATE PROCEDURE `g2studycounts`(
IN v_topcategory VARCHAR(32), IN v_endpointcategory VARCHAR(45),
IN v_guidance VARCHAR(255), IN v_reference VARCHAR(255), IN simthreshold DOUBLE,IN laplacek DOUBLE)

BEGIN
  DELETE from qsasheader 
  where topcategory=v_topcategory and endpointcategory=v_endpointcategory and guidance=v_guidance and reference=v_reference and threshold_sim=simthreshold;
  insert into qsasheader 
  values (null,0,simthreshold,null,0,now(),v_topcategory,v_endpointcategory,v_guidance,v_reference,null);

  set @idsasmap = LAST_INSERT_ID();
  set @laplacek = ifnull(laplacek,1E-7);
  insert into qsasmap4
	select @idsasmap,idchemical,
		sum(IF ((act>0) and (sim>=simthreshold),1,0)) a,
		sum(IF ((act>0) and (sim<simthreshold),1,0)) b,
		sum(IF ((act=0) and (sim>=simthreshold),1,0)) c,
		sum(IF ((act=0) and (sim<simthreshold),1,0)) d,0,0,null
		from 
	(
	select 
	f1.idchemical,
	if(f1.interpretation_result = f2.interpretation_result,0,1) act,
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
	from (
	SELECT idchemical,idsubstance,uuid as substance_uuid,prefix as substance_prefix,bc,fp1,fp2,fp3,fp4,fp5,fp6,fp7,fp8,fp9,fp10,fp11,fp12,fp13,fp14,fp15,fp16,interpretation_result,
	topcategory,endpointcategory,guidance FROM substance_relation
	join substance s using(idsubstance) join fp1024 using(idchemical) 
	join substance_protocolapplication p on p.substance_prefix=s.prefix and p.substance_uuid=s.uuid 
	where endpointcategory=v_endpointcategory and topcategory=v_topcategory and guidance=v_guidance and reference=v_reference
	) as f1
	join 
	(
	SELECT idchemical,idsubstance,uuid as substance_uuid,prefix as substance_prefix,bc,fp1,fp2,fp3,fp4,fp5,fp6,fp7,fp8,fp9,fp10,fp11,fp12,fp13,fp14,fp15,fp16,interpretation_result,
	topcategory,endpointcategory,guidance FROM substance_relation
	join substance s using(idsubstance) join fp1024 using(idchemical) 
	join substance_protocolapplication p on p.substance_prefix=s.prefix and p.substance_uuid=s.uuid 
	where endpointcategory=v_endpointcategory and topcategory=v_topcategory and guidance=v_guidance and reference=v_reference
	) as f2
	where f1.idchemical != f2.idchemical
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
   SELECT  idsasmap,0,threshold_sim,null,null,reference,concat(topcategory,"/",endpointcategory),guidance
   from qsasheader where idsasmap=@idsasmap;   
   

END $$

DELIMITER ;

DROP PROCEDURE IF EXISTS `g2studyprofile`;
DELIMITER $$
CREATE PROCEDURE `g2studyprofile`(IN a_topcategory VARCHAR(32), IN a_endpointcategory VARCHAR(45),IN a_guidance varchar(255),
 IN simthreshold DOUBLE,IN laplacek DOUBLE)
BEGIN
    DECLARE v_topcategory VARCHAR(32);
	DECLARE v_endpointcategory VARCHAR(45);
	DECLARE v_guidance varchar(255);
    DECLARE v_reference varchar(255);

    DECLARE no_more_rows BOOLEAN DEFAULT FALSE;
   
    DECLARE c INT DEFAULT 0;

    DECLARE props CURSOR FOR
		SELECT topcategory,endpointcategory,guidance,reference FROM substance_protocolapplication
		where 
		((a_topcategory is null) or topcategory=a_topcategory)
		and ((a_endpointcategory is null) or endpointcategory=a_endpointcategory)
		and ((a_guidance is null) or guidance=a_guidance) 
		group by topcategory,endpointcategory,guidance,reference;

    DECLARE CONTINUE HANDLER FOR NOT FOUND BEGIN
        SET no_more_rows = TRUE;
    END;

    OPEN props;

    the_loop: LOOP

		FETCH props into v_topcategory,v_endpointcategory,v_guidance,v_reference;
    	IF no_more_rows THEN
	    	CLOSE props;
		    LEAVE the_loop;
	    END IF;
		
 	    call g2studycounts(v_topcategory,v_endpointcategory,v_guidance,v_reference,simthreshold,laplacek);

    END LOOP the_loop;

   SELECT  idsasmap,0,threshold_sim,null,null,reference,concat(topcategory,"/",endpointcategory),guidance
   from qsasheader  where
	((a_topcategory is null) or topcategory=a_topcategory)
		and ((a_endpointcategory is null) or endpointcategory=a_endpointcategory)
		and ((a_guidance is null) or guidance=a_guidance) and threshold_sim=simthreshold;
END $$

DELIMITER ;

insert into version (idmajor,idminor,comment) values (8,16,"AMBIT2 schema");


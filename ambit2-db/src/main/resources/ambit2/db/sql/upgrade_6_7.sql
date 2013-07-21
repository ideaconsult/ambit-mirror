-- ----------------------------
-- Chemical space stats
-- ----------------------------

CREATE TABLE `qsasheader` (
  `idsasmap` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `threshold_dact` double NOT NULL,
  `threshold_sim` double NOT NULL,
  `id_srcdataset` int(10) unsigned NOT NULL,
  `idproperty` int(10) unsigned NOT NULL,
  `created` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idsasmap`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `qsasmap4` (
  `idsasmap` int(10) unsigned NOT NULL,
  `idchemical` int(10) unsigned NOT NULL,
  `a` int(10) unsigned NOT NULL,
  `b` int(10) unsigned NOT NULL,
  `c` int(10) unsigned NOT NULL,
  `d` int(10) unsigned NOT NULL,
  `fisher` double DEFAULT NULL,
  `g2` double unsigned NOT NULL,
  PRIMARY KEY (`idsasmap`,`idchemical`),
  KEY `idsasmap_idx` (`idsasmap`),
  KEY `idchemical_idx` (`idchemical`),
  KEY `fisher_index` (`idsasmap`,`fisher`),
  KEY `g2_index` (`g2`,`idsasmap`),
  CONSTRAINT `idchemical` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `idsasmap` FOREIGN KEY (`idsasmap`) REFERENCES `qsasheader` (`idsasmap`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
	sum(IF ((act<dactivity) and (sim<simthreshold),1,0)) d,0,0
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
   -- return sasmap id
   SELECT idsasmap,threshold_dact,threshold_sim,id_srcdataset,idproperty,p.name,p.comments,p.units,d.name 
   from qsasheader join src_dataset d using(id_srcdataset) join properties p using(idproperty) where idsasmap=@idsasmap;   
END $$

DELIMITER ;


-- profile all properties in a template
DROP procedure if exists `g2profile`;

delimiter $$

CREATE PROCEDURE `g2profile`(IN dataset INT, IN template INT, IN simthreshold DOUBLE,IN laplacek DOUBLE)
BEGIN

    DECLARE no_more_rows BOOLEAN DEFAULT FALSE;
    DECLARE property DOUBLE;
    DECLARE avgv DOUBLE;
    DECLARE stdev DOUBLE;
    
    DECLARE c INT DEFAULT 0;

    DECLARE notstarted BOOLEAN DEFAULT TRUE;
    DECLARE leftBin DOUBLE;
    DECLARE bin INT DEFAULT 0;
    DECLARE bins TEXT DEFAULT "";

    -- calculate bins - same as getBinnedRange
    -- sort and count
    DECLARE props CURSOR FOR
    select idproperty,avg(value_num) a,std(value_num) s from property_values 
	join template_def using(idproperty)
	where idtemplate=template group by idproperty order by s desc;

    DECLARE CONTINUE HANDLER FOR NOT FOUND BEGIN
        SET no_more_rows = TRUE;
    END;

    OPEN props;

    the_loop: LOOP

	    FETCH props into property,avgv,stdev;
    	IF no_more_rows THEN
	    	CLOSE props;
		    LEAVE the_loop;
	    END IF;
		
		IF stdev>0 THEN
            delete from qsasheader where id_srcdataset=dataset and idproperty=property;
			call g2counts(dataset,property,stdev,simthreshold,laplacek);
		END IF;

    END LOOP the_loop;

	SELECT idsasmap,threshold_dact,threshold_sim,id_srcdataset,idproperty,p.name,p.comments,p.units,d.name 
	   from qsasheader join src_dataset d using(id_srcdataset) join properties p using(idproperty)
	join template_def using(idproperty) where template_def.idtemplate=template;
END $$

DELIMITER ;
insert into version (idmajor,idminor,comment) values (6,7,"AMBIT Schema: chemical landscape support");

-- GRANT EXECUTE ON PROCEDURE g2counts TO 'guest'@'localhost';
-- GRANT EXECUTE ON PROCEDURE g2profile TO 'guest'@'localhost';

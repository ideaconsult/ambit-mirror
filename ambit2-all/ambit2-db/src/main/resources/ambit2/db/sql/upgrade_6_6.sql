-- ----------------------------
-- Chemical space stats
-- ----------------------------
CREATE TABLE `chemstats` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `idchemical` int(10) unsigned NOT NULL,
  `dactivity` int(10) unsigned NOT NULL,
  `similarity` int(10) unsigned NOT NULL,
  `count` int(10) unsigned NOT NULL DEFAULT '0',
  `threshold_dact` double NOT NULL,
  `threshold_sim` double NOT NULL,
  `id_srcdataset` int(10) unsigned NOT NULL,
  `idproperty` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Index_5` (`idchemical`,`id_srcdataset`,`idproperty`,`dactivity`,`similarity`),
  KEY `FK_chemstats_1` (`id_srcdataset`),
  KEY `FK_chemstats_2` (`idproperty`),
  CONSTRAINT `FK_chemstats_1` FOREIGN KEY (`id_srcdataset`) REFERENCES `src_dataset` (`id_srcdataset`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_chemstats_2` FOREIGN KEY (`idproperty`) REFERENCES `properties` (`idproperty`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_chemstats_3` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- 50 bins
-- -----------------------------------------------------
create view bins50 as
  select 0 as d
  union select 1 as d union select 2 as d union select 3 as d union select 4 as d union select 5 as d
  union select 6 as d union select 7 as d union select 8 as d union select 9 as d union select 10 as d
  union select 11 as d union select 12 as d union select 13 as d union select 14 as d union select 15 as d
  union select 16 as d union select 17 as d union select 18 as d union select 19 as d union select 20 as d
  union select 21 as d union select 22 as d union select 23 as d union select 24 as d union select 25 as d
  union select 26 as d union select 27 as d union select 28 as d union select 29 as d union select 30 as d
  union select 31 as d union select 32 as d union select 33 as d union select 34 as d union select 35 as d
  union select 36 as d union select 37 as d union select 38 as d union select 39 as d union select 40 as d
  union select 41 as d union select 42 as d union select 43 as d union select 44 as d union select 45 as d
  union select 46 as d union select 47 as d union select 48 as d union select 49 as d union select 50 as d;

-- -----------------------------------------------------
-- Returns Tanimoto similarity betweeh two compounds given by idchemical 
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS `tanimotoChemicals`;

DELIMITER $$
CREATE FUNCTION `tanimotoChemicals`(chemical1_id INT, chemical2_id INT) RETURNS double
BEGIN

  DECLARE tanimoto DOUBLE DEFAULT 0;

  select Nab/(Na+Nb-Nab) into tanimoto from (
  select
	bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16)
  as Nab, f1.bc as Na, f2.bc as Nb
  from fp1024 as f1
  join fp1024 as f2 
  where f1.idchemical=chemical1_id and f2.idchemical=chemical2_id) T;

  RETURN tanimoto;

END $$

DELIMITER ;


-- -----------------------------------------------------
-- Returns maximum value of a property within a dataset
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS `getPropertyMax`;
DELIMITER $$
CREATE FUNCTION `getPropertyMax`(property_id INT, dataset_id INT) RETURNS double
BEGIN

    DECLARE range_max REAL;

    select max(value_num) into range_max from property_values
    join struc_dataset using(idstructure) where
    idproperty=property_id and value_num is not null and id_srcdataset=dataset_id;

    RETURN range_max;

END $$
DELIMITER ;

-- -----------------------------------------------------
-- Returns minimum value of a property within a dataset
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS `getPropertyMin`;
DELIMITER $$
CREATE FUNCTION `getPropertyMin`(property_id INT, dataset_id INT) RETURNS double
BEGIN

    DECLARE range_min REAL;

    select min(value_num) into range_min from property_values
    join struc_dataset using(idstructure) where
    idproperty=property_id and value_num is not null and id_srcdataset=dataset_id;

    RETURN range_min;

END $$
DELIMITER ;

-- -----------------------------------------------------
-- Returns range with variable width bins
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS `getBinnedRange`;
DELIMITER $$
CREATE FUNCTION `getBinnedRange`(property_id INT, dataset_id INT, maxPoints INT) RETURNS text CHARSET utf8 COLLATE utf8_bin
BEGIN
    DECLARE no_more_rows BOOLEAN;
    DECLARE number DOUBLE;
    DECLARE c INT DEFAULT 0;
    -- sort and count
    DECLARE numbers CURSOR FOR
    select value_num from property_values join struc_dataset using(idstructure)
    where id_srcdataset=dataset_id and idproperty=property_id and value_num is not null
    order by value_num;

    DECLARE CONTINUE HANDLER FOR NOT FOUND     SET no_more_rows = TRUE;
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
          BEGIN
            return c;
          END;
    SET @left = NULL;
    SET @bin=0;
    SET @bins = '';
    OPEN numbers;
    the_loop: LOOP

	    FETCH numbers into number;
    	IF no_more_rows THEN
	    	CLOSE numbers;
		    LEAVE the_loop;
	    END IF;

      IF (@left IS NULL) THEN SET @left = number; END IF;

      SET c = c +1;
      IF (c >= maxPoints) THEN

        if (@bin>0) THEN SET @bins = concat(@bins," UNION "); END IF;
        SET @bins = concat(@bins,"SELECT ",@bin," as bin,",@left," as min1,",number," as max1,",c);
        SET c = 0;
        set @left = number;
        set @bin = @bin+1;
      END IF;

    END LOOP the_loop;

    IF (c > 0) THEN
        SET @bins = concat(@bins," UNION SELECT ",@bin," as bin,",@left," as min1,",number," as max1,",c);
    END IF;

    return @bins;
    /*
    prepare  sql_bins  from  @bins;
    execute  sql_bins;
    deallocate prepare sql_bins;
    */
END $$

DELIMITER ;

-- -----------------------------------------------------
-- Returns maximum value of a property within a dataset
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS `recordsInPropertyRange`;
DELIMITER $$
CREATE FUNCTION `recordsInPropertyRange`(property_id INT,dataset_id INT,minProperty DOUBLE, maxProperty DOUBLE) RETURNS INT
BEGIN
    DECLARE entries REAL;
    select count(*) into entries from property_values
    join struc_dataset using(idstructure) where
    idproperty=property_id and value_num is not null and id_srcdataset=dataset_id
    and value_num >= minProperty and value_num < maxProperty;
    RETURN entries;
END $$
DELIMITER ;

-- -----------------------------------------------------
-- Histogram
-- -----------------------------------------------------
DROP PROCEDURE IF EXISTS `histogram`;
DELIMITER $$
CREATE PROCEDURE `histogram`(property_num INT, dataset INT, minProperty DOUBLE, maxProperty DOUBLE)
begin
  select idchemical,idstructure, value_num,d, minProperty+d*(maxProperty-minProperty)/49 as a, minProperty+(d+1)*(maxProperty-minProperty)/49 as b from
  bins50
  join struc_dataset
  join property_values using(idstructure)
  where id_srcdataset=dataset and idproperty=property_num
  and value_num >= minProperty+d*(maxProperty-minProperty)/49  and value_num < minProperty+(d+1)*(maxProperty-minProperty)/49;
end $$
DELIMITER ;

-- -----------------------------------------------------
-- Grid with variable length cells, equal number of points in a cell, avg tanimoto distance in a cell
-- Example: call tanimotoBinnedSpace(13,3,10);
-- property_id
-- dataset_id
-- maxPoints
-- method -  0 : max; 1 : G2 likelihood
-- threshold_sim DOUBLE
-- threshold_act DOUBLE
-- --------------------------------------------------------
DROP PROCEDURE IF EXISTS `tanimotoBinnedSpace`;
DELIMITER $$
CREATE PROCEDURE `tanimotoBinnedSpace`(
                property_id INT,dataset_id INT, maxPoints INT, method INT, threshold_sim DOUBLE, threshold_act DOUBLE)
BEGIN
    DECLARE no_more_rows BOOLEAN DEFAULT FALSE;
    DECLARE number DOUBLE;
    DECLARE c INT DEFAULT 0;

    DECLARE notstarted BOOLEAN DEFAULT TRUE;
    DECLARE leftBin DOUBLE;
    DECLARE bin INT DEFAULT 0;
    DECLARE bins TEXT DEFAULT "";

    -- calculate bins - same as getBinnedRange
    -- sort and count
    DECLARE numbers CURSOR FOR
    select value_num from property_values join struc_dataset using(idstructure)
    where id_srcdataset=dataset_id and idproperty=property_id and value_num is not null
    order by value_num;

    DECLARE CONTINUE HANDLER FOR NOT FOUND BEGIN
        SET no_more_rows = TRUE;
    END;

    OPEN numbers;

    the_loop: LOOP

	    FETCH numbers into number;
    	IF no_more_rows THEN
	    	CLOSE numbers;
		    LEAVE the_loop;
	    END IF;

      IF (notstarted) THEN
          SET leftBin = number; SET notstarted = false;
      END IF;

      SET c = c +1;
      IF (c >= maxPoints) THEN

        if (number > leftBin) THEN

          if (bin>0) THEN SET bins = concat(bins," UNION "); END IF;

          SET bins = concat(bins,"SELECT ",bin," as bin,",leftBin," as min1,",number," as max1,",c , " as c");
          SET c = 0;
          set leftBin = number;
          set bin = bin+1;
        END IF;
      END IF;

    END LOOP the_loop;

    IF (c > 0) THEN
        IF (leftbin = number) THEN SET number = leftBin + 0.0001; END IF;
        SET bins = concat(bins," UNION SELECT ",bin," as bin,",leftBin," as min1,",number," as max1,",c, " as c");

        -- all so far for bins
        -- now the space itself
        -- if (method = 0) THEN
        --  SET @matrix = concat("select a.bin,a.min1,a.max1,b.bin,b.min1,b.max1,tanimotoCell(",
        --         property_id,",",dataset_id,",a.min1,a.max1,b.min1,b.max1),a.c,b.c from (",
        --          bins,") a join (",bins,") b where a.bin<=b.bin");
        if ((method=4) || (method=5)) THEN
          SET @matrix = concat("select a.bin,a.min1,a.max1,b.bin,b.min1,b.max1,tanimotoG2row(",
                  property_id,",",dataset_id,",a.min1,a.max1,",
                  method,",",threshold_sim,",b.max1),a.c,b.c from (",
                  bins,") a join (",bins,") b");
        else
          SET @matrix = concat("select a.bin,a.min1,a.max1,b.bin,b.min1,b.max1,tanimotoG2(",
                  property_id,",",dataset_id,",a.min1,a.max1,b.min1,b.max1,",
                  method,",",threshold_sim,",",threshold_act,"),a.c,b.c from (",
                  bins,") a join (",bins,") b where a.bin<=b.bin");

        end if;

        prepare  sqlMatrix  from  @matrix;
        execute  sqlMatrix;
        deallocate prepare  sqlMatrix;

    END IF;

END $$

DELIMITER ;

-- -------------
-- Histogram, writes slices into query_resutls table 
-- -------------
DROP PROCEDURE IF EXISTS `histogramFlexDB`;
DELIMITER $$
CREATE PROCEDURE `histogramFlexDB`(property_id INT,dataset_id INT,
                minProperty DOUBLE, maxProperty DOUBLE, maxPoints INT)
BEGIN
    DECLARE no_more_rows BOOLEAN;
    DECLARE number DOUBLE;
    DECLARE chemical INT DEFAULT 0;
    DECLARE structure INT DEFAULT 0;
    DECLARE c INT DEFAULT 0;

    -- sort and count
    DECLARE numbers CURSOR FOR
    select idchemical,idstructure,value_num from property_values join struc_dataset using(idstructure)
    where id_srcdataset=dataset_id and idproperty=property_id
    and value_num>=minProperty and value_num<maxProperty
    order by value_num;

    DECLARE CONTINUE HANDLER FOR NOT FOUND     SET no_more_rows = TRUE;


    INSERT IGNORE into sessions values (null,"admin",now(),now(),"chemspace");
    INSERT IGNORE into query (SELECT null,idsessions,"chemspace","",null from sessions where title="chemspace");
    DELETE r from query_results r,`query` q where q.idquery=r.idquery and q.name="chemspace";

    SET @left = minProperty;
    SET @bin=0;
    SET @bins = 'SELECT 0,0,0,0';
    SET @chemicals = '';
    OPEN numbers;
    the_loop: LOOP

	    FETCH numbers into chemical,structure,number;
    	IF no_more_rows THEN
	    	CLOSE numbers;
		    LEAVE the_loop;
	    END IF;

      SET c = c +1;
      IF (c >= maxPoints) THEN
        SET @bins = concat(@bins," UNION SELECT ",@bin," as bin,",@left," as min1,",number," as max1,",c,",'",@chemicals,"'");
        SET c = 0;
        set @left = number;
        set @bin = @bin+1;
        set @chemicals = chemical;
      ELSE
        SET @chemicals = concat(@chemicals,",",chemical);
      END IF;

      INSERT INTO query_results SELECT idquery,chemical,structure,1,@bin,number from query where name="chemspace";

    END LOOP the_loop;

    IF (c > 0) THEN
        SET @bins = concat(@bins," UNION SELECT ",@bin," as bin,",@left," as min1,",number," as max1,",c,",'",@chemicals,"'");
    END IF;

 SELECT @bins;

  --  prepare  sqlBins  from  @bins;
  --  execute  sqlBins;
  --  deallocate prepare  sqlBins;

END $$

DELIMITER ;

-- -----------------------------------------------------
-- Tanimoto distance of group of chemicals, given by property range
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS `tanimotoCell`;
DELIMITER $$
CREATE FUNCTION `tanimotoCell`(property_id INT, dataset_id INT, min1 DOUBLE, max1 DOUBLE, min2 DOUBLE, max2 DOUBLE) RETURNS double
BEGIN
  DECLARE tanimoto DOUBLE DEFAULT 0;
  select
	  max(
			  (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
			  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
			  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
			  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
			  /(f1.bc + f2.bc -
			  (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
			  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
			  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
			  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
			  )) into tanimoto
  from struc_dataset d1
  join struc_dataset d2
  join property_values s1 on d1.idstructure=s1.idstructure
  join property_values s2 on d2.idstructure=s2.idstructure
  join fp1024 f1 on f1.idchemical=s1.idchemical
  join fp1024 f2 on f2.idchemical=s2.idchemical
  where d1.id_srcdataset=dataset_id
  and d2.id_srcdataset= dataset_id
  and s1.idproperty=property_id
  and s2.idproperty=property_id
  and s1.value_num >= min1  and s1.value_num < max1
  and s2.value_num >= min2 and s2.value_num < max2
  and d1.idstructure < d2.idstructure
  ;
  return tanimoto;
end $$

DELIMITER ;
-- -----------------------------------------------------
-- G2 likelihood of dactivity > threshold_act, given similarity > threshold_sim
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS `tanimotoG2`;
DELIMITER $$
CREATE FUNCTION `tanimotoG2`(property_id INT, dataset_id INT,
                              min1 DOUBLE, max1 DOUBLE,
                              min2 DOUBLE, max2 DOUBLE,
                              method INT,
                              threshold_sim DOUBLE,threshold_dact DOUBLE) RETURNS double
BEGIN

  DECLARE no_more_rows BOOLEAN DEFAULT FALSE;
  DECLARE sim float;
  DECLARE act float;
  DECLARE num FLOAT;
  DECLARE sali FLOAT;
  DECLARE maxsali FLOAT default 0;
  DECLARE maxsim FLOAT default 0;

  DECLARE g2 FLOAT DEFAULT 0;
  DECLARE id INT;

  -- large_act_large_sim
  DECLARE a FLOAT DEFAULT 0;
  -- large_act_small_sim
  DECLARE b FLOAT DEFAULT 0;
  -- small_act_large_sim
  DECLARE c FLOAT DEFAULT 0;
  -- small_act_small_sim
  DECLARE d FLOAT DEFAULT 0;

  DECLARE stats CURSOR FOR
  select
			  (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
			  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
			  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
			  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
			  /(f1.bc + f2.bc -
			  (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
			  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
			  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
			  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16)
        )
        )
			  ,
        abs(s1.value_num-s2.value_num)
  from struc_dataset d1
  join struc_dataset d2
  join property_values s1 on d1.idstructure=s1.idstructure
  join property_values s2 on d2.idstructure=s2.idstructure
  join fp1024 f1 on f1.idchemical=s1.idchemical
  join fp1024 f2 on f2.idchemical=s2.idchemical
  where d1.id_srcdataset=dataset_id
  and d2.id_srcdataset= dataset_id
  and s1.idproperty=property_id
  and s2.idproperty=property_id
  and s1.value_num >= min1  and s1.value_num < max1
  and s2.value_num >= min2 and s2.value_num < max2
  and s1.idchemical != s2.idchemical
  ;

  DECLARE CONTINUE HANDLER FOR NOT FOUND BEGIN
        SET no_more_rows = TRUE;
  END;

  SET @k = 1E-7;

  OPEN stats;

  the_loop: LOOP

    FETCH stats into sim,act;

  	IF no_more_rows THEN
	    	CLOSE stats;
		    LEAVE the_loop;
    END IF;

    SET sali = act/(1 + @k - sim);

    if (sali > maxsali) THEN
      SET maxsali = sali;
    END IF;

    if (sim > maxsim) THEN
      SET maxsim = sim;
    END IF;

    IF (sim > threshold_sim) THEN
      IF (act >= threshold_dact) THEN
        SET a = a + 1;
      ELSE
        SET c = c + 1;
       END IF;
    ELSE
      IF (act >= threshold_dact) THEN
        SET b = b + 1;
      ELSE
        SET d = d + 1;
       END IF;
    END IF;


  END LOOP the_loop;



  IF (method=1) THEN
    -- G2 likelihood
    SET a = a + @k;
    SET b = b + @k;
    SET c = c + @k;
    SET d = d + @k;
    SET  g2 = IF(a=0,0,IF(a=0,0,a*LN(a*(c+d)/(c*(a+b))))+IF(b=0,0,b*LN(b*(c+d)/(d*(a+b)))));
  ELSEIF (method = 2) THEN
    -- Fisher
    SET  g2 = IF((a+b+c+d)=0,0,(a+c)/(a+b+c+d));
  ELSEIF (method = 6) THEN
    SET g2 = maxsali;
  ELSEIF (method = 0) THEN -- maxsim
    SET g2 = maxsim;
  ELSEIF (method = 3) THEN
    SET @all = a+b+c+d;
    if (@all = 0) THEN
      return 0;
    END IF;
    -- SET g2 = if (a>0,10 + a/@all,if(d>0, d/@all, if (b>0, 5 + b/@all, if (c>0, 3 + c/@all , 0)))); -- IF((a+c)=0,0,a/(a+c));
--     SET g2 = 256*(256*(256*a/@all + b/@all) + c/@all) ;
    SET g2 = a;
    return a;
  END IF;
  return g2;
end $$

DELIMITER ;

-- g2 for the entire row --

DROP FUNCTION IF EXISTS `tanimotoG2row`;
DELIMITER $$
CREATE FUNCTION `tanimotoG2row`(property_id INT, dataset_id INT,
                              min1 DOUBLE, max1 DOUBLE,
                              method INT,
                              threshold_sim DOUBLE,threshold_dact DOUBLE) RETURNS double
BEGIN

  DECLARE no_more_rows BOOLEAN DEFAULT FALSE;
  DECLARE sim double;
  DECLARE act double;
  DECLARE num FLOAT;
  DECLARE g2 FLOAT DEFAULT 0;

  -- large_act_large_sim
  DECLARE a FLOAT DEFAULT 0;
  -- large_act_small_sim
  DECLARE b FLOAT DEFAULT 0;
  -- small_act_large_sim
  DECLARE c FLOAT DEFAULT 1E-7;
  -- small_act_small_sim
  DECLARE d FLOAT DEFAULT 1E-7;

  DECLARE stats CURSOR FOR
  select
			  (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
			  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
			  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
			  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
			  /(f1.bc + f2.bc -
			  (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
			  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
			  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
			  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
        )
			  ,
        abs(s1.value_num-s2.value_num)
  from struc_dataset d1
  join struc_dataset d2
  join property_values s1 on d1.idstructure=s1.idstructure
  join property_values s2 on d2.idstructure=s2.idstructure
  join fp1024 f1 on f1.idchemical=s1.idchemical
  join fp1024 f2 on f2.idchemical=s2.idchemical
  where d1.id_srcdataset=dataset_id
  and d2.id_srcdataset= dataset_id
  and s1.idproperty=property_id
  and s2.idproperty=property_id
  and s1.value_num >= min1  and s1.value_num < max1
  and d1.idstructure != d2.idstructure
  ;

  DECLARE CONTINUE HANDLER FOR NOT FOUND BEGIN
        SET no_more_rows = TRUE;
  END;

  OPEN stats;

  the_loop: LOOP

    FETCH stats into sim,act;

  	IF no_more_rows THEN
	    	CLOSE stats;
		    LEAVE the_loop;
    END IF;

    IF (sim > threshold_sim) THEN
      IF (act >= threshold_dact) THEN
        SET a = a + 1;
      ELSE
        SET c = c + 1;
       END IF;
    ELSE
      IF (act >= threshold_dact) THEN
        SET b = b + 1;
      ELSE
        SET d = d + 1;
       END IF;
    END IF;


  END LOOP the_loop;

  IF (method=5) THEN
    SET g2 = a;
  ELSE
    SET  g2 = IF(a=0,0,a*LN(a*(c+d)/(c*(a+b))))+IF(b=0,0,b*LN(b*(c+d)/(d*(a+b))));
  END IF;
  return g2;
end $$

DELIMITER ;

-- -----------------------------------------------------
-- Tanimoto distance of group of chemicals, given by idquery
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS `tanimotoSet`;
DELIMITER $$
CREATE FUNCTION `tanimotoSet`(query_id INT, metric1 INT, metric2 INT) RETURNS double
BEGIN

  DECLARE tanimoto DOUBLE DEFAULT 0;

  select avg(
  (
  bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16)
  ) / ( f1.bc + f2.bc - (
  bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
  bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
  bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
  bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16)
  ))) into tanimoto
  from fp1024 as f1
  join fp1024 as f2
  where f1.idchemical in (select idchemical from query_results where idquery=idquery and metric=metric1)
  and f2.idchemical in (select idchemical from query_results where idquery=idquery and metric=metric2);

  RETURN tanimoto;

END $$

DELIMITER ;

-- -----------------------------------------------------
-- Binned 2D space, Tanimoto average distance in each cell
-- -----------------------------------------------------
DROP PROCEDURE IF EXISTS `tanimotoSpace`;
DELIMITER $$
CREATE PROCEDURE `tanimotoSpace`(
    query_id INT)
BEGIN
  select b1.d,b2.d,tanimotoSet(query_id,b1.d,b2.d) from bins100 b1
  join bins100 b2 where b1.d<b2.d;
end $$
DELIMITER ;

-- -----------------------------------------------------
-- Compound order by G2 (requires chemstats data)
-- -----------------------------------------------------
DROP PROCEDURE IF EXISTS `g2order`;
DELIMITER $$
CREATE PROCEDURE `g2order`(IN dataset INT, IN property INT, IN laplacek DOUBLE)
BEGIN
  set laplacek = ifnull(laplacek,1E-7);
  select idchemical,ifnull(a.count,0),ifnull(b.count,0),ifnull(c.count,0),ifnull(d.count,0),
  (ifnull(a.count,0)+ifnull(c.count,0))/(ifnull(a.count,0)+ifnull(b.count,0)+ifnull(c.count,0)+ifnull(d.count,0)) as fisher,
  if (a.count is null,0,
  if(a.count is null,laplacek,a.count+laplacek)*ln(
  if(a.count is null,laplacek,a.count+laplacek)*(if(c.count is null,laplacek,c.count+laplacek)+if(d.count is null,laplacek,d.count+laplacek))/
  (
  if(c.count is null,laplacek,c.count+laplacek)*(if(a.count is null,laplacek,a.count+laplacek)+if(b.count is null,laplacek,b.count+laplacek))
  )
  )+
  if(b.count is null,laplacek,b.count+laplacek)*ln(
  if(b.count is null,laplacek,b.count+laplacek)*(if(c.count is null,laplacek,c.count+laplacek)+if(d.count is null,laplacek,d.count+laplacek))/
  (
  if(d.count is null,laplacek,d.count+laplacek)*(if(a.count is null,laplacek,a.count+laplacek)+if(b.count is null,laplacek,b.count+laplacek))
  )
  )
  ) g2
  from chemstats
  left join (
  SELECT idchemical,count FROM chemstats  where dactivity=1 and similarity = 1 and idproperty=property and id_srcdataset=dataset
  ) a using(idchemical)
  left join
  (
  SELECT idchemical,count FROM chemstats  where dactivity=1 and similarity = 0 and idproperty=property and id_srcdataset=dataset
  ) b using(idchemical)
  left join
  (
  SELECT idchemical,count FROM chemstats  where dactivity=0 and similarity = 1 and idproperty=property and id_srcdataset=dataset
  ) c using(idchemical)
  left join
  (
  SELECT idchemical,count FROM chemstats  where dactivity=0 and similarity = 0 and idproperty=property and id_srcdataset=dataset
  ) d using(idchemical)
  where idproperty=property and id_srcdataset=dataset
  group by idchemical
  order by g2 desc;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- Pair order by SALI
-- -----------------------------------------------------
DROP PROCEDURE IF EXISTS `saliorder`;
DELIMITER $$
CREATE PROCEDURE `saliorder`(IN property_id INT, IN dataset_id INT, IN maxpair INT, IN zero DOUBLE)
BEGIN

  set zero = ifnull(zero,0.000001);

    select
  f1.idchemical,f2.idchemical,s1.value_num,s2.value_num,
      abs(s1.value_num-s2.value_num)/(1 + zero -
     (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
     bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
     bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
     bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
     /(f1.bc + f2.bc -
     (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
     bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
     bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
     bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
     )) as sali,
     (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
     bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
     bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
     bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
     /(f1.bc + f2.bc -
     (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
     bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
     bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
     bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
     ) as tanimoto
  from struc_dataset d1
  join struc_dataset d2
  join property_values s1 on d1.idstructure=s1.idstructure
  join property_values s2 on d2.idstructure=s2.idstructure
  join fp1024 f1 on f1.idchemical=s1.idchemical
  join fp1024 f2 on f2.idchemical=s2.idchemical
  where d1.id_srcdataset=dataset_id
  and d2.id_srcdataset= dataset_id
  and s1.idproperty= property_id
  and s2.idproperty= property_id
  and d1.idstructure < d2.idstructure
  order by sali desc limit maxpair
  ;

END $$

DELIMITER ;

--
DROP PROCEDURE IF EXISTS `g2chemstats`;
 
DELIMITER $$
CREATE PROCEDURE `g2chemstats`(
                                                      property_id INT,
                                                      dataset_id INT,
                                                      act_threshold double,
                                                      sim_threshold double,
                                                      IN laplacek DOUBLE)
BEGIN
      DELETE from chemstats;

      insert into chemstats
      select
      null,
      s1.idchemical,
      IF(abs(s1.value_num-s2.value_num)>act_threshold,1,0) as act,
      IF(
      (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
      bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
      bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
      bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
      /(f1.bc + f2.bc -
      (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
      bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
      bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
      bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
      ) > sim_threshold, 1,0) sim,
      count(s1.idchemical),
      act_threshold,
      sim_threshold,
      dataset_id,
      property_id
      from   struc_dataset d1
      join   struc_dataset d2
      join property_values s2 on d2.idstructure=s2.idstructure
      join property_values s1 on d1.idstructure=s1.idstructure
      join fp1024 f1 on f1.idchemical=s1.idchemical
      join fp1024 f2 on f2.idchemical=s2.idchemical
      where
      s2.idproperty= property_id
      and d1.id_srcdataset= dataset_id
      and d2.id_srcdataset= dataset_id
      and s1.idproperty= property_id
      and s1.idchemical != s2.idchemical
      group by s1.idchemical,act desc,sim desc;

  set laplacek = ifnull(laplacek,1E-7);

  select idchemical,ifnull(a.count,0),ifnull(b.count,0),ifnull(c.count,0),ifnull(d.count,0),
  (ifnull(a.count,0)+ifnull(c.count,0))/(ifnull(a.count,0)+ifnull(b.count,0)+ifnull(c.count,0)+ifnull(d.count,0)) as fisher,

  if (a.count is null,0,
  if(a.count is null,laplacek,a.count+laplacek)*ln(
  if(a.count is null,laplacek,a.count+laplacek)*(if(c.count is null,laplacek,c.count+laplacek)+if(d.count is null,laplacek,d.count+laplacek))/
  (
  if(c.count is null,laplacek,c.count+laplacek)*(if(a.count is null,laplacek,a.count+laplacek)+if(b.count is null,laplacek,b.count+laplacek))
  )
  )+
  if(b.count is null,laplacek,b.count+laplacek)*ln(
  if(b.count is null,laplacek,b.count+laplacek)*(if(c.count is null,laplacek,c.count+laplacek)+if(d.count is null,laplacek,d.count+laplacek))/
  (
  if(d.count is null,laplacek,d.count+laplacek)*(if(a.count is null,laplacek,a.count+laplacek)+if(b.count is null,laplacek,b.count+laplacek))
  )
  )
  ) g2
  from chemstats
  left join (
  SELECT idchemical,count FROM chemstats  where dactivity=1 and similarity = 1
  ) a using(idchemical)
  left join
  (
  SELECT idchemical,count FROM chemstats  where dactivity=1 and similarity = 0
  ) b using(idchemical)
  left join
  (
  SELECT idchemical,count FROM chemstats  where dactivity=0 and similarity = 1
  ) c using(idchemical)
  left join
  (
  SELECT idchemical,count FROM chemstats  where dactivity=0 and similarity = 0
  ) d using(idchemical)
  group by idchemical
  order by g2 desc;

END $$

DELIMITER ;

--
DROP PROCEDURE IF EXISTS `g2order`;
DELIMITER $$

CREATE PROCEDURE `g2order`(IN laplacek DOUBLE)
BEGIN

  set laplacek = ifnull(laplacek,1E-7);

  select idchemical,ifnull(a.count,0),ifnull(b.count,0),ifnull(c.count,0),ifnull(d.count,0),
  (ifnull(a.count,0)+ifnull(c.count,0))/(ifnull(a.count,0)+ifnull(b.count,0)+ifnull(c.count,0)+ifnull(d.count,0)) as fisher,

  if (a.count is null,0,
  if(a.count is null,laplacek,a.count+laplacek)*ln(
  if(a.count is null,laplacek,a.count+laplacek)*(if(c.count is null,laplacek,c.count+laplacek)+if(d.count is null,laplacek,d.count+laplacek))/
  (
  if(c.count is null,laplacek,c.count+laplacek)*(if(a.count is null,laplacek,a.count+laplacek)+if(b.count is null,laplacek,b.count+laplacek))
  )
  )+
  if(b.count is null,laplacek,b.count+laplacek)*ln(
  if(b.count is null,laplacek,b.count+laplacek)*(if(c.count is null,laplacek,c.count+laplacek)+if(d.count is null,laplacek,d.count+laplacek))/
  (
  if(d.count is null,laplacek,d.count+laplacek)*(if(a.count is null,laplacek,a.count+laplacek)+if(b.count is null,laplacek,b.count+laplacek))
  )
  )
  ) g2
  from chemstats
  left join (
  SELECT idchemical,count FROM chemstats  where dactivity=1 and similarity = 1
  ) a using(idchemical)
  left join
  (
  SELECT idchemical,count FROM chemstats  where dactivity=1 and similarity = 0
  ) b using(idchemical)
  left join
  (
  SELECT idchemical,count FROM chemstats  where dactivity=0 and similarity = 1
  ) c using(idchemical)
  left join
  (
  SELECT idchemical,count FROM chemstats  where dactivity=0 and similarity = 0
  ) d using(idchemical)
  group by idchemical
  order by g2 desc;

END $$

DELIMITER ;

-- 
DROP PROCEDURE IF EXISTS `saliorder`;
DELIMITER $$

CREATE PROCEDURE `saliorder`(IN property_id INT, IN dataset_id INT, IN maxpair INT, IN zero DOUBLE)
BEGIN

  set zero = ifnull(zero,0.000001);

    select
  f1.idchemical,f2.idchemical,s1.value_num,s2.value_num,
      abs(s1.value_num-s2.value_num)/(1 + zero -
     (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
     bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
     bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
     bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
     /(f1.bc + f2.bc -
     (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
     bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
     bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
     bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
     )) as sali,
     (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
     bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
     bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
     bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
     /(f1.bc + f2.bc -
     (bit_count(f1.fp1 & f2.fp1) + bit_count(f1.fp2 & f2.fp2) + bit_count(f1.fp3 & f2.fp3) + bit_count(f1.fp4 & f2.fp4) +
     bit_count(f1.fp5 & f2.fp5) + bit_count(f1.fp6 & f2.fp6) + bit_count(f1.fp7 & f2.fp7) + bit_count(f1.fp8 & f2.fp8) +
     bit_count(f1.fp9 & f2.fp9) + bit_count(f1.fp10 & f2.fp10) + bit_count(f1.fp11 & f2.fp11) + bit_count(f1.fp12 & f2.fp12) +
     bit_count(f1.fp13 & f2.fp13) + bit_count(f1.fp14 & f2.fp14) + bit_count(f1.fp15 & f2.fp15) + bit_count(f1.fp16 & f2.fp16))
     ) as tanimoto
  from struc_dataset d1
  join struc_dataset d2
  join property_values s1 on d1.idstructure=s1.idstructure
  join property_values s2 on d2.idstructure=s2.idstructure
  join fp1024 f1 on f1.idchemical=s1.idchemical
  join fp1024 f2 on f2.idchemical=s2.idchemical
  where d1.id_srcdataset=dataset_id
  and d2.id_srcdataset= dataset_id
  and s1.idproperty= property_id
  and s2.idproperty= property_id
  and d1.idstructure < d2.idstructure
  order by sali desc limit maxpair
  ;

END $$

DELIMITER ;

-- GRANT EXECUTE ON FUNCTION getBinnedrange TO 'guest'@'localhost';
-- GRANT EXECUTE ON FUNCTION getPropertyMax TO 'guest'@'localhost';
-- GRANT EXECUTE ON FUNCTION getPropertyMin TO 'guest'@'localhost';
-- GRANT EXECUTE ON FUNCTION recordsInpropertyRange TO 'guest'@'localhost';
-- GRANT EXECUTE ON FUNCTION tanimotoCell TO 'guest'@'localhost';
-- GRANT EXECUTE ON FUNCTION tanimotoCellSK TO 'guest'@'localhost';
-- GRANT EXECUTE ON FUNCTION tanimotoChemicals TO 'guest'@'localhost';
-- GRANT EXECUTE ON FUNCTION tanimotoChemicalsSK TO 'guest'@'localhost';
-- GRANT EXECUTE ON FUNCTION tanimotoG2 TO 'guest'@'localhost';
-- GRANT EXECUTE ON FUNCTION tanimotoG2row TO 'guest'@'localhost';
-- GRANT EXECUTE ON FUNCTION tanimotoSet TO 'guest'@'localhost';
-- GRANT EXECUTE ON FUNCTION tanimotoWeighted TO 'guest'@'localhost';
-- GRANT EXECUTE ON PROCEDURE tanimotoBinnedSpace TO 'guest'@'localhost';
-- GRANT EXECUTE ON PROCEDURE tanimotoSpace TO 'guest'@'localhost';
-- GRANT EXECUTE ON PROCEDURE saliorder TO 'guest'@'localhost';
-- GRANT EXECUTE ON PROCEDURE g2chemstats TO 'guest'@'localhost';
-- GRANT EXECUTE ON PROCEDURE g2order TO 'guest'@'localhost';
-- GRANT EXECUTE ON PROCEDURE g2stats TO 'guest'@'localhost';

-- GRANT EXECUTE ON PROCEDURE g2counts TO 'guest'@'localhost';
-- GRANT EXECUTE ON PROCEDURE g2profile TO 'guest'@'localhost';

insert into version (idmajor,idminor,comment) values (6,6,"AMBIT Schema: chemical landscape support");
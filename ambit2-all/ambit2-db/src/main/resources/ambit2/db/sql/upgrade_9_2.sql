-- -----------------------------------------------------
-- Returns Tanimoto similarity betweeh two compounds given by idchemical 
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS `tanimotoChemicals`;

DELIMITER $$
CREATE FUNCTION `tanimotoChemicals`(chemical1_id INT, chemical2_id INT) RETURNS double
READS SQL DATA
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
DROP FUNCTION IF EXISTS `getPropertyMax`;
DELIMITER $$
CREATE FUNCTION `getPropertyMax`(property_id INT, dataset_id INT) RETURNS double
READS SQL DATA
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
READS SQL DATA
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
READS SQL DATA
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
READS SQL DATA
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
-- Tanimoto distance of group of chemicals, given by property range
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS `tanimotoCell`;
DELIMITER $$
CREATE FUNCTION `tanimotoCell`(property_id INT, dataset_id INT, min1 DOUBLE, max1 DOUBLE, min2 DOUBLE, max2 DOUBLE) RETURNS double
READS SQL DATA
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
                              READS SQL DATA
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
                              READS SQL DATA
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
READS SQL DATA
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

-- version 9.2
insert into version (idmajor,idminor,comment) values (9,2,"AMBIT2 schema - function qualifiers");
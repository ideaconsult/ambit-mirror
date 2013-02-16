-- -------------------------
-- Ambit schema 6.2
-- This should work under MySQL 5.1.
-- -------------------------

ALTER TABLE `chemicals` ADD COLUMN `lastmodified` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `label`;


DROP PROCEDURE IF EXISTS `findByProperty`;
DELIMITER $$
CREATE PROCEDURE `findByProperty`(
                IN chemical_id INT,
                IN search_mode INT,  -- 0 : name ; 1 : alias; 2: idproperty ; 3: any ; 4: inchi ; 5: inchikey
                IN property_name VARCHAR(255),
                IN property_alias VARCHAR(255),
                IN property_id INT,
                IN query_value TEXT,  -- string value or inchi
                IN query_value_num DOUBLE,
                IN query_inchi TEXT,
                IN maxrows INT)
    READS SQL DATA
BEGIN
    DECLARE property_id INT DEFAULT -1;
    SET @found = null;
    IF (chemical_id<=0) THEN
      SET chemical_id = 0;
      IF (!isnull(query_value)) THEN
      -- text
          CASE search_mode
          WHEN 0 THEN
          select idchemical,idproperty,value_ci into chemical_id,property_id,@found  from properties join summary_property_chemicals using(idproperty)
            join property_ci p using(id_ci) where name = property_name and value_ci = query_value limit 1;
          WHEN 1 THEN
            select idchemical,idproperty,value_ci into chemical_id,property_id,@found  from properties join summary_property_chemicals using(idproperty)
            join property_ci using(id_ci) where comments = property_alias and value_ci = query_value limit 1;
          WHEN 2 THEN
            select idchemical,idproperty,value_ci into chemical_id,property_id,@found  from summary_property_chemicals
            join property_ci using(id_ci) where idproperty = property_id and value_ci = query_value limit 1;
          WHEN 3 THEN
            select idchemical,idproperty,value_ci into chemical_id,property_id,@found  from summary_property_chemicals
            join property_ci using(id_ci)  where value_ci = query_value limit 1;
          WHEN 5 THEN
            select idchemical,null,inchi into chemical_id,property_id,@found from chemicals where inchikey = query_inchi limit 1;
          ELSE BEGIN
            select idchemical,null,inchi into chemical_id,property_id,@found from chemicals where inchi = query_inchi limit 1;
          END;
          END CASE;
      ELSE
      -- numeric
          CASE search_mode
          WHEN 0 THEN
          select idchemical,idproperty,value_num into chemical_id,property_id,@found from properties join property_values using(idproperty)
            where name = property_name and value_num = query_value_num limit 1;
          WHEN 1 THEN
            select idchemical,idproperty,value_num into chemical_id,property_id,@found from properties join property_values using(idproperty)
            where comments = property_alias and value_num = query_value_num limit 1;
          WHEN 2 THEN
            select idchemical,idproperty,value_num into chemical_id,property_id,@found from property_values
            where idproperty = property_id and value_num = query_value_num limit 1;
          ELSE BEGIN
            select  idchemical,idproperty,value_num into chemical_id,property_id,@found from property_values where value_num = query_value_num limit 1;
          END;
          END CASE;
      END IF;
    END IF;
    -- fallback
    if ((chemical_id<=0) and !isnull(query_inchi)) THEN
      select idchemical,null,inchi into chemical_id,property_id,@found from chemicals where inchi = query_inchi limit 1;
    END IF;
    -- get the preferred structure
    IF (maxrows<=0) THEN
  	  	select 1,idchemical,idstructure,if(type_structure='NA',0,1) as selected, preference as metric,@found as text,property_id
        from structure where idchemical = chemical_id order by idchemical,preference,idstructure;
    ELSE
      	select 1,idchemical,idstructure,if(type_structure='NA',0,1) as selected, preference as metric,@found as text,property_id
        from structure where idchemical = chemical_id order by idchemical,preference,idstructure limit 1;
    END IF;
END $$

DELIMITER ;
insert into version (idmajor,idminor,comment) values (6,2,"AMBIT2 schema");
-- bundle_number and version
ALTER TABLE `bundle` CHANGE COLUMN `user_name` `user_name` VARCHAR(16) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL  , ADD COLUMN `bundle_number` VARBINARY(16) NULL  AFTER `description` , ADD COLUMN `version` INT NULL DEFAULT 1  AFTER `bundle_number` ;
update bundle set bundle_number=unhex(idbundle) where idbundle>0;
ALTER TABLE `bundle` CHANGE COLUMN `bundle_number` `bundle_number` VARBINARY(16) NOT NULL;
ALTER TABLE `bundle` ADD UNIQUE INDEX `bundle_number` (`bundle_number` ASC, `version` ASC) ;
-- support for draft, published, archived, deleted
ALTER TABLE `bundle` ADD COLUMN `published_status` ENUM('draft','published','archived','deleted') NULL DEFAULT 'draft'  AFTER `version` ;
ALTER TABLE `bundle` ADD INDEX `published_status` (`published_status` ASC) ;
-- updated timestamp
ALTER TABLE `bundle` CHANGE COLUMN `created` `created` TIMESTAMP NOT NULL  , ADD COLUMN `updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  AFTER `published_status` ;
update bundle set updated=created; 
-- the name is not anymore unique (could be the same across versions and copies)
ALTER TABLE `bundle` DROP INDEX `assessment_name` ADD INDEX `assessment_name` (`name` ASC) ;

-- -----------------------------------------------------
-- Creates version of a bundle 
-- keeps the bundle_number and increments the version
-- -----------------------------------------------------
DROP PROCEDURE IF EXISTS `createBundleVersion`;
DELIMITER $$

CREATE PROCEDURE `createBundleVersion`(
				IN id INT,
                OUT version_new INT,
				OUT this_bundle_number VARCHAR(36),
				OUT id_new INT)
BEGIN
    DECLARE no_more_rows BOOLEAN;
    DECLARE bn VARBINARY(16);
    
    DECLARE bundle CURSOR FOR
    	select max(version)+1,bundle_number from bundle where bundle_number in (select bundle_number from bundle where idbundle=id) LIMIT 1;
    	
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_rows = TRUE;
    
    OPEN bundle;
    the_loop: LOOP

	  FETCH bundle into version_new,bn;
	  IF no_more_rows THEN
		  CLOSE bundle;
		  LEAVE the_loop;
  	END IF;
        
 	
    insert into bundle (idbundle,name,user_name,idreference,created,licenseURI,rightsHolder,maintainer,stars,description,bundle_number,version,published_status,updated)
    select null,name,user_name,idreference,now(),licenseURI,rightsHolder,maintainer,stars,description,bn,version_new,'draft',now()
    from bundle where idbundle=id;
	
	SET id_new = LAST_INSERT_ID();

    update bundle set published_status='archived' where idbundle=id and published_status='draft';
	update bundle set published_status='archived' where published_status='draft' and bundle_number=bn and (version != version_new);
	set this_bundle_number = hex(bn);

	insert into bundle_chemicals (idbundle,idchemical,created,tag,remarks)
    select id_new,idchemical,now(),tag,remarks from bundle_chemicals where idbundle=id;

	insert into bundle_endpoints (idbundle,topcategory,endpointcategory,endpointhash,created)
    select id_new,topcategory,endpointcategory,endpointhash,now() from bundle_endpoints where idbundle=id;

	insert into bundle_substance (idsubstance,idbundle,created,substance_prefix,substance_uuid)
    select idsubstance,id_new,now(),substance_prefix,substance_uuid from bundle_substance where idbundle=id;

    END LOOP the_loop;

END $$
DELIMITER ;

-- -----------------------------------------------------
-- create assessment copy procedure
-- -----------------------------------------------------
DROP PROCEDURE IF EXISTS `createBundleCopy`;
DELIMITER $$

CREATE PROCEDURE `createBundleCopy`(
				IN id INT,
	            IN myusername VARCHAR(16),
                OUT id_new INT)
BEGIN
    DECLARE bn VARCHAR(36);
	SET bn = replace(uuid(),"-","");
    insert into bundle (idbundle,name,user_name,idreference,created,licenseURI,rightsHolder,maintainer,stars,description,bundle_number,version,published_status,updated)
    select null,name,myusername,idreference,now(),licenseURI,rightsHolder,maintainer,stars,description,unhex(bn),1,'draft',now()
    from bundle where idbundle=id;
	
    SET id_new = LAST_INSERT_ID();
	
	-- update bundle set bundle_number=unhex(id_new) where idbundle=id_new;

	insert into bundle_chemicals (idbundle,idchemical,created,tag,remarks)
    select id_new,idchemical,now(),tag,remarks from bundle_chemicals where idbundle=id;

	insert into bundle_endpoints (idbundle,topcategory,endpointcategory,endpointhash,created)
    select id_new,topcategory,endpointcategory,endpointhash,now() from bundle_endpoints where idbundle=id;

	insert into bundle_substance (idsubstance,idbundle,created,substance_prefix,substance_uuid)
    select idsubstance,id_new,now(),substance_prefix,substance_uuid from bundle_substance where idbundle=id;

END $$
DELIMITER ;


-- 
insert into version (idmajor,idminor,comment) values (8,8,"AMBIT2 schema");

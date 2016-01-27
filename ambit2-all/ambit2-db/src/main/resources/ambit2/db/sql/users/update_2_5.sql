-- moving bundle policies into separate table
DROP TABLE IF EXISTS `policy_bundle`;
CREATE TABLE `policy_bundle` (
  `idpolicy` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(40) NOT NULL DEFAULT 'ambit_guest',
  `prefix` varchar(255) NOT NULL,
  `resource` varbinary(16) NOT NULL,
  `level` smallint(6) DEFAULT '2',
  `mget` tinyint(1) NOT NULL DEFAULT '0',
  `mput` tinyint(1) NOT NULL DEFAULT '0',
  `mpost` tinyint(1) NOT NULL DEFAULT '0',
  `mdelete` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idpolicy`),
  UNIQUE KEY `uri` (`prefix`,`resource`,`role_name`),
  KEY `fkrole1_idx` (`role_name`),
  KEY `get` (`mget`),
  KEY `put` (`mput`),
  KEY `post` (`mpost`),
  KEY `delete` (`mdelete`),
  KEY `fk_resource` (`resource`),
  CONSTRAINT `fkbrole1` FOREIGN KEY (`role_name`) REFERENCES `roles` (`role_name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into policy_bundle
SELECT null,role_name,prefix,unhex(replace(replace(resource,"/bundle/",""),"-","")), level,mget,mput,mpost,mdelete FROM policy where resource regexp "^/bundle/";

DELETE FROM policy where resource regexp "^/bundle/";

insert into version_users (idmajor,idminor,comment) values (2,5,"AMBITDB users");
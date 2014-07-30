DROP TABLE IF EXISTS `policy`;
CREATE TABLE `policy` (
  `idpolicy` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(16) NOT NULL DEFAULT 'ambit_guest',
  `prefix` varchar(255) NOT NULL,
  `resource` varchar(255) NOT NULL,
  `level` smallint(6) DEFAULT '1',
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
  CONSTRAINT `fkrole1` FOREIGN KEY (`role_name`) REFERENCES `roles` (`role_name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

insert into roles value("ambit_datasetmgr");
insert into roles value("ambit_modeller");
insert into roles value("ambit_model_user");

delete from user_roles where role_name="ambit_curator";
delete from roles where role_name="ambit_curator";

insert ignore into policy values(null,"ambit_admin","/ambit2","/admin",1,1,1,1,1);
insert ignore into policy values(null,"ambit_admin","/ambit2","/user",1,1,1,1,1);
insert ignore into policy values(null,"ambit_admin","/ambit2","/algorithm",1,1,1,1,1);
insert ignore into policy values(null,"ambit_admin","/ambit2","/model",1,1,1,1,1);
insert ignore into policy values(null,"ambit_admin","/ambit2","/substance",1,1,1,1,1);
insert ignore into policy values(null,"ambit_admin","/ambit2","/dataset",1,1,1,1,1);
insert ignore into policy values(null,"ambit_admin","/ambit2","/ui/updatesubstance1",2,1,1,1,1);
insert ignore into policy values(null,"ambit_admin","/ambit2","/ui/updatesubstancei5",2,1,1,1,1);
insert ignore into policy values(null,"ambit_admin","/ambit2","/ui/uploadsubstance",2,1,1,1,1);

insert ignore into policy values(null,"ambit_datasetmgr","/ambit2","/dataset",1,1,1,1,1);
insert ignore into policy values(null,"ambit_datasetmgr","/ambit2","/algorithm",1,1,1,1,1);
insert ignore into policy values(null,"ambit_datasetmgr","/ambit2","/model",1,1,1,1,1);
insert ignore into policy values(null,"ambit_datasetmgr","/ambit2","/substance",1,1,1,1,1);
insert ignore into policy values(null,"ambit_datasetmgr","/ambit2","/ui/updatesubstance1",2,1,1,1,1);
insert ignore into policy values(null,"ambit_datasetmgr","/ambit2","/ui/updatesubstancei5",2,1,1,1,1);
insert ignore into policy values(null,"ambit_datasetmgr","/ambit2","/ui/uploadsubstance",2,1,1,1,1);

insert ignore into policy values(null,"ambit_user","/ambit2","/dataset",1,1,0,0,0);
insert ignore into policy values(null,"ambit_user","/ambit2","/algorithm",1,1,0,0,0);
insert ignore into policy values(null,"ambit_user","/ambit2","/model",1,1,0,0,0);
insert ignore into policy values(null,"ambit_user","/ambit2","/substance",1,1,0,0,0);

insert into version (idmajor,idminor,comment) values (2,2,"AMBITDB users");
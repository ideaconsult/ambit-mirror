-- Database: ambit_users
-- ------------------------------------------------------

--
-- Table structure for table `users`
--
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `user_name` varchar(16) NOT NULL,
  `user_pass` varchar(32) NOT NULL,
  PRIMARY KEY (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `roles`
--
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `role_name` varchar(40) NOT NULL DEFAULT 'ambit_guest',
  PRIMARY KEY (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `organisation`
--

DROP TABLE IF EXISTS `organisation`;
CREATE TABLE `organisation` (
  `idorganisation` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `ldapgroup` varchar(128) DEFAULT NULL,
  `cluster` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`idorganisation`),
  UNIQUE KEY `xorg2` (`name`),
  KEY `korg2` (`cluster`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `policy`
--
DROP TABLE IF EXISTS `policy`;
CREATE TABLE `policy` (
  `idpolicy` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(40) NOT NULL DEFAULT 'ambit_guest',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
-- Table structure for table `project`
--
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `idproject` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `ldapgroup` varchar(128) DEFAULT NULL,
  `cluster` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`idproject`),
  UNIQUE KEY `xprj2` (`name`),
  KEY `kprj2` (`cluster`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `user`
--
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `iduser` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(45) DEFAULT NULL COMMENT 'OpenAM user name',
  `title` varchar(45) DEFAULT NULL,
  `firstname` varchar(45) NOT NULL,
  `lastname` varchar(45) NOT NULL,
  `institute` varchar(128) DEFAULT NULL,
  `weblog` varchar(45) DEFAULT NULL,
  `homepage` varchar(45) DEFAULT NULL,
  `address` varchar(128) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `keywords` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '""',
  `reviewer` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'true if wants to become a reviewer',
  PRIMARY KEY (`iduser`),
  UNIQUE KEY `Index_2` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `user_organisation`
--
DROP TABLE IF EXISTS `user_organisation`;
CREATE TABLE `user_organisation` (
  `iduser` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `idorganisation` int(10) unsigned NOT NULL,
  `priority` int(2) unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`iduser`,`idorganisation`),
  KEY `FK_user_organisation_2` (`idorganisation`),
  KEY `kprjuo` (`iduser`,`priority`),
  CONSTRAINT `FK_user_organisation_1` FOREIGN KEY (`iduser`) REFERENCES `user` (`iduser`) ON UPDATE CASCADE,
  CONSTRAINT `FK_user_organisation_2` FOREIGN KEY (`idorganisation`) REFERENCES `organisation` (`idorganisation`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `user_project`
--
DROP TABLE IF EXISTS `user_project`;
CREATE TABLE `user_project` (
  `iduser` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `idproject` int(10) unsigned NOT NULL,
  `priority` int(2) unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`iduser`,`idproject`),
  KEY `FK_user_project_2` (`idproject`),
  KEY `kup2` (`iduser`,`priority`),
  CONSTRAINT `FK_user_project_1` FOREIGN KEY (`iduser`) REFERENCES `user` (`iduser`) ON UPDATE CASCADE,
  CONSTRAINT `FK_user_project_2` FOREIGN KEY (`idproject`) REFERENCES `project` (`idproject`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `user_registration`
--
DROP TABLE IF EXISTS `user_registration`;
CREATE TABLE `user_registration` (
  `user_name` varchar(16) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `confirmed` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `code` varchar(45) NOT NULL,
  `status` enum('disabled','commenced','confirmed') NOT NULL DEFAULT 'disabled',
  PRIMARY KEY (`user_name`),
  UNIQUE KEY `kur2` (`code`) USING BTREE,
  CONSTRAINT `` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `user_roles`
--
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
  `user_name` varchar(16) NOT NULL,
  `role_name` varchar(40) NOT NULL,
  PRIMARY KEY (`user_name`,`role_name`),
  KEY `urolefk_idx` (`role_name`),
  CONSTRAINT `urolefk` FOREIGN KEY (`role_name`) REFERENCES `roles` (`role_name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `userfk` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `version`
--

DROP TABLE IF EXISTS `version`;
CREATE TABLE `version_users` (
  `idmajor` int(5) unsigned NOT NULL,
  `idminor` int(5) unsigned NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `comment` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`idmajor`,`idminor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


insert into version_users (idmajor,idminor,comment) values (2,4,"AMBITDB users");

-- -----------------------------------------------------
-- Default users
-- -----------------------------------------------------
insert into users values("admin",MD5("admin"));
insert into users values("guest",MD5("guest"));
insert into roles values("ambit_admin");
insert into roles values("ambit_user");
insert into roles value("ambit_datasetmgr");
insert into roles value("ambit_modeller");
insert into roles value("ambit_model_user");

insert into user_roles values("admin","ambit_admin");
insert into user_roles values("admin","ambit_user");

insert ignore into user_registration
SELECT user_name,now(),now(),concat("SYSTEM_",user_name),'confirmed' FROM users;

insert into user values (null,'admin','','Admin','Administrator','AMBIT','http://ambit.sf.net','http://ambit.sf.net','','','admin',1);
insert into user values (null,'guest','','Guest','Guest','AMBIT','http://ambit.sf.net','http://ambit.sf.net','','','guest',1);

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
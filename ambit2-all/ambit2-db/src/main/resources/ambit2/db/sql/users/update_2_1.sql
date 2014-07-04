DROP TABLE IF EXISTS `policy`;
CREATE TABLE `policy` (
  `idpolicy` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(16) NOT NULL DEFAULT 'ambit_guest',
  `prefix` varchar(255) NOT NULL,
  `resource` varchar(255) NOT NULL,
  `mget` tinyint(1) NOT NULL DEFAULT '0',
  `mput` tinyint(1) NOT NULL DEFAULT '0',
  `mpost` tinyint(1) NOT NULL DEFAULT '0',
  `mdelete` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idpolicy`),
  UNIQUE KEY `uri` (`resource`,`prefix`,`role_name`),
  KEY `fkrole1_idx` (`role_name`),
  KEY `get` (`mget`),
  KEY `put` (`mput`),
  KEY `post` (`mpost`),
  KEY `delete` (`mdelete`),
  CONSTRAINT `fkrole1` FOREIGN KEY (`role_name`) REFERENCES `roles` (`role_name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into roles value("ambit_dataset_mgr");
insert into roles value("ambit_modeller");
insert into roles value("ambit_model_user");

insert into policy values(null,"ambit_admin","","/model",1,1,1,1)
on duplicate key update 
mget=values(mget),mpost=values(mpost),
mput=values(mput),mdelete=values(mdelete);

insert into policy values(null,"ambit_admin","","/dataset",1,1,1,1)
on duplicate key update 
mget=values(mget),mpost=values(mpost),
mput=values(mput),mdelete=values(mdelete);

insert into policy values(null,"ambit_user","","/dataset",1,0,0,0)
on duplicate key update 
mget=values(mget),mpost=values(mpost),
mput=values(mput),mdelete=values(mdelete);

insert into policy values(null,"ambit_user","","/model",1,0,0,0)
on duplicate key update 
mget=values(mget),mpost=values(mpost),
mput=values(mput),mdelete=values(mdelete);

insert into policy values(null,"ambit_user","","/substance",1,0,0,0)
on duplicate key update 
mget=values(mget),mpost=values(mpost),
mput=values(mput),mdelete=values(mdelete);

insert into policy values(null,"ambit_admin","","/substance",1,1,1,1)
on duplicate key update 
mget=values(mget),mpost=values(mpost),
mput=values(mput),mdelete=values(mdelete);

insert into policy values(null,"ambit_admin","","/ui/uploadsubstance",1,1,1,1)
on duplicate key update 
mget=values(mget),mpost=values(mpost),
mput=values(mput),mdelete=values(mdelete);

insert into policy values(null,"ambit_admin","","/user",1,1,1,1)
on duplicate key update 
mget=values(mget),mpost=values(mpost),
mput=values(mput),mdelete=values(mdelete);

insert into policy values(null,"ambit_admin","","/admin/role",1,1,1,1)
on duplicate key update 
mget=values(mget),mpost=values(mpost),
mput=values(mput),mdelete=values(mdelete);

insert into version (idmajor,idminor,comment) values (2,1,"AMBITDB users");
CREATE DATABASE `tomcat_users` /*!40100 DEFAULT CHARACTER SET utf8 */;

DROP TABLE IF EXISTS `tomcat_users`.`roles`;
CREATE TABLE  `tomcat_users`.`roles` (
  `role_name` varchar(16) character set latin1 NOT NULL,
  PRIMARY KEY  (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `tomcat_users`.`user_roles`;
CREATE TABLE  `tomcat_users`.`user_roles` (
  `user_name` varchar(16) character set latin1 NOT NULL,
  `role_name` varchar(16) character set latin1 NOT NULL,
  PRIMARY KEY  (`user_name`,`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `tomcat_users`.`users`;
CREATE TABLE  `tomcat_users`.`users` (
  `user_name` varchar(16) character set latin1 NOT NULL,
  `user_pass` varchar(32) character set latin1 NOT NULL,
  PRIMARY KEY  (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

use `tomcat_users`;
set names 'utf8';
insert into roles (role_name) values ("qmrf_user");
insert into roles (role_name) values ("qmrf_admin");
insert into users (user_name,user_pass) values ("admin",md5("qmrfadmin"));
insert into user_roles (user_name,role_name) values ("admin","qmrf_admin");

insert into users (user_name,user_pass) values ("guest",md5("qmrf"));
insert into user_roles (user_name,role_name) values ("guest","qmrf_user");


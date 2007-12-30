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
-- author (ordinary user)
insert into roles (role_name) values ("qmrf_user");
-- reviewer
insert into roles (role_name) values ("qmrf_admin");
-- administrative user
insert into roles (role_name) values ("qmrf_manager");
-- chief editor
insert into roles (role_name) values ("qmrf_editor");
insert into users (user_name,user_pass) values ("admin","90ccd6798c380a61df628a73895c5fb2");
insert into users (user_name,user_pass) values ("editor","4f0d4a73e581a9e08f2ce26a59582428");
insert into user_roles (user_name,role_name) values ("admin","qmrf_admin");
insert into user_roles (user_name,role_name) values ("admin","qmrf_manager");
insert into user_roles (user_name,role_name) values ("editor","qmrf_editor");

insert into users (user_name,user_pass) values ("guest","4f0d4a73e581a9e08f2ce26a59582428");
insert into user_roles (user_name,role_name) values ("guest","qmrf_user");


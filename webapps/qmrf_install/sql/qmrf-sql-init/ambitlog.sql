CREATE DATABASE `ambitlog` /*!40100 DEFAULT CHARACTER SET utf8 */;

DROP TABLE IF EXISTS `ambitlog`.`countries`;
CREATE TABLE  `ambitlog`.`countries` (
  `id` int(11) NOT NULL auto_increment,
  `iso` char(2) NOT NULL default '',
  `name` varchar(100) NOT NULL default '',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1000002 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `ambitlog`.`u_hits`;
CREATE TABLE  `ambitlog`.`u_hits` (
  `page` varchar(64) NOT NULL default '',
  `ip` varchar(30) NOT NULL default '',
  `ref` varchar(64) NOT NULL default '',
  `counter` int(10) unsigned default NULL,
  `last_access` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `first_access` timestamp NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`page`,`ip`,`ref`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
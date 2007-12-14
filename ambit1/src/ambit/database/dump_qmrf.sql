
1) Dump the DBs

mysqldump --user=root --port=3306 --opt --databases ambit_qmrf > ambit_qmrf.sql
mysqldump --user=root --port=3306 --opt --databases qmrf_documents > qmrf_documents.sql
mysqldump --user=root --port=3306 --opt --databases tomcat_users > tomcat_users.sql


2) delete the following folders/files

$APP_FOLDER/mysql/data/ambit
$APP_FOLDER/mysql/data/ib_logfile0
$APP_FOLDER/mysql/data/ib_logfile1
$APP_FOLDER/mysql/data/ibdata1
$APP_FOLDER/mysql/data/aquire

3) mysql --user=root --port=33060

// no significant performance gain in our case
SET AUTOCOMMIT = 0;
SET FOREIGN_KEY_CHECKS=0;
SET UNIQUE_CHECKS=0;

SOURCE ambit.sql;
SOURCE aquire.sql;

// not necessary if the above SET commands have been skipped
SET FOREIGN_KEY_CHECKS = 1;
SET UNIQUE_CHECKS=1;
COMMIT;
SET AUTOCOMMIT = 1;

4) mysql --user=root --port=33060

use ambit;
alter table alias engine=innodb;
alter table ambituser engine=innodb;
alter table atom_distance engine=innodb;
alter table atom_structure engine=innodb;
alter table author engine=innodb;
alter table cas engine=innodb;
alter table datasets engine=innodb;
alter table ddictionary engine=innodb;
alter table descrgroups engine=innodb;
alter table dgroup engine=innodb;
alter table dict_user engine=innodb;
alter table dsname engine=innodb;
alter table dvalues engine=innodb;
alter table experiment engine=innodb;
alter table fp1024 engine=innodb;
alter table fpae engine=innodb;
alter table fpaeid engine=innodb;
alter table gamut engine=innodb;
alter table hierarchy engine=innodb;
alter table journal engine=innodb;
alter table literature engine=innodb;
alter table localdvalues engine=innodb;
alter table modeltype engine=innodb;
alter table name engine=innodb;
alter table qsar_user engine=innodb;
alter table qsardata engine=innodb;
alter table qsardescriptors engine=innodb;
alter table qsars engine=innodb;
alter table query engine=innodb;
alter table ref_authors engine=innodb;
alter table src_dataset engine=innodb;
alter table struc_dataset engine=innodb;
alter table struc_user engine=innodb;
alter table structure engine=innodb;
alter table study engine=innodb;
alter table study_conditions engine=innodb;
alter table study_fieldnames engine=innodb;
alter table study_results engine=innodb;
alter table stype engine=innodb;
alter table substance engine=innodb;
alter table template engine=innodb;
alter table template_def engine=innodb;
alter table timings engine=innodb;
alter table version engine=innodb;

use aquire;
optimize table aquire;
optimize table chemicalinfo;
optimize table chemicals;
optimize table concentrationtypecodes;
optimize table effects;
optimize table endpoints;
optimize table exposuretypecodes;
optimize table reference;
optimize table species_common_names;
optimize table species_data;

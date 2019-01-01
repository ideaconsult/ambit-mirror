create database `enmrelease` character set utf8;
use `enmrelease`;
source create_tables.sql;
GRANT ALL ON `enmrelease`.* TO 'guest'@'localhost' IDENTIFIED BY 'guest';
GRANT ALL ON `enmrelease`.* TO 'guest'@'127.0.0.1' IDENTIFIED BY 'guest';
GRANT ALL ON `enmrelease`.* TO 'guest'@'::1' IDENTIFIED BY 'guest';

GRANT TRIGGER ON `enmrelease`.* TO 'guest'@'localhost';
GRANT TRIGGER ON `enmrelease`.* TO 'guest'@'127.0.0.1';
GRANT TRIGGER ON `enmrelease`.* TO 'guest'@'::1';

GRANT EXECUTE ON PROCEDURE `enmrelease`.findByProperty TO 'guest'@'localhost';
GRANT EXECUTE ON PROCEDURE `enmrelease`.findByProperty TO 'guest'@'127.0.0.1';
GRANT EXECUTE ON PROCEDURE `enmrelease`.findByProperty TO 'guest'@'::1';

GRANT EXECUTE ON PROCEDURE `enmrelease`.deleteDataset TO 'guest'@'localhost';
GRANT EXECUTE ON PROCEDURE `enmrelease`.deleteDataset TO 'guest'@'127.0.0.1';
GRANT EXECUTE ON PROCEDURE `enmrelease`.deleteDataset TO 'guest'@'::1';

GRANT EXECUTE ON PROCEDURE `enmrelease`.createBundleCopy TO 'guest'@'localhost';
GRANT EXECUTE ON PROCEDURE `enmrelease`.createBundleCopy TO 'guest'@'127.0.0.1';
GRANT EXECUTE ON PROCEDURE `enmrelease`.createBundleCopy TO 'guest'@'::1';

GRANT EXECUTE ON PROCEDURE `enmrelease`.createBundleVersion TO 'guest'@'localhost';
GRANT EXECUTE ON PROCEDURE `enmrelease`.createBundleVersion TO 'guest'@'127.0.0.1';
GRANT EXECUTE ON PROCEDURE `enmrelease`.createBundleVersion TO 'guest'@'::1';
create database `ambit2` character set utf8;
use `ambit2`;
source create_tables.sql;
GRANT ALL ON `ambit2`.* TO 'guest'@'localhost' IDENTIFIED BY 'guest';
GRANT ALL ON `ambit2`.* TO 'guest'@'127.0.0.1' IDENTIFIED BY 'guest';
GRANT ALL ON `ambit2`.* TO 'guest'@'::1' IDENTIFIED BY 'guest';

GRANT TRIGGER ON `ambit2`.* TO 'guest'@'localhost';
GRANT TRIGGER ON `ambit2`.* TO 'guest'@'127.0.0.1';
GRANT TRIGGER ON `ambit2`.* TO 'guest'@'::1';

GRANT EXECUTE ON PROCEDURE `ambit2`.findByProperty TO 'guest'@'localhost';
GRANT EXECUTE ON PROCEDURE `ambit2`.findByProperty TO 'guest'@'127.0.0.1';
GRANT EXECUTE ON PROCEDURE `ambit2`.findByProperty TO 'guest'@'::1';

GRANT EXECUTE ON PROCEDURE `ambit2`.deleteDataset TO 'guest'@'localhost';
GRANT EXECUTE ON PROCEDURE `ambit2`.deleteDataset TO 'guest'@'127.0.0.1';
GRANT EXECUTE ON PROCEDURE `ambit2`.deleteDataset TO 'guest'@'::1';

GRANT EXECUTE ON PROCEDURE `ambit2`.createBundleCopy TO 'guest'@'localhost';
GRANT EXECUTE ON PROCEDURE `ambit2`.createBundleCopy TO 'guest'@'127.0.0.1';
GRANT EXECUTE ON PROCEDURE `ambit2`.createBundleCopy TO 'guest'@'::1';

GRANT EXECUTE ON PROCEDURE `ambit2`.createBundleVersion TO 'guest'@'localhost';
GRANT EXECUTE ON PROCEDURE `ambit2`.createBundleVersion TO 'guest'@'127.0.0.1';
GRANT EXECUTE ON PROCEDURE `ambit2`.createBundleVersion TO 'guest'@'::1';
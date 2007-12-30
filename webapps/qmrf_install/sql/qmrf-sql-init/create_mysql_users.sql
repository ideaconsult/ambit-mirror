-- Grants for hits@localhost
GRANT USAGE ON *.* TO 'hits'@'localhost' IDENTIFIED BY PASSWORD '*3D1CD28CCD8A1B0559B5D578BA6428A93F8D1F45';
GRANT SELECT, INSERT, UPDATE ON `ambitlog`.* TO 'hits'@'localhost';

-- Grants for guest@localhost
GRANT USAGE ON *.* TO 'guest'@'localhost' IDENTIFIED BY PASSWORD '*11DB58B0DD02E290377535868405F11E4CBEFF58';
-- GRANT SELECT ON `ambitlog`.* TO 'guest'@'localhost';
GRANT SELECT ON `ambit\_qmrf`.* TO 'guest'@'localhost';

-- Grants for qmrf@localhost
GRANT USAGE ON *.* TO 'qmrf'@'localhost' IDENTIFIED BY PASSWORD '*E2D5CC2802B4A31B95BD72ED4FE530BAF2E0DAA1';
GRANT SELECT, INSERT, UPDATE, DELETE ON `tomcat\_users`.* TO 'qmrf'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE, SHOW VIEW ON `qmrf\_documents`.* TO 'qmrf'@'localhost';
GRANT SELECT ON `ambit\_qmrf`.* TO 'qmrf'@'localhost';

-- Grants for lri_admin@localhost
GRANT USAGE ON *.* TO 'lri_admin'@'localhost' IDENTIFIED BY PASSWORD '*398C5FD5D2EEAD35F60321885C1E0B9EA5917CC4';
GRANT ALL PRIVILEGES ON `ambit_qmrf`.* TO 'lri_admin'@'localhost' WITH GRANT OPTION;

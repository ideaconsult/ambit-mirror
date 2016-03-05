-- not null fields in primary keys tables substance_ids and bundle_endpoints
-- MySQL < 5.7 converts these automatically, even if not specified explicitly
-- MySQL 5.7 gives error. create_tables.sql updated . No updates necessary for existing tables. 
insert into version (idmajor,idminor,comment) values (8,15,"AMBIT2 schema");
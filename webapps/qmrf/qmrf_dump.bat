mysqldump --user=root --port=3306 -p --opt --add-drop-database  --databases ambit_qmrf > ambit_qmrf.sql
mysqldump --user=root --port=3306 -p --opt --add-drop-database  --databases qmrf_documents > qmrf_documents.sql
mysqldump --user=root --port=3306 -p --opt --add-drop-database  --databases tomcat_users > tomcat_users.sql

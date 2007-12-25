#!/bin/bash
#
# QMRF Inventory database initialisation script
#
echo "=====QMRF Inventory initialisation launched====="

echo "Removing QMRF Inventory databases..."
mysql -s -e "DROP DATABASE IF EXISTS ambit_qmrf"
mysql -s -e "DROP DATABASE IF EXISTS ambitlog"
mysql -s -e "DROP DATABASE IF EXISTS qmrf_documents"
mysql -s -e "DROP DATABASE IF EXISTS tomcat_users"

echo "Removing QMRF Inventory attachments folder..."
if [ -d /var/lib/qmrf ] ; then rm -r /var/lib/qmrf
fi

echo "Restoring MySQL defaults"
mysql -s -e "SOURCE information_schema.sql"
mysql -s -e "SOURCE mysql.sql"
mysql -s -e "SOURCE test.sql"

echo "Creating tomcat users..."
mysql -s -e "SOURCE tomcat_users.sql"

echo "Initialising QMRF documents database..."
mysql -s -e "SOURCE qmrf_documents.sql"

echo "Initialising AMBIT log database..."
mysql -s -e "SOURCE ambitlog.sql"

echo "Initialising AMBIT QMRF database..."
mysql -s -e "SOURCE ambit_qmrf.sql"

echo "Creating MySQL users..."
mysql -s -e "SOURCE create_mysql_users.sql"

echo "Creating QMRF Inventory attachments folder..."
mkdir /var/lib/qmrf

echo "=====QMRF Inventory initialisation finished====="

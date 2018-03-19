#!/bin/bash

basedir=$PWD
PATH=$PATH:$basedir/build/bin

DBSERVICE=
DBUSERNAME=
DBPASSWORD=
HOSTIP=
CLUSTERNAME=
NODEID=
DSRC_DIR=
DATA_DIR=
MODE=Release

if [ ! -d $basedir/build ]
then
	mkdir $basedir/build
fi

read -p "**********************************************************
Welcome this script will import new Objects into the Oracle database.
**********************************************************
Do you want to import new Objects into database? (y/n) " response
response=${response,,} # tolower
if [[ $response =~ ^(yes|y| ) ]]; then
/home/swg/swg-main/build_object_template_crc_string_tables.py
perl  /home/swg/swg-main/src/game/server/database/templates/processTemplateList.pl < /home/swg/swg-main/dsrc/sku.0/sys.server/built/game/misc/object_template_crc_string_table.tab > /home/swg/swg-main/build/templates.sql
sqlplus swg/swg@127.0.0.1:1521/swg @/home/swg/swg-main/build/templates.sql > /home/swg/swg-main/build/templates.out

fi
echo "Congratulations new Objects script is complete!"

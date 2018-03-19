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

cd /home/swg/swg-main
git pull

unset ORACLE_HOME;
unset ORACLE_SID;
unset JAVA_HOME;   
export ORACLE_HOME=/usr/lib/oracle/12.1/client;
export JAVA_HOME=/usr/java;
export ORACLE_SID=swg;
rm -rf /home/swg/swg-main/build
mkdir /home/swg/swg-main/build
mkdir /home/swg/swg-main/build/bin
cd $basedir/build	

	if [ $(arch) == "x86_64" ]; then
        	export LDFLAGS=-L/usr/lib32
		export CMAKE_PREFIX_PATH="/usr/lib32:/lib32:/usr/lib/i386-linux-gnu:/usr/include/i386-linux-gnu"

		cmake -DCMAKE_C_FLAGS=-m32 \
		-DCMAKE_CXX_FLAGS=-m32 \
		-DCMAKE_EXE_LINKER_FLAGS=-m32 \
		-DCMAKE_MODULE_LINKER_FLAGS=-m32 \
		-DCMAKE_SHARED_LINKER_FLAGS=-m32 \
		-DCMAKE_BUILD_TYPE=$MODE \
		$basedir/src
	else
		cmake $basedir/src -DCMAKE_BUILD_TYPE=$MODE
	fi

make -j$(nproc)
cd $basedir

oldPATH=$PATH
PATH=$basedir/build/bin:$PATH

$basedir/utils/build_java_multi.sh
$basedir/utils/build_miff.sh
$basedir/utils/build_tab_multi.sh
$basedir/utils/build_tpf_multi.sh

$basedir/utils/build_object_template_crc_string_tables.py
$basedir/utils/build_quest_crc_string_tables.py

PATH=$oldPATH

/home/swg/swg-main/build_object_template_crc_string_tables.py
perl  /home/swg/swg-main/src/game/server/database/templates/processTemplateList.pl < /home/swg/swg-main/dsrc/sku.0/sys.server/built/game/misc/object_template_crc_string_table.tab > /home/swg/swg-main/build/templates.sql
sqlplus swg/swg@127.0.0.1:1521/swg @/home/swg/swg-main/build/templates.sql > /home/swg/swg-main/build/templates.out

read -p "Server Patching Is Now Complete"

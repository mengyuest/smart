#!/bin/bash

Config_File=~/student/mengyue/drill/db_java/db_manager/config/param.config
Gson_Jar=~/student/mengyue/drill/db_java/db_manager/lib/gson-2.5.jar
PostgreSQL_Jar=~/student/mengyue/drill/db_java/db_manager/lib/postgresql-9.4.1209.jre6.jar
Src_Path=~/student/mengyue/drill/db_java/db_manager/out/production/db_manager

# Insert to database
echo -e
echo -e
echo -e "\e[36;1mInsert to database...\e[0m"
java -cp $Gson_Jar:$PostgreSQL_Jar:${Src_Path} util/InsertProcess $Config_File

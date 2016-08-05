#!/bin/bash
test_Dir=~/student/mengyue/drill/test/
Gson_Jar=~/student/mengyue/drill/db_java/db_manager/lib/gson-2.5.jar
PostgreSQL_Jar=~/student/mengyue/drill/db_java/db_manager/lib/postgresql-9.4.1209.jre6.jar
Src_Path=~/student/mengyue/drill/db_java/db_manager/out/production/db_manager
java -cp $Gson_Jar:$PostgreSQL_Jar:${Src_Path} util/ReadProcess $1 $2

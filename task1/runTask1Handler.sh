#Try to kill remaining process using the port and set up the database server
echo "Stage I:Set up the server"
cd $MENG/drill/dbserver/
. killListeningProcess.sh
. runDatabaseServer.sh

#Try to transform the binary data to txt format
echo "Stage II:Transform the format from binary to txt"
cd $MENG/drill/DynaMIT/output
convState3D.linux i3d.flw
convState3D.linux i3d.spd
convState3D.linux i3d.dsy
convState3D.linux i3d.que

#Try to prune the data to the anticipating format for the Postgre to read 
echo "Stage III:Prune the format following the database standard"
cd ..
cd ..
cd task1
python set_db_from_output.py 



print "Please enter the same choice again  1.External Kafka - Berkeley DB - Elastic Search \n 2.External Kafka - HBASE - Elastic Search \n 3.External Kafka - HBase - Solr""
read choice
#IF HBASE
if [[ $choice = 2 || $choice = 3 ]]
then

$KAFKA_HOME/bin/kafka-topics.sh --delete --zookeeper localhost:2181 --topic ATLAS_HOOK

#clean up hbase

cd $HBASE_HOME
rm -r logs
jps > jps.txt
file=jps.txt
cmd1=$(grep -ci "HMaster" $file)
cmd2=$(grep -ci "HRegionServer" $file)
if [[ $cmd1 != 0 || $cmd2 != 0 ]]
then
      $HBASE_HOME/bin/stop-hbase.sh
fi


#start hbase

$HBASE_HOME/bin/start-hbase.sh
cd $HBASE_HOME/bin

#clean up titan table
table='titan'
echo "truncate '$table'"| $HBASE_HOME/bin/hbase shell
fi

#start solr
if [[ $choice = 3 ]]
then

cd $SOLR_HOME

rm -r logs
rm -r data
bin/solr start -e cloud -noprompt -z localhost:2181
bin/solr create -c vertex_index -d $SOLR_HOME/solr
bin/solr create -c edge_index -d $SOLR_HOME/solr
bin/solr create -c fulltext_index -d $SOLR_HOME/solr


fi

#clean up hive

cd $HIVE_HOME
rm -r logs
rm -r data



bin/atlas_start.py


#start hiveserver
hiveserver2


$username
$password
#beeline -u jdbc:hive2://localhost -n $username -p $password -e "create table (id int)"
#beeline -u jdbc:hive2://localhost -n $username -p $password -f 

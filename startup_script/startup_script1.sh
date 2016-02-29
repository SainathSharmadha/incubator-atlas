#!/bin/bash


printf "Enter your configuration to test:\n 1.External Kafka - Berkeley DB - Elastic Search \n 2.External Kafka - HBASE - Elastic Search \n 3.External Kafka - HBase - Solr"
read choice

#stop Atlas
rm -rf $ATLAS_HOME/data
rm -rf $ATLAS_HOME/logs
$ATLAS_HOME/bin/atlas_stop.py


#Start Hadoop
#rm -rf $HADOOP_HOME/hadoopdata
#rm -rf $HADOOP_HOMElogs
#hdfs namenode -format
#$HADOOP_HOME/sbin/stop-all.sh
#$HADOOP_HOME/sbin/start-dfs.sh
#$HADOOP_HOME/sbin/start-yarn.sh


#Start zookeeper and kafka
rm -rf $KAFKA_HOME/logs
rm -rf $KAFKA_HOME/data
$KAFKA_HOME/bin/zookeeper-server-stop.sh
$KAFKA_HOME/bin/kafka-server-stop.sh

jps > processes.txt
sed -e "s/ /|/g" processes.txt > p.txt
IFS="|"
while read line
do
pid=`echo $line|cut -f1 -d"|"`
process=`echo $line|cut -f2 -d"|" `
if [[ $process = "QuorumPeerManager" || $process = "kafka" ]]
then
kill -9 $pid
fi
done < p.txt

$KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties &
$KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties &

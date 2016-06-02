 #
 # Licensed to the Apache Software Foundation (ASF) under one
 # or more contributor license agreements.  See the NOTICE file
 # distributed with this work for additional information
 # regarding copyright ownership.  The ASF licenses this file
 # to you under the Apache License, Version 2.0 (the
 # "License"); you may not use this file except in compliance
 # with the License.  You may obtain a copy of the License at
 #
 #     http://www.apache.org/licenses/LICENSE-2.0
 #
 # Unless required by applicable law or agreed to in writing, software
 # distributed under the License is distributed on an "AS IS" BASIS,
 # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 # See the License for the specific language governing permissions and
 # limitations under the License.
 #


#!/bin/bash


printf "Enter your configuration to test:\n 1.External Kafka - Berkeley DB - Elastic Search \n 2.External Kafka - HBASE - Elastic Search \n 3.External Kafka - HBase - Solr"
printf "Change configuration in Atlas accordingly \n"
read choice

#Stop Atlas If not stopped

jps > processes.txt
#sed -e "s/ /|/g" processes.txt > p.txt
IFS="|"
	while read line
	do
		pid=`echo $line|cut -f1 -d" "`
		process=`echo $line|cut -f2 -d" " `
		if [[ $process = "Atlas" ]]
		then
			kill -9 $pid
		fi
	done < processes.txt



#Start Hadoop
hdfs namenode -format
$HADOOP_HOME/sbin/start-dfs.sh
$HADOOP_HOME/sbin/start-yarn.sh


#Stop zookeeper if not stopped
jps > processes.txt
#sed -e "s/ /|/g" processes.txt > p.txt
IFS="|"
	while read line
	do
		pid=`echo $line|cut -f1 -d" "`
		process=`echo $line|cut -f2 -d" " `
		if [[ $process = "QuorumPeerMain" || $process = "kafka" ]]
		then
			kill -9 $pid
			rm -r $KAFKA_HOME/logs
			rm -r $KAFKA_HOME/data
		fi
		done < processes.txt
rm -r $KAFKA_HOME/logs
rm -r $KAFKA_HOME/data

#Start zookeeper
$KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties &
$KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties 2> file.txt  &



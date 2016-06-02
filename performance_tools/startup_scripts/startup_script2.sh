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

printf "Please enter the same choice again  1.External Kafka - Berkeley DB - Elastic Search \n 2.External Kafka - HBASE - Elastic Search \n 3.External Kafka - HBase - Solr"
read choice

$KAFKA_HOME/bin/kafka-topics.sh --delete --zookeeper localhost:2181 --topic ATLAS_HOOK
#IF HBASE

if [[ $choice = 2 || $choice = 3 ]]
then


	#clean up hbase

		#Stop hbase if not stopped
		cd $HBASE_HOME
		rm -r logs
		jps > processes.txt
        	sed -e "s/ /|/g" processes.txt > p.txt
        	IFS="|"
        	while read line
        	do
                	pid=`echo $line|cut -f1 -d"|"`
                	process=`echo $line|cut -f2 -d"|" `
                	if [[ $process = "HMaster" || $process = "HRegionServer" ]]
                	then
                        	kill -9 $pid
                	fi
        	done < p.txt


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

	#stop solr if not stopped

	jps > processes.txt
	sed -e "s/ /|/g" processes.txt > p.txt
	IFS="|"
	while read line
	do
		pid=`echo $line|cut -f1 -d"|"`
		process=`echo $line|cut -f2 -d"|"`
		if [[ $process = "jar" ]]
		then
			kill -9 $pid
		fi
	done < p.txt

	cd $SOLR_HOME

	rm -r logs
        cp $SOLR_HOME/server/solr/solr.xml $SOLR_HOME/data/node1/
	bin/solr -c -z localhost:2181 -p 8983 -s $SOLR_HOME/data/node1/
	bin/solr create -c vertex_index -d $SOLR_HOME/solr
	bin/solr create -c edge_index -d $SOLR_HOME/solr
	bin/solr create -c fulltext_index -d $SOLR_HOME/solr

fi

#clean up hive

cd $HIVE_HOME
rm -r logs
rm -r data




$ATLAS_HOME/bin/atlas_start.py

echo "beeline -u jdbc:hive2://localhost:10000 -n temp -p shakthi108$"
#start hiveserver



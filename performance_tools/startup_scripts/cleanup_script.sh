
#!/bin/bash


#Stop Atlas
$ATLAS_HOME/bin/atlas_stop.py
rm -rf $ATLAS_HOME/data
rm -rf $ATLAS_HOME/logs



#Stop Solr
$SOLR_HOME/bin/solr delete -c vertex_index
$SOLR_HOME/bin/solr delete -c edge_index
$SOLR_HOME/bin/solr delete -c fulltext_index
$SOLR_HOME/bin/solr stop -all


#Stop HBase
$HBASE_HOME/bin/stop-hbase.sh



#Stop kafka
$KAFKA_HOME/bin/kafka-server-stop.sh


#Stop zookeeper
$KAFKA_HOME/bin/zookeeper-server-stop.sh

#Stop hadoop
rm -rf $HADOOP_HOME/hadoopdata
rm -rf $HADOOP_HOMElogs
$HADOOP_HOME/sbin/stop-yarn.sh
$HADOOP_HOME/sbin/stop-dfs.sh

#!/bin/bash

service ssh start

start-dfs.sh
start-yarn.sh

# exit safe mode
hdfs dfsadmin -safemode leave

#hadoop jar /usr/local/hadoop/share/hadoop/mapreduce/hadoop-*-examples-*.jar pi 2 5

# run bash
/bin/bash



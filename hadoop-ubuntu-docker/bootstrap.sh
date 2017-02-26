#!/bin/bash

service ssh start

start-dfs.sh
start-yarn.sh

# exit safe mode
hdfs dfsadmin -safemode leave

# run bash
/bin/bash


